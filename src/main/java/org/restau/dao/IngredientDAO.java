package org.restau.dao;

import lombok.AllArgsConstructor;
import org.restau.db.DbConnection;
import org.restau.entity.Ingredient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor

public class IngredientDAO {
    private DbConnection dbConnection;
    private StockMovementDAO stockMovementDAO;

    public IngredientDAO() {
        this.dbConnection = new DbConnection();
        this.stockMovementDAO = new StockMovementDAO();
    }

    public Optional<Ingredient> findById(Long id) {
        Ingredient ingredient = new Ingredient();

        String sql = """
                select i.id_ingredient, i.name, i.update_datetime, sm.id_stock_movement, sm.movement, sm.quantity, sm.unit, sm.movement_datetime 
                from Ingredient i
                join Stock_Movement sm on i.id_ingredient = sm.id_ingredient
                where i.id_ingredient = ?
                """;
        try (Connection con = dbConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ingredient.setIdIngredient(rs.getLong("id_ingredient"));
                    ingredient.setName(rs.getString("name"));
                    ingredient.setUpdateDatetime(rs.getTimestamp("update_datetime").toLocalDateTime());
                    ingredient.setStockMovements(stockMovementDAO.findStockMovementByIdIngredient(id));
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return Optional.of(ingredient);
    }

    public List<Ingredient> findByCriteria(
            List<Criteria> criterias,
            String sortColumn,
            boolean ascending,
            int page,
            int size) {

        if (page < 1) {
            throw new IllegalArgumentException("Page must be greater than 0 but actual is " + page);
        }

        List<Ingredient> ingredients = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
        SELECT DISTINCT i.id_ingredient, i.name, 
        (SELECT p.unit FROM Price p WHERE p.id_ingredient = i.id_ingredient 
         ORDER BY p.date DESC LIMIT 1) AS unit, 
        i.update_datetime
        FROM Ingredient i
        LEFT JOIN Price p ON i.id_ingredient = p.id_ingredient
        WHERE 1=1
    """);

        for (Criteria c : criterias) {
            switch (c.getColumn()) {
                case "name":
                    sql.append(" AND i.name ILIKE ? ");
                    break;
                case "unit":
                    sql.append(" AND EXISTS (SELECT 1 FROM Price p WHERE p.id_ingredient = i.id_ingredient AND p.unit = ?) ");
                    break;
                case "min_price":
                    sql.append(" AND EXISTS (SELECT 1 FROM Price p WHERE p.id_ingredient = i.id_ingredient AND p.unit_price >= ?) ");
                    break;
                case "max_price":
                    sql.append(" AND EXISTS (SELECT 1 FROM Price p WHERE p.id_ingredient = i.id_ingredient AND p.unit_price <= ?) ");
                    break;
                case "start_date":
                    sql.append(" AND i.update_datetime >= ? ");
                    break;
                case "end_date":
                    sql.append(" AND i.update_datetime <= ? ");
                    break;
            }
        }

        if (sortColumn != null && !sortColumn.isEmpty()) {
            sql.append(" ORDER BY ").append(sortColumn);
            sql.append(ascending ? " ASC " : " DESC ");
        }

        sql.append(" LIMIT ? OFFSET ? ");

        try (Connection con = dbConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql.toString())) {

            int index = 1;
            for (Criteria c : criterias) {
                switch (c.getColumn()) {
                    case "name":
                        pstmt.setString(index++, "%" + c.getValue().toString() + "%");
                        break;
                    case "unit":
                        pstmt.setObject(index++, c.getValue().toString(), java.sql.Types.OTHER);
                        break;
                    case "min_price":
                        pstmt.setDouble(index++, Double.parseDouble(c.getValue().toString()));
                        break;
                    case "max_price":
                        pstmt.setDouble(index++, Double.parseDouble(c.getValue().toString()));
                        break;
                    case "start_date":
                        pstmt.setTimestamp(index++, Timestamp.valueOf(c.getValue().toString() + " 00:00:00"));
                        break;
                    case "end_date":
                        pstmt.setTimestamp(index++, Timestamp.valueOf(c.getValue().toString() + " 23:59:59"));
                        break;
                }
            }

            pstmt.setInt(index++, size);
            pstmt.setInt(index, size * (page - 1));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ingredients.add(ingredientRsMapper(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching for ingredients with these criteria", e);
        }

        return ingredients;
    }

    private Ingredient ingredientRsMapper(ResultSet rs) throws SQLException {
        Ingredient ingredient = new Ingredient();
        ingredient.setIdIngredient(rs.getLong("id_ingredient"));
        ingredient.setName(rs.getString("name"));
        ingredient.setUpdateDatetime(rs.getTimestamp("update_datetime").toLocalDateTime());
        ingredient.setStockMovements(stockMovementDAO.findStockMovementByIdIngredient(rs.getLong("id_ingredient")));
        return ingredient;
    }

}

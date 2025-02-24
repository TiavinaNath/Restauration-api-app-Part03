package org.restau.dao;

import lombok.AllArgsConstructor;
import org.restau.db.DbConnection;
import org.restau.entity.Ingredient;
import org.restau.entity.Price;
import org.restau.entity.Unit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor

public class IngredientDAO implements CrudDAO<Ingredient> {

    private DbConnection dbConnection;

    public IngredientDAO() {
        this.dbConnection = new DbConnection();
    }

    public List<Ingredient> getAllPaginated(int page, int size) {
        List<Ingredient> ingredients = new ArrayList<>();

        String sql = "SELECT id_ingredient, name, update_datetime FROM ingredient LIMIT ? OFFSET ?";

        try (Connection con = dbConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, size);
            pstmt.setInt(2, page * size);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ingredients.add(ingredientRsMapper(rs));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return ingredients;
    }

    public Optional<Ingredient> findById(Long id) {
        Ingredient ingredient = null;
        String sql = "SELECT id_ingredient, name, update_datetime FROM ingredient WHERE id_ingredient = ?";

        try (Connection con = dbConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ingredient = ingredientRsMapper(rs);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return Optional.ofNullable(ingredient);
    }
/*
    public Optional<Ingredient> findById(Long id) {
        String sql = """
        SELECT i.id_ingredient, i.name, i.update_datetime, 
               p.id_price, p.unit_price, p.unit, p.date
        FROM ingredient i
        LEFT JOIN price p ON i.id_ingredient = p.id_ingredient
        WHERE p.date = (SELECT MAX(p2.date) FROM price p2 WHERE p2.id_ingredient = i.id_ingredient)
          AND i.id_ingredient = ?
    """;

        try (Connection con = dbConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Ingredient ingredient = ingredientRsMapper(rs); // Mapper l’ingrédient
                    // Récupérer le dernier prix sans l'ajouter à l'entité Ingredient
                    Price price = priceRsMapper(rs);
                    // Afficher ou utiliser le prix comme nécessaire (par exemple, log ou afficher)
                    // Tu n'as pas besoin de l’ajouter dans l’objet Ingredient
                    System.out.println("Dernier prix de l’ingrédient: " + price);
                    return Optional.of(ingredient);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return Optional.empty();
    }*/



    public Ingredient save(Ingredient ingredient) {
        String sql = """
            INSERT INTO ingredient (name, update_datetime) 
            VALUES (?, ?)
            ON CONFLICT (name) 
            DO UPDATE SET 
                update_datetime = EXCLUDED.update_datetime
            RETURNING id_ingredient
        """;

        try (Connection con = dbConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, ingredient.getName());
            pstmt.setTimestamp(2, Timestamp.valueOf(ingredient.getUpdateDatetime()));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ingredient.setIdIngredient(rs.getLong("id_ingredient"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return ingredient;
    }

    public List<Ingredient> saveAll(List<Ingredient> ingredients) {
        List<Ingredient> savedIngredients = new ArrayList<>();
        for (Ingredient ingredient : ingredients) {
            savedIngredients.add(save(ingredient));
        }
        return savedIngredients;
    }

    public boolean delete(Long id) {
        String sql = "DELETE FROM ingredient WHERE id_ingredient = ?";
        try (Connection con = dbConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0; // Retourne true si une ligne a été supprimée
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
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

/*
    private Price priceRsMapper(ResultSet rs, Ingredient ingredient) throws SQLException {
        if (rs.getObject("id_price") == null) {
            return null; // Aucun prix trouvé
        }

        Price price = new Price();
        price.setIdPrice(rs.getLong("id_price"));
        price.setIngredient(ingredient);
        price.setUnitPrice(rs.getDouble("unit_price"));
        price.setUnit(Unit.valueOf(rs.getString("unit")));
        price.setDate(rs.getDate("date").toLocalDate());

        return price;
    }

    private Price priceRsMapper(ResultSet rs) throws SQLException {
        Price price = new Price();
        price.setIdPrice(rs.getLong("id_price"));
        price.setUnitPrice(rs.getDouble("unit_price"));
        price.setUnit(Unit.valueOf(rs.getString("unit")));
        price.setDate(rs.getDate("date").toLocalDate());

        return price;
    }
*/


    private Ingredient ingredientRsMapper(ResultSet rs) throws SQLException {
        Ingredient ingredient = new Ingredient();
        ingredient.setIdIngredient(rs.getLong("id_ingredient"));
        ingredient.setName(rs.getString("name"));
        ingredient.setUpdateDatetime(rs.getTimestamp("update_datetime").toLocalDateTime());
        return ingredient;
    }

}

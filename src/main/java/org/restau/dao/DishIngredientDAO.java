package org.restau.dao;

import lombok.AllArgsConstructor;
import org.restau.db.DbConnection;
import org.restau.entity.DishIngredient;
import org.restau.entity.Ingredient;
import org.restau.entity.Unit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class DishIngredientDAO implements CrudDAO<DishIngredient> {

    private DbConnection dbConnection;

    public DishIngredientDAO() {
        this.dbConnection = new DbConnection();
    }

    @Override
    public List<DishIngredient> getAllPaginated(int page, int size) {
        List<DishIngredient> dishIngredients = new ArrayList<>();
        String sql = """
            SELECT di.id_dish, di.id_ingredient, di.required_quantity, di.unit, i.name, i.update_datetime
            FROM dish_ingredient di
            JOIN ingredient i ON di.id_ingredient = i.id_ingredient
            LIMIT ? OFFSET ?
        """;

        try (Connection con = dbConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, size);
            pstmt.setInt(2, page * size);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    dishIngredients.add(dishIngredientRsMapper(rs));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return dishIngredients;
    }

    @Override
    public Optional<DishIngredient> findById(Long id) {
        return Optional.empty(); // Cette table ne peut pas être recherchée par un seul ID
    }

    public List<DishIngredient> findByDishId(Long dishId) {
        List<DishIngredient> dishIngredients = new ArrayList<>();
        String sql = """
            SELECT di.id_dish, di.id_ingredient, di.required_quantity, di.unit, i.name, i.update_datetime
            FROM dish_ingredient di
            JOIN ingredient i ON di.id_ingredient = i.id_ingredient
            WHERE di.id_dish = ?
        """;

        try (Connection con = dbConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, dishId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    dishIngredients.add(dishIngredientRsMapper(rs));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return dishIngredients;
    }

    @Override
    public DishIngredient save(DishIngredient entity) {
        String sql = """
            INSERT INTO dish_ingredient (id_dish, id_ingredient, required_quantity, unit)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (id_dish, id_ingredient) 
            DO UPDATE SET required_quantity = EXCLUDED.required_quantity, unit = EXCLUDED.unit
        """;

        try (Connection con = dbConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, entity.getIngredient().getIdIngredient());
            pstmt.setBigDecimal(2, entity.getRequiredQuantity());
            pstmt.setString(3, entity.getUnit().name());

            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return entity;
    }

    @Override
    public List<DishIngredient> saveAll(List<DishIngredient> entities) {
        for (DishIngredient di : entities) {
            save(di);
        }
        return entities;
    }

    @Override
    public boolean delete(Long id) {
        return false; // La suppression se fait par dishId et ingredientId
    }

    public boolean delete(Long dishId, Long ingredientId) {
        String sql = "DELETE FROM dish_ingredient WHERE id_dish = ? AND id_ingredient = ?";
        try (Connection con = dbConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, dishId);
            pstmt.setLong(2, ingredientId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private DishIngredient dishIngredientRsMapper(ResultSet rs) throws SQLException {
        Ingredient ingredient = new Ingredient();
        ingredient.setIdIngredient(rs.getLong("id_ingredient"));
        ingredient.setName(rs.getString("name"));
        ingredient.setUpdateDatetime(rs.getTimestamp("update_datetime").toLocalDateTime());

        DishIngredient dishIngredient = new DishIngredient();
        dishIngredient.setIngredient(ingredient);
        dishIngredient.setRequiredQuantity(rs.getBigDecimal("required_quantity"));
        dishIngredient.setUnit(Unit.valueOf(rs.getString("unit")));

        return dishIngredient;
    }
}

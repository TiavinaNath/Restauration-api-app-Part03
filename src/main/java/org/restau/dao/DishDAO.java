package org.restau.dao;

import lombok.AllArgsConstructor;
import org.restau.db.DbConnection;
import org.restau.entity.Dish;
import org.restau.entity.DishIngredient;
import org.restau.entity.Ingredient;
import org.restau.entity.Unit;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@AllArgsConstructor

public class DishDAO implements CrudDAO<Dish> {
    private DbConnection dbConnection;

    public DishDAO() {
        this.dbConnection = new DbConnection();
    }


    @Override
    public List<Dish> getAllPaginated(int page, int size) {
        Map<Long, Dish> dishMap = new HashMap<>();

        String sql = """
            SELECT d.id_dish, d.name, d.unit_price,
                   i.id_ingredient, i.name AS ingredient_name, i.update_datetime,
                   di.required_quantity, di.unit
            FROM dish d
            LEFT JOIN dish_ingredient di ON d.id_dish = di.id_dish
            LEFT JOIN ingredient i ON di.id_ingredient = i.id_ingredient
            LIMIT ? OFFSET ?
        """;

        try (Connection con = dbConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, size);
            pstmt.setInt(2, (page-1) * size);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Long dishId = rs.getLong("id_dish");

                    // Vérifie si le plat est déjà dans la map
                    Dish dish = dishMap.computeIfAbsent(dishId, k -> {
                        try {
                            return mapDish(rs);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    });

                    // Si un ingrédient est associé, on l'ajoute
                    if (rs.getObject("id_ingredient") != null) {
                        DishIngredient dishIngredient = mapDishIngredient(rs);
                        dish.getDishIngredients().add(dishIngredient);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return new ArrayList<>(dishMap.values());
    }

    @Override
    public Optional<Dish> findById(Long id) {
        Dish dish = null;

        String sql = """
            SELECT d.id_dish, d.name, d.unit_price,
                   i.id_ingredient, i.name AS ingredient_name, i.update_datetime,
                   di.required_quantity, di.unit
            FROM dish d
            LEFT JOIN dish_ingredient di ON d.id_dish = di.id_dish
            LEFT JOIN ingredient i ON di.id_ingredient = i.id_ingredient
            WHERE d.id_dish = ?
        """;

        try (Connection con = dbConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    if (dish == null) {
                        dish = mapDish(rs);
                    }

                    // Ajout des ingrédients associés
                    if (rs.getObject("id_ingredient") != null) {
                        DishIngredient dishIngredient = mapDishIngredient(rs);
                        dish.getDishIngredients().add(dishIngredient);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return Optional.ofNullable(dish);
    }

    @Override
    public Dish save(Dish dish) {
        String sql = """
            INSERT INTO dish (name, unit_price) 
            VALUES (?, ?)
            ON CONFLICT (name) 
            DO UPDATE SET unit_price = EXCLUDED.unit_price
            RETURNING id_dish
        """;

        try (Connection con = dbConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, dish.getName());
            pstmt.setDouble(2, dish.getUnitPrice());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    dish.setIdDish(rs.getLong("id_dish"));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return dish;
    }

    @Override
    public List<Dish> saveAll(List<Dish> dishes) {
        List<Dish> savedDishes = new ArrayList<>();
        for (Dish dish : dishes) {
            save(dish);
        }
        return savedDishes;
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM dish WHERE id_dish = ?";

        try (Connection con = dbConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }


    public Integer getIngredientCost(Long idDish, LocalDate date) {
        String sql = """
        SELECT SUM(di.required_quantity * p.unit_price) AS total_cost
        FROM Dish_Ingredient di
        JOIN Price p ON di.id_ingredient = p.id_ingredient
        WHERE di.id_dish = ? 
          AND p.date = (
              SELECT MAX(date)
              FROM Price 
              WHERE id_ingredient = di.id_ingredient 
                AND date <= ?
          )
        GROUP BY di.id_dish;
    """;

        try (Connection con = dbConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, idDish);
            pstmt.setDate(2, Date.valueOf(date));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return (int) Math.round(rs.getDouble("total_cost"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static Dish mapDish(ResultSet rs) throws SQLException {
        return new Dish(
                rs.getLong("id_dish"),
                rs.getString("name"),
                rs.getDouble("unit_price"),
                new ArrayList<>()
        );
    }

    public static DishIngredient mapDishIngredient(ResultSet rs) throws SQLException {
        Ingredient ingredient = new Ingredient(
                rs.getLong("id_ingredient"),
                rs.getString("ingredient_name"),
                rs.getTimestamp("update_datetime").toLocalDateTime()
        );

        return new DishIngredient(
                ingredient,
                rs.getBigDecimal("required_quantity"),
                Unit.valueOf(rs.getString("unit"))
        );
    }

}

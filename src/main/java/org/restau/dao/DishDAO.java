package org.restau.dao;

import lombok.AllArgsConstructor;
import org.restau.db.DbConnection;
import org.restau.entity.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor

public class DishDAO {
    private DbConnection dbConnection;
    private StockMovementDAO stockMovementDAO;

    public DishDAO() {
        this.dbConnection = new DbConnection();
        this.stockMovementDAO = new StockMovementDAO();
    }

/*
    public Optional<Dish> findById(Long idDish) {
        String sql = "SELECT d.id_dish, d.name, d.unit_price FROM Dish d WHERE d.id_dish = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idDish);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Dish dish = new Dish();
                dish.setIdDish(rs.getLong("id_dish"));
                dish.setName(rs.getString("name"));
                dish.setUnitPrice(rs.getDouble("unit_price"));
                dish.setDishIngredients(getDishIngredients(idDish, conn)); // Récupérer les ingrédients
                return Optional.of(dish);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public Optional<Dish> findByIdWithDate(Long idDish, LocalDate date) {
        String sql = """
            SELECT d.id_dish, d.name, d.unit_price 
            FROM Dish d
            WHERE d.id_dish = ?""";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, idDish);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Dish dish = new Dish();
                dish.setIdDish(rs.getLong("id_dish"));
                dish.setName(rs.getString("name"));
                dish.setUnitPrice(rs.getDouble("unit_price"));
                dish.setDishIngredients(getDishIngredientsWithDate(idDish, date, conn)); // Filtrer les prix par date
                return Optional.of(dish);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    private List<DishIngredient> getDishIngredients(Long idDish, Connection conn) throws SQLException {
        String sql = """
            SELECT di.id_ingredient, i.name, di.required_quantity, di.unit, p.id_price, p.date, p.unit_price, p.unit 
            FROM Dish_Ingredient di
            JOIN Ingredient i ON di.id_ingredient = i.id_ingredient
            JOIN Price p ON di.id_price = p.id_price
            WHERE di.id_dish = ?""";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idDish);
            ResultSet rs = stmt.executeQuery();

            List<DishIngredient> dishIngredients = new ArrayList<>();
            while (rs.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setIdIngredient(rs.getLong("id_ingredient"));
                ingredient.setName(rs.getString("name"));

                Price price = new Price();
                price.setIdPrice(rs.getLong("id_price"));
                price.setDate(rs.getDate("date").toLocalDate());
                price.setUnitPrice(rs.getDouble("unit_price"));
                price.setUnit(Unit.valueOf(rs.getString("unit")));
                price.setIngredient(ingredient);

                DishIngredient dishIngredient = new DishIngredient();
                dishIngredient.setIngredient(ingredient);
                dishIngredient.setPrice(price);
                dishIngredient.setRequiredQuantity(rs.getBigDecimal("required_quantity"));
                dishIngredient.setUnit(Unit.valueOf(rs.getString("unit")));

                dishIngredients.add(dishIngredient);
            }
            return dishIngredients;
        }
    }

    private List<DishIngredient> getDishIngredientsWithDate(Long idDish, LocalDate date, Connection conn) throws SQLException {
        String sql = """
            SELECT di.id_ingredient, i.name, di.required_quantity, di.unit, p.id_price, p.date, p.unit_price, p.unit 
            FROM Dish_Ingredient di
            JOIN Ingredient i ON di.id_ingredient = i.id_ingredient
            JOIN Price p ON di.id_price = p.id_price
            WHERE di.id_dish = ? AND p.date = ?""";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, idDish);
            stmt.setDate(2, Date.valueOf(date));
            ResultSet rs = stmt.executeQuery();

            List<DishIngredient> dishIngredients = new ArrayList<>();
            while (rs.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setIdIngredient(rs.getLong("id_ingredient"));
                ingredient.setName(rs.getString("name"));

                Price price = new Price();
                price.setIdPrice(rs.getLong("id_price"));
                price.setDate(rs.getDate("date").toLocalDate());
                price.setUnitPrice(rs.getDouble("unit_price"));
                price.setUnit(Unit.valueOf(rs.getString("unit")));
                price.setIngredient(ingredient);

                DishIngredient dishIngredient = new DishIngredient();
                dishIngredient.setIngredient(ingredient);
                dishIngredient.setPrice(price);
                dishIngredient.setRequiredQuantity(rs.getBigDecimal("required_quantity"));
                dishIngredient.setUnit(Unit.valueOf(rs.getString("unit")));

                dishIngredients.add(dishIngredient);
            }
            return dishIngredients;
        }
    }*/

    public Optional<Dish> findById(Long id) {
        String sql = "select * from dish where id_dish = ?";
        Dish dish = new Dish();

        try (Connection con = dbConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    dish.setIdDish(rs.getLong("id_dish"));
                    dish.setName(rs.getString("name"));
                    dish.setUnitPrice(rs.getDouble("unit_price"));
                    dish.setDishIngredients(getDishIngredientOfDish(id));
                }
            }

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return Optional.of(dish);
    }

    public Optional<Dish> findById(Long id, LocalDate date) {
        String sql = "select * from dish where id_dish = ?";
        Dish dish = new Dish();

        try (Connection con = dbConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    dish.setIdDish(rs.getLong("id_dish"));
                    dish.setName(rs.getString("name"));
                    dish.setUnitPrice(rs.getDouble("unit_price"));
                    dish.setDishIngredients(getDishIngredientOfDishWithDate(id, date));
                }
            }

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return Optional.of(dish);
    }

    public List<DishIngredient> getDishIngredientOfDish (Long idDish) {
        String sql = """
                select i.id_ingredient, i.name, i.update_datetime, p.id_price, p.date, p.unit_price, p.unit, di.required_quantity from ingredient i
                join price p on i.id_ingredient = p.id_ingredient
                join dish_ingredient di on di.id_ingredient = i.id_ingredient
                join dish d on d.id_dish = di.id_dish
                where d.id_dish = ?
                """;

        List<DishIngredient> dishIngredients = new ArrayList<>();

        try (Connection con = dbConnection.getConnection();
            PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, idDish);

            try (ResultSet rs = pstmt.executeQuery())  {
                while (rs.next()) {
                    /*Ingredient ingredient = ingredientMapper(rs);
                    Price price = priceMapper(rs, ingredient);*/

                    Ingredient ingredient = new Ingredient();
                    ingredient.setIdIngredient(rs.getLong("id_ingredient"));
                    ingredient.setName(rs.getString("name"));
                    ingredient.setUpdateDatetime(rs.getTimestamp("update_datetime").toLocalDateTime());
                    ingredient.setStockMovements(stockMovementDAO.findStockMovementByIdIngredient(rs.getLong("id_ingredient")));

                    Price price = new Price();
                    price.setIdPrice(rs.getLong("id_price"));
                    price.setIngredient(ingredient);
                    price.setDate(rs.getDate("date").toLocalDate());
                    price.setUnitPrice(rs.getDouble("unit_price"));
                    price.setUnit(Unit.valueOf(rs.getString("unit")));

                    DishIngredient dishIngredientToAdd = new DishIngredient();
                    dishIngredientToAdd.setIngredient(ingredient);
                    dishIngredientToAdd.setPrice(price);
                    dishIngredientToAdd.setRequiredQuantity(rs.getBigDecimal("required_quantity"));
                    dishIngredientToAdd.setUnit(Unit.valueOf(rs.getString("unit")));

                    dishIngredients.add(dishIngredientToAdd);
                }
            }

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return dishIngredients;
    }

    public List<DishIngredient> getDishIngredientOfDishWithDate (Long idDish, LocalDate date) {
        String sql = """
                select i.id_ingredient, i.name, i.update_datetime, p.id_price, p.date, p.unit_price, p.unit, di.required_quantity from ingredient i
                join price p on i.id_ingredient = p.id_ingredient
                join dish_ingredient di on di.id_ingredient = i.id_ingredient
                join dish d on d.id_dish = di.id_dish
                where d.id_dish = ?
                and p.id_price = (
                    select p2.id_price from price p2
                    where di.id_ingredient = p2.id_ingredient
                    and p2.date <= ?
                    order by date desc
                    limit 1   
                )
                order by di.id_ingredient
                """;

        List<DishIngredient> dishIngredients = new ArrayList<>();

        try (Connection con = dbConnection.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setLong(1, idDish);
            pstmt.setDate(2, Date.valueOf(date));

            try (ResultSet rs = pstmt.executeQuery())  {
                while (rs.next()) {
                    /*Ingredient ingredient = ingredientMapper(rs);
                    Price price = priceMapper(rs, ingredient);*/

                    Ingredient ingredient = new Ingredient();
                    ingredient.setIdIngredient(rs.getLong("id_ingredient"));
                    ingredient.setName(rs.getString("name"));
                    ingredient.setUpdateDatetime(rs.getTimestamp("update_datetime").toLocalDateTime());
                    ingredient.setStockMovements(stockMovementDAO.findStockMovementByIdIngredient(rs.getLong("id_ingredient")));

                    Price price = new Price();
                    price.setIdPrice(rs.getLong("id_price"));
                    price.setIngredient(ingredient);
                    price.setDate(rs.getDate("date").toLocalDate());
                    price.setUnitPrice(rs.getDouble("unit_price"));
                    price.setUnit(Unit.valueOf(rs.getString("unit")));

                    DishIngredient dishIngredientToAdd = new DishIngredient();
                    dishIngredientToAdd.setIngredient(ingredient);
                    dishIngredientToAdd.setPrice(price);
                    dishIngredientToAdd.setRequiredQuantity(rs.getBigDecimal("required_quantity"));
                    dishIngredientToAdd.setUnit(Unit.valueOf(rs.getString("unit")));

                    dishIngredients.add(dishIngredientToAdd);
                }
            }

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        return dishIngredients;
    }




/*
    public static Dish dishMapper(ResultSet rs) throws SQLException {
        Dish dish = new Dish();
        dish.setIdDish(rs.getLong("id_dish"));
        dish.setName(rs.getString("name"));
        dish.setUnitPrice(rs.getDouble("unit_price"));
        dish.setDishIngredients(new ArrayList<>());

        return dish;
    }


    public Ingredient ingredientMapper(ResultSet rs) throws SQLException {
        Ingredient ingredient = new Ingredient();
        ingredient.setIdIngredient(rs.getLong("id_ingredient"));
        ingredient.setName(rs.getString("name"));
        ingredient.setUpdateDatetime(rs.getTimestamp("update_datetime").toLocalDateTime());

        return ingredient;
    }

    public Price priceMapper (ResultSet rs, Ingredient ingredient) throws SQLException {
        Price price = new Price();
        price.setIdPrice(rs.getLong("id_price"));
        price.setIngredient(ingredient);
        price.setDate(rs.getDate("date").toLocalDate());
        price.setUnitPrice(rs.getDouble("unit_price"));
        price.setUnit(Unit.valueOf(rs.getString("unit")));

        return price;
    }*/
}

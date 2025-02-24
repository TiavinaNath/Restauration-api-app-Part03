package org.restau;

import org.restau.dao.DishDAO;
import org.restau.dao.IngredientDAO;
import org.restau.db.DbConnection;
import org.restau.entity.Dish;
import org.restau.entity.Ingredient;

import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        IngredientDAO ingredientDAO = new IngredientDAO(new DbConnection());
        DishDAO dishDAO = new DishDAO(new DbConnection());

        /*List<Dish> result = dishDAO.getAllPaginated(1, 10);
        System.out.println(result);*/

        Optional<Ingredient> ingredient = ingredientDAO.findById(1L);
        System.out.println(ingredient.get());
    }
}
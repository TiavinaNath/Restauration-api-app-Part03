package org.restau;

import org.restau.dao.IngredientDAO;
import org.restau.dao.StockMovementDAO;
import org.restau.db.DbConnection;
import org.restau.entity.Dish;
import org.restau.entity.Ingredient;
import org.restau.entity.StockMovement;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        IngredientDAO ingredientDAO = new IngredientDAO();

        Ingredient ingredient = ingredientDAO.findById(3L).get();

        System.out.println(ingredient);
        System.out.println(ingredient.getAvailableStock());


        StockMovementDAO stockMovementDAO = new StockMovementDAO(new DbConnection());

        List<StockMovement> stockMovements = stockMovementDAO.findStockMovementByIdIngredient(3L);

        System.out.println(stockMovements);

        List<StockMovement> allStockMovements = stockMovementDAO.getAll();
        System.out.println(allStockMovements);


    }
}
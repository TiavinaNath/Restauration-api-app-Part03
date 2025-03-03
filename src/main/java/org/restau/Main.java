package org.restau;

import org.restau.dao.Criteria;
import org.restau.dao.DishDAO;
import org.restau.dao.IngredientDAO;
import org.restau.dao.StockMovementDAO;
import org.restau.db.DbConnection;
import org.restau.entity.Dish;
import org.restau.entity.Ingredient;
import org.restau.entity.StockMovement;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

      /*  IngredientDAO ingredientDAO = new IngredientDAO();

        Ingredient ingredient = ingredientDAO.findById(3L).get();

        System.out.println(ingredient);
        System.out.println(ingredient.getAvailableStock());


        StockMovementDAO stockMovementDAO = new StockMovementDAO(new DbConnection());

        List<StockMovement> stockMovements = stockMovementDAO.findStockMovementByIdIngredient(3L);

        System.out.println(stockMovements);

        List<StockMovement> allStockMovements = stockMovementDAO.getAll();
        System.out.println(allStockMovements);*/
        IngredientDAO subject = new IngredientDAO();

        /*List<Criteria> criterias = new ArrayList<>();
        criterias.add(new Criteria("name", "huile"));

        List<Ingredient> result = subject.findByCriteria(criterias, "name", true, 1, 10);

        System.out.println(result);*/
/*
        Ingredient Saucisse = subject.findById(1L).get();
        Ingredient Huile = subject.findById(2L).get();
        Ingredient Oeuf = subject.findById(3L).get();
        Ingredient Pain = subject.findById(4L).get();


        System.out.println(Saucisse.getAvailableStock());
        System.out.println(Huile.getAvailableStock());
        System.out.println(Oeuf.getAvailableStock());
        System.out.println(Pain.getAvailableStock());*/

        DishDAO dishDAO = new DishDAO();
        Dish hotDog = dishDAO.findById(1L).get();


        System.out.println(hotDog);
    }
}
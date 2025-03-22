package org.restau.exception;

import org.restau.entity.Dish;
import org.restau.entity.Ingredient;

import java.util.Map;

public class InsufficientIngredientsException extends RuntimeException {
    private final Map<Dish, Map<Ingredient, Double>> missingIngredients;

    public InsufficientIngredientsException(Map<Dish, Map<Ingredient, Double>> missingIngredients) {
        super(buildMessage(missingIngredients));
        this.missingIngredients = missingIngredients;
    }

    public Map<Dish, Map<Ingredient, Double>> getMissingIngredients() {
        return missingIngredients;
    }

    private static String buildMessage(Map<Dish, Map<Ingredient, Double>> missingIngredients) {
        StringBuilder sb = new StringBuilder("Commande impossible : ingrédients insuffisants.\n");
        for (Map.Entry<Dish, Map<Ingredient, Double>> dishEntry : missingIngredients.entrySet()) {
            sb.append("Plat : ").append(dishEntry.getKey().getName()).append("\n");
            for (Map.Entry<Ingredient, Double> ingredientEntry : dishEntry.getValue().entrySet()) {
                sb.append(" - Ingrédient : ").append(ingredientEntry.getKey().getName())
                        .append(" / Quantité manquante : ").append(ingredientEntry.getValue()).append("\n");
            }
        }
        return sb.toString();
    }
}
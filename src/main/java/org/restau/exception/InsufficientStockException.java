package org.restau.exception;

import org.restau.entity.Ingredient;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(Map<Ingredient, BigDecimal> missingIngredients) {
        super(formatMessage(missingIngredients));
    }

    private static String formatMessage(Map<Ingredient, BigDecimal> missingIngredients) {
        return missingIngredients.entrySet().stream()
                .map(e -> e.getValue() + " missing for " + e.getKey().getName())
                .collect(Collectors.joining(", "));
    }
}
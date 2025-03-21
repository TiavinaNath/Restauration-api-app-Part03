package org.restau.entity;

import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString

public class Dish {
    private Long idDish;
    private String name;
    private Double unitPrice;
    private List<DishIngredient> dishIngredients;

    public Dish(String name, Double unitPrice, List<DishIngredient> dishIngredients) {
        this.name = name;
        this.unitPrice = unitPrice;
        this.dishIngredients = dishIngredients;
    }

    public Dish(Long idDish, String name, Double unitPrice) {
        this.idDish = idDish;
        this.name = name;
        this.unitPrice = unitPrice;
        this.dishIngredients = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Dish {\n" +
                "  idDish=" + idDish + ",\n" +
                "  name='" + name + "',\n" +
                "  unitPrice=" + unitPrice + ",\n" +
                "  dishIngredients=\n" + dishIngredients + "\n}";
    }

    public Double getIngredientCost() {
        return dishIngredients.stream()
                .mapToDouble(e ->
                        (e.getPrice().getUnitPrice() != null ? e.getPrice().getUnitPrice() : 0.0)
                                * e.getRequiredQuantity().doubleValue()
                )
                .sum();
    }

    public Double getGrossMargin() {
        return unitPrice - getIngredientCost();
    }

    public int getAvailableQuantity() {
        return dishIngredients.stream()
                .map(di -> di.getIngredient().getAvailableStock().divide(di.getRequiredQuantity(), RoundingMode.FLOOR))
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO)
                .intValue();
    }

    public int getAvailableQuantityAt(LocalDate date ) {
        return dishIngredients.stream()
                .map(di -> di.getIngredient().getAvailableStock(date).divide(di.getRequiredQuantity(), RoundingMode.FLOOR))
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO)
                .intValue();
    }
}

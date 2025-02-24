package org.restau.entity;

import lombok.*;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode

public class DishIngredient {
    private Ingredient ingredient;
    private BigDecimal requiredQuantity;
    private Unit unit;

    @Override
    public String toString() {
        return "    DishIngredient {\n" +
                "      ingredient=" + ingredient.getName() + ",\n" +
                "      requiredQuantity=" + requiredQuantity + ",\n" +
                "      unit=" + unit + "\n" +
                "    }";
    }
}

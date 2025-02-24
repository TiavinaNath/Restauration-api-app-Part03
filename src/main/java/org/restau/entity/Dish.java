package org.restau.entity;

import lombok.*;

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
}

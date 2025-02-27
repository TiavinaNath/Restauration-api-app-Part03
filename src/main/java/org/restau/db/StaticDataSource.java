package org.restau.db;

import org.restau.entity.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StaticDataSource {
    private static final List<Dish> dishes = new ArrayList<>();

    static {
        Ingredient saucisse = new Ingredient(1L, "Saucisse", LocalDateTime.now());
        Ingredient huile = new Ingredient(2L, "Huile", LocalDateTime.now());
        Ingredient oeuf = new Ingredient(3L, "Oeuf", LocalDateTime.now());
        Ingredient pain = new Ingredient(4L, "Pain", LocalDateTime.now());

        Price p_saucisse = new Price(1L,saucisse , LocalDate.now(), 20.0, Unit.G);
        Price p_huile = new Price(2L, huile, LocalDate.now(), 10000.0,  Unit.L);
        Price p_oeuf = new Price(3L, oeuf, LocalDate.now(), 1000.0, Unit.U);
        Price p_pain = new Price(4L, pain, LocalDate.now(), 1000.0, Unit.U);

        DishIngredient di1 = new DishIngredient(saucisse, p_saucisse, new BigDecimal(100), Unit.G);
        DishIngredient di2 = new DishIngredient(huile, p_huile, new BigDecimal(0.15), Unit.L);
        DishIngredient di3 = new DishIngredient(oeuf, p_oeuf, new BigDecimal(1), Unit.U);
        DishIngredient di4 = new DishIngredient(pain, p_pain, new BigDecimal(1), Unit.U);

        Dish hotDog = new Dish(1L, "Hot Dog", 15000.0, Arrays.asList(di1, di2, di3, di4));

        dishes.add(hotDog);
    }

    public static List<Dish> getDishes() {
        return dishes;
    }
}

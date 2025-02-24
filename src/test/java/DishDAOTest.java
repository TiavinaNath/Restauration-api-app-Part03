import org.junit.jupiter.api.Test;
import org.restau.dao.DishDAO;
import org.restau.db.DbConnection;
import org.restau.entity.Dish;
import org.restau.entity.DishIngredient;
import org.restau.entity.Ingredient;
import org.restau.entity.Unit;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DishDAOTest {
    DishDAO subject = new DishDAO(new DbConnection());

    @Test
    void testFindById() {
        Ingredient saucisse = new Ingredient("Saucisse", LocalDateTime.now());
        Ingredient huile = new Ingredient( "Huile", LocalDateTime.now());
        Ingredient oeuf = new Ingredient( "Oeuf", LocalDateTime.now());
        Ingredient pain = new Ingredient( "Pain", LocalDateTime.now());
        DishIngredient di1 = new DishIngredient(saucisse, new BigDecimal(100), Unit.G);
        DishIngredient di2 = new DishIngredient(huile, new BigDecimal(0.15), Unit.L);
        DishIngredient di3 = new DishIngredient(oeuf, new BigDecimal(1), Unit.U);
        DishIngredient di4 = new DishIngredient(pain, new BigDecimal(1), Unit.U);

        Dish expectedDish = new Dish("Hot Dog", 15000.00, Arrays.asList(di1, di2, di3, di4));


        Optional<Dish> result = subject.findById(1L);

        assertNotNull(result, "Le résultat ne doit pas etre nul");
        assertEquals(expectedDish.getName(), result.get().getName(), "Le nom retrouné doit etre égale à Hot Dog");
        assertEquals(expectedDish.getUnitPrice(), result.get().getUnitPrice(), "Le prix retourné doit être égal à 15000.00");
        assertEquals(expectedDish.getDishIngredients().size(), result.get().getDishIngredients().size(), "Il devrait y avoir 4 types d'ingredients utilisé dans le plat");

        for (int i = 0; i < expectedDish.getDishIngredients().size(); i++ ) {
            assertEquals(expectedDish.getDishIngredients().get(i).getIngredient().getName(), result.get().getDishIngredients().get(i).getIngredient().getName());
            assertEquals(expectedDish.getDishIngredients().get(i).getUnit(), result.get().getDishIngredients().get(i).getUnit());
        }

    }

    @Test
    void testGetIngredientCost() throws SQLException {

        Integer expectedCost = 5500;

        Integer result = subject.getIngredientCost(1L, LocalDate.of(2026,01,01));

        assertNotNull(result, "Le résultat ne doit pas etre nul");
        assertEquals(expectedCost, result, "Le prix retourné doit être égal à 5500");
    }

    @Test
    void testGrossMargin() throws SQLException {

        Double expectedMargin = 9500.0;

        Double result = subject.getGrossMargin(1L, LocalDate.now());

        assertNotNull(result, "Le résultat ne doit pas etre nul");
        assertEquals(expectedMargin, result, "Le prix retourné doit être égal à 5500");
    }




}

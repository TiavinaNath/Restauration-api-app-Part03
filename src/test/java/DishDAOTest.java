import org.junit.jupiter.api.Test;
import org.restau.dao.DishDAO;
import org.restau.db.DbConnection;
import org.restau.entity.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DishDAOTest {
    DishDAO subject = new DishDAO();

    @Test
    void testFindById() {
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

        Dish expectedDish = new Dish(1L, "Hot Dog", 15000.0, Arrays.asList(di1, di2, di3, di4));


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
        Dish dishSubject = subject.findById(1L).get();

        Double expectedCost = 5500.0;
        Double result = dishSubject.getIngredientCost();

        assertNotNull(result, "Le résultat ne doit pas etre nul");
        assertEquals(expectedCost, result, "Le prix retourné doit être égal à 5500");
    }

    @Test
    void testGrossMargin() throws SQLException {
        Dish dishSubject = subject.findById(1L).get();
        Double expectedMargin = 9500.0;

        Double result = dishSubject.getGrossMargin();

        assertNotNull(result, "Le résultat ne doit pas etre nul");
        assertEquals(expectedMargin, result, "Le prix retourné doit être égal à 5500");
    }

}

import org.junit.jupiter.api.Test;
import org.restau.dao.DishDAO;
import org.restau.db.DbConnection;
import org.restau.entity.Dish;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DishDAOTest {
    DishDAO subject = new DishDAO(new DbConnection());

    @Test
    void testFindById() {

        Optional<Dish> result = subject.findById(1L);

        assertNotNull(result, "Le résultat ne doit pas etre nul");
        assertEquals("Hot Dog", result.get().getName(), "Le nom retrouné doit etre égale à Hot Dog");
        assertEquals(15000.00, result.get().getUnitPrice(), "Le prix retourné doit être égal à 15000.00");

    }

    @Test
    void testGetIngredientCost() throws SQLException {

        Integer result = subject.getIngredientCost(1L, LocalDate.of(2026,01,01));

        assertNotNull(result, "Le résultat ne doit pas etre nul");
        assertEquals(5500, result, "Le prix retourné doit être égal à 5500");
    }
}

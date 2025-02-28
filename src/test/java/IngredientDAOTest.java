import org.junit.jupiter.api.Test;
import org.restau.dao.IngredientDAO;
import org.restau.entity.Ingredient;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IngredientDAOTest {

    IngredientDAO subject = new IngredientDAO();

    @Test
    void testGetAvailableQuantityWithDate () {
        Ingredient saucisse = subject.findById(1L).get();
        Ingredient huile = subject.findById(2L).get();
        Ingredient oeuf = subject.findById(3L).get();
        Ingredient pain = subject.findById(4L).get();
        BigDecimal saucisseActualQuantity = saucisse.getAvailableStock(LocalDate.of(2025, 02, 02));
        BigDecimal huileActualQuantity = huile.getAvailableStock(LocalDate.of(2025, 02, 02));
        BigDecimal oeufActualQuantity = oeuf.getAvailableStock(LocalDate.of(2025, 02, 02));
        BigDecimal painActualQuantity = pain.getAvailableStock(LocalDate.of(2025, 02, 02));


        BigDecimal saucisseExpecteQuantity = new BigDecimal("10000.000");
        BigDecimal huileExpectedQuantity = new BigDecimal("20.000");
        BigDecimal oeufExpectedQuantity = new BigDecimal("100.000");
        BigDecimal painExpectedQuantity = new BigDecimal("50.000");


        assertEquals(saucisseExpecteQuantity, saucisseActualQuantity);
        assertEquals(huileExpectedQuantity, huileActualQuantity);
        assertEquals(oeufExpectedQuantity, oeufActualQuantity);
        assertEquals(painExpectedQuantity, painActualQuantity);

    }

    @Test
    void testGetAvailableQuantity () {
        Ingredient saucisse = subject.findById(1L).get();
        Ingredient huile = subject.findById(2L).get();
        Ingredient oeuf = subject.findById(3L).get();
        Ingredient pain = subject.findById(4L).get();
        BigDecimal saucisseActualQuantity = saucisse.getAvailableStock();
        BigDecimal huileActualQuantity = huile.getAvailableStock();
        BigDecimal oeufActualQuantity = oeuf.getAvailableStock();
        BigDecimal painActualQuantity = pain.getAvailableStock();


        BigDecimal saucisseExpecteQuantity = new BigDecimal("10000.000");
        BigDecimal huileExpectedQuantity = new BigDecimal("20.000");
        BigDecimal oeufExpectedQuantity = new BigDecimal("80.000");
        BigDecimal painExpectedQuantity = new BigDecimal("30.000");


        assertEquals(saucisseExpecteQuantity, saucisseActualQuantity);
        assertEquals(huileExpectedQuantity, huileActualQuantity);
        assertEquals(oeufExpectedQuantity, oeufActualQuantity);
        assertEquals(painExpectedQuantity, painActualQuantity);

    }
}

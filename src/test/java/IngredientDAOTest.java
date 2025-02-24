import org.junit.jupiter.api.Test;
import org.restau.dao.Criteria;
import org.restau.dao.IngredientDAO;
import org.restau.db.DbConnection;
import org.restau.entity.Ingredient;
import org.restau.entity.Unit;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class IngredientDAOTest {
    IngredientDAO subject = new IngredientDAO(new DbConnection());

    @Test
    public void testFindByCriteria_FilterByName() {
        List<Criteria> criterias = new ArrayList<>();
        criterias.add(new Criteria("name", "huile"));

        List<Ingredient> result = subject.findByCriteria(criterias, "name", true, 1, 10);

        assertEquals(1, result.size());
        assertEquals("Huile", result.get(0).getName());
    }

    @Test
    public void testFindByCriteria_FilterByUnit() {
        List<Criteria> criterias = new ArrayList<>();
        criterias.add(new Criteria("unit", Unit.L));

        List<Ingredient> result = subject.findByCriteria(criterias, "name", true, 1, 10);

        assertEquals(1, result.size());
        assertEquals("Huile", result.get(0).getName());
    }

    @Test
    public void testFindByCriteria_FilterByPriceRange() {
        List<Criteria> criterias = new ArrayList<>();
        criterias.add(new Criteria("min_price", 500.00));
        criterias.add(new Criteria("max_price", 1500.00));

        List<Ingredient> result = subject.findByCriteria(criterias, "id_ingredient", true, 1, 10);

        assertEquals(2, result.size());
        assertEquals("Oeuf", result.get(0).getName());
        assertEquals("Pain", result.get(1).getName());
    }


    @Test
    public void testFindByCriteria_FilterByStartDate() {
        List<Criteria> criterias = new ArrayList<>();
        criterias.add(new Criteria("start_date", "2025-02-23"));

        List<Ingredient> result = subject.findByCriteria(criterias, "update_datetime", true, 1, 10);

        assertEquals(4, result.size());
    }

    @Test
    public void testFindByCriteria_FilterByEndDate() {
        List<Criteria> criterias = new ArrayList<>();
        criterias.add(new Criteria("end_date", "2025-02-22"));

        List<Ingredient> result = subject.findByCriteria(criterias, "update_datetime", true, 1, 10);

        assertEquals(0, result.size());
    }

    @Test
    public void testFindByCriteria_FilterByDateRange() {
        List<Criteria> criterias = new ArrayList<>();
        criterias.add(new Criteria("start_date", "2025-02-23"));
        criterias.add(new Criteria("end_date", "2025-02-24"));

        List<Ingredient> result = subject.findByCriteria(criterias, "update_datetime", true, 1, 10);

        assertEquals(4, result.size());
    }

    @Test
    public void testFindByCriteria_Pagination() {
        List<Criteria> criterias = new ArrayList<>();

        List<Ingredient> resultPage1 = subject.findByCriteria(criterias, "name", true, 1, 1);
        List<Ingredient> resultPage2 = subject.findByCriteria(criterias, "name", true, 2, 1);

        assertEquals(1, resultPage1.size());
        assertEquals(1, resultPage2.size());
        assertNotEquals(resultPage1.get(0).getName(), resultPage2.get(0).getName());
    }
}


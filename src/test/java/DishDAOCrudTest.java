
import org.junit.jupiter.api.*;
import org.restau.dao.DishDAO;
import org.restau.db.DbConnection;
import org.restau.entity.Dish;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class DishDAOCrudTest {
    private DishDAO dishDAO;
    private DbConnection dbConnection;

    @BeforeAll
    void setupDatabase() throws SQLException {
        dbConnection = new DbConnection();
        dishDAO = new DishDAO();

        try (Connection connection = dbConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement("DELETE FROM dish")) {
            stmt.executeUpdate();
        }
    }

    @Test
    @Order(1)
    void testCreateAndRead() {
        Dish dish = new Dish(null, "Pizza", 12.5, null);
        dishDAO.save(dish);

        assertNotNull(dish.getIdDish(), "L'ID du plat ne devrait pas être null après la création");

        Optional<Dish> fetchedDish = dishDAO.findById(dish.getIdDish());
        assertNotNull(fetchedDish, "Le plat doit être récupérable");
        assertEquals("Pizza", fetchedDish.get().getName());
        assertEquals(12.5, fetchedDish.get().getUnitPrice());
    }

    @Test
    @Order(2)
    void testUpdate() {
        Dish dish = new Dish(null, "Burger", 8.0, null);
        dishDAO.save(dish);

        dish.setName("Cheese Burger");
        dish.setUnitPrice(9.5);
        dishDAO.update(dish);

        Optional<Dish> updatedDish = dishDAO.findById(dish.getIdDish());
        assertEquals("Cheese Burger", updatedDish.get().getName());
        assertEquals(9.5, updatedDish.get().getUnitPrice());
    }

    @Test
    @Order(3)
    void testDelete() {
        Dish dish = new Dish(null, "Sushi", 15.0, null);
        dishDAO.save(dish);
        assertTrue(dishDAO.findById(dish.getIdDish()).isPresent());

        dishDAO.delete(dish.getIdDish());
        assertFalse(dishDAO.findById(dish.getIdDish()).isPresent(), "Le plat devrait être supprimé");

    }
}

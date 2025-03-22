import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.restau.entity.Dish;
import org.restau.entity.DishOrder;
import org.restau.entity.StatusDishOrder;

import static org.junit.jupiter.api.Assertions.*;

class DishOrderStaticTest {
    private Dish dish;
    private DishOrder dishOrder;

    @BeforeEach
    void setUp() {
        dish = new Dish(1L, "Pizza", 10.0);
        dishOrder = new DishOrder(1L, dish, 2.0); // 2 pizzas
    }

    @Test
    void shouldReturnCreatedAsInitialStatus() {
        assertEquals(StatusDishOrder.CREATED, dishOrder.getActualStatus());
    }

    @Test
    void shouldUpdateStatusCorrectly() {
        dishOrder.updateStatus(StatusDishOrder.CONFIRMED);
        assertEquals(StatusDishOrder.CONFIRMED, dishOrder.getActualStatus());

        dishOrder.updateStatus(StatusDishOrder.IN_PREPARATION);
        assertEquals(StatusDishOrder.IN_PREPARATION, dishOrder.getActualStatus());

        dishOrder.updateStatus(StatusDishOrder.COMPLETED);
        assertEquals(StatusDishOrder.COMPLETED, dishOrder.getActualStatus());

        dishOrder.updateStatus(StatusDishOrder.SERVED);
        assertEquals(StatusDishOrder.SERVED, dishOrder.getActualStatus());
    }

    @Test
    void shouldThrowExceptionForInvalidStatusTransition() {
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            dishOrder.updateStatus(StatusDishOrder.IN_PREPARATION);
        });
        assertTrue(thrown.getMessage().contains("Transition de statut invalide"));
    }

    @Test
    void shouldCalculateTotalPriceCorrectly() {
        assertEquals(20.0, dishOrder.getTotalPrice()); // 2 pizzas x 10€
    }
}
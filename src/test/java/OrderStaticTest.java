import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.restau.entity.*;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {
    private Order order;
    private Dish dish;
    private DishOrder dishOrder;

    @BeforeEach
    void setUp() {
        order = new Order("ORDER-20250322-011");
        dish = new Dish(1L, "Pizza", 10.0);
        dishOrder = new DishOrder(1L, dish, 2.0);
    }

    @Test
    void shouldReturnCreatedAsInitialOrderStatus() {
        assertEquals(StatusOrder.CREATED, order.getActualStatus());
    }

    @Test
    void shouldAddDishOrders() {
        order.addDishOrders(List.of(dishOrder));
        assertFalse(order.getDishOrders().isEmpty());
    }

    @Test
    void shouldAddValidStatusHistory() {
        OrderStatusHistory statusHistory = new OrderStatusHistory(1L, StatusOrder.CONFIRMED, Instant.now());
        order.addStatusHistory(List.of(statusHistory));
        assertEquals(StatusOrder.CONFIRMED, order.getActualStatus());
    }

    @Test
    void shouldThrowExceptionForInvalidStatusTransition() {
        OrderStatusHistory invalidStatus = new OrderStatusHistory(1L, StatusOrder.SERVED, Instant.now());
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            order.addStatusHistory(List.of(invalidStatus));
        });
        assertTrue(thrown.getMessage().contains("Transition invalide"));
    }
}
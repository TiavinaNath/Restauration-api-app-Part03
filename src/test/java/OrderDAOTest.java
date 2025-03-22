import org.junit.jupiter.api.Test;
import org.restau.dao.DishDAO;
import org.restau.dao.OrderDAO;
import org.restau.entity.*;

import java.time.Instant;
import java.util.List;

import static java.time.Instant.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.restau.entity.StatusDishOrder.CREATED;

public class OrderDAOTest {
    DishDAO dishDAO = new DishDAO();
    OrderDAO subject = new OrderDAO();

    @Test
    void save_order() {
        Order order = new Order("ORDER-20250322-012");
        DishOrder dishOrder = new DishOrder(1L, dishDAO.findById(1L).orElseThrow(), 1.0);
        order.addDishOrders(List.of(dishOrder));
        order.addStatusHistory(List.of(new OrderStatusHistory(1L, StatusOrder.CREATED, Instant.now())));

        List<Order> actual = subject.saveAll(List.of(order));

        assertEquals(1, actual.size());
        Order actualOrder = actual.getFirst();
        List<DishOrder> dishOrders = actualOrder.getDishOrders();
        assertTrue(dishOrders.stream().allMatch(d -> CREATED.equals(d.getActualStatus())));
        assertEquals(StatusOrder.CREATED, actualOrder.getActualStatus());
    }
}

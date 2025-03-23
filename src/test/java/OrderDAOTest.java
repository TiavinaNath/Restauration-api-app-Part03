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
        Order order = new Order("Amennn");
        DishOrder dishOrder = new DishOrder(1L, dishDAO.findById(1L).orElseThrow(), 1.0);
        order.addDishOrders(List.of(dishOrder));
        order.addStatusHistory(List.of(new OrderStatusHistory(1L, StatusOrder.CREATED, Instant.now())));

        List<Order> actual = subject.saveAll(List.of(order));
        System.out.println(actual);

        assertEquals(1, actual.size());
        Order actualOrder = actual.getFirst();
        List<DishOrder> dishOrders = actualOrder.getDishOrders();
        assertTrue(dishOrders.stream().allMatch(d -> CREATED.equals(d.getActualStatus())));
        assertEquals(StatusOrder.CREATED, actualOrder.getActualStatus());
    }

    /*@Test
    void save_order_with_update_history() {
        Order order = new Order("Hafa ndray farany ndray hoe hafa");
        DishOrder dishOrder = new DishOrder(1L, dishDAO.findById(1L).orElseThrow(), 1.0);
        order.addDishOrders(List.of(dishOrder));
        order.addStatusHistory(List.of(new OrderStatusHistory(1L, StatusOrder.CREATED, Instant.now())));

        List<Order> before = subject.saveAll(List.of(order));
        System.out.println(before);

        assertEquals(1, before.get(0).getDishOrders().get(0).getStatusHistory().size());
        assertEquals(1, before.get(0).getStatusHistory().size());

        before.get(0).getDishOrders().get(0).updateStatus(StatusDishOrder.CONFIRMED);
        before.get(0).addStatusHistory(List.of(new OrderStatusHistory(2L, StatusOrder.CONFIRMED, Instant.now())));


        List<Order> after = subject.saveAll(before);
        System.out.println(after);
        assertEquals(2, after.get(0).getDishOrders().get(0).getStatusHistory().size());
        assertEquals(2, after.get(0).getStatusHistory().size());

    }*/
}

package org.restau.entity;

import lombok.*;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

import static org.restau.entity.StatusOrder.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Order {
    private Long idOrder;
    private String reference;
    private Instant creationDatetime;
    private List<DishOrder> dishOrders;
    private List<OrderStatusHistory> statusHistory;

    public StatusOrder getActualStatus() {
        return statusHistory.isEmpty() ? StatusOrder.CREATED : statusHistory.get(statusHistory.size() - 1).getStatus();
    }

    public OrderStatusHistory getActualOrderStatus() {
        return statusHistory.stream()
                .max(Comparator.comparing(OrderStatusHistory::getCreationDateTime))
                .orElse(new OrderStatusHistory(CREATED));
    }

    public double getTotalAmount() {
        return dishOrders.stream()
                .mapToDouble(e -> e.getDish().getUnitPrice() * e.getQuantity())
                .sum();
    }

    public List<DishOrder> addDishOrders(List<DishOrder> dishOrders) {
        if(!CREATED.equals(getActualStatus())) {
            throw new RuntimeException("Only CREATED status can be updated");
        }
        getDishOrders().addAll(dishOrders);
        return getDishOrders();
    }
}

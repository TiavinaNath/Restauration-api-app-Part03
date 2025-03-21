package org.restau.entity;

import lombok.*;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class DishOrder {
    private Long idDishOrder;
    private Dish dish;
    private Double quantity;
    private List<DishOrderStatusHistory> statusHistory;

    public StatusDishOrder getActualStatus() {
        return statusHistory.isEmpty() ? StatusDishOrder.CREATED : statusHistory.get(statusHistory.size() - 1).getStatus();
    }

    public double getTotalPrice() {
        return dish.getUnitPrice() * quantity;
    }
}

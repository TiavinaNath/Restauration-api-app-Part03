package org.restau.entity;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


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

    public DishOrder(Long idDishOrder, Dish dish, Double quantity) {
        this.idDishOrder = idDishOrder;
        this.dish = dish;
        this.quantity = quantity;
        this.statusHistory = new ArrayList<>();
    }


    @Override
    public String toString() {
        return "DishOrder {\n" +
                "  idDishOrder=" + idDishOrder + ",\n" +
                "  dish=" + dish + ",\n" +
                "  quantity=" + quantity + ",\n" +
                "  statusHistory=" + (statusHistory == null ? "[]" :
                statusHistory.stream()
                        .map(DishOrderStatusHistory::toString)
                        .collect(Collectors.joining(",\n    ", "[\n    ", "\n  ]"))) + "\n" +
                "}";
    }


    public StatusDishOrder getActualStatus() {
        return statusHistory.isEmpty() ? StatusDishOrder.CREATED : statusHistory.get(statusHistory.size() - 1).getStatus();
    }

    public double getTotalPrice() {
        return dish.getUnitPrice() * quantity;
    }

    public void updateStatus(StatusDishOrder newStatus) {
        StatusDishOrder currentStatus = getActualStatus();

        if (!isNextStatus(currentStatus, newStatus)) {
            throw new IllegalStateException("Transition de statut invalide de " + currentStatus + " vers " + newStatus);
        }

        statusHistory.add(new DishOrderStatusHistory(newStatus));
    }

    private boolean isNextStatus(StatusDishOrder current, StatusDishOrder next) {
        return switch (current) {
            case CREATED -> next == StatusDishOrder.CONFIRMED;
            case CONFIRMED -> next == StatusDishOrder.IN_PREPARATION;
            case IN_PREPARATION -> next == StatusDishOrder.COMPLETED;
            case COMPLETED -> next == StatusDishOrder.SERVED;
            default -> false;
        };
    }

    public void completeDish(Order parentOrder) {
        if (!StatusDishOrder.IN_PREPARATION.equals(getActualStatus())) {
            throw new IllegalStateException("Le plat doit être EN_PREPARATION pour être TERMINÉ");
        }

        updateStatus(StatusDishOrder.COMPLETED);
        parentOrder.checkIfCompleted();
    }

    public void serveDish(Order parentOrder) {
        if (!StatusDishOrder.COMPLETED.equals(getActualStatus())) {
            throw new IllegalStateException("Le plat doit être TERMINÉ pour être SERVI");
        }

        updateStatus(StatusDishOrder.SERVED);
        parentOrder.checkIfServed();
    }
}


/*package org.restau.service;

import org.restau.dao.DishOrderDAO;
import org.restau.dao.OrderDAO;
import org.restau.entity.*;
import org.restau.exception.InsufficientStockException;
import org.restau.exception.InvalidStatusTransitionException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class OrderService {
    private final OrderDAO orderDAO;
    private final DishOrderDAO dishOrderDAO;

    public OrderService(OrderDAO orderDAO, DishOrderDAO dishOrderDAO) {
        this.orderDAO = orderDAO;
        this.dishOrderDAO = dishOrderDAO;
    }

    public void confirmOrder(Order order) throws InsufficientStockException {
        Map<Ingredient, BigDecimal> missingIngredients = new HashMap<>();

        for (DishOrder dishOrder : order.getDishOrders()) {
            Double requestedQuantity = dishOrder.getQuantity();
            int availableQuantity = dishOrder.getDish().getAvailableQuantity();

            if (requestedQuantity > availableQuantity) {
                dishOrder.getDish().getDishIngredients().forEach(di -> {
                    Ingredient ingredient = di.getIngredient();
                    BigDecimal requiredTotal = di.getRequiredQuantity().multiply(BigDecimal.valueOf(requestedQuantity));
                    BigDecimal missing = requiredTotal.subtract(ingredient.getAvailableStock());
                    if (missing.compareTo(BigDecimal.ZERO) > 0) {
                        missingIngredients.put(ingredient, missing);
                    }
                });
            }
        }

        if (!missingIngredients.isEmpty()) {
            throw new InsufficientStockException(missingIngredients);
        }

        order.addStatus(StatusOrder.CONFIRMED);
        orderDAO.updateOrderStatus(order);
        for (DishOrder dishOrder : order.getDishOrders()) {
            dishOrder.addStatus(StatusDishOrder.CONFIRMED);
            dishOrderDAO.updateDishOrderStatus(dishOrder);
        }
    }

    public void updateDishOrderStatus(DishOrder dishOrder, StatusDishOrder newStatus) {
        StatusDishOrder currentStatus = dishOrder.getActualStatus();
        if (!isValidTransition(currentStatus, newStatus)) {
            throw new InvalidStatusTransitionException(currentStatus, newStatus);
        }
        dishOrder.addStatus(newStatus);
        dishOrderDAO.updateDishOrderStatus(dishOrder);
    }

    public void updateOrderStatusIfNeeded(Order order) {
        boolean allCompleted = order.getDishOrders().stream()
                .allMatch(d -> d.getActualStatus() == StatusDishOrder.COMPLETED);
        if (allCompleted) {
            order.addStatus(StatusOrder.COMPLETED);
            orderDAO.updateOrderStatus(order);
        }
    }

    private boolean isValidTransition(StatusDishOrder current, StatusDishOrder next) {
        return switch (current) {
            case CREATED -> next == StatusDishOrder.CONFIRMED;
            case CONFIRMED -> next == StatusDishOrder.IN_PREPARATION;
            case IN_PREPARATION -> next == StatusDishOrder.COMPLETED;
            case COMPLETED -> next == StatusDishOrder.SERVED;
            default -> false;
        };
    }
}*/

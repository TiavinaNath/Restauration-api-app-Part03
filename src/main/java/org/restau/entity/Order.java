package org.restau.entity;

import lombok.*;
import org.restau.exception.InsufficientIngredientsException;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.restau.entity.StatusOrder.*;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Order {
    private Long idOrder;
    private String reference;
    private Instant creationDatetime;
    private List<DishOrder> dishOrders = new ArrayList<>();
    private List<OrderStatusHistory> statusHistory = new ArrayList<>();

   /* public StatusOrder getActualStatus() {
        return statusHistory.isEmpty() ? StatusOrder.CREATED : statusHistory.get(statusHistory.size() - 1).getStatus();
    }*/

    public Order() {
        this.creationDatetime = Instant.now();
    }

    public Order(String reference) {
        this.reference = reference;
        this.creationDatetime = Instant.now();
    }


    @Override
    public String toString() {
        return "Order {\n" +
                "  idOrder=" + idOrder + ",\n" +
                "  reference='" + reference + "',\n" +
                "  creationDatetime=" + creationDatetime + ",\n" +
                "  dishOrders=" + dishOrders.stream()
                .map(DishOrder::toString)
                .collect(Collectors.joining(",\n    ", "[\n    ", "\n  ]")) + ",\n" +
                "  statusHistory=" + statusHistory.stream()
                .map(OrderStatusHistory::toString)
                .collect(Collectors.joining(",\n    ", "[\n    ", "\n  ]")) + "\n" +
                "}";
    }


    public StatusOrder getActualStatus() {
        return statusHistory.stream()
                .max(Comparator.comparing(OrderStatusHistory::getCreationDateTime))
                .orElse(new OrderStatusHistory(CREATED)).getStatus();
    }

    public double getTotalAmount() {
        return dishOrders.stream()
                .mapToDouble(e -> e.getDish().getUnitPrice() * e.getQuantity())
                .sum();
    }
/*
    public List<OrderStatusHistory> addStatusHistory(List<OrderStatusHistory> statusHistoryEntries) {
        if (statusHistoryEntries == null) {
            throw new IllegalArgumentException("Status history entry cannot be null");
        }
        this.statusHistory.addAll(statusHistoryEntries);

        return this.statusHistory;
    }*/

   public List<OrderStatusHistory> addStatusHistory(List<OrderStatusHistory> newStatus) {
        if (newStatus == null || newStatus.isEmpty()) {
            throw new IllegalArgumentException("Le statut ne peut pas être null ou vide.");
        }

        OrderStatusHistory currentStatus = null;
        if (!this.statusHistory.isEmpty()) {
            currentStatus = this.statusHistory.get(this.statusHistory.size() - 1); // Dernier statut enregistré
        }

        // Liste des statuts dans l'ordre
        List<StatusOrder> statusOrderList = List.of(
                StatusOrder.CREATED,
                StatusOrder.CONFIRMED,
                StatusOrder.IN_PREPARATION,
                StatusOrder.COMPLETED,
                StatusOrder.SERVED
        );

        if (currentStatus == null) {
            // Premier statut (doit être CREATED)
            if (!newStatus.get(0).getStatus().equals(StatusOrder.CREATED)) {
                throw new IllegalStateException("Le premier statut doit être CREATED.");
            }
        } else {
            int currentIndex = statusOrderList.indexOf(currentStatus.getStatus());
            int newIndex = statusOrderList.indexOf(newStatus.get(0).getStatus());

            if (newIndex == -1) {
                throw new IllegalArgumentException("Statut inconnu : " + newStatus.get(0).getStatus());
            }

            if (newIndex != currentIndex + 1) {
                throw new IllegalStateException("Transition invalide de " + currentStatus.getStatus() + " vers " + newStatus.get(0).getStatus());
            }
        }

        this.statusHistory.addAll(newStatus);

        return this.statusHistory;
    }
   /*public List<OrderStatusHistory> addStatusHistory(List<OrderStatusHistory> newStatuses) {
       if (newStatuses == null || newStatuses.isEmpty()) {
           throw new IllegalArgumentException("La liste des nouveaux statuts ne peut pas être vide.");
       }

       StatusOrder currentStatus = getActualStatus();

       for (OrderStatusHistory newStatusHistory : newStatuses) {
           StatusOrder newStatus = newStatusHistory.getStatus();

           if (currentStatus != null && currentStatus == StatusOrder.CREATED && newStatus == StatusOrder.CREATED) {
               continue;
           }

           if (!isNextStatus(currentStatus, newStatus)) {
               throw new IllegalStateException("Transition invalide de " + currentStatus + " vers " + newStatus);
           }

           statusHistory.add(newStatusHistory);
           currentStatus = newStatus;
       }

       return statusHistory;
   }*/

    public List<DishOrder> addDishOrders(List<DishOrder> dishOrders) {
        if(!CREATED.equals(getActualStatus())) {
            throw new RuntimeException("Only CREATED status can be");
        }

        getDishOrders().addAll(dishOrders);
        return getDishOrders();
    }

    public void confirm(Map<Ingredient, Double> availableStock) {
        if (!StatusOrder.CREATED.equals(getActualStatus())) {
            throw new IllegalStateException("La commande doit être au statut CRÉÉ pour être confirmée.");
        }

        Map<Dish, Map<Ingredient, Double>> missingIngredients = new HashMap<>();

        for (DishOrder dishOrder : dishOrders) {
            Dish dish = dishOrder.getDish();
            double quantityOrdered = dishOrder.getQuantity();

            // Parcourir la liste des DishIngredient du plat
            for (DishIngredient dishIngredient : dish.getDishIngredients()) {
                Ingredient ingredient = dishIngredient.getIngredient();

                // Quantité requise pour UN plat * la quantité de plats commandés
                double requiredPerDish = dishIngredient.getRequiredQuantity().doubleValue();
                double requiredTotal = requiredPerDish * quantityOrdered;

                // Vérification du stock
                double availableQuantity = availableStock.getOrDefault(ingredient, 0.0);
                double missingQuantity = requiredTotal - availableQuantity;

                if (missingQuantity > 0) {
                    missingIngredients
                            .computeIfAbsent(dish, k -> new HashMap<>())
                            .put(ingredient, missingQuantity);
                }
            }
        }

        if (!missingIngredients.isEmpty()) {
            throw new InsufficientIngredientsException(missingIngredients);
        }

        // Mise à jour du stock si tout est OK
        for (DishOrder dishOrder : dishOrders) {
            Dish dish = dishOrder.getDish();
            double quantityOrdered = dishOrder.getQuantity();

            for (DishIngredient dishIngredient : dish.getDishIngredients()) {
                Ingredient ingredient = dishIngredient.getIngredient();

                double requiredPerDish = dishIngredient.getRequiredQuantity().doubleValue();
                double requiredTotal = requiredPerDish * quantityOrdered;

                // Décrémenter le stock disponible
                availableStock.put(ingredient, availableStock.get(ingredient) - requiredTotal);
            }

            dishOrder.updateStatus(StatusDishOrder.CONFIRMED);
        }

        updateStatus(StatusOrder.CONFIRMED);
    }


    public void updateStatus(StatusOrder newStatus) {
        StatusOrder currentStatus = getActualStatus();

        if (!isNextStatus(currentStatus, newStatus)) {
            throw new IllegalStateException("Transition de statut invalide de " + currentStatus + " vers " + newStatus);
        }

        statusHistory.add(new OrderStatusHistory(newStatus));
    }

    private boolean isNextStatus(StatusOrder current, StatusOrder next) {
        return switch (current) {
            case CREATED -> next == StatusOrder.CONFIRMED;
            case CONFIRMED -> next == StatusOrder.IN_PREPARATION;
            case IN_PREPARATION -> next == StatusOrder.COMPLETED;
            case COMPLETED -> next == StatusOrder.SERVED;
            default -> false;
        };
    }

    public void startPreparation() {
        if (!StatusOrder.CONFIRMED.equals(getActualStatus())) {
            throw new IllegalStateException("Commande doit être CONFIRMÉE pour démarrer la préparation.");
        }

        // Passer la commande et tous les plats en PREPARATION
        updateStatus(StatusOrder.IN_PREPARATION);
        dishOrders.forEach(d -> d.updateStatus(StatusDishOrder.IN_PREPARATION));
    }

    public void checkIfCompleted() {
        boolean allDishesCompleted = dishOrders.stream()
                .allMatch(d -> StatusDishOrder.COMPLETED.equals(d.getActualStatus()));

        if (allDishesCompleted && StatusOrder.IN_PREPARATION.equals(getActualStatus())) {
            updateStatus(StatusOrder.COMPLETED);
        }
    }

    public void checkIfServed() {
        boolean allDishesServed = dishOrders.stream()
                .allMatch(d -> StatusDishOrder.SERVED.equals(d.getActualStatus()));

        if (allDishesServed && StatusOrder.COMPLETED.equals(getActualStatus())) {
            updateStatus(StatusOrder.SERVED);
        }
    }
}


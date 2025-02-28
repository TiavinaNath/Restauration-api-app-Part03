package org.restau.entity;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString

public class Ingredient {
    private Long idIngredient;
    private String name;
    private LocalDateTime updateDatetime;
    private List<StockMovement> stockMovements;

    public Ingredient(Long idIngredient, String name, LocalDateTime updateDatetime) {
        this.idIngredient = idIngredient;
        this.name = name;
        this.updateDatetime = updateDatetime;
    }

    public Ingredient(String name, LocalDateTime updateDatetime) {
        this.name = name;
        this.updateDatetime = updateDatetime;
    }

    public Ingredient(String name) {
        this.name = name;
        this.updateDatetime = LocalDateTime.now();
    }

    public BigDecimal getAvailableStock(LocalDate date) {
        return stockMovements.stream()
                .filter(e -> !e.getMovementDatetime().isAfter(date.atStartOfDay()))
                .map(e -> e.getMovement() == Movement.IN ? e.getQuantity() : e.getQuantity().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getAvailableStock() {
        return stockMovements.stream()
                .map(e -> e.getMovement() == Movement.IN? e.getQuantity() : e.getQuantity().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public String toString() {
        String stockMovementsStr = (stockMovements == null || stockMovements.isEmpty())
                ? "    (Aucun mouvement de stock)"
                : stockMovements.stream()
                .map(sm -> "    " + sm.toString().replace("\n", "\n    ")) // Indentation des mouvements
                .collect(Collectors.joining("\n"));

        return String.format(
                "Ingredient:\n" +
                        "  ID: %d\n" +
                        "  Nom: %s\n" +
                        "  Dernière mise à jour: %s\n" +
                        "  Mouvements de stock:\n%s",
                idIngredient, name, updateDatetime, stockMovementsStr
        );
    }

}

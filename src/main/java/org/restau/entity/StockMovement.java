package org.restau.entity;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString

public class StockMovement {
    private final Long idStockMovement;
    private final Long idIngredient;
    private final Movement movement;
    private final BigDecimal quantity;
    private final Unit unit;
    private final LocalDateTime movementDatetime;


    @Override
    public String toString() {
        return String.format(
                "StockMovement:\n" +
                        "      ID: %d\n" +
                        "      Ingrédient ID: %d\n" +
                        "      Type: %s\n" +
                        "      Quantité: %.3f %s\n" +
                        "      Date: %s",
                idStockMovement,
                idIngredient,
                movement, quantity, unit, movementDatetime
        );
    }
}

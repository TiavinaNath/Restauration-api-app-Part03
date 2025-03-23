package org.restau.entity;

import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class DishOrderStatusHistory {
    private Long idDishOrderStatus;
    private StatusDishOrder status;
    private Instant creationDateTime;

    public DishOrderStatusHistory(StatusDishOrder status) {
        this.status = status;
        this.creationDateTime = Instant.now();
    }
    @Override
    public String toString() {
        return "DishOrderStatusHistory {\n" +
                "  idDishOrderStatus=" + idDishOrderStatus + ",\n" +
                "  status=" + status + ",\n" +
                "  creationDateTime=" + creationDateTime + "\n" +
                "}";
    }
}

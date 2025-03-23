package org.restau.entity;

import lombok.*;

import java.time.Instant;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class OrderStatusHistory {
    private Long idOrderStatus;
    private StatusOrder status;
    private Instant creationDateTime;

    public OrderStatusHistory(StatusOrder status) {
        this.status = status;
        this.creationDateTime = Instant.now();
    }
    @Override
    public String toString() {
        return "OrderStatusHistory {\n" +
                "  idOrderStatus=" + idOrderStatus + ",\n" +
                "  status=" + status + ",\n" +
                "  creationDateTime=" + creationDateTime + "\n" +
                "}";
    }
}

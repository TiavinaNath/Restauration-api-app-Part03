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
}

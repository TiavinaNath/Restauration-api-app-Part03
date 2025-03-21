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
}

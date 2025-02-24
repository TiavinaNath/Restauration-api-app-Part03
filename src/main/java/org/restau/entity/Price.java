package org.restau.entity;

import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Price {
    private Long idPrice;
    private Ingredient ingredient;
    private LocalDate date;
    private Double unitPrice;
    private Unit unit;
}

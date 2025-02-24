package org.restau.entity;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    public Ingredient(Long idIngredient, Unit unit) {
        this.idIngredient = idIngredient;
        this.name = name;
        this.updateDatetime = LocalDateTime.now();
    }

    public Ingredient(String name, LocalDateTime updateDatetime) {
        this.name = name;
        this.updateDatetime = updateDatetime;
    }

    public Ingredient(String name) {
        this.name = name;
        this.updateDatetime = LocalDateTime.now();
    }
}

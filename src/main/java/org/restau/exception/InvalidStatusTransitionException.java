package org.restau.exception;

import org.restau.entity.StatusDishOrder;

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(StatusDishOrder current, StatusDishOrder next) {
        super("Invalid transition from " + current + " to " + next);
    }
}

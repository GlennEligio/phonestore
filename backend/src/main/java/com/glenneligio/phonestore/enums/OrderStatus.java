package com.glenneligio.phonestore.enums;

import java.util.stream.Stream;

public enum OrderStatus {
    PENDING("PENDING"),
    COMPLETED("COMPLETED");

    private final String type;

    OrderStatus(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static OrderStatus getOrderStatusType(String typeString) {
        return Stream.of(OrderStatus.values())
                .filter(type -> typeString.equals(type.getType()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}

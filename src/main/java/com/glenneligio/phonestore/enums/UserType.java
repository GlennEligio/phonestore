package com.glenneligio.phonestore.enums;

import java.util.stream.Stream;

public enum UserType {
    CUSTOMER("CUSTOMER"),
    ADMIN("ADMIN");

    private final String type;

    UserType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static UserType getAccountType(String typeString) {
        return Stream.of(UserType.values())
                .filter(type -> typeString.equals(type.getType()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}

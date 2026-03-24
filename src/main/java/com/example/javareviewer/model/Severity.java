package com.example.javareviewer.model;

public enum Severity {
    LOW,
    MEDIUM,
    HIGH;

    public int weight() {
        return switch (this) {
            case LOW -> 1;
            case MEDIUM -> 2;
            case HIGH -> 3;
        };
    }
}

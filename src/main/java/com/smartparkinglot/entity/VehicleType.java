package com.smartparkinglot.entity;

public enum VehicleType {
    MOTORCYCLE(0.8),
    CAR(1.0),
    BUS(2.0);

    private final double rateMultiplier;

    VehicleType(double rateMultiplier) {
        this.rateMultiplier = rateMultiplier;
    }

    public double getRateMultiplier() {
        return rateMultiplier;
    }
}

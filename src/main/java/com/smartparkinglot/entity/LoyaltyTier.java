package com.smartparkinglot.entity;

public enum LoyaltyTier {
    SILVER(0.10),
    GOLD(0.20),
    PLATINUM(0.30);

    private final double discountRate;

    LoyaltyTier(double discountRate) {
        this.discountRate = discountRate;
    }

    public double getDiscountRate() {
        return discountRate;
    }
}

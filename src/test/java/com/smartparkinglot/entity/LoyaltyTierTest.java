package com.smartparkinglot.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoyaltyTierTest {

    @Test
    void shouldExposeExpectedDiscountRates() {
        assertEquals(0.10, LoyaltyTier.SILVER.getDiscountRate(), 0.0001);
        assertEquals(0.20, LoyaltyTier.GOLD.getDiscountRate(), 0.0001);
        assertEquals(0.30, LoyaltyTier.PLATINUM.getDiscountRate(), 0.0001);
    }
}

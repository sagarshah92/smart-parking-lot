package com.smartparkinglot.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CustomerTest {

    @Test
    void shouldStoreCustomerProperties() {
        Customer customer = new Customer("C-1", "Alice", LoyaltyTier.GOLD);

        assertEquals("C-1", customer.getCustomerId());
        assertEquals("Alice", customer.getName());
        assertEquals(LoyaltyTier.GOLD, customer.getLoyaltyTier());

        customer.setCustomerId("C-2");
        customer.setName("Bob");
        customer.setLoyaltyTier(LoyaltyTier.SILVER);

        assertEquals("C-2", customer.getCustomerId());
        assertEquals("Bob", customer.getName());
        assertEquals(LoyaltyTier.SILVER, customer.getLoyaltyTier());
    }

    @Test
    void shouldCreateUsefulToString() {
        Customer customer = new Customer("C-3", "Cara", LoyaltyTier.PLATINUM);

        String text = customer.toString();
        assertNotNull(text);
        assertTrue(text.contains("C-3"));
        assertTrue(text.contains("Cara"));
        assertTrue(text.contains("PLATINUM"));
    }
}

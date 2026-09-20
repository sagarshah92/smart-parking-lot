package com.smartparkinglot.entity;

public class Customer {
    private String customerId;
    private String name;
    private LoyaltyTier loyaltyTier;

    public Customer() {
    }

    public Customer(String customerId, String name, LoyaltyTier loyaltyTier) {
        this.customerId = customerId;
        this.name = name;
        this.loyaltyTier = loyaltyTier;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LoyaltyTier getLoyaltyTier() {
        return loyaltyTier;
    }

    public void setLoyaltyTier(LoyaltyTier loyaltyTier) {
        this.loyaltyTier = loyaltyTier;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId='" + customerId + '\'' +
                ", name='" + name + '\'' +
                ", loyaltyTier=" + loyaltyTier +
                '}';
    }
}

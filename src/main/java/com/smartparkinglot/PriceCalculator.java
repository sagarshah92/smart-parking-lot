package com.smartparkinglot;

import com.smartparkinglot.entity.PricingPolicy;
import com.smartparkinglot.entity.Ticket;
import com.smartparkinglot.entity.VehicleType;

import java.util.ArrayList;
import java.util.List;

public class PriceCalculator {
    // simngleton pattern to ensure only one instance of PriceCalculator exists
    private static final PriceCalculator INSTANCE = new PriceCalculator();

    private PriceCalculator() {
    }

    public static PriceCalculator getInstance() {
        return INSTANCE;
    }

    public List<Double> calculateAllPolicyPrices(Ticket ticket, VehicleType vehicleType) {
        List<Double> prices = new ArrayList<>();

        if (ticket == null || vehicleType == null || ticket.getEntryTime() == null || ticket.getExitTime() == null) {
            return prices;
        }

        if (ticket.getExitTime().isBefore(ticket.getEntryTime())) {
            return prices;
        }

        for (PricingPolicy.Type type : PricingPolicy.Type.values()) {
            PricingPolicy policy = new PricingPolicy(type);
            double price;

            if (type == PricingPolicy.Type.STANDARD_HOURLY_RATE) {
                price = policy.calculateStandardHourlyRate(vehicleType.getRateMultiplier(), ticket);
            } else if (type == PricingPolicy.Type.EARLY_BIRD_SPECIAL) {
                price = policy.calculateEarlyBirdPrice(vehicleType.getRateMultiplier(), ticket);
            } else {
                price = policy.calculateNightOwlPrice(vehicleType.getRateMultiplier(), ticket);
            }

            if (price >= 0) {
                prices.add(price);
            }
        }

        return prices;
    }

    public double calculateBestPrice(Ticket ticket, VehicleType vehicleType) {
        List<Double> prices = calculateAllPolicyPrices(ticket, vehicleType);

        if (prices.isEmpty()) {
            return -1.0;
        }

        double minimum = Double.MAX_VALUE;
        for (double price : prices) {
            if (price < minimum) {
                minimum = price;
            }
        }

        return minimum;
    }
}

package com.smartparkinglot;

import com.smartparkinglot.entity.Customer;
import com.smartparkinglot.entity.LoyaltyTier;
import com.smartparkinglot.entity.ParkingSpot;
import com.smartparkinglot.entity.ParkingSpotType;
import com.smartparkinglot.entity.Ticket;
import com.smartparkinglot.entity.Vehicle;
import com.smartparkinglot.entity.VehicleType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PriceCalculatorTest {

    @Test
    void shouldReturnEmptyOrBestPriceForInvalidAndValidTickets() {
        PriceCalculator calculator = PriceCalculator.getInstance();

        Ticket nullTicket = null;
        assertTrue(calculator.calculateAllPolicyPrices(nullTicket, VehicleType.CAR).isEmpty());

        Ticket invalidOrder = new Ticket();
        invalidOrder.setEntryTime(LocalDateTime.of(2026, 9, 20, 12, 0));
        invalidOrder.setExitTime(LocalDateTime.of(2026, 9, 20, 11, 0));
        assertTrue(calculator.calculateAllPolicyPrices(invalidOrder, VehicleType.CAR).isEmpty());

        Ticket earlyBird = new Ticket();
        earlyBird.setCustomer(new Customer("C-1", "Alice", LoyaltyTier.GOLD));
        earlyBird.setVehicle(new Vehicle("CAR-1", VehicleType.CAR));
        earlyBird.setParkingSpot(new ParkingSpot("SPOT-1", "A1", ParkingSpotType.COMPACT));
        earlyBird.setEntryTime(LocalDateTime.of(2026, 9, 20, 8, 0));
        earlyBird.setExitTime(LocalDateTime.of(2026, 9, 20, 17, 0));

        List<Double> prices = calculator.calculateAllPolicyPrices(earlyBird, VehicleType.CAR);
        assertEquals(2, prices.size());
        assertEquals(12.0, calculator.calculateBestPrice(earlyBird, VehicleType.CAR), 0.0001);
        assertEquals(-1.0, calculator.calculateBestPrice(new Ticket(), VehicleType.CAR), 0.0001);
    }
}

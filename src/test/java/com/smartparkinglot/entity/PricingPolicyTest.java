package com.smartparkinglot.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PricingPolicyTest {

    @Test
    void shouldStoreAndExposePolicyConfiguration() {
        PricingPolicy policy = new PricingPolicy();
        policy.setType(PricingPolicy.Type.NIGHT_OWL_SPECIAL);
        policy.setFirstHourRate(6.0);
        policy.setSecondHourRate(4.0);
        policy.setSubsequentHourRate(2.5);
        policy.addPeakHourWindow(9.999, 10.001, 2.0);
        policy.setEarlyBirdFlatFee(20.0);
        policy.setEarlyBirdEntryStartHour(5.5);
        policy.setEarlyBirdEntryEndHour(8.5);
        policy.setEarlyBirdExitStartHour(15.0);
        policy.setEarlyBirdExitEndHour(18.0);
        policy.setNightOwlFlatFee(9.0);
        policy.setNightOwlEntryStartHour(19.0);
        policy.setNightOwlEntryEndHour(23.999);
        policy.setNightOwlExitStartHour(4.0);
        policy.setNightOwlExitEndHour(25.0);

        assertEquals(PricingPolicy.Type.NIGHT_OWL_SPECIAL, policy.getType());
        assertEquals(6.0, policy.getFirstHourRate(), 0.0001);
        assertEquals(4.0, policy.getSecondHourRate(), 0.0001);
        assertEquals(2.5, policy.getSubsequentHourRate(), 0.0001);
        assertEquals(20.0, policy.getEarlyBirdFlatFee(), 0.0001);
        assertEquals(5.5, policy.getEarlyBirdEntryStartHour(), 0.0001);
        assertEquals(8.5, policy.getEarlyBirdEntryEndHour(), 0.0001);
        assertEquals(15.0, policy.getEarlyBirdExitStartHour(), 0.0001);
        assertEquals(18.0, policy.getEarlyBirdExitEndHour(), 0.0001);
        assertEquals(9.0, policy.getNightOwlFlatFee(), 0.0001);
        assertEquals(19.0, policy.getNightOwlEntryStartHour(), 0.0001);
        assertEquals(23.999, policy.getNightOwlEntryEndHour(), 0.0001);
        assertEquals(4.0, policy.getNightOwlExitStartHour(), 0.0001);
        assertEquals(25.0, policy.getNightOwlExitEndHour(), 0.0001);

        List<PeakHourWindow> windows = policy.getPeakHours();
        assertNotNull(windows);
        assertFalse(windows.isEmpty());

        policy.setPeakHours(List.of(new PeakHourWindow(7.0, 10.0, 1.5)));
        assertEquals(1, policy.getPeakHours().size());
    }

    @Test
    void shouldCalculateStandardHourlyRateWithPeakHourSurcharge() {
        PricingPolicy policy = new PricingPolicy(PricingPolicy.Type.STANDARD_HOURLY_RATE);
        Ticket ticket = new Ticket();
        ticket.setVehicle(new Vehicle("STD-1", VehicleType.CAR));
        ticket.setEntryTime(LocalDateTime.of(2026, 9, 20, 7, 30));
        ticket.setExitTime(LocalDateTime.of(2026, 9, 20, 9, 0));

        assertEquals(12.0, policy.calculateStandardHourlyRate(VehicleType.CAR.getRateMultiplier(), ticket), 0.0001);

        Ticket invalidTicket = new Ticket();
        invalidTicket.setEntryTime(LocalDateTime.of(2026, 9, 20, 10, 0));
        invalidTicket.setExitTime(LocalDateTime.of(2026, 9, 20, 9, 30));
        assertEquals(-1.0, policy.calculateStandardHourlyRate(1.0, invalidTicket), 0.0001);
        assertEquals(-1.0, policy.calculateStandardHourlyRate(1.0, null), 0.0001);

        policy.addPeakHourWindow(24.0, 25.0, 1.5);
        Ticket midnightTicket = new Ticket();
        midnightTicket.setEntryTime(LocalDateTime.of(2026, 9, 20, 23, 30));
        midnightTicket.setExitTime(LocalDateTime.of(2026, 9, 21, 0, 30));
        assertDoesNotThrow(() -> policy.calculateStandardHourlyRate(1.0, midnightTicket));
    }

    @Test
    void shouldCalculateEarlyBirdAndNightOwlPricingWithLoyaltyDiscounts() {
        PricingPolicy policy = new PricingPolicy(PricingPolicy.Type.EARLY_BIRD_SPECIAL);
        Customer customer = new Customer("C-1", "Alice", LoyaltyTier.GOLD);

        Ticket earlyBird = new Ticket();
        earlyBird.setCustomer(customer);
        earlyBird.setVehicle(new Vehicle("EB-1", VehicleType.CAR));
        earlyBird.setEntryTime(LocalDateTime.of(2026, 9, 20, 8, 0));
        earlyBird.setExitTime(LocalDateTime.of(2026, 9, 20, 17, 0));
        assertEquals(12.0, policy.calculateEarlyBirdPrice(VehicleType.CAR.getRateMultiplier(), earlyBird), 0.0001);

        Ticket invalidEarlyBird = new Ticket();
        invalidEarlyBird.setCustomer(customer);
        invalidEarlyBird.setVehicle(new Vehicle("EB-2", VehicleType.CAR));
        invalidEarlyBird.setEntryTime(LocalDateTime.of(2026, 9, 20, 10, 0));
        invalidEarlyBird.setExitTime(LocalDateTime.of(2026, 9, 21, 7, 0));
        assertEquals(-1.0, policy.calculateEarlyBirdPrice(1.0, invalidEarlyBird), 0.0001);

        Ticket nightOwl = new Ticket();
        nightOwl.setCustomer(customer);
        nightOwl.setVehicle(new Vehicle("NO-1", VehicleType.CAR));
        nightOwl.setEntryTime(LocalDateTime.of(2026, 9, 20, 20, 0));
        nightOwl.setExitTime(LocalDateTime.of(2026, 9, 21, 6, 0));
        assertEquals(6.4, new PricingPolicy(PricingPolicy.Type.NIGHT_OWL_SPECIAL)
                .calculateNightOwlPrice(VehicleType.CAR.getRateMultiplier(), nightOwl), 0.0001);

        Ticket invalidNightOwl = new Ticket();
        invalidNightOwl.setCustomer(customer);
        invalidNightOwl.setVehicle(new Vehicle("NO-2", VehicleType.CAR));
        invalidNightOwl.setEntryTime(LocalDateTime.of(2026, 9, 20, 18, 0));
        invalidNightOwl.setExitTime(LocalDateTime.of(2026, 9, 20, 22, 0));
        assertEquals(-1.0, new PricingPolicy(PricingPolicy.Type.NIGHT_OWL_SPECIAL)
                .calculateNightOwlPrice(1.0, invalidNightOwl), 0.0001);

        policy.setNightOwlExitEndHour(25.0);
        Ticket midnightExit = new Ticket();
        midnightExit.setCustomer(customer);
        midnightExit.setVehicle(new Vehicle("NO-3", VehicleType.CAR));
        midnightExit.setEntryTime(LocalDateTime.of(2026, 9, 20, 20, 0));
        midnightExit.setExitTime(LocalDateTime.of(2026, 9, 21, 0, 30));
        assertEquals(-1.0, policy.calculateNightOwlPrice(1.0, midnightExit), 0.0001);
    }
}

package com.smartparkinglot.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TicketTest {

    @Test
    void shouldInitializeWithCurrentEntryTimeAndSetters() {
        Ticket ticket = new Ticket();
        assertNotNull(ticket.getEntryTime());

        Customer customer = new Customer("C-1", "Alice", LoyaltyTier.GOLD);
        Vehicle vehicle = new Vehicle("ABC-123", VehicleType.CAR);
        ParkingSpot spot = new ParkingSpot("SPOT-1", "A1", ParkingSpotType.COMPACT);

        ticket.setTicketId("T-1");
        ticket.setCustomer(customer);
        ticket.setVehicle(vehicle);
        ticket.setParkingSpot(spot);
        ticket.setEntryTime(LocalDateTime.of(2026, 9, 20, 8, 0));
        ticket.setExitTime(LocalDateTime.of(2026, 9, 20, 9, 30));

        assertEquals("T-1", ticket.getTicketId());
        assertEquals(customer, ticket.getCustomer());
        assertEquals(vehicle, ticket.getVehicle());
        assertEquals(spot, ticket.getParkingSpot());
        assertEquals(LocalDateTime.of(2026, 9, 20, 8, 0), ticket.getEntryTime());
        assertEquals(LocalDateTime.of(2026, 9, 20, 9, 30), ticket.getExitTime());
    }

    @Test
    void shouldCreateTicketWithConstructorAndRenderReadableString() {
        Customer customer = new Customer("C-2", "Bob", LoyaltyTier.SILVER);
        Vehicle vehicle = new Vehicle("XYZ-888", VehicleType.MOTORCYCLE);
        ParkingSpot spot = new ParkingSpot("SPOT-2", "B2", ParkingSpotType.COMPACT);

        Ticket ticket = new Ticket("T-2", customer, vehicle, spot);
        String text = ticket.toString();

        assertEquals("T-2", ticket.getTicketId());
        assertEquals(customer, ticket.getCustomer());
        assertEquals(vehicle, ticket.getVehicle());
        assertEquals(spot, ticket.getParkingSpot());
        assertNotNull(ticket.getEntryTime());
        assertTrue(text.contains("T-2"));
        assertTrue(text.contains("Alice") || text.contains("Bob"));
    }
}

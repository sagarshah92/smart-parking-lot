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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

class ParkingLotTest {

    @Test
    void shouldParkAndExitVehicleWithExplicitTimes() {
        ParkingLot lot = new ParkingLot(2, 1);
        Vehicle car = new Vehicle("ABC-123", VehicleType.CAR);
        Customer customer = new Customer("C-1", "Alice", LoyaltyTier.GOLD);
        ParkingSpot spot = new ParkingSpot("SPOT-1", "A1", ParkingSpotType.COMPACT);

        Ticket ticket = lot.park(car, customer, spot, LocalDateTime.of(2026, 9, 20, 9, 0));

        assertNotNull(ticket);
        assertEquals(1, lot.occupiedSlots());
        assertEquals(1, lot.availableSlotsByType(ParkingSpotType.COMPACT));
        assertTrue(spot.isOccupied());
        assertEquals(1, lot.occupiedSlotsByType(ParkingSpotType.COMPACT));

        double price = lot.exit(ticket.getTicketId(), LocalDateTime.of(2026, 9, 20, 12, 30));
        assertEquals(14.5, price, 0.0001);
        assertEquals(2, lot.availableSlotsByType(ParkingSpotType.COMPACT));
        assertFalse(spot.isOccupied());
    }

    @Test
    void shouldRejectInvalidParkRequestAndHandleFullLot() {
        ParkingLot lot = new ParkingLot(0, 0);
        Vehicle car = new Vehicle("BAD-123", VehicleType.CAR);
        ParkingSpot spot = new ParkingSpot("SPOT-2", "A2", ParkingSpotType.COMPACT);

        Ticket ticket = lot.park(car, new Customer("C-2", "Bob", LoyaltyTier.SILVER), spot, LocalDateTime.now());
        assertNull(ticket);

        IllegalArgumentException badSpot = assertThrows(
                IllegalArgumentException.class,
                () -> lot.park(
                        new Vehicle("X-1", VehicleType.BUS),
                        new Customer("C-3", "Cara", LoyaltyTier.GOLD),
                        new ParkingSpot("SPOT-3", "B1", ParkingSpotType.COMPACT),
                        LocalDateTime.now()));
        assertTrue(badSpot.getMessage().contains("type"));

        assertEquals(0, lot.availableSlots());
        assertEquals(0, lot.occupiedSlots());
    }

    @Test
    void shouldRejectNullExitTimeAndMissingTicket() {
        ParkingLot lot = new ParkingLot(1, 0);
        Vehicle car = new Vehicle("ABC-777", VehicleType.CAR);
        ParkingSpot spot = new ParkingSpot("SPOT-4", "A4", ParkingSpotType.COMPACT);
        Ticket ticket = lot.park(car, new Customer("C-4", "Dana", LoyaltyTier.PLATINUM), spot, LocalDateTime.of(2026, 9, 20, 8, 0));

        assertNotNull(ticket);
        assertEquals(-1.0, lot.exit("missing-ticket", LocalDateTime.now()), 0.0001);

        IllegalArgumentException nullExit = assertThrows(
                IllegalArgumentException.class,
                () -> lot.exit(ticket.getTicketId(), null));
        assertTrue(nullExit.getMessage().contains("Exit time"));

        assertEquals(0, lot.availableSlotsByType(ParkingSpotType.LARGE));
    }

    @Test
    void shouldCoverConvenienceMethodsAndUnsupportedVehicleType() throws Exception {
        ParkingLot lot = new ParkingLot(2, 1);
        Vehicle car = new Vehicle("COV-1", VehicleType.CAR);
        Customer customer = new Customer("C-5", "Erin", LoyaltyTier.GOLD);
        ParkingSpot compactSpot = new ParkingSpot("SPOT-9", "A9", ParkingSpotType.COMPACT);

        Ticket ticket = lot.park(car, customer, compactSpot);
        assertNotNull(ticket);
        assertEquals(1, lot.availableSlots());
        assertEquals(1, lot.occupiedSlots());
        assertEquals(1, lot.occupiedSlotsByType(ParkingSpotType.COMPACT));
        assertEquals(0, lot.availableSlotsByType(ParkingSpotType.LARGE));

        double leavePrice = lot.leave(ticket.getTicketId());
        assertTrue(leavePrice >= -1.0);

        IllegalArgumentException badVehicleType = assertThrows(
                IllegalArgumentException.class,
                () -> lot.park(new Vehicle("UNSUPPORTED", createUnsupportedVehicleType()),
                        new Customer("C-6", "Frank", LoyaltyTier.SILVER),
                        new ParkingSpot("SPOT-10", "B10", ParkingSpotType.COMPACT),
                        LocalDateTime.of(2026, 9, 21, 9, 0)));
        assertTrue(badVehicleType.getMessage().contains("Unsupported vehicle type"));

        IllegalArgumentException missingVehicle = assertThrows(
                IllegalArgumentException.class,
                () -> lot.park(new Vehicle("", VehicleType.CAR), customer, compactSpot, LocalDateTime.now()));
        assertTrue(missingVehicle.getMessage().contains("plate number"));

        IllegalArgumentException nullSpot = assertThrows(
                IllegalArgumentException.class,
                () -> lot.park(car, customer, null, LocalDateTime.now()));
        assertTrue(nullSpot.getMessage().contains("spot"));

        IllegalArgumentException nullEntry = assertThrows(
                IllegalArgumentException.class,
                () -> lot.park(car, customer, compactSpot, null));
        assertTrue(nullEntry.getMessage().contains("Entry time"));
    }

    private VehicleType createUnsupportedVehicleType() throws Exception {
        Field field = Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        Unsafe unsafe = (Unsafe) field.get(null);
        return (VehicleType) unsafe.allocateInstance(VehicleType.class);
    }
}

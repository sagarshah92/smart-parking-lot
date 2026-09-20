package com.smartparkinglot;

import com.smartparkinglot.entity.Customer;
import com.smartparkinglot.entity.LoyaltyTier;
import com.smartparkinglot.entity.ParkingSpot;
import com.smartparkinglot.entity.ParkingSpotType;
import com.smartparkinglot.entity.Ticket;
import com.smartparkinglot.entity.Vehicle;
import com.smartparkinglot.entity.VehicleType;

import java.time.LocalDateTime;

public class App {
    public static void main(String[] args) {
        ParkingLot parkingLot = new ParkingLot(5, 2);

        Ticket standardTicket = parkingLot.park(
                new Vehicle("STD-100", VehicleType.CAR),
                new Customer("C-1", "Alice", LoyaltyTier.GOLD),
                new ParkingSpot("SPOT-1", "A1", ParkingSpotType.COMPACT),
                LocalDateTime.of(2026, 9, 20, 9, 15));
        System.out.println("Standard scenario price: " + parkingLot.exit(standardTicket.getTicketId(), LocalDateTime.of(2026, 9, 20, 12, 30)));

        Ticket earlyBirdTicket = parkingLot.park(
                new Vehicle("EB-200", VehicleType.CAR),
                new Customer("C-2", "Bob", LoyaltyTier.GOLD),
                new ParkingSpot("SPOT-2", "A2", ParkingSpotType.COMPACT),
                LocalDateTime.of(2026, 9, 20, 8, 0));
        System.out.println("Early bird scenario price: " + parkingLot.exit(earlyBirdTicket.getTicketId(), LocalDateTime.of(2026, 9, 20, 17, 0)));

        Ticket nightOwlTicket = parkingLot.park(
                new Vehicle("NO-300", VehicleType.CAR),
                new Customer("C-3", "Cara", LoyaltyTier.GOLD),
                new ParkingSpot("SPOT-3", "A3", ParkingSpotType.COMPACT),
                LocalDateTime.of(2026, 9, 20, 20, 0));
        System.out.println("Night owl scenario price: " + parkingLot.exit(nightOwlTicket.getTicketId(), LocalDateTime.of(2026, 9, 21, 6, 0)));

        Ticket longStayTicket = parkingLot.park(
                new Vehicle("LONG-400", VehicleType.CAR),
                new Customer("C-4", "David", LoyaltyTier.SILVER),
                new ParkingSpot("SPOT-4", "A4", ParkingSpotType.COMPACT),
                LocalDateTime.of(2026, 9, 20, 20, 0));
        System.out.println("Long stay fallback price: " + parkingLot.exit(longStayTicket.getTicketId(), LocalDateTime.of(2026, 9, 22, 10, 0)));

        Ticket busTicket = parkingLot.park(
                new Vehicle("BUS-500", VehicleType.BUS),
                new Customer("C-5", "Eve", LoyaltyTier.PLATINUM),
                new ParkingSpot("SPOT-5", "B1", ParkingSpotType.LARGE),
                LocalDateTime.of(2026, 9, 20, 14, 0));
        System.out.println("Bus standard price: " + parkingLot.exit(busTicket.getTicketId(), LocalDateTime.of(2026, 9, 20, 18, 0)));

        System.out.println("Current occupied slots: " + parkingLot.occupiedSlots());
        System.out.println("Available compact slots: " + parkingLot.availableSlotsByType(ParkingSpotType.COMPACT));
        System.out.println("Available large slots: " + parkingLot.availableSlotsByType(ParkingSpotType.LARGE));
    }
}

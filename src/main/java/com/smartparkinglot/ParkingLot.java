package com.smartparkinglot;

import com.smartparkinglot.entity.Customer;
import com.smartparkinglot.entity.ParkingSpot;
import com.smartparkinglot.entity.ParkingSpotType;
import com.smartparkinglot.entity.Ticket;
import com.smartparkinglot.entity.Vehicle;
import com.smartparkinglot.entity.VehicleType;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ParkingLot {
    // spotInventory tracks the current remaining capacity for each spot type.
    private final Map<ParkingSpotType, Integer> spotInventory;

    // totalSpots keeps the original configured capacity so we can calculate occupied slots
    // by type without losing the baseline inventory after vehicles enter/exit.
    private final Map<ParkingSpotType, Integer> totalSpots;

    private final Map<String, Ticket> activeTicketsByTicketId = new HashMap<>();

    public ParkingLot(int compactSpots, int largeSpots) {
        if (compactSpots < 0 || largeSpots < 0) {
            throw new IllegalArgumentException("Spot counts cannot be negative.");
        }

        this.totalSpots = new HashMap<>();
        this.totalSpots.put(ParkingSpotType.COMPACT, compactSpots);
        this.totalSpots.put(ParkingSpotType.LARGE, largeSpots);

        this.spotInventory = new HashMap<>();
        this.spotInventory.put(ParkingSpotType.COMPACT, compactSpots);
        this.spotInventory.put(ParkingSpotType.LARGE, largeSpots);
    }

    public Ticket park(Vehicle vehicle, Customer customer, ParkingSpot parkingSpot) {
        return park(vehicle, customer, parkingSpot, LocalDateTime.now());
    }

    public Ticket park(Vehicle vehicle, Customer customer, ParkingSpot parkingSpot, LocalDateTime entryTime) {
        if (vehicle == null || vehicle.getLicensePlate() == null || vehicle.getLicensePlate().isBlank()) {
            throw new IllegalArgumentException("Vehicle plate number cannot be empty.");
        }

        if (parkingSpot == null) {
            throw new IllegalArgumentException("Parking spot cannot be null.");
        }

        if (entryTime == null) {
            throw new IllegalArgumentException("Entry time cannot be null.");
        }

        ParkingSpotType requiredSpotType = getRequiredSpotType(vehicle.getType());
        if (parkingSpot.getType() != requiredSpotType) {
            throw new IllegalArgumentException("Parking spot type does not match the vehicle type.");
        }

        Integer available = spotInventory.get(requiredSpotType);
        if (available == null || available <= 0) {
            return null;
        }

        Ticket ticket = new Ticket();
        ticket.setTicketId("TICKET-" + System.nanoTime());
        ticket.setCustomer(customer);
        ticket.setVehicle(vehicle);
        ticket.setParkingSpot(parkingSpot);
        ticket.setEntryTime(entryTime);

        parkingSpot.setOccupied(true);
        activeTicketsByTicketId.put(ticket.getTicketId(), ticket);
        spotInventory.put(requiredSpotType, available - 1);
        return ticket;
    }

    public double leave(String ticketId) {
        return exit(ticketId, LocalDateTime.now());
    }

    public double exit(String ticketId, LocalDateTime exitTime) {
        Ticket ticket = activeTicketsByTicketId.remove(ticketId);
        if (ticket == null) {
            return -1.0;
        }

        if (exitTime == null) {
            throw new IllegalArgumentException("Exit time cannot be null.");
        }

        Ticket exitingTicket = ticket;
        exitingTicket.setExitTime(exitTime);

        ParkingSpot releasedSpot = exitingTicket.getParkingSpot();
        if (releasedSpot != null) {
            releasedSpot.setOccupied(false);
            ParkingSpotType releasedSpotType = releasedSpot.getType();
            Integer available = spotInventory.get(releasedSpotType);
            if (available != null) {
                spotInventory.put(releasedSpotType, available + 1);
            }
        }

        return PriceCalculator.getInstance().calculateBestPrice(exitingTicket, exitingTicket.getVehicle().getType());
    }

    public int availableSlots() {
        int total = 0;
        for (int count : spotInventory.values()) {
            total += count;
        }
        return total;
    }

    public int occupiedSlots() {
        return activeTicketsByTicketId.size();
    }

    public int availableSlotsByType(ParkingSpotType spotType) {
        return spotInventory.getOrDefault(spotType, 0);
    }

    public int occupiedSlotsByType(ParkingSpotType spotType) {
        int total = totalSpots.getOrDefault(spotType, 0);
        int available = availableSlotsByType(spotType);
        return Math.max(0, total - available);
    }

    // mapping for vehicle types to required parking spot types
    private ParkingSpotType getRequiredSpotType(VehicleType vehicleType) {
        if (vehicleType == null) {
            throw new IllegalArgumentException("Vehicle type cannot be null.");
        }

        switch (vehicleType) {
            case MOTORCYCLE:
            case CAR:
                return ParkingSpotType.COMPACT;
            case BUS:
                return ParkingSpotType.LARGE;
            default:
                throw new IllegalArgumentException("Unsupported vehicle type: " + vehicleType);
        }
    }
}

package com.smartparkinglot.entity;

import java.time.LocalDateTime;

public class Ticket {
    private String ticketId;
    private Customer customer;
    private Vehicle vehicle;
    private ParkingSpot parkingSpot;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;

    public Ticket() {
        this.entryTime = LocalDateTime.now();
    }

    public Ticket(String ticketId, Customer customer, Vehicle vehicle, ParkingSpot parkingSpot) {
        this.ticketId = ticketId;
        this.customer = customer;
        this.vehicle = vehicle;
        this.parkingSpot = parkingSpot;
        this.entryTime = LocalDateTime.now();
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    public void setParkingSpot(ParkingSpot parkingSpot) {
        this.parkingSpot = parkingSpot;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId='" + ticketId + '\'' +
                ", customer=" + customer +
                ", vehicle=" + vehicle +
                ", parkingSpot=" + parkingSpot +
                ", entryTime=" + entryTime +
                ", exitTime=" + exitTime +
                '}';
    }
}

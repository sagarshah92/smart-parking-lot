package com.smartparkinglot.entity;

public class ParkingSpot {
    private String id;
    private String spotNumber;
    private ParkingSpotType type;
    private boolean occupied;

    public ParkingSpot() {
    }

    public ParkingSpot(String id, String spotNumber, ParkingSpotType type) {
        this.id = id;
        this.spotNumber = spotNumber;
        this.type = type;
        this.occupied = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSpotNumber() {
        return spotNumber;
    }

    public void setSpotNumber(String spotNumber) {
        this.spotNumber = spotNumber;
    }

    public ParkingSpotType getType() {
        return type;
    }

    public void setType(ParkingSpotType type) {
        this.type = type;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    @Override
    public String toString() {
        return "ParkingSpot{" +
                "id='" + id + '\'' +
                ", spotNumber='" + spotNumber + '\'' +
                ", type=" + type +
                ", occupied=" + occupied +
                '}';
    }
}


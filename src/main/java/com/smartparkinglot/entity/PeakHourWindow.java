package com.smartparkinglot.entity;

public class PeakHourWindow {
    private double startHour;
    private double endHour;
    private double surcharge;

    public PeakHourWindow() {
    }

    public PeakHourWindow(double startHour, double endHour, double surcharge) {
        this.startHour = startHour;
        this.endHour = endHour;
        this.surcharge = surcharge;
    }

    public double getStartHour() {
        return startHour;
    }

    public void setStartHour(double startHour) {
        this.startHour = startHour;
    }

    public double getEndHour() {
        return endHour;
    }

    public void setEndHour(double endHour) {
        this.endHour = endHour;
    }

    public double getSurcharge() {
        return surcharge;
    }

    public void setSurcharge(double surcharge) {
        this.surcharge = surcharge;
    }
}

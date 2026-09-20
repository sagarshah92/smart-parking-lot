package com.smartparkinglot.entity;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PricingPolicy {
    public enum Type {
        STANDARD_HOURLY_RATE,
        EARLY_BIRD_SPECIAL,
        NIGHT_OWL_SPECIAL
    }

    private Type type;

    private double firstHourRate = 5.0;
    private double secondHourRate = 3.0;
    private double subsequentHourRate = 2.0;

    private final List<PeakHourWindow> peakHours = new ArrayList<>();

    private double earlyBirdFlatFee = 15.0;
    private double earlyBirdEntryStartHour = 6.0;
    private double earlyBirdEntryEndHour = 9.0;
    private double earlyBirdExitStartHour = 15.5;
    private double earlyBirdExitEndHour = 19.0;

    private double nightOwlFlatFee = 8.0;
    private double nightOwlEntryStartHour = 18.0;
    private double nightOwlEntryEndHour = 23.999;
    private double nightOwlExitStartHour = 5.0;
    private double nightOwlExitEndHour = 10.0;

    public PricingPolicy() {
        peakHours.add(new PeakHourWindow(7.0, 10.0, 1.5));
        peakHours.add(new PeakHourWindow(16.0, 19.0, 1.5));
    }

    public PricingPolicy(Type type) {
        this.type = type;
        peakHours.add(new PeakHourWindow(7.0, 10.0, 1.5));
        peakHours.add(new PeakHourWindow(16.0, 19.0, 1.5));
    }

    public double calculateStandardHourlyRate(double multiplier, Ticket ticket) {
        if (ticket == null || ticket.getEntryTime() == null || ticket.getExitTime() == null) {
            return -1.0;
        }

        long totalMinutes = Duration.between(ticket.getEntryTime(), ticket.getExitTime()).toMinutes();
        if (totalMinutes <= 0) {
            return -1.0;
        }

        int billableHours = (int) Math.ceil(totalMinutes / 60.0);
        double total = 0.0;

        for (int hourIndex = 0; hourIndex < billableHours; hourIndex++) {
            LocalDateTime bucketStart = ticket.getEntryTime().plusHours(hourIndex);
            LocalDateTime bucketEnd = bucketStart.plusHours(1);
            double bucketRate = getRateForHourIndex(hourIndex, multiplier);
            boolean overlapsPeak = overlapsPeakHour(bucketStart, bucketEnd);

            total += overlapsPeak ? bucketRate * 1.5 : bucketRate;
        }

        return total;
    }

    private double getRateForHourIndex(int hourIndex, double multiplier) {
        if (hourIndex == 0) {
            return firstHourRate * multiplier;
        }
        if (hourIndex == 1) {
            return secondHourRate * multiplier;
        }
        return subsequentHourRate * multiplier;
    }

    private boolean overlapsPeakHour(LocalDateTime bucketStart, LocalDateTime bucketEnd) {
        LocalDate day = bucketStart.toLocalDate();

        for (PeakHourWindow window : peakHours) {
            LocalDateTime peakStart = toLocalDateTime(day, window.getStartHour());
            LocalDateTime peakEnd = toLocalDateTime(day, window.getEndHour());

            if (bucketStart.isBefore(peakEnd) && bucketEnd.isAfter(peakStart)) {
                return true;
            }
        }

        return false;
    }

    private LocalDateTime toLocalDateTime(LocalDate date, double hourValue) {
        int hour = (int) Math.floor(hourValue);
        int minute = (int) Math.round((hourValue - hour) * 60);

        if (minute == 60) {
            hour += 1;
            minute = 0;
        }

        if (hour >= 24) {
            return date.plusDays(1).atStartOfDay();
        }

        return date.atTime(hour, minute);
    }

    private LocalTime toLocalTime(double hourValue) {
        int hour = (int) Math.floor(hourValue);
        int minute = (int) Math.round((hourValue - hour) * 60);

        if (minute == 60) {
            hour += 1;
            minute = 0;
        }

        if (hour >= 24) {
            return LocalTime.MIDNIGHT;
        }

        return LocalTime.of(hour, minute);
    }

    public double calculateEarlyBirdPrice(double multiplier, Ticket ticket) {
        if (ticket == null || ticket.getEntryTime() == null || ticket.getExitTime() == null) {
            return -1.0;
        }

        if (ticket.getExitTime().isBefore(ticket.getEntryTime())) {
            return -1.0;
        }

        if (!ticket.getEntryTime().toLocalDate().equals(ticket.getExitTime().toLocalDate())) {
            return -1.0;
        }

        LocalTime entryTime = ticket.getEntryTime().toLocalTime();
        LocalTime exitTime = ticket.getExitTime().toLocalTime();

        boolean validEntryWindow = !entryTime.isBefore(toLocalTime(earlyBirdEntryStartHour))
                && entryTime.isBefore(toLocalTime(earlyBirdEntryEndHour));
        boolean validExitWindow = !exitTime.isBefore(toLocalTime(earlyBirdExitStartHour))
                && exitTime.isBefore(toLocalTime(earlyBirdExitEndHour));

        if (validEntryWindow && validExitWindow) {
            return applyLoyaltyDiscount(earlyBirdFlatFee * multiplier, ticket);
        }

        return -1.0;
    }

    public double calculateNightOwlPrice(double multiplier, Ticket ticket) {
        if (ticket == null || ticket.getEntryTime() == null || ticket.getExitTime() == null) {
            return -1.0;
        }

        if (ticket.getExitTime().isBefore(ticket.getEntryTime())) {
            return -1.0;
        }

        LocalTime entryTime = ticket.getEntryTime().toLocalTime();
        LocalTime exitTime = ticket.getExitTime().toLocalTime();

        boolean nextDayExit = ticket.getExitTime().toLocalDate().equals(ticket.getEntryTime().toLocalDate().plusDays(1));
        if (!nextDayExit) {
            return -1.0;
        }

        LocalTime nightOwlEntryEndLimit = LocalTime.of(23, 59, 59);
        boolean validEntryWindow = !entryTime.isBefore(toLocalTime(nightOwlEntryStartHour))
                && !entryTime.isAfter(nightOwlEntryEndLimit);
        boolean validExitWindow = !exitTime.isBefore(toLocalTime(nightOwlExitStartHour))
                && exitTime.isBefore(toLocalTime(nightOwlExitEndHour));

        if (validEntryWindow && validExitWindow) {
            return applyLoyaltyDiscount(nightOwlFlatFee * multiplier, ticket);
        }

        return -1.0;
    }

    private double applyLoyaltyDiscount(double amount, Ticket ticket) {
        if (ticket == null || ticket.getCustomer() == null || ticket.getCustomer().getLoyaltyTier() == null) {
            return amount;
        }

        double discountRate = ticket.getCustomer().getLoyaltyTier().getDiscountRate();
        return amount * (1 - discountRate);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public double getFirstHourRate() {
        return firstHourRate;
    }

    public void setFirstHourRate(double firstHourRate) {
        this.firstHourRate = firstHourRate;
    }

    public double getSecondHourRate() {
        return secondHourRate;
    }

    public void setSecondHourRate(double secondHourRate) {
        this.secondHourRate = secondHourRate;
    }

    public double getSubsequentHourRate() {
        return subsequentHourRate;
    }

    public void setSubsequentHourRate(double subsequentHourRate) {
        this.subsequentHourRate = subsequentHourRate;
    }

    public List<PeakHourWindow> getPeakHours() {
        return peakHours;
    }

    public void addPeakHourWindow(double startHour, double endHour, double surcharge) {
        peakHours.add(new PeakHourWindow(startHour, endHour, surcharge));
    }

    public void setPeakHours(List<PeakHourWindow> peakHours) {
        this.peakHours.clear();
        this.peakHours.addAll(peakHours);
    }

    public double getEarlyBirdFlatFee() {
        return earlyBirdFlatFee;
    }

    public void setEarlyBirdFlatFee(double earlyBirdFlatFee) {
        this.earlyBirdFlatFee = earlyBirdFlatFee;
    }

    public double getEarlyBirdEntryStartHour() {
        return earlyBirdEntryStartHour;
    }

    public void setEarlyBirdEntryStartHour(double earlyBirdEntryStartHour) {
        this.earlyBirdEntryStartHour = earlyBirdEntryStartHour;
    }

    public double getEarlyBirdEntryEndHour() {
        return earlyBirdEntryEndHour;
    }

    public void setEarlyBirdEntryEndHour(double earlyBirdEntryEndHour) {
        this.earlyBirdEntryEndHour = earlyBirdEntryEndHour;
    }

    public double getEarlyBirdExitStartHour() {
        return earlyBirdExitStartHour;
    }

    public void setEarlyBirdExitStartHour(double earlyBirdExitStartHour) {
        this.earlyBirdExitStartHour = earlyBirdExitStartHour;
    }

    public double getEarlyBirdExitEndHour() {
        return earlyBirdExitEndHour;
    }

    public void setEarlyBirdExitEndHour(double earlyBirdExitEndHour) {
        this.earlyBirdExitEndHour = earlyBirdExitEndHour;
    }

    public double getNightOwlFlatFee() {
        return nightOwlFlatFee;
    }

    public void setNightOwlFlatFee(double nightOwlFlatFee) {
        this.nightOwlFlatFee = nightOwlFlatFee;
    }

    public double getNightOwlEntryStartHour() {
        return nightOwlEntryStartHour;
    }

    public void setNightOwlEntryStartHour(double nightOwlEntryStartHour) {
        this.nightOwlEntryStartHour = nightOwlEntryStartHour;
    }

    public double getNightOwlEntryEndHour() {
        return nightOwlEntryEndHour;
    }

    public void setNightOwlEntryEndHour(double nightOwlEntryEndHour) {
        this.nightOwlEntryEndHour = nightOwlEntryEndHour;
    }

    public double getNightOwlExitStartHour() {
        return nightOwlExitStartHour;
    }

    public void setNightOwlExitStartHour(double nightOwlExitStartHour) {
        this.nightOwlExitStartHour = nightOwlExitStartHour;
    }

    public double getNightOwlExitEndHour() {
        return nightOwlExitEndHour;
    }

    public void setNightOwlExitEndHour(double nightOwlExitEndHour) {
        this.nightOwlExitEndHour = nightOwlExitEndHour;
    }
}

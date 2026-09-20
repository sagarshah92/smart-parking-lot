package com.smartparkinglot.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PeakHourWindowTest {

    @Test
    void shouldStoreWindowProperties() {
        PeakHourWindow window = new PeakHourWindow(7.0, 10.0, 1.5);

        assertEquals(7.0, window.getStartHour(), 0.0001);
        assertEquals(10.0, window.getEndHour(), 0.0001);
        assertEquals(1.5, window.getSurcharge(), 0.0001);

        window.setStartHour(16.0);
        window.setEndHour(19.0);
        window.setSurcharge(2.0);

        assertEquals(16.0, window.getStartHour(), 0.0001);
        assertEquals(19.0, window.getEndHour(), 0.0001);
        assertEquals(2.0, window.getSurcharge(), 0.0001);
    }
}

package com.smartparkinglot.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParkingSpotTest {

    @Test
    void shouldInitializeSpotDefaults() {
        ParkingSpot spot = new ParkingSpot("S-1", "A1", ParkingSpotType.COMPACT);

        assertEquals("S-1", spot.getId());
        assertEquals("A1", spot.getSpotNumber());
        assertEquals(ParkingSpotType.COMPACT, spot.getType());
        assertFalse(spot.isOccupied());
    }

    @Test
    void shouldAllowOccupancyUpdates() {
        ParkingSpot spot = new ParkingSpot();
        spot.setId("S-2");
        spot.setSpotNumber("B4");
        spot.setType(ParkingSpotType.LARGE);
        spot.setOccupied(true);

        assertEquals("S-2", spot.getId());
        assertEquals("B4", spot.getSpotNumber());
        assertEquals(ParkingSpotType.LARGE, spot.getType());
        assertTrue(spot.isOccupied());
    }

    @Test
    void shouldRenderToString() {
        ParkingSpot spot = new ParkingSpot("S-3", "C7", ParkingSpotType.COMPACT);
        spot.setOccupied(true);

        String text = spot.toString();
        assertNotNull(text);
        assertTrue(text.contains("S-3"));
        assertTrue(text.contains("C7"));
        assertTrue(text.contains("COMPACT"));
        assertTrue(text.contains("occupied=true"));
    }
}

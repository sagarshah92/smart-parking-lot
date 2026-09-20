package com.smartparkinglot.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VehicleTest {

    @Test
    void shouldStoreVehicleProperties() {
        Vehicle vehicle = new Vehicle("ABC-123", VehicleType.CAR);

        assertEquals("ABC-123", vehicle.getLicensePlate());
        assertEquals(VehicleType.CAR, vehicle.getType());

        vehicle.setLicensePlate("XYZ-999");
        vehicle.setType(VehicleType.BUS);

        assertEquals("XYZ-999", vehicle.getLicensePlate());
        assertEquals(VehicleType.BUS, vehicle.getType());
    }

    @Test
    void shouldRenderToString() {
        Vehicle vehicle = new Vehicle("BUS-42", VehicleType.BUS);

        String text = vehicle.toString();
        assertNotNull(text);
        assertTrue(text.contains("BUS-42"));
        assertTrue(text.contains("BUS"));
    }

    @Test
    void shouldSupportDefaultConstructor() {
        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate("MOT-7");
        vehicle.setType(VehicleType.MOTORCYCLE);

        assertEquals("MOT-7", vehicle.getLicensePlate());
        assertEquals(VehicleType.MOTORCYCLE, vehicle.getType());
    }
}

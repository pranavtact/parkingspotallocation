package com.airtribe.entity;

/**
 * Represents a bus vehicle.
 */
public class Bus extends Vehicle {
    
    public Bus(String licensePlate) {
        super(licensePlate, VehicleType.BUS);
    }
    
    @Override
    public SpotSize getRequiredSpotSize() {
        return SpotSize.LARGE;
    }
}

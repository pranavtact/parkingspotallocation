package com.airtribe.entity;

/**
 * Represents a motorcycle vehicle.
 */
public class Motorcycle extends Vehicle {
    
    public Motorcycle(String licensePlate) {
        super(licensePlate, VehicleType.MOTORCYCLE);
    }
    
    @Override
    public SpotSize getRequiredSpotSize() {
        return SpotSize.SMALL;
    }
}

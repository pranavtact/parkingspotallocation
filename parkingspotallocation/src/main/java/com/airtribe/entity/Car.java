package com.airtribe.entity;

/**
 * Represents a car vehicle.
 */
public class Car extends Vehicle {
    
    public Car(String licensePlate) {
        super(licensePlate, VehicleType.CAR);
    }
    
    @Override
    public SpotSize getRequiredSpotSize() {
        return SpotSize.MEDIUM;
    }
}

package com.airtribe.entity;

/**
 * Abstract base class representing a vehicle.
 * Follows the Open/Closed Principle - open for extension, closed for modification.
 */
public abstract class Vehicle {
    private String licensePlate;
    private VehicleType type;
    
    public Vehicle(String licensePlate, VehicleType type) {
        this.licensePlate = licensePlate;
        this.type = type;
    }
    
    public String getLicensePlate() {
        return licensePlate;
    }
    
    public VehicleType getType() {
        return type;
    }
    
    /**
     * Returns the required spot size for this vehicle.
     * This is implemented by subclasses to define their specific requirements.
     */
    public abstract SpotSize getRequiredSpotSize();
    
    @Override
    public String toString() {
        return type + " [" + licensePlate + "]";
    }
}

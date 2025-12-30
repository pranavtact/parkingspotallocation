package com.airtribe.entity;

/**
 * Represents a parking spot in the parking lot.
 * Thread-safe for concurrent access.
 */
public class ParkingSpot {
    private final String spotId;
    private final SpotSize size;
    private final int floorNumber;
    private ParkingSpotStatus status;
    private Vehicle parkedVehicle;
    
    public ParkingSpot(String spotId, SpotSize size, int floorNumber) {
        this.spotId = spotId;
        this.size = size;
        this.floorNumber = floorNumber;
        this.status = ParkingSpotStatus.AVAILABLE;
        this.parkedVehicle = null;
    }
    
    public synchronized boolean isAvailable() {
        return status == ParkingSpotStatus.AVAILABLE;
    }
    
    public synchronized boolean canFitVehicle(Vehicle vehicle) {
        // A spot can fit a vehicle if it's available and the size matches or is larger
        if (!isAvailable()) {
            return false;
        }
        
        SpotSize requiredSize = vehicle.getRequiredSpotSize();
        
        // Check if spot size can accommodate the vehicle
        switch (this.size) {
            case LARGE:
                return true; // Large spots can fit any vehicle
            case MEDIUM:
                return requiredSize == SpotSize.SMALL || requiredSize == SpotSize.MEDIUM;
            case SMALL:
                return requiredSize == SpotSize.SMALL;
            default:
                return false;
        }
    }
    
    public synchronized boolean parkVehicle(Vehicle vehicle) {
        if (canFitVehicle(vehicle)) {
            this.parkedVehicle = vehicle;
            this.status = ParkingSpotStatus.OCCUPIED;
            return true;
        }
        return false;
    }
    
    public synchronized Vehicle removeVehicle() {
        Vehicle vehicle = this.parkedVehicle;
        this.parkedVehicle = null;
        this.status = ParkingSpotStatus.AVAILABLE;
        return vehicle;
    }
    
    public String getSpotId() {
        return spotId;
    }
    
    public SpotSize getSize() {
        return size;
    }
    
    public int getFloorNumber() {
        return floorNumber;
    }
    
    public synchronized ParkingSpotStatus getStatus() {
        return status;
    }
    
    public synchronized Vehicle getParkedVehicle() {
        return parkedVehicle;
    }
    
    @Override
    public String toString() {
        return "Spot[" + spotId + ", Floor:" + floorNumber + ", Size:" + size + ", Status:" + status + "]";
    }
}

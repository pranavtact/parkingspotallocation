package com.airtribe.entity;

import com.airtribe.strategy.SpotFindingStrategy;

import java.util.*;

/**
 * Represents a floor in the parking lot containing multiple parking spots.
 * Follows Single Responsibility Principle - manages spots on a single floor.
 * Uses Strategy Pattern for flexible spot-finding algorithms.
 */
public class ParkingFloor {
    private final int floorNumber;
    private final List<ParkingSpot> spots;
    private SpotFindingStrategy spotFindingStrategy;
    
    public ParkingFloor(int floorNumber, SpotFindingStrategy spotFindingStrategy) {
        this.floorNumber = floorNumber;
        this.spots = new ArrayList<>();
        this.spotFindingStrategy = spotFindingStrategy;
    }
    
    public void addSpot(ParkingSpot spot) {
        spots.add(spot);
    }
    
    /**
     * Finds an available spot for the given vehicle using the configured strategy.
     * Thread-safe method that delegates to the strategy implementation.
     */
    public synchronized ParkingSpot findAvailableSpot(Vehicle vehicle) {
        return spotFindingStrategy.findSpot(spots, vehicle);
    }
    
    /**
     * Allows changing the spot-finding strategy at runtime.
     * Thread-safe setter for the strategy.
     */
    public synchronized void setSpotFindingStrategy(SpotFindingStrategy spotFindingStrategy) {
        this.spotFindingStrategy = spotFindingStrategy;
    }
    
    /**
     * Gets the current spot-finding strategy.
     */
    public synchronized SpotFindingStrategy getSpotFindingStrategy() {
        return this.spotFindingStrategy;
    }
    
    public int getFloorNumber() {
        return floorNumber;
    }
    
    public List<ParkingSpot> getSpots() {
        return new ArrayList<>(spots);
    }
    
    public long getAvailableSpotCount() {
        return spots.stream().filter(ParkingSpot::isAvailable).count();
    }
    
    public long getAvailableSpotCountBySize(SpotSize size) {
        return spots.stream()
                .filter(spot -> spot.isAvailable() && spot.getSize() == size)
                .count();
    }
    
    @Override
    public String toString() {
        return "Floor " + floorNumber + " [Available: " + getAvailableSpotCount() + "/" + spots.size() + "]";
    }
}

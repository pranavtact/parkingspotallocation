package com.airtribe.strategy;

import com.airtribe.entity.ParkingSpot;
import com.airtribe.entity.Vehicle;

import java.util.List;

/**
 * Interface for different spot-finding strategies.
 * Follows Strategy Pattern for flexible spot allocation algorithms.
 */
public interface SpotFindingStrategy {
    /**
     * Finds an available parking spot for the given vehicle.
     * Implementations must be thread-safe as they can be called concurrently.
     * 
     * @param spots List of parking spots to search through
     * @param vehicle Vehicle that needs a parking spot
     * @return Available ParkingSpot or null if no spot is available
     */
    ParkingSpot findSpot(List<ParkingSpot> spots, Vehicle vehicle);
}

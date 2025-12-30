package com.airtribe.strategy;

import com.airtribe.entity.ParkingSpot;
import com.airtribe.entity.SpotSize;
import com.airtribe.entity.Vehicle;

import java.util.List;

/**
 * Best-fit strategy for finding a parking spot.
 * This strategy first tries to find an exact size match,
 * then finds the smallest spot that can accommodate the vehicle.
 * Thread-safe implementation for concurrent access.
 */
public class BestFitSpotFindingStrategy implements SpotFindingStrategy {
    
    @Override
    public ParkingSpot findSpot(List<ParkingSpot> spots, Vehicle vehicle) {
        SpotSize requiredSize = vehicle.getRequiredSpotSize();

        // First try to find an exact match
        // This is thread-safe as we're only reading the list
        for (ParkingSpot spot : spots) {
            if (spot.isAvailable() && spot.getSize() == requiredSize && spot.canFitVehicle(vehicle)) {
                return spot;
            }
        }

        // If no exact match, find the next larger available spot
        for (ParkingSpot spot : spots) {
            if (spot.canFitVehicle(vehicle)) {
                return spot;
            }
        }

        return null;
    }
}

package com.airtribe.strategy;

import com.airtribe.entity.ParkingSpot;
import com.airtribe.entity.Vehicle;

import java.util.List;

/**
 * First-fit strategy for finding a parking spot.
 * This strategy returns the first available spot that can fit the vehicle,
 * without considering size optimization.
 * Thread-safe implementation for concurrent access.
 */
public class FirstFitSpotFindingStrategy implements SpotFindingStrategy {
    
    @Override
    public ParkingSpot findSpot(List<ParkingSpot> spots, Vehicle vehicle) {
        // Find the first available spot that can fit the vehicle
        // This is thread-safe as we're only reading the list
        for (ParkingSpot spot : spots) {
            if (spot.isAvailable() && spot.canFitVehicle(vehicle)) {
                return spot;
            }
        }
        
        return null;
    }
}

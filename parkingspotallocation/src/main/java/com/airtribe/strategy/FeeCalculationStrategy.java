package com.airtribe.strategy;

import com.airtribe.entity.ParkingTicket;

/**
 * Interface for fee calculation strategy.
 * Follows Strategy Pattern and Open/Closed Principle.
 */
public interface FeeCalculationStrategy {
    double calculateFee(ParkingTicket ticket);
}

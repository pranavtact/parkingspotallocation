package com.airtribe.service;

import com.airtribe.entity.ParkingTicket;
import com.airtribe.strategy.FeeCalculationStrategy;

/**
 * Fee calculator that uses a pluggable strategy for calculation.
 * Follows Strategy Pattern and Dependency Inversion Principle.
 */
public class FeeCalculator {
    private FeeCalculationStrategy strategy;
    
    public FeeCalculator(FeeCalculationStrategy strategy) {
        this.strategy = strategy;
    }
    
    public void setStrategy(FeeCalculationStrategy strategy) {
        this.strategy = strategy;
    }
    
    public double calculateFee(ParkingTicket ticket) {
        return strategy.calculateFee(ticket);
    }
}

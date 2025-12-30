package com.airtribe.strategy;

import com.airtribe.entity.ParkingTicket;
import com.airtribe.entity.VehicleType;

/**
 * Hourly-based fee calculation strategy.
 * Different rates for different vehicle types.
 */
public class HourlyFeeStrategy implements FeeCalculationStrategy {
    private static final double MOTORCYCLE_RATE_PER_HOUR = 10.0;
    private static final double CAR_RATE_PER_HOUR = 20.0;
    private static final double BUS_RATE_PER_HOUR = 40.0;
    private static final double BASE_FEE = 5.0;
    
    @Override
    public double calculateFee(ParkingTicket ticket) {
        long hours = ticket.getParkingDurationInHours();
        double hourlyRate = getHourlyRate(ticket.getVehicle().getType());
        return BASE_FEE + (hours * hourlyRate);
    }
    
    private double getHourlyRate(VehicleType type) {
        switch (type) {
            case MOTORCYCLE:
                return MOTORCYCLE_RATE_PER_HOUR;
            case CAR:
                return CAR_RATE_PER_HOUR;
            case BUS:
                return BUS_RATE_PER_HOUR;
            default:
                return CAR_RATE_PER_HOUR;
        }
    }
}

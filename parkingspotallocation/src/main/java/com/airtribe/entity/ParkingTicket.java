package com.airtribe.entity;

import java.time.LocalDateTime;
import java.time.Duration;

/**
 * Represents a parking ticket issued when a vehicle enters the parking lot.
 * Contains all information about the parking transaction.
 */
public class ParkingTicket {
    private static long ticketCounter = 0;
    
    private final String ticketId;
    private final Vehicle vehicle;
    private final ParkingSpot assignedSpot;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private double fee;
    private boolean isPaid;
    
    public ParkingTicket(Vehicle vehicle, ParkingSpot assignedSpot) {
        this.ticketId = generateTicketId();
        this.vehicle = vehicle;
        this.assignedSpot = assignedSpot;
        this.entryTime = LocalDateTime.now();
        this.exitTime = null;
        this.fee = 0.0;
        this.isPaid = false;
    }
    
    private static synchronized String generateTicketId() {
        return "TKT-" + String.format("%06d", ++ticketCounter);
    }
    
    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }
    
    public void setFee(double fee) {
        this.fee = fee;
    }
    
    public void markAsPaid() {
        this.isPaid = true;
    }
    
    public String getTicketId() {
        return ticketId;
    }
    
    public Vehicle getVehicle() {
        return vehicle;
    }
    
    public ParkingSpot getAssignedSpot() {
        return assignedSpot;
    }
    
    public LocalDateTime getEntryTime() {
        return entryTime;
    }
    
    public LocalDateTime getExitTime() {
        return exitTime;
    }
    
    public double getFee() {
        return fee;
    }
    
    public boolean isPaid() {
        return isPaid;
    }
    
    /**
     * Calculates the duration of parking in hours.
     */
    public long getParkingDurationInHours() {
        if (exitTime == null) {
            return 0;
        }
        Duration duration = Duration.between(entryTime, exitTime);
        long hours = duration.toHours();
        // Round up to at least 1 hour
        return hours == 0 ? 1 : hours;
    }
    
    /**
     * Calculates the duration of parking in minutes (for demonstration).
     */
    public long getParkingDurationInMinutes() {
        if (exitTime == null) {
            return 0;
        }
        Duration duration = Duration.between(entryTime, exitTime);
        return duration.toMinutes();
    }
    
    @Override
    public String toString() {
        return "Ticket[" + ticketId + ", " + vehicle + ", Spot:" + assignedSpot.getSpotId() + 
               ", Entry:" + entryTime + ", Fee:" + fee + "]";
    }
}

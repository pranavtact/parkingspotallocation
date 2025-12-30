package com.airtribe.service;

import com.airtribe.entity.*;
import com.airtribe.strategy.BestFitSpotFindingStrategy;
import com.airtribe.strategy.HourlyFeeStrategy;
import com.airtribe.strategy.SpotFindingStrategy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Main parking lot management class.
 * Implements Singleton Pattern for single instance management.
 * Thread-safe implementation for concurrent vehicle entry/exit.
 */
public class ParkingLot {
    private static ParkingLot instance;
    private static final Object lock = new Object();
    
    private final String name;
    private final List<ParkingFloor> floors;
    private final Map<String, ParkingTicket> activeTickets; // ticketId -> ParkingTicket
    private final FeeCalculator feeCalculator;
    private final SpotFindingStrategy defaultSpotFindingStrategy;
    
    private ParkingLot(String name, int numberOfFloors) {
        this.name = name;
        this.floors = new ArrayList<>();
        this.activeTickets = new ConcurrentHashMap<>();
        this.feeCalculator = new FeeCalculator(new HourlyFeeStrategy());
        this.defaultSpotFindingStrategy = new BestFitSpotFindingStrategy();
        
        // Initialize floors with default strategy
        for (int i = 1; i <= numberOfFloors; i++) {
            floors.add(new ParkingFloor(i, defaultSpotFindingStrategy));
        }
    }
    
    /**
     * Gets the singleton instance of ParkingLot.
     * Thread-safe double-checked locking.
     */
    public static ParkingLot getInstance(String name, int numberOfFloors) {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ParkingLot(name, numberOfFloors);
                }
            }
        }
        return instance;
    }
    
    public static ParkingLot getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ParkingLot not initialized. Call getInstance(name, floors) first.");
        }
        return instance;
    }
    
    /**
     * Adds a parking spot to a specific floor.
     */
    public void addParkingSpot(int floorNumber, ParkingSpot spot) {
        if (floorNumber < 1 || floorNumber > floors.size()) {
            throw new IllegalArgumentException("Invalid floor number: " + floorNumber);
        }
        floors.get(floorNumber - 1).addSpot(spot);
    }
    
    /**
     * Parks a vehicle in the parking lot.
     * Automatically finds and assigns an available spot.
     * Thread-safe for concurrent vehicle entries.
     */
    public synchronized ParkingTicket parkVehicle(Vehicle vehicle) {
        // Find an available spot across all floors
        ParkingSpot availableSpot = findAvailableSpot(vehicle);
        
        if (availableSpot == null) {
            System.out.println("No available spot for " + vehicle);
            return null;
        }
        
        // Park the vehicle in the spot
        boolean parked = availableSpot.parkVehicle(vehicle);
        
        if (!parked) {
            System.out.println("Failed to park " + vehicle + " in spot " + availableSpot.getSpotId());
            return null;
        }
        
        // Create and store parking ticket
        ParkingTicket ticket = new ParkingTicket(vehicle, availableSpot);
        activeTickets.put(ticket.getTicketId(), ticket);
        
        System.out.println("✓ Vehicle parked successfully!");
        System.out.println("  Ticket: " + ticket.getTicketId());
        System.out.println("  Vehicle: " + vehicle);
        System.out.println("  Spot: " + availableSpot.getSpotId() + " (Floor " + availableSpot.getFloorNumber() + ")");
        System.out.println("  Entry Time: " + ticket.getEntryTime());
        
        return ticket;
    }
    
    /**
     * Processes vehicle exit and calculates parking fee.
     * Thread-safe for concurrent vehicle exits.
     */
    public synchronized double exitVehicle(String ticketId) {
        ParkingTicket ticket = activeTickets.get(ticketId);
        
        if (ticket == null) {
            System.out.println("Invalid ticket ID: " + ticketId);
            return -1;
        }
        
        // Set exit time
        ticket.setExitTime(LocalDateTime.now());
        
        // Calculate fee
        double fee = feeCalculator.calculateFee(ticket);
        ticket.setFee(fee);
        ticket.markAsPaid();
        
        // Remove vehicle from spot
        ParkingSpot spot = ticket.getAssignedSpot();
        spot.removeVehicle();
        
        // Remove ticket from active tickets
        activeTickets.remove(ticketId);
        
        System.out.println("\n✓ Vehicle exit processed!");
        System.out.println("  Ticket: " + ticketId);
        System.out.println("  Vehicle: " + ticket.getVehicle());
        System.out.println("  Entry Time: " + ticket.getEntryTime());
        System.out.println("  Exit Time: " + ticket.getExitTime());
        System.out.println("  Duration: " + ticket.getParkingDurationInHours() + " hours");
        System.out.println("  Parking Fee: $" + String.format("%.2f", fee));
        
        return fee;
    }
    
    /**
     * Finds an available parking spot for the given vehicle.
     * Uses best-fit algorithm across all floors.
     */
    private ParkingSpot findAvailableSpot(Vehicle vehicle) {
        // Try to find spot on each floor
        for (ParkingFloor floor : floors) {
            ParkingSpot spot = floor.findAvailableSpot(vehicle);
            if (spot != null) {
                return spot;
            }
        }
        return null;
    }
    
    /**
     * Displays real-time availability of parking spots.
     */
    public void displayAvailability() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Parking Lot: " + name);
        System.out.println("=".repeat(50));
        
        for (ParkingFloor floor : floors) {
            System.out.println(floor);
            System.out.println("  Small spots: " + floor.getAvailableSpotCountBySize(SpotSize.SMALL));
            System.out.println("  Medium spots: " + floor.getAvailableSpotCountBySize(SpotSize.MEDIUM));
            System.out.println("  Large spots: " + floor.getAvailableSpotCountBySize(SpotSize.LARGE));
        }
        
        System.out.println("\nActive Vehicles: " + activeTickets.size());
        System.out.println("=".repeat(50));
    }
    
    /**
     * Gets total available spots across all floors.
     */
    public long getTotalAvailableSpots() {
        return floors.stream()
                .mapToLong(ParkingFloor::getAvailableSpotCount)
                .sum();
    }
    
    /**
     * Gets total spots across all floors.
     */
    public long getTotalSpots() {
        return floors.stream()
                .mapToLong(floor -> floor.getSpots().size())
                .sum();
    }
    
    public String getName() {
        return name;
    }
    
    public List<ParkingFloor> getFloors() {
        return new ArrayList<>(floors);
    }
    
    public Map<String, ParkingTicket> getActiveTickets() {
        return new ConcurrentHashMap<>(activeTickets);
    }
}

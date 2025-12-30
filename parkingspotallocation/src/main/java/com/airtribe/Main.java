package com.airtribe;

import com.airtribe.entity.*;
import com.airtribe.service.ParkingLot;

/**
 * Main class demonstrating the Smart Parking Lot System.
 * Showcases all functional requirements:
 * - Parking spot allocation
 * - Check-in and check-out
 * - Fee calculation
 * - Real-time availability updates
 * - Concurrent vehicle handling
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════╗");
        System.out.println("║      SMART PARKING LOT MANAGEMENT SYSTEM        ║");
        System.out.println("╚══════════════════════════════════════════════════╝\n");
        
        // Initialize parking lot with 3 floors
        ParkingLot parkingLot = ParkingLot.getInstance("Downtown Parking", 3);
        
        // Setup parking spots on each floor
        setupParkingSpots(parkingLot);
        
        // Display initial availability
        System.out.println("\n>>> INITIAL PARKING LOT STATUS");
        parkingLot.displayAvailability();
        
        // Demonstrate parking operations
        demonstrateParking(parkingLot);
        
        // Demonstrate concurrent operations
        demonstrateConcurrentParking(parkingLot);
        
        // Display final status
        System.out.println("\n>>> FINAL PARKING LOT STATUS");
        parkingLot.displayAvailability();
    }
    
    /**
     * Sets up parking spots across multiple floors with different sizes.
     */
    private static void setupParkingSpots(ParkingLot parkingLot) {
        System.out.println(">>> INITIALIZING PARKING LOT");
        System.out.println("Setting up parking spots...\n");
        
        // Floor 1: Mix of all sizes
        for (int i = 1; i <= 5; i++) {
            parkingLot.addParkingSpot(1, new ParkingSpot("F1-S" + i, SpotSize.SMALL, 1));
        }
        for (int i = 1; i <= 8; i++) {
            parkingLot.addParkingSpot(1, new ParkingSpot("F1-M" + i, SpotSize.MEDIUM, 1));
        }
        for (int i = 1; i <= 3; i++) {
            parkingLot.addParkingSpot(1, new ParkingSpot("F1-L" + i, SpotSize.LARGE, 1));
        }
        
        // Floor 2: Mostly medium spots
        for (int i = 1; i <= 3; i++) {
            parkingLot.addParkingSpot(2, new ParkingSpot("F2-S" + i, SpotSize.SMALL, 2));
        }
        for (int i = 1; i <= 10; i++) {
            parkingLot.addParkingSpot(2, new ParkingSpot("F2-M" + i, SpotSize.MEDIUM, 2));
        }
        for (int i = 1; i <= 2; i++) {
            parkingLot.addParkingSpot(2, new ParkingSpot("F2-L" + i, SpotSize.LARGE, 2));
        }
        
        // Floor 3: Mix of all sizes
        for (int i = 1; i <= 4; i++) {
            parkingLot.addParkingSpot(3, new ParkingSpot("F3-S" + i, SpotSize.SMALL, 3));
        }
        for (int i = 1; i <= 6; i++) {
            parkingLot.addParkingSpot(3, new ParkingSpot("F3-M" + i, SpotSize.MEDIUM, 3));
        }
        for (int i = 1; i <= 4; i++) {
            parkingLot.addParkingSpot(3, new ParkingSpot("F3-L" + i, SpotSize.LARGE, 3));
        }
        
        System.out.println("✓ Parking lot setup complete!");
        System.out.println("  Total spots: " + parkingLot.getTotalSpots());
    }
    
    /**
     * Demonstrates basic parking operations.
     */
    private static void demonstrateParking(ParkingLot parkingLot) {
        System.out.println("\n\n>>> DEMONSTRATION: VEHICLE PARKING OPERATIONS");
        System.out.println("=".repeat(50));
        
        // Create vehicles
        Vehicle motorcycle1 = new Motorcycle("MH-01-1234");
        Vehicle car1 = new Car("KA-05-5678");
        Vehicle car2 = new Car("DL-03-9999");
        Vehicle bus1 = new Bus("MH-12-BUS1");
        Vehicle motorcycle2 = new Motorcycle("GJ-01-4567");
        
        // Park vehicles
        System.out.println("\n1. Parking Motorcycle 1:");
        ParkingTicket ticket1 = parkingLot.parkVehicle(motorcycle1);
        
        System.out.println("\n2. Parking Car 1:");
        ParkingTicket ticket2 = parkingLot.parkVehicle(car1);
        
        System.out.println("\n3. Parking Car 2:");
        ParkingTicket ticket3 = parkingLot.parkVehicle(car2);
        
        System.out.println("\n4. Parking Bus 1:");
        ParkingTicket ticket4 = parkingLot.parkVehicle(bus1);
        
        System.out.println("\n5. Parking Motorcycle 2:");
        ParkingTicket ticket5 = parkingLot.parkVehicle(motorcycle2);
        
        // Display current status
        System.out.println("\n>>> PARKING STATUS AFTER CHECK-INS");
        parkingLot.displayAvailability();
        
        // Simulate some time passing and then exit some vehicles
        System.out.println("\n\n>>> DEMONSTRATION: VEHICLE EXIT & FEE CALCULATION");
        System.out.println("=".repeat(50));
        
        try {
            // Simulate parking duration
            Thread.sleep(100); // Small delay for demonstration
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Exit vehicles
        if (ticket1 != null) {
            System.out.println("\n1. Processing exit for Motorcycle 1:");
            parkingLot.exitVehicle(ticket1.getTicketId());
        }
        
        if (ticket2 != null) {
            System.out.println("\n2. Processing exit for Car 1:");
            parkingLot.exitVehicle(ticket2.getTicketId());
        }
        
        // Display updated availability
        System.out.println("\n>>> PARKING STATUS AFTER SOME EXITS");
        parkingLot.displayAvailability();
    }
    
    /**
     * Demonstrates concurrent parking operations using threads.
     */
    private static void demonstrateConcurrentParking(ParkingLot parkingLot) {
        System.out.println("\n\n>>> DEMONSTRATION: CONCURRENT VEHICLE OPERATIONS");
        System.out.println("=".repeat(50));
        System.out.println("Simulating multiple vehicles arriving simultaneously...\n");
        
        // Create multiple threads for concurrent parking
        Thread[] threads = new Thread[5];
        
        threads[0] = new Thread(() -> {
            Vehicle car = new Car("KA-01-CONC1");
            parkingLot.parkVehicle(car);
        });
        
        threads[1] = new Thread(() -> {
            Vehicle motorcycle = new Motorcycle("MH-02-CONC2");
            parkingLot.parkVehicle(motorcycle);
        });
        
        threads[2] = new Thread(() -> {
            Vehicle car = new Car("DL-07-CONC3");
            parkingLot.parkVehicle(car);
        });
        
        threads[3] = new Thread(() -> {
            Vehicle bus = new Bus("KA-15-BUS2");
            parkingLot.parkVehicle(bus);
        });
        
        threads[4] = new Thread(() -> {
            Vehicle motorcycle = new Motorcycle("GJ-03-CONC5");
            parkingLot.parkVehicle(motorcycle);
        });
        
        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        System.out.println("\n✓ All concurrent operations completed successfully!");
    }
}
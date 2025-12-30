# Smart Parking Lot Management System

Backend system for managing a smart parking lot with multiple floors, automatic spot allocation, and dynamic fee calculation.

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [System Design](#system-design)
- [Class Diagram](#class-diagram)
- [Design Patterns](#design-patterns)
- [SOLID Principles](#solid-principles)
- [Getting Started](#getting-started)
- [Usage Examples](#usage-examples)
- [Fee Structure](#fee-structure)
- [Concurrency Handling](#concurrency-handling)

##  Overview

This system provides an efficient solution for managing parking operations in a multi-floor parking lot. It automatically assigns parking spots based on vehicle size, tracks entry/exit times, calculates fees, and handles concurrent operations safely.

##  Features

### Core Functionality
- **Automatic Spot Allocation**: Intelligently assigns parking spots using pluggable strategies (Best-Fit, First-Fit)
- **Multi-Floor Support**: Manages parking across multiple floors with independent strategies per floor
- **Real-Time Availability**: Provides instant updates on available parking spots
- **Dynamic Fee Calculation**: Calculates fees using Strategy Pattern for flexible pricing models
- **Thread-Safe Operations**: Handles concurrent vehicle entry/exit operations with synchronized methods

### Vehicle Support
- Motorcycles/Bike (Small spots)
- Cars (Medium spots)
- Buses (Large spots)

## System Design

### Architecture Overview

Follows a layered architecture with clear separation of concerns:

```
┌─────────────────────────────────────┐
│         Main Application            │
├─────────────────────────────────────┤
│      Service Layer (Business)       │
│    - ParkingLot (Singleton)         │
│    - FeeCalculator                  │
├─────────────────────────────────────┤
│       Strategy Layer                │
│    - SpotFindingStrategy            │
│    - FeeCalculationStrategy         │
├─────────────────────────────────────┤
│       Entity Layer (Domain)         │
│    - ParkingFloor, ParkingSpot      │
│    - Vehicle, ParkingTicket         │
└─────────────────────────────────────┘
```

### Data Model

#### Core Entities

1. **Vehicle** (Abstract)
   - Properties: licensePlate, type
   - Subclasses: Motorcycle, Car, Bus
   - Each subclass defines its required spot size

2. **ParkingSpot**
   - Properties: spotId, size, floorNumber, status, parkedVehicle
   - Thread-safe operations for concurrent access
   - Supports AVAILABLE, OCCUPIED, RESERVED states

3. **ParkingFloor**
   - Manages collection of parking spots
   - Uses pluggable SpotFindingStrategy for allocation algorithms
   - Supports runtime strategy switching
   - Tracks availability by size

4. **ParkingTicket**
   - Records entry/exit times
   - Stores vehicle and spot information
   - Calculates parking duration
   - Tracks payment status

5. **ParkingLot** (Singleton)
   - Central management system
   - Handles vehicle entry/exit
   - Coordinates fee calculation
   - Thread-safe for concurrent operations

## 📊 Class Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         CLASS DIAGRAM                                   │
└─────────────────────────────────────────────────────────────────────────┘

                    ┌──────────────────┐
                    │   ParkingLot     │◄──────────── Singleton Pattern
                    │ (Service Layer)  │
                    ├──────────────────┤
                    │ - name           │
                    │ - floors         │
                    │ - activeTickets  │
                    │ - feeCalculator  │
                    │ - spotStrategy   │
                    ├──────────────────┤
                    │ + parkVehicle()  │
                    │ + exitVehicle()  │
                    │ + displayAvail() │
                    └────────┬─────────┘
                             │
                             │ 1..*
                    ┌────────▼─────────┐
                    │  ParkingFloor    │◄──────── Uses Strategy
                    │  (Entity Layer)  │
                    ├──────────────────┤
                    │ - floorNumber    │
                    │ - spots          │
                    │ - strategy       │
                    ├──────────────────┤
                    │ + findSpot()     │
                    │ + setStrategy()  │
                    │ + getAvailable() │
                    └────────┬─────────┘
                             │
                             │ 1..*
                    ┌────────▼─────────┐
                    │  ParkingSpot     │
                    │  (Entity Layer)  │
                    ├──────────────────┤
                    │ - spotId         │
                    │ - size           │
                    │ - status         │
                    │ - parkedVehicle  │
                    ├──────────────────┤
                    │ + parkVehicle()  │
                    │ + removeVehicle()│
                    │ + isAvailable()  │
                    └──────────────────┘


┌──────────────────┐          ┌──────────────────────────┐
│  ParkingTicket   │          │   FeeCalculator          │
│  (Entity Layer)  │          │   (Service Layer)        │
├──────────────────┤          ├──────────────────────────┤
│ - ticketId       │          │ - strategy               │
│ - vehicle        │◄─────────┤──────────────────────────┤
│ - assignedSpot   │          │ + calculateFee()         │
│ - entryTime      │          │ + setStrategy()          │
│ - exitTime       │          └───────────┬──────────────┘
│ - fee            │                      │
│ - isPaid         │                      │
├──────────────────┤           ┌──────────▼──────────────┐
│ + getDuration()  │           │ FeeCalculationStrategy  │
│ + setExitTime()  │           │ (Strategy Layer)        │
│ + setFee()       │           │   <<interface>>         │
└──────────────────┘           ├─────────────────────────┤
                               │ + calculateFee()        │
                               └──────────┬──────────────┘
                                          │
                                          │ implements
                               ┌──────────▼──────────────┐
                               │  HourlyFeeStrategy      │
                               │  (Strategy Layer)       │
                               ├─────────────────────────┤
                               │ - MOTORCYCLE_RATE       │
                               │ - CAR_RATE              │
                               │ - BUS_RATE              │
                               │ - BASE_FEE              │
                               ├─────────────────────────┤
                               │ + calculateFee()        │
                               └─────────────────────────┘


┌───────────────────────┐
│ SpotFindingStrategy   │
│ (Strategy Layer)      │
│   <<interface>>       │
├───────────────────────┤
│ + findSpot()          │
└──────────┬────────────┘
           │
    ┌──────┴─────────────────┐
    │                        │
┌───▼────────────────┐  ┌───▼──────────────┐
│ BestFitSpotFinding │  │ FirstFitSpotFinding│
│    Strategy        │  │    Strategy        │
│ (Strategy Layer)   │  │ (Strategy Layer)   │
├────────────────────┤  ├────────────────────┤
│ + findSpot()       │  │ + findSpot()       │
│ (exact match first)│  │ (first available)  │
└────────────────────┘  └────────────────────┘


      ┌──────────────┐
      │   Vehicle    │◄──────────── Abstract Base Class
      │  (Abstract)  │              (Entity Layer)
      ├──────────────┤
      │ - license    │
      │ - type       │
      ├──────────────┤
      │ + getRequired│
      │   SpotSize() │
      └──────┬───────┘
             │
      ┌──────┴──────┬──────────┐
      │             │          │
┌─────▼─────┐ ┌────▼────┐ ┌───▼────┐
│ Motorcycle│ │   Car   │ │  Bus   │
│  (Entity) │ │(Entity) │ │(Entity)│
├───────────┤ ├─────────┤ ├────────┤
│ + get     │ │ + get   │ │ + get  │
│   Required│ │   Reqd  │ │   Reqd │
│   Size()  │ │   Size()│ │   Size()│
└───────────┘ └─────────┘ └────────┘


┌──────────────┐  ┌──────────────┐  ┌──────────────────┐
│ VehicleType  │  │  SpotSize    │  │ ParkingSpotStatus│
│   (enum)     │  │   (enum)     │  │     (enum)       │
│ (Entity)     │  │  (Entity)    │  │   (Entity)       │
├──────────────┤  ├──────────────┤  ├──────────────────┤
│ MOTORCYCLE   │  │ SMALL        │  │ AVAILABLE        │
│ CAR          │  │ MEDIUM       │  │ OCCUPIED         │
│ BUS          │  │ LARGE        │  │ RESERVED         │
└──────────────┘  └──────────────┘  └──────────────────┘
```

### Key Relationships

- **ParkingLot** contains multiple **ParkingFloors** (1 to many)
- **ParkingFloor** contains multiple **ParkingSpots** (1 to many)
- **ParkingFloor** uses **SpotFindingStrategy** (Strategy Pattern - runtime configurable)
- **ParkingSpot** can hold one **Vehicle** (0 or 1)
- **ParkingTicket** references one **Vehicle** and one **ParkingSpot**
- **FeeCalculator** uses **FeeCalculationStrategy** (Strategy Pattern - runtime configurable)
- **Vehicle** is inherited by **Motorcycle**, **Car**, and **Bus**

## 🎨 Design Patterns

### 1. Singleton Pattern
**Location**: `ParkingLot` class (Service Layer)

**Purpose**: Ensures only one instance of the parking lot exists throughout the application.

**Implementation**:
```java
public class ParkingLot {
    private static ParkingLot instance;
    private static final Object lock = new Object();
    
    public static ParkingLot getInstance(String name, int floors) {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ParkingLot(name, floors);
                }
            }
        }
        return instance;
    }
}
```

**Benefits**:
- Single point of control for parking operations
- Thread-safe initialization using double-checked locking
- Global access to parking lot instance

### 2. Strategy Pattern - Spot Finding
**Location**: `SpotFindingStrategy` interface and implementations (Strategy Layer)

**Purpose**: Allows flexible spot allocation algorithms that can be changed at runtime per floor.

**Implementation**:
```java
public interface SpotFindingStrategy {
    ParkingSpot findSpot(List<ParkingSpot> spots, Vehicle vehicle);
}

public class BestFitSpotFindingStrategy implements SpotFindingStrategy {
    @Override
    public ParkingSpot findSpot(List<ParkingSpot> spots, Vehicle vehicle) {
        // Find smallest spot that fits the vehicle
        // 1. Try exact size match first
        // 2. Then try next larger size
    }
}

public class FirstFitSpotFindingStrategy implements SpotFindingStrategy {
    @Override
    public ParkingSpot findSpot(List<ParkingSpot> spots, Vehicle vehicle) {
        // Return first available spot that fits
    }
}
```

**Usage**:
```java
// Each floor can have its own strategy
ParkingFloor floor = new ParkingFloor(1, new BestFitSpotFindingStrategy());

// Change strategy at runtime
floor.setSpotFindingStrategy(new FirstFitSpotFindingStrategy());
```

**Benefits**:
- Easy to add new allocation algorithms (e.g., closest to entrance, handicap priority)
- Each floor can use different strategies independently
- Can switch strategies dynamically without modifying floor logic
- Thread-safe strategy execution

### 3. Strategy Pattern - Fee Calculation
**Location**: `FeeCalculationStrategy` interface and implementations (Strategy Layer)

**Purpose**: Allows flexible fee calculation algorithms that can be changed at runtime.

**Implementation**:
```java
public interface FeeCalculationStrategy {
    double calculateFee(ParkingTicket ticket);
}

public class HourlyFeeStrategy implements FeeCalculationStrategy {
    @Override
    public double calculateFee(ParkingTicket ticket) {
        long hours = ticket.getParkingDurationInHours();
        double hourlyRate = getHourlyRate(ticket.getVehicle().getType());
        return BASE_FEE + (hours * hourlyRate);
    }
}
```

**Benefits**:
- Easy to add new fee calculation strategies (e.g., daily, weekly, promotional)
- Separates pricing logic from core parking operations
- Can switch strategies dynamically for different pricing models

### 4. Factory Pattern (Implicit)
**Location**: Vehicle creation in `Main` class

**Purpose**: Encapsulates object creation logic for different vehicle types.

**Benefits**:
- Clean vehicle instantiation
- Easy to extend with new vehicle types

## 🏛️ SOLID Principles

### Single Responsibility Principle (SRP)
Each class has a single, well-defined responsibility:
- **ParkingSpot**: Manages individual spot state
- **ParkingFloor**: Manages spots on one floor
- **ParkingLot**: Coordinates overall parking operations
- **ParkingTicket**: Tracks parking transaction details
- **FeeCalculator**: Handles fee calculations

### Open/Closed Principle (OCP)
- **Vehicle** class is abstract, open for extension (new vehicle types) but closed for modification
- **SpotFindingStrategy** interface allows new allocation algorithms without modifying ParkingFloor
- **FeeCalculationStrategy** interface allows new fee strategies without modifying FeeCalculator
- New strategies can be added without changing existing code

### Liskov Substitution Principle (LSP)
- All `Vehicle` subclasses (Motorcycle, Car, Bus) can be used interchangeably
- Any `FeeCalculationStrategy` implementation can be substituted in `FeeCalculator`
- Any `SpotFindingStrategy` implementation can be substituted in `ParkingFloor`

### Interface Segregation Principle (ISP)
- **FeeCalculationStrategy** provides focused interface for fee calculation
- **SpotFindingStrategy** provides focused interface for spot allocation
- Classes implement only the methods they need

### Dependency Inversion Principle (DIP)
- **FeeCalculator** depends on `FeeCalculationStrategy` interface, not concrete implementations
- **ParkingFloor** depends on `SpotFindingStrategy` interface, not concrete implementations
- High-level modules don't depend on low-level implementation details

## 🚀 Getting Started

### Prerequisites
- Java 8 or higher
- Maven (for building)

### Building the Project

```bash
# Navigate to project directory
cd parkingspotallocation

# Compile the project using Maven
mvn clean compile

# Run the application
mvn exec:java -Dexec.mainClass="com.airtribe.Main"
```

### Alternative: Direct Java Compilation

```bash
# Compile
javac -d target/classes src/main/java/com/airtribe/*.java

# Run
java -cp target/classes com.airtribe.Main
```

## 💡 Usage Examples

### Creating a Parking Lot

```java
// Initialize singleton instance with default Best-Fit strategy
ParkingLot parkingLot = ParkingLot.getInstance("Downtown Parking", 3);

// Add parking spots
parkingLot.addParkingSpot(1, new ParkingSpot("F1-S1", SpotSize.SMALL, 1));
parkingLot.addParkingSpot(1, new ParkingSpot("F1-M1", SpotSize.MEDIUM, 1));
```

### Changing Spot-Finding Strategy

```java
// Change strategy for a specific floor at runtime
ParkingFloor floor1 = parkingLot.getFloors().get(0);
floor1.setSpotFindingStrategy(new FirstFitSpotFindingStrategy());

// Each floor can have its own strategy
ParkingFloor floor2 = parkingLot.getFloors().get(1);
floor2.setSpotFindingStrategy(new BestFitSpotFindingStrategy());
```

### Parking a Vehicle

```java
// Create a vehicle
Vehicle car = new Car("KA-05-1234");

// Park the vehicle
ParkingTicket ticket = parkingLot.parkVehicle(car);

if (ticket != null) {
    System.out.println("Vehicle parked at spot: " + ticket.getAssignedSpot().getSpotId());
    System.out.println("Ticket ID: " + ticket.getTicketId());
}
```

### Exiting a Vehicle

```java
// Process exit
double fee = parkingLot.exitVehicle(ticket.getTicketId());

System.out.println("Parking fee: $" + fee);
```

### Checking Availability

```java
// Display real-time availability
parkingLot.displayAvailability();

// Get specific counts
long totalAvailable = parkingLot.getTotalAvailableSpots();
```

## 💰 Fee Structure

### Base Fee
- **$5.00** - Applicable to all vehicles

### Hourly Rates
- **Motorcycle**: $10/hour
- **Car**: $20/hour
- **Bus**: $40/hour

### Calculation Formula
```
Total Fee = Base Fee + (Hours Parked × Hourly Rate)
```

### Examples
- Motorcycle parked for 2 hours: $5 + (2 × $10) = **$25**
- Car parked for 3 hours: $5 + (3 × $20) = **$65**
- Bus parked for 1 hour: $5 + (1 × $40) = **$45**

**Note**: Parking duration is rounded up to the nearest hour (minimum 1 hour).

## 🔒 Concurrency Handling

### Thread-Safe Design

The system handles concurrent operations through multiple mechanisms:

#### 1. Synchronized Methods
```java
public synchronized boolean parkVehicle(Vehicle vehicle) {
    // Thread-safe parking operation
}

public synchronized Vehicle removeVehicle() {
    // Thread-safe removal
}
```

#### 2. ConcurrentHashMap
```java
private final Map<String, ParkingTicket> activeTickets = new ConcurrentHashMap<>();
```

#### 3. Double-Checked Locking
```java
public static ParkingLot getInstance(String name, int floors) {
    if (instance == null) {
        synchronized (lock) {
            if (instance == null) {
                instance = new ParkingLot(name, floors);
            }
        }
    }
    return instance;
}
```

### Concurrent Operation Support
- Multiple vehicles can enter/exit simultaneously
- Spot availability is updated atomically
- No race conditions in spot allocation
- Thread-safe ticket generation

## 📁 Project Structure

```
parkingspotallocation/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── airtribe/
│                   ├── Main.java                         # Entry point & demonstrations
│                   ├── entity/                           # Domain Layer
│                   │   ├── Vehicle.java                  # Abstract vehicle class
│                   │   ├── Motorcycle.java               # Motorcycle implementation
│                   │   ├── Car.java                      # Car implementation
│                   │   ├── Bus.java                      # Bus implementation
│                   │   ├── ParkingFloor.java             # Floor management
│                   │   ├── ParkingSpot.java              # Individual spot
│                   │   ├── ParkingTicket.java            # Ticket management
│                   │   ├── VehicleType.java              # Vehicle type enum
│                   │   ├── SpotSize.java                 # Spot size enum
│                   │   └── ParkingSpotStatus.java        # Spot status enum
│                   ├── service/                          # Service Layer
│                   │   ├── ParkingLot.java               # Main controller (Singleton)
│                   │   └── FeeCalculator.java            # Fee calculator service
│                   └── strategy/                         # Strategy Layer
│                       ├── SpotFindingStrategy.java      # Spot allocation strategy interface
│                       ├── BestFitSpotFindingStrategy.java # Best-fit algorithm
│                       ├── FirstFitSpotFindingStrategy.java # First-fit algorithm
│                       ├── FeeCalculationStrategy.java   # Fee calculation strategy interface
│                       └── HourlyFeeStrategy.java        # Hourly fee strategy
├── pom.xml                                               # Maven configuration
└── README.md                                             # This file
```

## 🧪 Testing Scenarios

The `Main.java` class demonstrates:

1. **Initial Setup**: Creating parking lot with multiple floors
2. **Basic Parking**: Sequential vehicle parking and exit
3. **Different Vehicle Types**: Motorcycles, cars, and buses
4. **Availability Updates**: Real-time status display
5. **Fee Calculation**: Different rates for different vehicles
6. **Concurrent Operations**: Multiple vehicles arriving simultaneously

## 🔍 Key Algorithms

### Spot Allocation Algorithm

The system supports multiple allocation strategies through the Strategy Pattern:

**Best-Fit Strategy (Default)**:
```
For each vehicle entering:
  1. Determine required spot size based on vehicle type
  2. Search each floor sequentially using configured strategy
  3. Best-Fit Algorithm:
     a. First, look for exact size match
     b. If not found, look for next larger size
  4. If spot found:
     - Mark as occupied atomically
     - Create ticket
     - Return ticket
  5. If no spot found:
     - Return null (parking full)
```

**First-Fit Strategy**:
```
For each vehicle entering:
  1. Determine required spot size based on vehicle type
  2. Search each floor sequentially
  3. First-Fit Algorithm:
     a. Return first available spot that can fit vehicle
     b. No size optimization - faster execution
  4. If spot found:
     - Mark as occupied atomically
     - Create ticket
     - Return ticket
  5. If no spot found:
     - Return null (parking full)
```

**Strategy Selection**:
- Each floor can use a different strategy
- Strategies can be changed at runtime
- Thread-safe strategy execution

### Fee Calculation Algorithm

```
For each vehicle exit:
  1. Calculate parking duration in hours
  2. Get base fee ($5)
  3. Get vehicle-specific hourly rate
  4. Calculate: Base + (Hours × Rate)
  5. Update ticket with fee
  6. Return total fee
```

##  Design Decisions

### Why Singleton for ParkingLot?
- Only one physical parking lot exists
- Centralized control and state management
- Thread-safe global access

### Why Strategy Pattern for Spot Finding?
- Different allocation algorithms (Best-Fit, First-Fit, etc.)
- Each floor can have independent allocation strategy
- Easy to add new algorithms (e.g., closest to entrance, handicap priority)
- Runtime strategy switching without modifying floor logic
- Thread-safe strategy execution

### Why Strategy Pattern for Fee Calculation?
- Different pricing models (hourly, daily, monthly)
- Easy to add promotional pricing
- Separates pricing logic from core operations
- Can switch between pricing models dynamically

### Why Abstract Vehicle Class?
- Common behavior across all vehicles
- Type-safe vehicle handling
- Easy to add new vehicle types

### Why Synchronized Methods?
- Prevents race conditions
- Ensures data consistency
- Simple concurrency model

## Future Enhancements

Potential improvements for the system:

1. **Reservation System**: Pre-book parking spots
2. **Payment Integration**: Multiple payment methods
3. **Dynamic Pricing**: Peak/off-peak hour rates
4. **Database Integration**: Persistent storage
5. **REST API**: Web service interface
6. **Notification System**: SMS/Email alerts
7. **Reporting**: Analytics and statistics
8. **VIP Parking**: Reserved premium spots
9. **Multi-tenancy**: Support multiple parking lots
10. **Mobile App Integration**: Real-time mobile access

## Author

Developed as a demonstration of low-level design principles, OOP concepts, and SOLID principles in Java.
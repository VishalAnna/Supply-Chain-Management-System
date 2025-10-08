# Supply Chain Management System (SCMS) - CLI Backend

A robust, command-line-based backend system for managing a supply chain, built with Java. This project serves as a comprehensive demonstration of core Java skills, Object-Oriented Programming (OOP) principles, custom exception handling, and a thorough testing strategy using TestNG.

---
## Overview

The SCMS is designed to simulate the core logic of a real-world supply chain. It provides functionalities to manage the flow of goods from suppliers to customers, handling inventory across multiple warehouses, processing sales and purchase orders, and generating critical business reports. The primary interface is a Command-Line Interface (CLI) that interacts with a service-oriented backend.

---
## Core Features

* **📦 Inventory Management**
    * Track stock levels of products in different warehouses.
    * Atomically add, remove, and transfer stock between warehouses.
    * Prevents stock levels from dropping below zero through robust business rules.

* **📄 Order Processing**
    * Create sales orders for customers, checking for stock availability before confirming.
    * Fulfill sales orders, which automatically deducts stock and creates a shipment record.
    * A foundational structure for creating purchase orders to replenish stock from suppliers.

* **📊 Reporting**
    * Generate a "Low Stock Report" to identify products that need to be reordered based on a specified threshold.
    * A framework for generating sales and full inventory reports.

* **🛡️ Robust Error Handling**
    * Implements custom checked exceptions for specific business rule violations, such as `InsufficientStockException`, `InvalidQuantityException`, and `EntityNotFoundException`. This ensures data integrity and predictable error states.

---
## Architecture and Design Principles

The system is built using a classic layered architecture to separate concerns, making it clean, scalable, and maintainable.

* **`App.java` (Presentation Layer)**: A simple CLI that acts as the user interface. It takes user input and calls the appropriate service methods.
* **Service Layer (`/service`)**: Contains all the core business logic. This layer is responsible for orchestrating operations between the data models and the repository layer.
* **Repository Layer (`/repository`)**: A data access layer that simulates a database. It uses in-memory `HashMap` collections to store and retrieve data, abstracting the data source from the business logic.

### OOP Concepts Demonstrated
* **Encapsulation**: Data (attributes) and the methods that operate on them are bundled together within classes (e.g., `Product`, `SalesOrder`), with access controlled through getters and setters.
* **Composition**: Objects are composed of other objects. For example, a `SalesOrder` *contains* a `Product` (referenced by its ID), demonstrating a "has-a" relationship.
* **Inheritance**: Custom exception classes inherit from Java's base `Exception` class to create a specialized error-handling hierarchy.

---
## Testing Strategy

A key focus of this project is a comprehensive test suite built with **TestNG** to ensure the reliability and correctness of the business logic.

* **State-Based Testing**: Tests are designed to verify the state of the system after an operation. For example, after fulfilling an order, a test asserts that the inventory level was correctly reduced.
* **Exception Testing**: `expectedExceptions` is used to confirm that the application correctly throws custom exceptions (`InsufficientStockException`, `InvalidQuantityException`) when business rules are violated.
* **Test Organization**: `@BeforeMethod` is used to set up a clean state for each test, ensuring test isolation. Tests are organized logically within their respective classes (`InventoryServiceTest`, `OrderServiceTest`).
* **Advanced Features**: The suite uses `dependsOnMethods` to create logical test chains (e.g., an order must be created before it can be fulfilled).

---
## Technologies Used
* **Java 11**
* **Maven** (Build Tool and Dependency Management)
* **TestNG** (Testing Framework)

---
## Getting Started

### Prerequisites
* Java Development Kit (JDK) 11 or higher
* Apache Maven

### Installation and Running
1.  **Clone the repository**:
    ```bash
    git clone <your-repository-url>
    cd <repository-folder>
    ```
2.  **Build the project**:
    This command compiles the code, downloads dependencies, and runs the TestNG suite.
    ```bash
    mvn clean install
    ```
3.  **Run the application from the command line**:
    ```bash
    java -cp target/supply-chain-management-1.0-SNAPSHOT.jar com.company.scm.App
    ```

### Importing into an IDE (Eclipse/IntelliJ)
1.  Open your IDE.
2.  Select **File -> Import...**.
3.  Choose **Maven -> Existing Maven Projects**.
4.  Browse to the project's root directory and click **Finish**.
5.  Once imported, find `App.java`, right-click on it, and select **Run As -> Java Application**.

---
## Running the Test Suite
To run the complete TestNG suite independently, use the following Maven command:
```bash
mvn test
```
---



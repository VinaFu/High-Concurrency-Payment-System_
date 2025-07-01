<<<<<<< HEAD
# High Concurrency Payment System — Java | Spring Boot | Kafka | Redis

A high-performance payment processing system designed to handle high concurrency scenarios with robust order management, asynchronous event-driven architecture, and idempotent operations.

## Key Features & Technical Highlights

- **Order Management:** Full lifecycle CRUD operations for orders with strict status control.
- **High Concurrency Handling:** Utilizes Redis for distributed locking and idempotency checks to prevent duplicate processing.
- **Asynchronous Processing:** Employs Kafka to decouple payment processing and notification with event-driven messaging.
- **Robust Exception Handling:** Custom exceptions for clear failure modes, ensuring system stability.
- **Comprehensive Unit Tests:** Covers critical business logic and edge cases to ensure reliability.

## Tech Stack

- Java 17, Spring Boot 3.x
- Apache Kafka for asynchronous messaging
- Redis for caching and distributed locks
- JUnit 5 & Mockito for testing

## Running the Project

1. Setup Kafka and Redis on your local machine or docker.
2. Configure application properties with Kafka and Redis endpoints.
3. Build and run Spring Boot application:
=======
# highCurrency
>>>>>>> 08f045b (first commit)

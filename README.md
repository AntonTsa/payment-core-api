# Payment Core API
Payment Core API is a production-oriented backend system that simulates the core functionality of modern financial platforms. The application provides secure account management, money transfers, transaction processing, and ledger-based balance tracking while ensuring data consistency and transactional integrity.
The system is designed using Clean Architecture principles and follows a modular monolith approach to keep business logic isolated from infrastructure concerns. Every financial operation is recorded as an immutable transaction, allowing full auditability and reliable balance calculations.

## Key Features:
- User and account management
- Money deposits and withdrawals
- Account-to-account transfers
- Ledger-based transaction tracking
- Idempotent payment processing
- Transaction history and audit trail
- ACID-compliant financial operations
- RESTful API design

## Technical Highlights:
- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Gradle
- Docker
- Flyway Database Migrations
- Testcontainers Integration Testing
- GitHub Actions CI/CD

## Architecture

The project follows a modular monolith architecture with clearly separated layers:

Domain Layer – business rules and entities
Application Layer – use cases and orchestration
Infrastructure Layer – persistence and external integrations
API Layer – REST endpoints

This architecture allows the application to evolve into a microservices-based system if future scaling requirements demand it.

## Project Goals

The primary objective of the project is to demonstrate the implementation of real-world backend engineering practices, including transaction management, domain modeling, testing strategies, database consistency, and maintainable software architecture commonly used in fintech environments.

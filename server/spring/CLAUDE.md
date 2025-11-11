# CLAUDE.md - Spring Boot Server

This file provides Spring Boot-specific guidance. For shared server architecture, API endpoints, and database design, see [Server Documentation](../CLAUDE.md).

## Module Overview

**Purpose**: Spring Boot implementation of the REST API server
**Technology**: Kotlin/JVM with Spring Boot, PostgreSQL, JPA/Hibernate, Flyway
**Module Path**: `server/spring/`

## Spring Boot Specifics

### Technology Stack
- **Spring Boot**: Web framework with auto-configuration and dependency injection
- **JPA/Hibernate**: ORM for database entity mapping
- **Flyway**: Database migration management with version tracking
- **Spring Data JPA**: Repository abstractions with query generation
- **TestContainers**: JVM library for PostgreSQL container testing

## Architecture

### Spring Boot Structure
```
server/spring/
├── src/main/kotlin/br/com/gabryel/reginaesanguine/server/
│   ├── Server.kt                       # Main Spring Boot application
│   ├── controller/                     # REST API controllers
│   │   ├── AdminController.kt          # Pack seeding admin endpoints
│   │   ├── DeckController.kt           # Deck management endpoints
│   │   └── GameController.kt           # Game session endpoints
│   ├── entity/                         # JPA entities
│   │   ├── PackEntity.kt               # Pack entity with metadata
│   │   ├── PackCardEntity.kt           # Card entity with stats
│   │   └── PackCardEffectEntity.kt     # Effect entity with JSONB
│   ├── jpa/                            # JPA repository interfaces
│   │   ├── PackJpaRepository.kt        # Pack CRUD operations
│   │   ├── PackCardJpaRepository.kt    # Card CRUD operations
│   │   └── PackCardEffectJpaRepository.kt # Effect CRUD operations
│   ├── repository/                     # Repository implementations
│   │   └── SpringPackRepository.kt     # PackRepository implementation
│   ├── service/                        # Service layer
│   │   └── SpringPackLoader.kt         # Pack loading from resources
│   └── configuration/                  # Spring configuration
│       ├── ParsingConfiguration.kt     # JSON parsing setup
│       ├── ServiceConfiguration.kt     # Service beans
│       └── WebConfiguration.kt         # CORS and web config
└── src/main/resources/
    ├── application.yaml                # Spring Boot configuration
    └── db/migration/                   # Flyway migrations
        └── V1__initial_schema.sql      # Initial database schema
└── src/test/kotlin/
    ├── SpringServerIntegrationTest.kt  # Integration tests
    └── TestContainersExtension.kt      # TestContainers lifecycle
```

### Key Components
- **Controllers**: Handle HTTP requests and responses
- **Entities**: JPA entities mapping to PostgreSQL tables
- **JPA Repositories**: Spring Data JPA interfaces for database access
- **Repository Implementations**: Implements common PackRepository interface with batch optimizations
- **Services**: Business logic and pack loading from resources
- **Configuration**: Spring Boot auto-configuration and custom beans
- **Migrations**: Flyway SQL scripts for schema versioning
- **TestContainers**: Docker-based PostgreSQL for integration tests

## Spring Boot Guidelines
- Use Spring Boot's auto-configuration where possible
- Leverage dependency injection for service layers
- Implement proper exception handling with `@ControllerAdvice`
- Use configuration properties for environment-specific settings
- Flyway automatically runs migrations on application startup

### JPA/Hibernate Patterns
- Use `@Entity` for domain objects mapped to database tables
- Implement batch operations with `saveAll()` and `findAllById()`
- Use `@Transactional` for multi-operation consistency
- Leverage Spring Data JPA query methods

## Build and Run Commands

```bash
./gradlew :server:spring:build    # Build server
./gradlew :server:spring:bootRun  # Run in development
./gradlew :server:spring:test     # Run tests
```

## Configuration

- Managed through `application.yaml`
- Server port and context path
- PostgreSQL datasource configuration (URL, username, password)
- JPA/Hibernate settings (dialect, DDL mode)
- Flyway migration settings
- Logging configuration
- CORS settings for client integration

## Dependencies

### Spring Boot Starters
- `spring-boot-starter-web`: Web MVC and embedded Tomcat
- `spring-boot-starter-data-jpa`: JPA/Hibernate ORM support
- `spring-boot-starter-actuator`: Health checks and monitoring

### Database
- `postgresql`: PostgreSQL JDBC driver
- `flyway-core`: Database migration management
- `testcontainers`: PostgreSQL containers for testing
# CLAUDE.md - Server Module (Spring Boot)

This file provides specific guidance for working with the Spring Boot server module of the Reginae Sanguine project.

## Module Overview

**Purpose**: Spring Boot web server providing REST API for online multiplayer functionality  
**Technology**: Kotlin/JVM with Spring Boot framework  
**Module Path**: `server/spring/`

## Responsibilities

### Core Functionality
- **REST API Endpoints**: Game management, deck operations, and multiplayer sessions
- **Game State Management**: Persistent storage and synchronization of game sessions
- **Card Pack Serving**: Provide card data and metadata to client applications
- **Player Session Management**: Handle online multiplayer game coordination

### Integration Points
- **Core Module Dependency**: Uses domain logic from `core` module for game rules
- **Client Communication**: Serves API endpoints for mobile app and future web clients
- **Data Persistence**: Manages game state storage (future: database integration)

## Architecture

### Spring Boot Structure
```
server/spring/
├── src/main/kotlin/br/com/reginaesanguine/server/
│   ├── Server.kt                    # Main Spring Boot application
│   ├── controller/                  # REST API controllers
│   │   ├── DeckController.kt        # Deck management endpoints
│   │   └── GameController.kt        # Game session endpoints  
│   ├── domain/                      # Server-specific domain models
│   │   ├── GameSummary.kt          # Game summary data transfer objects
│   │   └── Pack.kt                 # Card pack metadata
│   └── configuration/               # Spring configuration
│       └── ParsingConfiguration.kt  # JSON parsing setup
└── src/main/resources/
    ├── application.yaml            # Spring Boot configuration
    └── queens_blood_pack_info.json # Card pack metadata
```

### Key Components
- **Controllers**: Handle HTTP requests and responses
- **Configuration**: Spring Boot auto-configuration and custom beans
- **Domain Models**: Server-specific DTOs and data structures
- **Resources**: Static configuration and card pack data

## Development Guidelines

### API Design
- Follow REST conventions for endpoint naming and HTTP methods
- Use appropriate HTTP status codes for different response scenarios
- Implement proper error handling with meaningful error messages
- Version API endpoints when necessary for backward compatibility

### Spring Boot Best Practices
- Use Spring Boot's auto-configuration where possible
- Leverage dependency injection for service layers
- Implement proper exception handling with `@ControllerAdvice`
- Use configuration properties for environment-specific settings

### Testing Strategy
- Write integration tests for REST endpoints using `@SpringBootTest`
- Mock external dependencies and focus on API contract testing
- Test both successful and error scenarios
- Use TestContainers for database integration tests (when implemented)

## Build and Run Commands

```bash
# Build server module only
./gradlew :server:spring:build

# Run server in development mode
./gradlew :server:spring:bootRun

# Build executable JAR
./gradlew :server:spring:bootJar

# Run tests for server module
./gradlew :server:spring:test
```

## Configuration

### Application Properties
Configuration is managed through `application.yaml`:
- Server port and context path
- Database connection settings (future)
- Logging configuration
- CORS settings for client integration

## API Endpoints

### Current Endpoints
- **Deck Management**: `/api/decks/*` - Card deck operations
- **Game Sessions**: `/api/games/*` - Multiplayer game management
- **Health Check**: `/actuator/health` - Service health monitoring

### Future Endpoints (Planned)
- **User Management**: Player registration and authentication

## Integration with Core Module

### Domain Logic Usage
- Leverages game rules and validation from core module
- Maintains separation between web API concerns and business logic
- Uses core module's Result<T> pattern for error handling

### Data Flow
1. HTTP requests received by controllers
2. Request validation and transformation
3. Delegation to core module for business logic
4. Response transformation and HTTP status mapping

## Dependencies

### Spring Boot Starters
- `spring-boot-starter-web`: Web MVC and embedded Tomcat
- `spring-boot-starter-actuator`: Health checks and monitoring
- `spring-boot-starter-test`: Testing framework integration

### Project Dependencies
- `core`: Game engine and domain logic
- Kotlin standard library and reflection

## Deployment Considerations

### Production Readiness
- Actuator endpoints for health monitoring
- Configurable logging levels
- Environment-specific configuration support
- Error handling and graceful degradation

### Future Enhancements
- Database integration for persistent storage
- Authentication and authorization
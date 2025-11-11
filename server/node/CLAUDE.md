# CLAUDE.md - Node.js Server Module

This file provides specific guidance for working with the Node.js/Express server module of the Reginae Sanguine project.

## Module Overview

**Purpose**: Node.js/Express alternative server implementation with identical REST API
**Technology**: Kotlin/JS with Express, PostgreSQL, node-postgres (pg)
**Module Path**: `server/node/`

## Responsibilities

### Core Functionality
- **REST API Endpoints**: Game management, deck operations, and pack management (matching Spring server)
- **PostgreSQL Persistence**: Direct SQL with node-postgres driver and optimized batch operations
- **Card Pack Management**: Database-backed pack storage with seeding capabilities
- **JavaScript Migrations**: Custom migration runner with version tracking

### Integration Points
- **Server Common Module**: Shared DTOs, services, and repository interfaces
- **Core Module Dependency**: Uses domain logic from `core` module for game rules
- **Client Communication**: Serves identical API to Spring server for client compatibility
- **Data Persistence**: PostgreSQL database with raw SQL queries via pg driver

## Architecture

### Node.js/Express Structure
```
server/node/
├── src/jsMain/kotlin/br/com/gabryel/reginaesanguine/server/node/
│   ├── Server.kt                       # Express app setup and main entry
│   ├── pg/                             # PostgreSQL bindings
│   │   └── PgBindings.kt               # External declarations for pg
│   ├── repository/                     # Repository implementations
│   │   └── NodePackRepository.kt       # PackRepository with raw SQL
│   ├── service/                        # Service layer
│   │   └── NodePackLoader.kt           # Pack loading from resources
│   └── MigrationRunner.kt              # Custom migration system
├── src/commonMain/resources/
│   └── db/migration/                   # SQL migration scripts
│       └── V0__init_pack.sql           # Initial database schema
└── src/jsTest/kotlin/br/com/gabryel/reginaesanguine/server/
    ├── NodeServerIntegrationTest.kt    # Integration tests
    ├── NodeTestContainersExtension.kt  # TestContainers lifecycle
    └── node/
        └── TestContainersBindings.kt   # External declarations for @testcontainers/postgresql
```

### Key Components
- **Express Application**: HTTP server with CORS support
- **pg Driver**: PostgreSQL client for Node.js with connection pooling
- **Repository Implementations**: Implements common PackRepository interface with batch SQL operations
- **Migration Runner**: Custom migration system reading from classpath resources
- **Services**: Business logic and pack loading from resources
- **TestContainers**: Docker-based PostgreSQL for integration tests

## Development Guidelines

### API Design
- **API Parity**: Maintains identical endpoints to Spring server for client compatibility
- Follow REST conventions for endpoint naming and HTTP methods
- Use appropriate HTTP status codes for different response scenarios
- Implement proper error handling with meaningful error messages

### Node.js Best Practices
- Use connection pooling for PostgreSQL connections
- Properly handle async operations with Kotlin coroutines and JS Promises
- Use parameterized queries to prevent SQL injection
- Implement proper transaction management

### Database Operations
- **Batch Operations**: Use multi-row INSERT statements to prevent N+1 queries
- **Parameterized Queries**: Always use `$1, $2, ...` placeholders for values
- **JSONB Storage**: Store effect data as JSONB for flexibility
- **Type Casting**: Use `::uuid` and `::jsonb` casts when needed for PostgreSQL types

### Testing Strategy
- Write integration tests using Kotest FunSpec
- Use TestContainers PostgreSQL (@testcontainers/postgresql npm package) for real database tests
- Test REST endpoints with full database round-trips
- Verify pack seeding, deck operations, and game management
- Inherit from AbstractServerIntegrationTest for consistency with Spring tests

## Build and Run Commands

```bash
./gradlew :server:node:build        # Build Node server
./gradlew :server:node:jsNodeRun    # Run in development
./gradlew :server:node:jsNodeTest   # Run tests
```

## Configuration

### Environment Variables
Configuration via environment variables (with defaults):
- `DATABASE_HOST`: PostgreSQL host (default: localhost)
- `DATABASE_PORT`: PostgreSQL port (default: 5432)
- `DATABASE_NAME`: Database name (default: reginae_sanguine)
- `DATABASE_USER`: Database user (default: postgres)
- `DATABASE_PASSWORD`: Database password (default: postgres)
- `PORT`: Server port (default: 3000)

## API Endpoints

### Current Endpoints
- **Admin Pack Management**: `POST /api/admin/packs/seed` - Seed card packs into database
- **Pack Listing**: `GET /api/packs?page={page}&size={size}` - Paginated pack listing
- **Deck Management**: `GET /api/decks/{deckId}` - Get deck details
- **Game Creation**: `POST /game` - Create new game session
- **Game Status**: `GET /game/{gameId}` - Get current game state
- **Game Actions**: `POST /game/{gameId}/action` - Execute game action

### Future Endpoints (Planned)
- **User Management**: Player registration and authentication
- **Deck Building**: Create and manage custom decks

## Integration with Core Module

### Domain Logic Usage
- Leverages game rules and validation from core module
- Maintains separation between web API concerns and business logic
- Uses core module's Result<T> pattern for error handling

### Data Flow
1. HTTP requests received by Express route handlers
2. Request validation and transformation
3. Delegation to core module for business logic via services
4. Response transformation and HTTP status mapping

## Dependencies

### NPM Dependencies
- `express`: Web framework for Node.js
- `pg`: PostgreSQL client for Node.js
- `@testcontainers/postgresql`: PostgreSQL containers for testing (test only)
- `node-fetch`: HTTP client for testing (test only)

### Project Dependencies
- `server:common`: Shared DTOs, services, and repository interfaces
- `core`: Game engine and domain logic
- Kotlin standard library, coroutines, and serialization

## Deployment Considerations

### Production Readiness
- CORS configuration for client integration
- Connection pooling for database efficiency
- Environment-based configuration
- Error handling and graceful degradation
- PostgreSQL persistence with custom migration system

### Database Optimization
- Multi-row INSERT operations to prevent N+1 queries
- Batch loading with `WHERE id = ANY($1::uuid[])` for efficient fetches
- JSONB columns for flexible effect data storage
- Proper indexing on foreign keys and frequently queried fields

### Testing with TestContainers
- Uses @testcontainers/postgresql npm package
- Kotest extension manages container lifecycle
- Dynamic configuration with container host/port
- Automatic cleanup after tests

## Kotlin/JS Specifics

### External Declarations
- **pg Module**: External bindings for node-postgres in `PgBindings.kt`
- **TestContainers**: External bindings for @testcontainers/postgresql in `TestContainersBindings.kt`
- **Express**: Direct require() calls for Express framework

### JavaScript Interop
- Use `js()` function for dynamic JavaScript code
- Use `unsafeCast<T>()` for type conversions from dynamic types
- Promise integration with Kotlin coroutines via `.await()`
- Handle JavaScript errors with try/catch around `await()` calls

### Type Considerations
- UUIDs are strings in JavaScript (no special UUID type)
- PostgreSQL parameters start at $1 (not $0)
- JSONB is stored/retrieved as strings, require parsing
- Dynamic types require careful casting to Kotlin types

## Future Enhancements
- Authentication and authorization
- Real-time multiplayer with WebSockets (socket.io)
- Deck building and management endpoints
- Connection to shared Flyway migrations with Spring server

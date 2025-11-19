# CLAUDE.md - Server Modules

This file provides shared guidance for both Spring Boot and Node.js server implementations.

## Module Overview

**Purpose**: REST API servers for online multiplayer functionality
**Implementations**: Spring Boot (JVM) and Node.js/Express (Kotlin/JS)
**Module Path**: `server/`

Both implementations provide identical REST APIs with different technology stacks:

- **server/spring**: Spring Boot + PostgreSQL + JPA/Hibernate + Flyway
- **server/node**: Node.js/Express + PostgreSQL + pg driver + custom migrations
- **server/common**: Shared DTOs, services, and repository interfaces

## Shared Architecture

### API Endpoints

Both servers implement identical REST endpoints:

- `POST /admin/seed-packs` - Seed card packs into database
- `GET  /packs?page={page}&size={size}` - Paginated pack listing
- `GET  /decks/{deckId}` - Get deck details
- `POST /game` - Create new game session
- `GET  /game/{gameId}` - Get current game state
- `POST /game/{gameId}/action` - Execute game action

### Integration Points

**Server Common Module**: Shared DTOs, services, and repository interfaces

- `PackRepository`: Common repository interface implemented by both servers
- `DeckService`: Deck management service
- `GameService`: Game session management
- `PackSeederService`: Pack seeding logic

**Core Module**: Game engine and domain logic

- Game rules and validation
- Domain entities (Game, Player, Card, Board)
- Result<T> pattern for error handling

**Client Communication**:

- REST API with JSON payloads
- CORS support for cross-origin requests
- Consistent response formats across implementations

### Database Design

Both implementations use PostgreSQL with identical schema:

**Tables**:

- `pack`: Card pack metadata (id, alias, name)
- `pack_card`: Card data (id, pack_id, name, tier, rank, power, increments as JSONB)
- `pack_card_effect`: Effect data (id references pack_card, type, target, effect_data as JSONB)

**Key Design Decisions**:

- JSONB columns for flexible effect and increment data storage
- Foreign key relationships with proper indexing
- UUID primary keys across tables
- Batch operations to prevent N+1 query problems

### Database Optimization

Both implementations follow these optimization patterns:

**Batch Operations**:

- Multi-row INSERT for cards and effects (prevents N+1 writes)
- Batch SELECT with IN clauses or ANY arrays (prevents N+1 reads)
- Single transaction for related operations

**Example Pattern** (conceptual):

```kotlin
// BAD: N+1 queries
cards.forEach { card ->
    insertCard(card)  // N database calls
}

// GOOD: Single batch operation
insertAllCards(cards)  // 1 database call
```

**JSONB Usage**:

- Store complex effect data as JSONB for flexibility
- Store position-based increments as JSONB arrays
- Allows querying and indexing on JSON fields when needed

### Testing Strategy

Both implementations use:

- **TestContainers**: Docker-based PostgreSQL for integration tests
    - Spring: testcontainers JVM library
    - Node: @testcontainers/postgresql npm package
- **Kotest FunSpec**: Shared test framework
- **AbstractServerIntegrationTest**: Common test base class in server/common
- **Full round-trip testing**: HTTP → Service → Repository → Database → Response

### Data Flow

1. HTTP request received (Spring Controller / Express route handler)
2. Request validation and parsing
3. Service layer delegates to repository
4. Repository executes database operations
5. Domain logic from core module applied
6. Response transformation and HTTP status mapping
7. JSON response returned to client

## Development Guidelines

### API Design Principles

- **API Parity**: Both implementations must maintain identical endpoints
- **REST Conventions**: Standard HTTP methods (GET, POST, PUT, DELETE)
- **Status Codes**: Appropriate HTTP status codes (200, 201, 400, 404, 500)
- **Error Handling**: Consistent error response format with meaningful messages
- **No Generic Types**: Never return generic types (List, Map, Set) directly from endpoints - always wrap in a specific DTO
- **Prefer Pagination**: Always prefer returning a Page/PageDto over a bare List for collection endpoints - this allows future pagination without breaking API contracts

### Database Best Practices

- **Parameterized Queries**: Always use placeholders to prevent SQL injection
- **Transactions**: Wrap related operations in database transactions
- **Connection Pooling**: Use connection pools for efficient database access
- **Migration Management**: Version database schema changes

### Testing Best Practices

- **Integration Tests**: Test full HTTP → Database round trips
- **TestContainers**: Use real PostgreSQL containers, not mocks
- **Test Data**: Create isolated test data per test
- **Cleanup**: Ensure tests clean up after themselves

### Migration Best Practices

- **Migration Location**: Store SQL migrations in `src/commonMain/resources/db/migration/` directory
- **Naming Convention**: Use format `V{number}__{description}.sql`
- **Version Control**: All schema changes must be versioned migrations
- **Immutability**: Never modify existing migrations after deployment
- **Testing**: Test migrations with TestContainers before production
- **Rollback Strategy**: Consider how to rollback breaking changes

## Implementation-Specific Details

For implementation-specific guidance, see:

- [Spring Server Documentation](spring/CLAUDE.md) - JPA, Hibernate, Flyway, Spring beans
- [Node.js Server Documentation](node/CLAUDE.md) - Express, pg driver, Kotlin/JS interop
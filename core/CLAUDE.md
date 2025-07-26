# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Module Overview

The **Core module** provides the pure game engine implementation with domain logic, game rules, and state management. This module contains minimal external dependencies and serves as the foundation for all client applications.

## Development Commands

```bash
# Build core module
./gradlew :core:build

# Run core tests
./gradlew :core:jvmTest

# Check code style (run before committing)
./gradlew :core:ktlintCheck

# Format code
./gradlew :core:ktlintFormat

# Clean core module
./gradlew :core:clean

# When validating changes, run:
./gradle check :cli:linkDebugExecutableLinuxX64 # Change targets on other platforms
```

## Architecture & Structure

### Technology Stack
- **Kotlin Multiplatform** with pure domain model in `core/src/commonMain/kotlin/`
- **Arrow-kt** for functional error handling via `Raise` interface
- **Kotest** testing framework with custom domain-specific matchers
- **KtLint** enforcing official Kotlin code style
- **Domain-Driven Design** with immutable data classes

### Core Module Structure
```
core/
├── build.gradle.kts                    # Module build configuration
└── src/
    ├── commonMain/kotlin/              # Shared domain logic
    │   └── br/com/gabryel/reginaesanguine/
    │       └── domain/                 # Domain model and game engine
    │           ├── util/               # Domain utilities
    │           └── [impl files]        # Code implementation
    └── commonTest/kotlin/              # Domain tests
        └── br/com/gabryel/reginaesanguine/domain/
            ├── matchers/               # Custom test matchers
            ├── helpers/                # Test utilities and sample data
            └── [test files]            # Domain tests
```

### Domain Model (`br.com.gabryel.reginaesanguine.domain`)
**Core Entities:**
- `Game` - Root aggregate managing state and turns
- `Board` - 5x3 grid implementing `CellContainer` interface  
- `Player` - Hand/deck management with rank system
- `Card` - Cost, power, position-based increments, and effects
- `Cell` - Board cells with owner, ranks, and applied effects

**Key Types:**
- `Position` - Type alias for `Pair<Int, Int>` coordinates
- `PlayerPosition` - `LEFT`/`RIGHT` enum with navigation logic
- `Action` - `Skip` or `Play(position, card)` sealed interface
- `State` - `Ongoing`, `Tie`, `Won(player)` game states

### Error Handling Pattern
- Uses custom `Result<T>` type (`Success<T>` | `Failure`) with specific failure types:
- All domain methods return `Result<T>` instead of throwing exceptions
- Integration with Arrow-kt's `Raise` for functional composition

**Testing Patterns:**
- Result-based assertions: `result shouldBeSuccessfulAnd haveState(Won(LEFT))`
- Property-based testing with Kotest
- Custom DSL for domain-specific assertions
- Comprehensive test coverage of game rules and edge cases
- When starting new features, should always create tests for relevant scenarios first

## Game Rules Implementation
- **Board**: 5 columns × 3 rows, players start controlling 3 cells each
- **Rank System**: Cards rank, placement increments adjacent cells
- **Card Placement**: Requires sufficient ranks and empty cells
- **Win Condition**: Row-based scoring system comparing cell power
- **Game End**: Two consecutive skips or board full

## Key Patterns
1. **Immutable Design**: All domain objects are immutable data classes
2. **Functional Composition**: Heavy use of `map`, `flatMap`, `fold` on `Result<T>`
3. **Position-based Logic**: Extensive use of 2D coordinate calculations
4. **State Management**: Game maintains previous state for history tracking
5. **Pure Domain**: No external dependencies in domain layer

## Code Quality
- KtLint enforces official Kotlin code style (configured in `gradle.properties`)
- GitHub Actions CI runs `./gradlew build` on every push
- Always run `ktlintCheck` before committing
- Comprehensive test coverage expected for all domain logic
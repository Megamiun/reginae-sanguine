# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Reginae Sanguine** is a study project implementing a card game engine inspired by Final Fantasy VII Rebirth's "Queen's Blood" minigame. Built using **Kotlin Multiplatform** with **Domain-Driven Design (DDD)** principles, focusing on creating a portable, modular game engine.

## Development Commands

```bash
# Build the project
./gradlew build

# Run tests
./gradlew jvmTest

# Check code style (run before committing)
./gradlew ktlintCheck

# Format code
./gradlew ktlintFormat

# Clean build
./gradlew clean
```

## Architecture & Structure

### Technology Stack
- **Kotlin Multiplatform** with pure domain model in `core/src/commonMain/kotlin/`
- **Arrow-kt** for functional error handling via `Raise` interface
- **Kotest** testing framework with custom domain-specific matchers
- **KtLint** enforcing official Kotlin code style
- **Domain-Driven Design** with immutable data classes

### Domain Model (`br.com.gabryel.reginaesanguine.domain`)

**Core Entities:**
- `Game` - Root aggregate managing state and turns
- `Board` - 5x3 grid implementing `CellContainer` interface  
- `Player` - Hand/deck management with pin system
- `Card` - Cost, power, position-based increments, and effects
- `Cell` - Board cells with owner, pins, and applied effects

**Key Types:**
- `Position` - Type alias for `Pair<Int, Int>` coordinates
- `PlayerPosition` - `LEFT`/`RIGHT` enum with navigation logic
- `Action` - `Skip` or `Play(position, card)` sealed interface
- `State` - `Ongoing`, `Tie`, `Won(player)` game states

### Error Handling Pattern
Uses custom `Result<T>` type (`Success<T>` | `Failure`) with specific failure types:
- `GameEnded`, `NotPlayerTurn`, `CardNotOnHand`, `OutOfBoard`, `CellOccupied`, `InsufficientPins`
- All domain methods return `Result<T>` instead of throwing exceptions
- Integration with Arrow-kt's `Raise` for functional composition

### Effects System
- `Effect` interface for card effects with position-relative targeting
- `RaisePower` concrete implementation
- Effects apply relative to card placement position

## Testing Architecture

**Test Structure:**
- Domain tests in `core/src/commonTest/kotlin/`
- Custom matchers in `domain/matchers/` (e.g., `GameMatchers.kt`, `CellMatchers.kt`)
- Test helpers in `domain/helpers/` (e.g., `SampleCards.kt`, `BoardHelpers.kt`)

**Testing Patterns:**
- Result-based assertions: `result shouldBeSuccessfulAnd haveState(Won(LEFT))`
- Property-based testing with Kotest
- Custom DSL for domain-specific assertions
- Comprehensive test coverage of game rules and edge cases
- When starting new features, should always create tests for relevant scenarios first

## Game Rules Implementation

- **Board**: 5 columns × 3 rows, players start controlling 3 cells each
- **Pin System**: Cards cost pins, placement increments adjacent cells
- **Card Placement**: Requires sufficient pins and empty cells
- **Win Condition**: Row-based scoring system comparing cell power
- **Game End**: Two consecutive skips or board full

## Key Patterns

1. **Immutable Design**: All domain objects are immutable data classes
2. **Functional Composition**: Heavy use of `map`, `flatMap`, `fold` on `Result<T>`
3. **Position-based Logic**: Extensive use of 2D coordinate calculations
4. **State Management**: Game maintains previous state for history tracking
5. **Pure Domain**: No external dependencies in domain layer

## Module Structure

Currently single `core` module, designed for future expansion:
- **Native GUI**: Linux desktop application
- **Web Server**: Online multiplayer 
- **Android App**: Mobile client
- **Configuration**: File-based game setup

## Code Quality

- KtLint enforces official Kotlin code style (configured in `gradle.properties`)
- GitHub Actions CI runs `./gradlew build` on every push
- Always run `ktlintCheck` before committing
- Comprehensive test coverage expected for all domain logic
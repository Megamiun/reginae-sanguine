# CLAUDE.md - Core Module

This file provides guidance to Claude Code when working with the Core module of the Reginae Sanguine project.

## Module Overview

**Purpose**: Pure game engine with domain logic  
**Technology**: Kotlin Multiplatform (Common)  
**Targets**: JVM, Native (Linux, Windows, macOS), iOS

The **Core module** provides the pure game engine implementation with domain logic, game rules, and state management. This module contains minimal external dependencies and serves as the foundation for all client applications including the Compose UI, CLI, and server implementations.

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
./gradlew check :app:cli:linkDebugExecutableLinuxX64 # Change targets on other platforms
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
├── CLAUDE.md                           # This file - Core module documentation  
├── build.gradle.kts                    # Module build configuration
└── src/
    ├── commonMain/kotlin/              # Shared domain logic
    │   └── br/com/gabryel/reginaesanguine/
    │       ├── domain/                 # Domain model and game engine
    │       │   ├── util/               # Domain utilities
    │       │   ├── parser/             # JSON serialization support
    │       │   ├── Pack.kt             # Card pack domain model
    │       │   └── [game entities]     # Core game implementation
    │       └── [other packages]        # Additional domain packages
    ├── commonTest/kotlin/              # Domain tests
    │   └── br/com/gabryel/reginaesanguine/domain/
    │       ├── matchers/               # Custom test matchers
    │       ├── helpers/                # Test utilities and sample data
    │       └── [test files]            # Comprehensive domain tests
    └── jvmTest/                        # JVM-specific tests
        └── resources/                  # Test resources (card packs, etc.)
```

### Domain Model (`br.com.gabryel.reginaesanguine.domain`)
**Core Entities:**
- `Game` - Root aggregate managing state and turns
- `Board` - 5x3 grid implementing `CellContainer` interface  
- `Player` - Hand/deck management with rank system
- `Card` - Cost, power, position-based increments, and effects
- `Cell` - Board cells with owner, ranks, and applied effects
- `Pack` - Card pack definition with metadata and card collection
- `Effect` - Effects that run over a certain trigger. They need to always implement one, and only one, of the following interfaces:
  - `AddCardsToHand` - Spawn cards on players hand
  - `DestroyCards` - Destroys cards at the affect cells
  - `RaiseCell` - Raise cards power at the affected cells
  - `RaiseLane` - Raise all lanes power
  - `RaiseRank` - Modifies rank raise on affected cells
  - `ReplaceAlly` - Replaces card and create modified effect based on base card
  - `Spawn` - Spawn cards on owned cells
- `Trigger` - A trigger that says when an action should be considered
  - `WhenPlayed`: Effect activates when card is played (scope: ALLIES/ENEMIES/ANY - for counting triggers, defaults to SELF)
  - `WhenDestroyed`: Effect activates when card is destroyed (scope: ALLIES/ENEMIES/ANY - for counting triggers, defaults to SELF)
  - `WhenFirstStatusChanged`: Effect activates when card status first changes (status: ENHANCED/ENFEEBLED)
  - `WhenFirstReachesPower`: Effect activates when card's power first reaches threshold (threshold: integer)
  - `WhileActive`: Effect is active while card remains on board
  - `WhenLaneWon`: Effect activates when the player wins the lane
  - `None`: No trigger (used for FlavourText effects)

**Key Types:**
- `Position` - Type alias for `Pair<Int, Int>` coordinates
- `PlayerPosition` - `LEFT`/`RIGHT` enum with navigation logic
- `Action` - `Skip` or `Play(position, card)` sealed interface
- `State` - `Ongoing`, `Tie`, `Won(player)` game states

### Error Handling Pattern
- Uses custom `Result<T>` type (`Success<T>` | `Failure`) with specific failure types:
- All domain methods return `Result<T>` instead of throwing exceptions
- Integration with Arrow-kt's `Raise` for functional composition

### Status Types (Enum Values)
- `ENHANCED`: Card is in enhanced state
- `ENFEEBLED`: Card is in enfeebled state
- `ANY`: Cards is in any affected state

### Target Types (Enum Values)
- `ALLIES`: Effect only applies to allied cards
- `ENEMIES`: Effect only applies to enemy cards
- `ANY`: Effect applies to both allied and enemy cards
- `SELF`: Effect applies to the card itself or its player

### Effects

Effects are extensible and should always implement one of the given interfaces. If the category of Effect has the same name as a Effect, then the Effect class will have an `Default` added to the end.

The needed effects to run a base Queen's Blood game are:

#### RaiseCell
- `RaisePower`: Modifies power of cards (amount: positive/negative integer, target: ALLIES/ENEMIES/ANY/SELF)
- `RaisePowerByCount`: Modifies power of cards based on numbers of cards in certain status and ownership (amount: positive/negative integer, target: ALLIES/ENEMIES/ANY/SELF, status: ENHANCED/ENFEEBLED/ANY, scope: ALLIES/ENEMIES/ANY - both default to ANY)
- `RaisePowerOnStatus`: Power modification based on card state (enhancedAmount/enfeebledAmount: integers)

#### RaiseLane
- `RaiseLaneIfWon`: Provides score bonus (amount: positive integer)
- `RaiseWinnerLanesByLoserScore`: Provides score bonus equal to loser's score (no additional fields)

#### DestroyCards
- `DestroyCards`: Destroys cards on affected tiles

#### RaiseRank
- `RaiseRank` - Modifies rank raise on affected cells

#### ReplaceAlly
- `ReplaceAlly`: Replaces an allied card
- `ReplaceAllyRaise`: Replaces an allied card with optional power modification (powerMultiplier: 1=raise by replaced power, -1=lower by replaced power; target: ALLIES/ANY for power modification target)

#### Spawn
- `SpawnCardsPerRank`: Spawns specific cards in empty positions (cardIds: array of card IDs to spawn depending on rank)

#### AddCardsToHand
- `AddCardsToHand`: Adds specific card to player's hand (cardIds: related card IDs)

#### Others
- `NoEffect`: No effect
- `FlavourText`: Only a description

## Testing Patterns
- Result-based assertions: `result shouldBeSuccessfulAnd haveState(Won(LEFT))`
- Property-based testing with Kotest
- Custom DSL for domain-specific assertions
- Comprehensive test coverage of game rules and edge cases
- When starting new features, should always create tests for relevant scenarios first
- Testing Effects(can be more than one):
  - Effect: Should test the Effect implementation, without creating a new game or board
  - Effect Triggering/Management/Notification: Should test the EffectRegistry implementation, without creating a new game
  - Effect that change the board state: Should test the Board implementation, without creating a new game
  - Effect that change the player state: Should test the Game implementation

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
# CLAUDE.md - ViewModel Module

This file provides specific guidance for working with the ViewModel module of the Reginae Sanguine project.

## Module Overview

**Purpose**: Shared UI state management across client platforms  
**Technology**: Kotlin Multiplatform (Common)  
**Module Path**: `viewmodel/`

## Responsibilities

### Core Functionality
- **State Management**: Centralized UI state handling with reactive patterns
- **Game Flow Control**: UI state transitions for game actions and phases
- **Cross-Platform Compatibility**: Shared ViewModels usable across Android, Desktop, and future platforms
- **Error Handling**: UI-appropriate error state management

### Integration Points
- **Core Module Dependency**: Uses domain logic and game state from `core` module
- **Client Integration**: Consumed by `app` module and other UI implementations
- **Reactive Updates**: State flow patterns for real-time UI updates

## Architecture

### Module Structure
```
viewmodel/
├── src/commonMain/kotlin/br/com/gabryel/reginaesanguine/viewmodel/
│   ├── GameViewModel.kt    # Main game state management
│   └── State.kt           # UI state definitions and transitions
└── build.gradle.kts       # Kotlin Multiplatform configuration
```

### Key Components
- **GameViewModel**: Primary state manager for game UI interactions
- **State sealed classes**: Type-safe representation of UI states
- **StateFlow Integration**: Reactive state management with Kotlin Coroutines

## State Management Pattern

### State Definitions
The module uses sealed classes to represent different UI states:
- `ChooseAction`: Player deciding between skip or play card
- `ChooseCard`: Player selecting which card to play
- `ChoosePosition`: Player choosing where to place the selected card

### State Transitions
```kotlin
ChooseAction -> ChooseCard -> ChoosePosition -> ChooseAction
    |                                           ^
    |-------- skip() --------------------------|
```

### Reactive Updates
- Uses `StateFlow` for observing state changes
- UI components subscribe to state updates
- Immutable state objects ensure predictable updates

## Development Guidelines

### ViewModel Best Practices
- **Immutable States**: All state objects should be immutable
- **Type Safety**: Use sealed classes and contracts for state validation
- **Error Handling**: Capture and expose UI-relevant errors in state
- **Platform Agnostic**: Keep UI logic independent of platform specifics

### State Management Rules
- **Single Source of Truth**: ViewModel holds the authoritative UI state
- **Predictable Updates**: State changes should be explicit and traceable
- **Error Recovery**: Provide mechanisms to recover from invalid states
- **Performance**: Minimize state object creation and use efficient comparison

### Integration Patterns
- **Domain Separation**: Keep UI concerns separate from business logic
- **Core Module Usage**: Delegate game rules to core module
- **Client Integration**: Provide simple, reactive APIs for UI components

## Build Configuration

### Kotlin Multiplatform Setup
```kotlin
kotlin {
    jvm()                    // For Android and CLI usage
    linuxX64(), linuxArm64() // Native desktop targets
    mingwX64()               // Windows native
    macosX64(), macosArm64() // macOS native (conditional)
}
```

### Dependencies
- **Core Module**: Game domain logic and state
- **Arrow-kt**: Functional programming utilities
- **Mosaic Runtime**: Terminal UI support for CLI clients
- **Kotlin Coroutines**: StateFlow and reactive patterns

## Testing Strategy

### Unit Testing Focus
- State transition correctness
- Error handling scenarios
- State validation logic
- ViewModel lifecycle management

### Test Organization
```bash
# Run viewmodel module tests
./gradlew :viewmodel:test

# Platform-specific tests
./gradlew :viewmodel:jvmTest
./gradlew :viewmodel:linuxX64Test
```

## Integration Examples

### Android Integration
```kotlin
// In Compose UI
val viewModel = remember { GameViewModel.forGame(game) }
val state by viewModel.state.collectAsState()

when (state) {
    is ChooseAction -> ActionUI(onSkip = viewModel::skip)
    is ChooseCard -> CardSelectionUI(onChoose = viewModel::chooseCard)
    is ChoosePosition -> PositionUI(onChoose = viewModel::choosePosition)
}
```

### CLI Integration
```kotlin
// In terminal UI
val viewModel = GameViewModel.forGame(game)
runBlocking {
    viewModel.state.collect { state ->
        when (state) {
            is ChooseAction -> renderActionPrompt()
            is ChooseCard -> renderCardSelection()
            is ChoosePosition -> renderBoardPositions()
        }
    }
}
```

## Error Handling

### State Error Management
- States can contain error information
- UI components should handle and display errors appropriately
- Error recovery mechanisms should guide users back to valid states

### Validation Patterns
- Use Kotlin contracts for compile-time state validation
- Runtime checks with meaningful error messages
- Graceful degradation for invalid state transitions

## Future Enhancements

### Planned Features
- **Animation States**: Support for UI transition animations
- **History Management**: Undo/redo functionality
- **Persistence**: Save and restore UI state
- **Multiplayer States**: Support for networked game states
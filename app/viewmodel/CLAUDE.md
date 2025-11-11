# CLAUDE.md - ViewModel Module

This file provides specific guidance for working with the ViewModel module of the Reginae Sanguine project.

## Module Overview

**Purpose**: Shared UI state management across client platforms  
**Technology**: Kotlin Multiplatform (Common) with Coroutines  
**Module Path**: `app/viewmodel/` (moved from root `viewmodel/` directory)  
**Targets**: JVM, Native (Linux, Windows, macOS), iOS

## Responsibilities

### Core Functionality
- **State Management**: Centralized UI state handling with reactive patterns
- **Game Flow Control**: UI state transitions for game actions and phases
- **Cross-Platform Compatibility**: Shared ViewModels usable across Android, Desktop, and future platforms
- **Error Handling**: UI-appropriate error state management

### Integration Points
- **Core Module Dependency**: Uses domain logic and game state from `core` module
- **Client Integration**: Consumed by `app/compose` and `app/cli` modules
- **Reactive Updates**: StateFlow patterns for real-time UI updates across platforms
- **Cross-Platform Compatibility**: Shared ViewModels work on Android, Desktop, and CLI

## Architecture

### Module Structure
```
app/viewmodel/
├── CLAUDE.md              # This file - ViewModel module documentation
├── build.gradle.kts       # Kotlin Multiplatform configuration
└── src/commonMain/kotlin/br/com/gabryel/reginaesanguine/viewmodel/
    ├── GameViewModel.kt   # Main game state management
    └── State.kt          # UI state definitions and transitions
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
    jvm()                                         // For Android and desktop usage
    linuxX64(), linuxArm64(), mingwX64()          // Native desktop targets
    macosX64(), macosArm64()                      // macOS native (conditional)
    iosArm64(), iosX64(), iosSimulatorArm64()     // iOS targets
    js(), wasmJs()                                // Web targets
}
```

### Dependencies
- **Core Module**: Game domain logic and state
- **Arrow-kt**: Functional programming utilities and Result<T> handling
- **Kotlin Coroutines**: StateFlow and reactive state management patterns

## Testing Strategy

### Unit Testing Focus
- State transition correctness
- Error handling scenarios
- State validation logic
- ViewModel lifecycle management

## Error Handling
### State Error Management
- States can contain error information
- UI components should handle and display errors appropriately
- Error recovery mechanisms should guide users back to valid states

### Validation Patterns
- Use Kotlin contracts for compile-time state validation
- Runtime checks with meaningful error messages
- Graceful degradation for invalid state transitions
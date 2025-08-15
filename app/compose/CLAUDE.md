# CLAUDE.md - App Module (Android)

This file provides guidance for working with the Android application module of the Reginae Sanguine project.

## Module Overview

**Purpose**: Android mobile client application  
**Technology**: Kotlin for Android with Jetpack Compose  
**Target**: Android devices (minSdk 28, compileSdk 36)

## Module Structure

```
app/
├── CLAUDE.md                     # This file - Android module documentation
├── build.gradle.kts              # Android build configuration
└── src/main/
    ├── AndroidManifest.xml       # App manifest and permissions
    ├── kotlin/br/com/gabryel/reginaesanguine/
    │   ├── GameActivity.kt       # Main activity with game board UI
    │   └── ui/theme/             # Compose UI theme definitions
    └── res/
        ├── font/                 # Font assets
        └── values/               # String resources and themes
```

## Architecture & Design
### UI Framework
- **Jetpack Compose** with Material 3 design system
- **Landscape orientation** enforced for optimal game board viewing

### Dependencies on Core Module
- Direct dependency on `core` module for game logic
- Uses domain entities: `Game`, `Player`, `Card`, `Cell`, `Position`
- Leverages functional `Result<T>` type for error handling
- No business logic duplication - pure presentation layer

### Current Implementation Status
- **Static game board display** showing initial game state
- **Grid-based layout** with lane scoring visualization
- **Minimal interactivity** - currently displays random generated game
- **Placeholder UI** for card information and game state

## Technical Specifications

### Android Configuration
- **Namespace**: `br.com.gabryel.reginaesanguine`
- **Min SDK**: 28 (Android 9.0)
- **Kotlin JVM Target**: 11

## Development Guidelines
### UI Components
- Follow **Material 3 design principles**
- Use **Jetpack Compose** best practices for state management
- Implement **responsive layouts** that work across different screen sizes
- Maintain **accessibility standards** for inclusive design

### Game Integration
- **Read-only access** to core domain models
- Use **immutable state** patterns from core module
- Handle **Result<T>** types properly in UI layer
- Avoid duplicating business logic - delegate to core module

### Code Style
- Follow **official Android Kotlin style guide**
- Use **Compose naming conventions** for composable functions
- Maintain **separation of concerns** between UI and domain logic
- Apply **single responsibility principle** to composable functions

## Development Commands
```bash
# Build Android module only
./gradlew :app:build

# Install debug APK to connected device
./gradlew :app:installDebug

# Run Android-specific tests
./gradlew :app:test

# Check code style for Android module
./gradlew :app:ktlintCheck

# Format code for Android module
./gradlew :app:ktlintFormat
```

## Future Development Areas
### Immediate Priorities
1. **Interactive game board** - allow card placement and moves
2. **Game state management** - proper state handling with ViewModel
3. **Turn-based gameplay** - implement player interactions
4. **Card hand display** - show available cards to play

### Advanced Features
1. **Animations and transitions** for card placement and effects
2. **Multiplayer support** integration with future server module
3. **Settings and preferences** for customizable experience
4. **Game history and statistics** tracking

### Technical Improvements
1. **ViewModel architecture** for proper state management
2. **Navigation component** for multi-screen flows
3. **Comprehensive testing** strategy for UI components

## Integration Points
### With Core Module
- **Game engine**: All game logic handled by core
- **Domain models**: Direct usage of core entities
- **Error handling**: Consistent Result<T> pattern
- **Immutable state**: Following functional programming principles

### With Future Modules
- **Server integration**: HTTP client for online multiplayer
- **Local persistence**: Room database for game history

## Notes for Development
- **UI-first approach**: Focus on user experience and visual polish
- **Offline-first**: Ensure core gameplay works without network connectivity

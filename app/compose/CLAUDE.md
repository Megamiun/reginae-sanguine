# CLAUDE.md - Compose App Module

This file provides guidance for working with the multiplatform Compose application module of the Reginae Sanguine project.

## Module Overview

**Purpose**: Multiplatform UI application (Android, Desktop, iOS)  
**Technology**: Kotlin Multiplatform with Jetpack Compose  
**Targets**: Android, JVM (Desktop), iOS (iosArm64, iosX64, iosSimulatorArm64)

## Module Structure

```
app/compose/
├── CLAUDE.md                           # This file - Compose module documentation
├── build.gradle.kts                    # Multiplatform build configuration
└── src/
    ├── androidMain/
    │   ├── AndroidManifest.xml         # Android app manifest
    │   ├── kotlin/                     # Android-specific implementations
    │   └── res/                        # Android resources
    ├── commonMain/
    │   ├── kotlin/br/com/gabryel/reginaesanguine/app/
    │   │   ├── App.kt                  # Main application entry point
    │   │   ├── services/               # Resource loading and card image services
    │   │   ├── ui/                     # UI components and screens
    │   │   │   ├── components/         # Reusable UI components
    │   │   │   ├── theme/             # Compose theme definitions
    │   │   │   ├── GameBoard.kt       # Main game board UI
    │   │   │   ├── HomeScreen.kt      # Main menu screen
    │   │   │   └── Navigation.kt      # Custom navigation system
    │   │   └── util/                  # Utility functions
    │   └── composeResources/          # Generated compose resources
    ├── jvmMain/
    │   ├── kotlin/                    # JVM/Desktop-specific implementations
    │   └── Main.kt                    # Desktop application entry point
    ├── nativeMain/
    │   └── kotlin/                    # Native platform implementations
    └── iosMain/ (generated)
        └── kotlin/                    # iOS-specific implementations
```

## Architecture & Design

### UI Framework
- **Jetpack Compose Multiplatform** with Material design system
- **Custom navigation system** with animated screen transitions
- **Context receivers** for dependency injection (experimental Kotlin feature)
- **Dynamic resource loading** system for assets and fonts

### Navigation System
- **Custom NavigationStack** with type-safe screen enum
- **Animated transitions** with slide animations between screens
- **Stack-based navigation** supporting push/pop operations
- **Composable-scoped navigation** using context receivers

### Asset Management
- **Dynamic asset preparation** via Gradle build tasks
- **Build-time resource generation** for images and fonts
- **Multiplatform resource access** through generated Res classes
- **Platform-agnostic resource loading** interfaces

### Dependencies on Other Modules
- **Core module**: Game logic, domain entities, and business rules
- **ViewModel module**: Shared state management and UI logic
- **Generated resources**: Dynamic asset loading from build directory

## Current Implementation Status

### Completed Features
- **Home screen** with basic navigation to game
- **Interactive game board** with drag-and-drop card placement
- **Custom navigation system** with animated transitions
- **Multiplatform resource loading** for fonts and images
- **Platform-specific implementations** for Android, Desktop, and iOS

### UI Components
- **GameBoard**: Main interactive game interface with grid layout
- **HomeScreen**: Entry point with game launch button
- **Card**: Detailed card display with art and stats
- **Cell**: Game board cell with positioning and state
- **Navigation**: Custom stack-based navigation with animations

## Technical Specifications

### Platform Targets
- **Android**: API 28+ with Compose UI
- **JVM/Desktop**: Desktop application with Compose Desktop
- **iOS**: Native iOS app (iosArm64, iosX64, iosSimulatorArm64)

### Build Configuration
- **Namespace**: `br.com.gabryel.reginaesanguine.app`
- **Kotlin JVM Target**: 11
- **Context receivers**: Enabled for dependency injection
- **When guards**: Enabled for advanced pattern matching

## Development Guidelines

### UI Architecture
- **Context receivers** for implicit dependency passing
- **Compose state management** with StateFlow and collectAsState
- **Immutable state** patterns following functional programming principles
- **Platform-specific implementations** using expect/actual declarations

### Resource Management
- **Dynamic asset loading** from build-generated directories
- **Type-safe resource access** through generated Res classes
- **Platform-agnostic interfaces** for resource loading abstraction
- **Build-time asset preparation** for optimal runtime performance

### Navigation Patterns
- **Type-safe navigation** using enum-based screen definitions
- **Composable-scoped navigation** through context receivers
- **Animated transitions** for polished user experience
- **Stack-based navigation** for predictable back navigation

## Development Commands

```bash
# Build all platforms
./gradlew :app:compose:build

# Run desktop application
./gradlew :app:compose:run

# Build Android APK
./gradlew :app:compose:assembleDebug

# Install Android debug build
./gradlew :app:compose:installDebug

# Prepare assets (runs automatically during build)
./gradlew prepareAssets

# Check code style
./gradlew :app:compose:ktlintCheck

# Format code
./gradlew :app:compose:ktlintFormat
```

## Future Development Areas

### Immediate Priorities
1. **Card selection screen** for deck building
2. **Game settings and preferences** management
3. **Enhanced game state management** with proper persistence
4. **Multiplayer lobby** integration with server module

### Advanced Features
1. **Custom card animations** during gameplay
2. **Audio and haptic feedback** integration
3. **Accessibility improvements** for inclusive design
4. **Performance optimizations** for smooth gameplay

### Platform-Specific Features
1. **Android**: Integration with Android-specific APIs
2. **Desktop**: Keyboard shortcuts and menu bar integration
3. **iOS**: Native iOS design patterns and gestures

## Integration Points

### With Core Module
- **Game engine**: All business logic delegated to core
- **Domain models**: Direct usage of Game, Player, Card entities
- **Functional patterns**: Result<T> for error handling
- **Immutable state**: Following core module principles

### With ViewModel Module
- **Shared state management**: GameViewModel for UI state
- **Coroutines integration**: StateFlow for reactive UI updates
- **Platform-agnostic logic**: Business logic shared across platforms

### With Asset System
- **Build-time preparation**: Assets processed during compilation
- **Dynamic loading**: Resources loaded at runtime from generated directories
- **Type-safe access**: Generated resource classes for compile-time safety

## Notes for Development

### Experimental Features
- **Context receivers** are experimental but provide clean dependency injection
- **When guards** enable advanced pattern matching in UI logic
- Monitor Kotlin evolution for stability of experimental features

### Performance Considerations
- **Asset loading** happens at build time for optimal runtime performance
- **Navigation animations** use hardware acceleration when available
- **Resource caching** minimizes redundant loading operations

### Cross-Platform Compatibility
- **Platform-specific implementations** handle differences in drag-and-drop, file access
- **Consistent UI** maintained across all target platforms
- **Feature parity** ensured through expect/actual declarations
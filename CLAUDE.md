# CLAUDE.md - Reginae Sanguine Project

This file provides guidance to Claude Code when working with the Reginae Sanguine project. For module-specific guidance, see the CLAUDE.md files in each module directory.

## Project Overview

**Reginae Sanguine** is a study project implementing a card game engine inspired by Final Fantasy VII Rebirth's "Queen's Blood" minigame. The project uses **Kotlin Multiplatform** with a modular architecture designed for multiple client implementations.

## Project Structure

```
reginae-sanguine/
├── CLAUDE.md             # This file - project overview
├── core/                 # Game engine module
│   ├── CLAUDE.md         # Core module documentation
│   └── src/              # Domain logic and game rules
├── app/                  # Application modules directory
│   ├── cli/              # Command-line interface module
│   │   ├── CLAUDE.md     # CLI module documentation
│   │   └── src/          # CLI implementation
│   ├── compose/          # Multiplatform UI application
│   │   ├── CLAUDE.md     # Compose app documentation
│   │   └── src/          # Compose UI implementation
│   └── viewmodel/        # Shared UI state management
│       ├── CLAUDE.md     # ViewModel module documentation
│       └── src/          # ViewModels for UI components
├── server/spring/        # Spring Boot server module
│   ├── CLAUDE.md         # Server module documentation
│   └── src/              # REST API and web server
├── assets/               # Game assets and resources
│   ├── packs/            # Card pack definitions and assets
│   ├── fonts/            # Font resources
│   └── static/           # Static UI assets
├── buildSrc/             # Custom Gradle build logic
│   └── src/              # Asset preparation tasks
└── [future modules]      # Additional platforms
```

## Module Division & Responsibilities

### Core Module (`core/`)
**Purpose**: Pure game engine with domain logic  
**Technology**: Kotlin Multiplatform (Common)  
**Responsibilities**:
- Game rules and mechanics implementation
- Board state management and validation
- Card effects and interaction systems
- Win condition evaluation
- Immutable domain model with functional error handling

### CLI Module (`app/cli/`)
**Purpose**: Command-line interface for human players  
**Technology**: Kotlin Multiplatform (Native)  
**Responsibilities**:
- Text-based user interface
- Command parsing and validation
- Game session management
- Human-readable output formatting

### Compose App Module (`app/compose/`)
**Purpose**: Multiplatform UI application (Android, Desktop, iOS, Web)  
**Technology**: Kotlin Multiplatform with Jetpack Compose  
**Responsibilities**:
- Cross-platform game interface with touch/mouse interactions
- Custom navigation system with animated transitions
- Dynamic asset loading and resource management
- Platform-specific implementations for Android, JVM, iOS, and WASM
- Integration with core game engine and viewmodel

### Server Module (`server/spring/`)
**Purpose**: Spring Boot web server for online multiplayer  
**Technology**: Kotlin/JVM with Spring Boot  
**Responsibilities**:
- REST API for game management and deck operations
- Online multiplayer game sessions
- Card pack management and serving
- Game state persistence and synchronization

### ViewModel Module (`app/viewmodel/`)
**Purpose**: Shared UI state management across client platforms  
**Technology**: Kotlin Multiplatform (Common)  
**Responsibilities**:
- Shared ViewModels for UI components
- State management patterns with coroutines
- UI logic abstraction for multiple platforms

### Asset Management (`assets/`, `buildSrc/`)
**Purpose**: Dynamic asset preparation and resource management  
**Technology**: Gradle build logic with custom tasks  
**Responsibilities**:
- Asset preparation tasks for fonts, images, and card packs
- Dynamic resource generation for Compose multiplatform
- Build-time asset processing and organization

## Development Commands

```bash
# Build entire project
./gradlew build

# Run all tests
./gradlew test

# Check code style across all modules
./gradlew ktlintCheck

# Format code across all modules
./gradlew ktlintFormat

# Clean build
./gradlew clean
```

## Architecture Principles
### Domain-Driven Design
- **Core module** contains pure domain logic with no external dependencies
- **Application modules** (CLI, Web, etc.) depend on core but not on each other
- Clear separation between domain and infrastructure concerns

### Functional Programming
- Immutable data structures throughout
- **Result<T>** type for error handling instead of exceptions
- **Arrow-kt** for functional composition and monadic operations

### Kotlin Multiplatform Strategy
- **Common code** in core module for maximum reusability
- **Platform-specific implementations** in application modules
- Consistent API across all platforms and clients
- **Native targets**: linuxX64, linuxArm64, mingwX64, macosX64, macosArm64
- **Mobile targets**: Android, iOS (iosArm64, iosX64, iosSimulatorArm64)
- **JVM target**: Desktop applications and server components
- **WASM target**: Web browser applications with Kotlin/WASM

## Code Quality Standards
- **KtLint** enforces official Kotlin code style
- **Comprehensive testing** with Kotest framework
- **GitHub Actions CI** runs full build on every push
- **Domain-specific matchers** for expressive test assertions

## Coding Rules
- Never use !!. Always use error(), ensure() or throw explicit exceptions when something that shouldn't be null is null
- Test names should follow pattern: `[given setup], when doing action, should expect result`

## AI Coding Rules
- Tests for debugging should be cleaned after debugging is done

## Getting Started
1. **New Features**: Start with core module tests and domain implementation
2. **Client Development**: Depend on core module and implement platform-specific UI
3. **Testing**: Follow existing patterns in each module's test directory
4. **Code Style**: Run `ktlintCheck` before committing

## Commit Message Style
- When creating commit message, limit the message to 6 bullet points using `-` for bullet points
  - Use less if the commit is simple enough
- If Claude participated significantly in implementing current commit:
  - Start commit message with 🤖 followed by a blank space 
  - Add credits at the end: "This commit was implemented by Claude."
  - Do NOT use other credit formats like "Generated with Claude Code" or "Co-Authored-By"
- Otherwise, only add credits at the end: "This commit message was generated by Claude."

For detailed module-specific guidance, refer to:
- [Core Module Documentation](core/CLAUDE.md)
- [CLI Module Documentation](app/cli/CLAUDE.md)
- [Compose App Documentation](app/compose/CLAUDE.md)
- [ViewModel Module Documentation](app/viewmodel/CLAUDE.md)
- [Server Module Documentation](server/spring/CLAUDE.md)
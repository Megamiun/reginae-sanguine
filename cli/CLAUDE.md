# CLAUDE.md - CLI Module

This file provides guidance to Claude Code when working with the CLI module of Reginae Sanguine.

## Module Overview

The **CLI module** provides a command-line interface for the Reginae Sanguine card game engine. This module demonstrates how to build native applications using the core game engine.

## Development Commands

```bash
# Build CLI module specifically
./gradlew :cli:build

# Run CLI tests
./gradlew :cli:allTests

# Check code style for CLI module
./gradlew :cli:ktlintCheck

# Format CLI module code
./gradlew :cli:ktlintFormat

# Run the CLI application
./gradlew :cli:linkDebugExecutableLinuxX64 && ./cli/build/bin/linuxX64/debugExecutable/cli.kexe  # Change targets on other platforms

# When validating changes, run:
./gradlew :cli:check :cli:linkDebugExecutableLinuxX64 # Change targets on other platforms
```

## Architecture & Structure

### Technology Stack
- **Kotlin Multiplatform** for Native platforms (Linux, Windows, macOS)
- **Jetpack Compose** with Mosaic for terminal UI
- **Arrow-kt** for functional programming and error handling
- **Dependencies on core module** for game engine functionality

### Module Structure
```
cli/
├── build.gradle.kts           # Module build configuration
└── src/
    └── commonMain/kotlin/     # Common CLI implementation
        └── br/com/gabryel/reginaesanguine/cli/
            ├── GameApp.kt         # Main Compose TUI application
            ├── GameViewModel.kt   # Game state management
            ├── Main.kt           # Entry point
            └── components/       # Reusable UI components
```

### Key Responsibilities
- **Terminal UI**: Compose-based terminal user interface
- **Game Flow**: Managing game sessions and player interactions
- **Input Handling**: Keyboard input processing and command validation
- **Visual Display**: Rich board visualization with colors and formatting
- **State Management**: ViewModel pattern for game state handling

### Integration with Core
- Uses `br.com.gabryel.reginaesanguine.domain.*` for all game logic
- Implements CLI-specific adapters for game events and state changes
- Handles Result<T> types from core domain methods
- Provides human-readable error messages for domain failures

## Implementation Guidelines

### CLI Design Principles
1. **Simple Commands**: Easy-to-remember command syntax
2. **Clear Feedback**: Always show current game state after actions
3. **Error Handling**: Convert domain errors to user-friendly messages
4. **Progressive Disclosure**: Show help and hints as needed

## Development Notes
- **Platform Targets**: Linux (x64/ARM64), Windows (x64), macOS (x64/ARM64)
- **Dependency Management**: Inherits versions from root project
- **Code Style**: Follows same KtLint rules as core module
- **Error Handling**: Must handle all Result<T> cases from core domain
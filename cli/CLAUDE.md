# CLAUDE.md - CLI Module

This file provides guidance to Claude Code when working with the CLI module of Reginae Sanguine.

## Module Overview

The **CLI module** provides a command-line interface for the Reginae Sanguine card game engine. This module demonstrates how to build native applications using the core game engine.

## Development Commands

```bash
# Build CLI module specifically
./gradlew :cli:build

# Run CLI tests
./gradlew :cli:jvmTest

# Check code style for CLI module
./gradlew :cli:ktlintCheck

# Format CLI module code
./gradlew :cli:ktlintFormat

# Run the CLI application (when implemented)
./gradlew :cli:run
```

## Architecture & Structure

### Technology Stack
- **Kotlin Multiplatform** targeting JVM and Native platforms
- **Arrow-kt** for functional programming and error handling
- **Dependencies on core module** for game engine functionality

### Module Structure
```
cli/
├── build.gradle.kts           # Module build configuration
└── src/
    ├── nativeMain/kotlin/     # Native CLI implementation
    └── nativeTest/kotlin/     # Native-specific tests
```

### Key Responsibilities
- **User Interface**: Text-based game interface for human players
- **Game Flow**: Managing game sessions and player interactions
- **Input Handling**: Processing user commands and validating input
- **Output Formatting**: Displaying game state, board, and results
- **Configuration**: Loading game settings and card definitions

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
- **Platform Targets**: Currently configured for Linux and Windows
- **Dependency Management**: Inherits versions from root project
- **Code Style**: Follows same KtLint rules as core module
- **Error Handling**: Must handle all Result<T> cases from core domain
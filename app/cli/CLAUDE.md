# CLAUDE.md - CLI Module

This file provides guidance to Claude Code when working with the CLI module of Reginae Sanguine.

## Module Overview

**Purpose**: Command-line interface for human players  
**Technology**: Kotlin Multiplatform (Native) with Mosaic TUI  
**Location**: `app/cli/` (moved from root `cli/` directory)  
**Targets**: linuxX64, linuxArm64, mingwX64, macosX64, macosArm64

The **CLI module** provides a terminal-based interface for the Reginae Sanguine card game engine, demonstrating native multiplatform application development.

## Development Commands

```bash
# Build CLI module specifically
./gradlew :app:cli:build

# Run CLI tests
./gradlew :app:cli:allTests

# Check code style for CLI module
./gradlew :app:cli:ktlintCheck

# Format CLI module code
./gradlew :app:cli:ktlintFormat

# Run the CLI application (Linux x64)
./gradlew :app:cli:linkDebugExecutableLinuxX64 && ./app/cli/build/bin/linuxX64/debugExecutable/cli.kexe

# Run on other platforms:
# Linux ARM64: ./gradlew :app:cli:linkDebugExecutableLinuxArm64 && ./app/cli/build/bin/linuxArm64/debugExecutable/cli.kexe
# Windows x64: ./gradlew :app:cli:linkDebugExecutableMingwX64 && ./app/cli/build/bin/mingwX64/debugExecutable/cli.exe
# macOS x64:   ./gradlew :app:cli:linkDebugExecutableMacosX64 && ./app/cli/build/bin/macosX64/debugExecutable/cli.kexe
# macOS ARM64: ./gradlew :app:cli:linkDebugExecutableMacosArm64 && ./app/cli/build/bin/macosArm64/debugExecutable/cli.kexe

# When validating changes, run:
./gradlew :app:cli:check :app:cli:linkDebugExecutableLinuxX64
```

## Architecture & Structure

### Technology Stack
- **Kotlin Multiplatform** for Native platforms (Linux, Windows, macOS)
- **Jetpack Compose** with Mosaic for terminal UI
- **Arrow-kt** for functional programming and error handling
- **Dependencies on core module** for game engine functionality

### Module Structure
```
app/cli/
├── CLAUDE.md                  # This file - CLI module documentation
├── build.gradle.kts           # Module build configuration
└── src/
    └── commonMain/
        ├── kotlin/br/com/gabryel/reginaesanguine/cli/
        │   ├── GameApp.kt     # Main Compose TUI application
        │   ├── Main.kt        # Entry point and platform detection
        │   └── components/    # Reusable TUI components
        │       ├── FillModifier.kt     # Layout utilities
        │       ├── Grid.kt            # Grid display component
        │       └── OptionChooser.kt   # Input selection component
        └── resources/         # CLI resources and assets
```

### Key Responsibilities
- **Terminal UI**: Compose-based terminal user interface with Mosaic
- **Game Flow**: Managing game sessions and player interactions
- **Input Handling**: Keyboard input processing and command validation
- **Visual Display**: Rich board visualization with colors and formatting
- **Cross-Platform Support**: Native binaries for multiple OS and architectures
- **Resource Loading**: Dynamic resource loading with generated assets

### Dependencies and Integration
- **Core Module**: Uses game engine for all business logic
- **Generated Resources**: Access to dynamically prepared assets
- **Mosaic Runtime**: Terminal UI framework for Compose
- **Arrow-kt**: Functional programming patterns and Result<T> handling

### Current Implementation
- **Platform Detection**: Automatic platform detection in Main.kt
- **Component Architecture**: Reusable TUI components for grid display and input
- **Resource Integration**: Connected to asset preparation system
- **Multiplatform Builds**: Supports all major desktop platforms

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
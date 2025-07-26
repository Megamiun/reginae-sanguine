# Reginae Sanguine

Study project for learning Domain Driven Design and Kotlin Multiplatform inspired on Final Fantasy VII Rebirth's Queens Blood.

## Running

### Tests

```bash
  ./gradlew jvmTest
```

### CLI

```bash
# Linux x64
./gradlew :cli:linkDebugExecutableLinuxX64 && ./cli/build/bin/linuxX64/debugExecutable/cli.kexe

# Linux ARM64
./gradlew :cli:linkDebugExecutableLinuxArm64 && ./cli/build/bin/linuxArm64/debugExecutable/cli.kexe

# Windows x64
./gradlew :cli:linkDebugExecutableMingwX64 && ./cli/build/bin/mingwX64/debugExecutable/cli.exe

# macOS x64 (macOS systems only)
./gradlew :cli:linkDebugExecutableMacosX64 && ./cli/build/bin/macosX64/debugExecutable/cli.kexe

# macOS ARM64 (macOS systems only)
./gradlew :cli:linkDebugExecutableMacosArm64 && ./cli/build/bin/macosArm64/debugExecutable/cli.kexe
```

## Goals
### Base
- [ ] Create a multiplatform core as the game engine
  - [ ] Should be able to support the game with all the original rules
  - [x] Should allow for custom cards
  - [x] Should allow for configuring different board initial size and configuration
- [ ] Create a Spring webserver which allows people to play online
  - [ ] Should allow users to register in server
  - [ ] Should allow users to create custom decks
  - [ ] Should allow users to invite each other
- [ ] Have a Kotlin/Native CLI version using Jetpack Compose
  - [ ] Should receive initial configuration via config files
  - [x] Should allow for local play on same device
  - [ ] Should allow signup/login to server
  - [ ] Should allow for remote play via server
- [ ] Have an Android/iOS/Web/Desktop version using Jetpack Compose
  - [ ] Should allow for deck management
  - [ ] Should allow for local play on same device
  - [ ] Should allow signup/login to server
  - [ ] Should allow for remote play via server

### Stretch
- [ ] Create a Ktor webserver which allows people to play online
  - [ ] Should allow users to register in server
  - [ ] Should allow users to create custom decks
  - [ ] Should allow users to invite each other
- [ ] Have a GUI Linux version using C interop
  - [ ] Should allow for deck management
  - [ ] Should allow for local play on same device
  - [ ] Should allow signup/login to server
  - [ ] Should allow for remote play via server
- [ ] Have an Android version using OpenGL
  - [ ] Should allow for deck management
  - [ ] Should allow for local play on same device
  - [ ] Should allow signup/login to server
  - [ ] Should allow for remote play via server
- [ ] Have an local iOS version using Swift and calling core library via interop
  - [ ] Should allow for deck management
  - [ ] Should allow for local play on same device
- [ ] Create a Kotlin/JS webserver which allows people to play online
  - [ ] Should allow users to register in server
  - [ ] Should allow users to create custom decks
  - [ ] Should allow users to invite each other

## Decisions
- This is a study project
- Claude will be used (sparingly probably) to better understand AI agents usage. All uses will be declared on the touched commits
- This project will be modular, made to be reusable and portable to different contexts
- Other than for the modularity, it should be kept as simple as possible internally
- Shortcuts will be taken, if it expedites testability
- Arrow Raise interface will be used as an experiment
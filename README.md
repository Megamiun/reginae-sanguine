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

- [ ] Create a multiplatform core as the game engine
  - [ ] Should be able to support the game with all the original rules
  - [x] Should allow for custom cards
  - [x] Should allow for configuring different board initial size and configuration
- [ ] Have a Kotlin/Native CLI version
  - [ ] Should receive initial configuration via config files
  - [x] Should allow for local play on same device
  - [ ] Should allow for remote play via server
- [ ] Have a GUI version using Jetpack Compose
- [ ] Have a GUI Linux version using C interop
- [ ] Create a webserver which allows people to play online
  - [ ] Should allow users to register in
  - [ ] Should allow users to create custom decks
  - [ ] Should allow users to invite each other
- [ ] Have an Android version
  - [ ] Should allow for local play on same device
  - [ ] Should allow for remote play via server

## Decisions

- This is a study project
- Claude will be used (sparingly probably) to better understand AI agents usage. All uses will be declared on the touched commits
- This project will be modular, made to be reusable and portable to different contexts
- Other than for the modularity, it should be kept as simple as possible internally
- Shortcuts will be taken, if it expedites testability
- Arrow Raise interface will be used as an experiment
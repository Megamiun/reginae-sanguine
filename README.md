# Reginae Sanguine

Study project for learning Domain Driven Design and Kotlin Multiplatform inspired on Final Fantasy VII Rebirth's Queens Blood.

## Running

### Tests

```bash
  ./gradlew jvmTest
```

### CLI

```bash
  # To run on a Linux System. Other possible setups: linuxArm64, mingw64
  ./gradlew :cli:linkDebugExecutableLinuxX64; ./cli/build/bin/linuxX64/debugExecutable/cli.kexe
```

## Goals

- [ ] Create a multiplatform core as the game engine
  - [ ] Should be able to support the game with all the original rules
  - [ ] Should allow for custom cards
  - [ ] Should allow for configuring different board initial size and configuration
- [ ] Have a native GUI Linux version that allows two player to choose decks and play
  - [ ] Should receive initial configuration via config files
- [ ] Create a webserver which allows people to play online
  - [ ] Should allow users to register in
  - [ ] Should allow users to create custom decks
  - [ ] Should allow users to invite each other
- [ ] Have an Android version that interfaces with the server

## Decisions

- This is a study project
- Claude will be used (sparingly probably) to better understand AI agents usage. All uses will be declared on the touched commits
- This project will be modular, made to be reusable and portable to different contexts
- Other than for the modularity, it should be kept as simple as possible internally
- Shortcuts will be taken, if it expedites testability
- Arrow Raise interface will be used as an experiment
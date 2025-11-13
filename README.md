# Reginae Sanguine

Study project for learning Domain Driven Design and Kotlin Multiplatform inspired by Final Fantasy VII Rebirth's Queen's Blood.

## Running

### Tests

```bash
  ./gradlew jvmTest
```

### CLI

```bash
# Linux x64
./gradlew :app:cli:linkDebugExecutableLinuxX64 && ./app/cli/build/bin/linuxX64/debugExecutable/cli.kexe

# Linux ARM64
./gradlew :app:cli:linkDebugExecutableLinuxArm64 && ./app/cli/build/bin/linuxArm64/debugExecutable/cli.kexe

# Windows x64
./gradlew :app:cli:linkDebugExecutableMingwX64 && ./app/cli/build/bin/mingwX64/debugExecutable/cli.exe

# macOS x64 (macOS systems only)
./gradlew :app:cli:linkDebugExecutableMacosX64 && ./app/cli/build/bin/macosX64/debugExecutable/cli.kexe

# macOS ARM64 (macOS systems only)
./gradlew :app:cli:linkDebugExecutableMacosArm64 && ./app/cli/build/bin/macosArm64/debugExecutable/cli.kexe
```

## Goals
### Base

- [x] Create a multiplatform core as the game engine
  - [x] Should be able to support the game with all the original rules
  - [x] Should allow for custom cards
  - [x] Should allow for configuring different board initial size and configuration
  - [ ] Test all scenarios for Costa del Sol Queen's Blood challenges
- [ ] Create a Spring/Node.js webserver which allows people to play online
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
  - [ ] Make sure that all versions sizes are relative to user monitor size/responsive

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
- [ ] Have a local iOS version using Swift and calling core library via interop
  - [ ] Should allow for deck management
  - [ ] Should allow for local play on same device

## Decisions

- This is a study project
- Claude will be used (sparingly probably) to better understand AI agents usage. All uses will be declared on the touched commits
- This project will be modular, made to be reusable and portable to different contexts
- Other than for the modularity, it should be kept as simple as possible internally
- Shortcuts will be taken, if it expedites testability
- Arrow Raise interface will be used as an experiment

## Credits

At the moment, I would like to first of all acknowledge Square Enix® marvelous work in creating this board game inside their video game "Final Fantasy VII Rebirth". From the get go, this game captured my attention and seemed like the perfect game to both play and practice my programming and engineering skills with. This would have not been possible without their original input.

Also, I would like to credit [Miguel Espírito Santo](https://miguelsanto.com/), which has created the assets I will be using for card set derived from the main game. Without his help, this project would be a lot less visually complete and amateurish.

## License

As I see right, this project will include two licenses, one [AGPLv3 license](LICENSE) for the general public to allow only for study reasons, and another unrestricted [MIT License](LICENSE-SQUARE-ENIX) for Square Enix as they are the only reason this could exist.

## Disclaimer

This project is a fan-made study project inspired by the "Queen's Blood" minigame from Final Fantasy VII Rebirth. All concepts, characters, card designs, imagery, logos, and other intellectual property related to Final Fantasy VII are the exclusive property of Square Enix Co., Ltd. This project is created solely for educational and non-commercial purposes and is not affiliated with, endorsed by, or sponsored by Square Enix.

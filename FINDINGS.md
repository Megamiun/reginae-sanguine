# Findings while developing the project

## Claude
- Doesn't work well with images
  - Very slow and fails on parsing some types of content(coordinates on a grid, for example)
- Can reason quite well in general
  - But isn't very proactive(follows a bad path you ask for alternatives or argue against)
  - Planning capabilities are surprisingly good
  - Was able to discover if the failing test reason is because the implementation is wrong, or if the test is wrongly defined
- Needs a good specification
- Will do the bare minimum to deliver
  - Needs constant attention to outputs
  - Already said that tests were passing once, when they clearly weren't

## Jetpack Compose
### Desktop
- Took quite some time to replicate the same behaviour as in the Android impl
  - Not as many resources as Android, lost a lot of time:
    - To identify how to create a LocalFontFamilyResolver
    - To understand that I needed a Window Composable as a root
    - Commit #d544fd97
- Bugs recognized: 
  - Scaffolding loses padding when window of the application is changed
  - Elements partially out of screen are not drawn at first
- androidx.compose prefix is confusing, as both androidx.compose and jetbrains.compose exist 
  - Multiple androidx.compose packages work at all targets, but not *all*

## Multiplatform
- Potentially more slow to build than other targets, even more with multiple targets
- Tests with space in the name fail on native targets
- Multiple implementations can be very bothersome to implement per platform, as:
  - Drag and drop
  - Logging
  - Resources loading

# Interesting topics
- Serialization with Kotlinx
  - Extension ready serialization
- Jetpack Compose for multiple devices
  - Differences and difficulties
  - Kotlin/Native has no auto bundled resources and a classLoader
  - Features disappear when you add more targets
    - i.e., I only lost access to Java libs in the :app common sourceSet when I added iOS as a target
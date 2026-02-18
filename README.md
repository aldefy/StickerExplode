# StickerExplode

A Compose Multiplatform sticker canvas app where you can place, drag, rotate, and scale fun stickers on a canvas. Built with Kotlin Multiplatform targeting Android and iOS.

<p align="center">
  <img src="assets/sticker_demo.gif" width="280" alt="Demo"/>
</p>

## Screenshots

<p align="center">
  <img src="assets/sticker_canvas.jpg" width="280" alt="Sticker Canvas"/>
  &nbsp;&nbsp;
  <img src="assets/sticker_tray.jpg" width="280" alt="Sticker Tray"/>
  &nbsp;&nbsp;
  <img src="assets/drag_demo.jpg" width="280" alt="Drag Demo"/>
</p>

## Features

- **Draggable Stickers** — Freely drag stickers anywhere on the canvas
- **Pinch to Zoom** — Scale stickers with two-finger pinch gestures
- **Rotation** — Rotate stickers with multi-touch gestures
- **Double Tap to Zoom** — Quick 2x zoom toggle with spring animation
- **Sticker Tray** — Bottom sheet with 16 sticker types to choose from
- **Peel-off Effect** — Stickers lift and scale when grabbed, with dynamic shadow
- **Die-cut Outline** — White border around stickers mimicking real die-cut stickers
- **Tilt Shimmer** — Device tilt-based shimmer/glow effect on stickers (accelerometer)
- **Haptic Feedback** — Tactile feedback on grab, drop, tap, and selection
- **Z-ordering** — Tapped stickers come to the front

## Sticker Types

| Sticker | Type |
|---------|------|
| Kotlin logo | Custom Canvas drawing |
| Hello text | Styled typography |
| Build / Code | Material icon with gradient background |
| Gift, Dev, Heart, Star, Fire, Rocket, Sparkles, Party, Thumbs Up, Lightning, Rainbow, Eyes | Emoji stickers |

## Tech Stack

- **Kotlin Multiplatform** — Shared code for Android & iOS
- **Compose Multiplatform** — Declarative UI across platforms
- **Material 3** — Bottom sheet, FAB, icons
- **Compose Gestures** — `detectTransformGestures`, `detectTapGestures`
- **Spring Animations** — `animateFloatAsState` with spring specs
- **Platform Sensors** — Accelerometer/gyroscope for tilt effects (expect/actual)
- **Platform Haptics** — Native haptic feedback (expect/actual)

## Project Structure

```
composeApp/
  src/
    commonMain/       # Shared UI & logic
      App.kt          # Root composable
      StickerCanvas.kt # Canvas with draggable stickers
      StickerTray.kt  # Bottom sheet sticker picker
      ShimmerGlow.kt  # Tilt-based shimmer modifier
      model/          # Sticker data models
      sensor/         # TiltSensor expect declarations
      haptics/        # HapticFeedback expect declarations
    androidMain/      # Android implementations (sensor, haptics)
    iosMain/          # iOS implementations (sensor, haptics)
iosApp/               # iOS app entry point (SwiftUI)
```

## Getting Started

### Prerequisites

- Android Studio Ladybug or later
- Kotlin 2.1+
- JDK 17+
- Xcode 15+ (for iOS)

### Run on Android

```bash
./gradlew :composeApp:installDebug
```

### Run on iOS

Open `iosApp/iosApp.xcodeproj` in Xcode and run on a simulator or device.

## License

This project is provided as a sample/demo.

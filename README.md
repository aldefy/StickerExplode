# StickerExplode

A sticker canvas app built with Compose Multiplatform. Place, drag, rotate, pinch-to-scale, and peel off stickers with spring physics, holographic shimmer, and haptic feedback. Runs on Android and iOS from shared Kotlin code.

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

## Blog series

I wrote a three-part series walking through how everything works, from gesture handling down to the shader code:

1. [Part 1: Gestures, physics, and making stickers feel real](https://aditlal.dev/building-stickerexplode-part-1-gestures-physics-and-making-stickers-feel-real/) - architecture, data model, gesture system, spring physics
2. [Part 2: The peel-off effect and holographic shimmer](https://aditlal.dev/stickerexplode-part-2/) - four simultaneous peel animations, AGSL shader for iridescence, tilt-reactive shimmer with iOS fallback
3. [Part 3: The full end-to-end build](https://aditlal.dev/stickerexplode-part-3/) - die-cut outlines (stamp technique), tilt sensors, haptics, persistence, and the five expect/actual boundaries

## What's in here

- Drag, pinch-to-scale, and rotate with multi-touch gestures
- Double tap for a spring-animated 2x zoom toggle
- Peel-off grab: stickers lift, tilt, and cast a dynamic shadow when you pick them up
- Die-cut outline: white border drawn using a 32-pass offset stamp technique with `BlendMode.SrcIn`
- Holographic shimmer: AGSL shader on Android 13+, gradient brush fallback on iOS and older Android. Responds to device tilt via accelerometer
- Haptic feedback on grab, drop, tap, and selection (platform-native)
- Z-ordering: tapped stickers come to front
- Bottom sheet sticker tray with 16 sticker types
- No persistence yet (stickers reset on app restart)

## How it's built

The interesting parts are the gesture system and the visual effects. Three `pointerInput` blocks handle drag, transform, and tap separately. The peel-off runs four animations at once (scale, tilt, translation, shadow elevation), all on spring specs so they feel physical rather than tweened.

The holographic shimmer uses three optical layers: thin-film iridescence, specular reflection, and Fresnel edge glow. On Android 13+ this runs as an AGSL shader. On iOS and older Android, a gradient brush approximation does the job. Tilt data comes from the accelerometer, smoothed with springs to avoid jitter.

Die-cut borders use a stamp technique: draw the sticker 32 times at small offsets with a white tint, then draw the real sticker on top. Looks like a real die-cut vinyl sticker.

Three `expect`/`actual` boundaries: tilt sensor, haptic feedback, and the holographic shader node. That's all the platform code.

## Project structure

```
composeApp/
  src/
    commonMain/       # Shared UI and logic
      App.kt          # Root composable
      StickerCanvas.kt # Canvas with draggable stickers
      StickerTray.kt  # Bottom sheet sticker picker
      ShimmerGlow.kt  # Tilt-based shimmer modifier
      model/          # Sticker data models
      sensor/         # TiltSensor expect declarations
      haptics/        # HapticFeedback expect declarations
    androidMain/      # Android implementations (sensor, haptics, AGSL shader)
    iosMain/          # iOS implementations (CMMotionManager, UIFeedbackGenerator)
iosApp/               # iOS app entry point (SwiftUI wrapper)
```

## Building

Android:
```bash
./gradlew :composeApp:installDebug
```

iOS: open `iosApp/iosApp.xcodeproj` in Xcode, pick a simulator, hit Run.

## Requirements

- Android Studio Ladybug or later
- Kotlin 2.1+
- JDK 17+
- Xcode 15+ (for iOS)

## License

This project is provided as a sample/demo.

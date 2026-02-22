# StickerExplode

A sticker canvas app built with Compose Multiplatform. Place, drag, rotate, pinch-to-scale, and peel off stickers with spring physics, holographic shimmer, and haptic feedback. Runs on Android and iOS from shared Kotlin code.

<p align="center">
  <img src="assets/sticker_demo.gif" width="360" alt="Demo"/>
</p>

## Screenshots

| Canvas | Sticker tray | Drag + rotate |
|--------|-------------|---------------|
| <img src="assets/sticker_canvas.jpg" width="240"/> | <img src="assets/sticker_tray.jpg" width="240"/> | <img src="assets/drag_demo.jpg" width="240"/> |

## Inspiration

Apple's WWDC sticker animations. The way stickers peel off, tilt toward your finger, and cast shadows when you grab them. I wanted to see how close I could get in Compose, and whether the same code could run on iOS too.

## Blog series

I wrote a three-part series walking through how everything works:

1. [Part 1: Gestures, physics, and making stickers feel real](https://aditlal.dev/building-stickerexplode-part-1-gestures-physics-and-making-stickers-feel-real/) - architecture, data model, gesture system, spring physics
2. [Part 2: The peel-off effect and holographic shimmer](https://aditlal.dev/stickerexplode-part-2/) - four simultaneous peel animations, AGSL shader for iridescence, tilt-reactive shimmer with iOS fallback
3. [Part 3: The full end-to-end build](https://aditlal.dev/stickerexplode-part-3/) - die-cut outlines (stamp technique), tilt sensors, haptics, and the three expect/actual boundaries

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

Three `pointerInput` blocks handle drag, transform, and tap separately. The peel-off runs four animations at once, all on spring specs so they feel physical rather than tweened:

```
Peel-off grab

  at rest        on grab                on release
  +------+         +--------+            +------+
  |      |  -->  ,-|  1.08x |-,  -->     |      |
  | 1.0x |      /  |  tilted | \  spring | 1.0x |
  +------+     '   +--------+  ' back    +------+
  no shadow    tilt toward finger         no shadow
               shadow grows underneath
```

The holographic shimmer uses three optical layers: thin-film iridescence, specular reflection, and Fresnel edge glow. On Android 13+ this runs as an AGSL shader. On iOS and older Android, a gradient brush approximation gets close enough. Tilt data comes from the accelerometer, smoothed with springs to avoid jitter.

Die-cut borders use a stamp technique: draw the sticker image 32 times at tiny offsets, all tinted white, so they merge into one fat outline. Then draw the real sticker centered on top. Ends up looking like a vinyl die-cut.

## Platform boundaries

Only three `expect`/`actual` declarations in the whole project: tilt sensor, haptic feedback, and the holographic shader node.

That's it. Gestures, animations, the canvas, the peel-off, die-cut borders -- all shared Kotlin.

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

## Acknowledgments

README written with help from [Claude](https://claude.ai).

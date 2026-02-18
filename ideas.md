# StickerExplode — Feature Ideas

## Current State
- **Canvas Screen** — drag, pinch-zoom, rotate stickers with peel-off animations, holographic shine, haptics
- **History Screen** — chronological log of added stickers
- **16 sticker types**, full state persistence via DataStore

---

## High Impact / Fun

### 1. Delete Stickers
- Long-press a sticker to enter "delete mode" with a visual shake/wiggle animation (iOS-style)
- Show a small X badge on the sticker corner
- On confirm, play a crumple/explosion particle animation before removing
- Add haptic feedback (HeavyImpact) on delete
- Update history with a "removed" entry so users can see what was deleted

### 2. Sticker Packs / Categories
- Organize the 16+ stickers into themed tabs: Dev Tools, Emoji, Text, Shapes
- Each pack has a distinct color accent in the tray
- Allow users to "favorite" stickers for quick access in a pinned row at the top of the tray
- Lay groundwork for downloadable/unlockable packs in the future
- Show pack icon + sticker count badge on each tab

### 3. Export / Share Canvas
- Capture the entire canvas as a high-res PNG (2x or 3x density)
- Crop to the bounding box of all stickers with padding, or export full canvas
- Open the native share sheet (Android ShareSheet / iOS UIActivityViewController)
- Option to export with or without the background color
- Add a "Made with StickerExplode" watermark toggle
- Support copying to clipboard as an alternative to sharing

### 4. Custom Text Stickers
- Tap "Add Text" in the tray to open a text editor overlay
- Type custom text with a live preview on canvas
- Pick from 5-6 curated fonts (bold, handwritten, monospace, serif, rounded)
- Color picker with preset palette + custom hex input
- Apply the same die-cut outline and holographic shine as other stickers
- Text stickers persist and are editable on double-tap

### 5. Sticker Search
- Add a search bar at the top of the sticker tray
- Filter stickers by name/label as user types
- Show "no results" state with suggestion to create a custom text sticker
- Remember recently used stickers and surface them first

---

## Visual Polish

### 6. Dark Mode Canvas
- Toggle between light (#F2F2F7) and dark (#1C1C1E) canvas backgrounds
- Adjust shadow rendering: lighter shadow on dark, darker on light
- Holographic shine intensity increases slightly on dark backgrounds for pop
- Persist the preference in DataStore
- Animate the background transition with a smooth crossfade
- Respect system dark mode setting as default, with manual override

### 7. Sticker Lock
- Single-tap a sticker, then tap a lock icon in a mini toolbar that appears
- Locked stickers ignore drag/pinch/rotate gestures
- Show a subtle lock badge overlay on locked stickers
- Locked stickers still respond to single-tap to unlock
- Useful for building up compositions without accidentally disturbing placed stickers
- Lock state persists across app restarts

### 8. Canvas Zoom & Pan
- Pinch on the empty canvas background to zoom in/out (0.5x to 3x)
- Two-finger drag on background to pan the viewport
- Show a minimap in the corner when zoomed in beyond 1.5x
- Double-tap on background to reset to 1x zoom
- Sticker positions remain absolute; only the viewport moves
- Enables much larger compositions than the screen size allows

### 9. Undo/Redo
- Maintain a command stack (max 50 actions) tracking: add, delete, move, resize, rotate
- Undo button in a floating toolbar or top bar
- Redo button appears after an undo
- Shake-to-undo gesture support on both platforms
- Stack resets on app relaunch (session-only, not persisted)
- Each undo/redo triggers a subtle haptic pulse

### 10. Sticker Outline / Border Customization
- After placing a sticker, tap to select and choose an outline style
- Options: none, thin white (current default), thick white, colored border, dashed
- Border color picker synced with text sticker color palette
- Border width slider (1dp to 6dp)
- Applies per-sticker, persisted in StickerItem data class

---

## Social / Sharing

### 11. Animated Stickers
- Introduce a new sticker category with looping animations (sparkle, bounce, pulse, spin)
- Use Compose animation APIs (infiniteTransition) for lightweight effects
- Animated stickers show a small play badge in the tray
- Animations pause when sticker is being dragged for performance
- Export as GIF or APNG when sharing (static PNG fallback)

### 12. Canvas Templates
- Offer 4-5 pre-made layouts on a "New Canvas" screen
- Templates: "WWDC Style" (scattered dev stickers), "Birthday" (party emoji arrangement), "Blank", "Grid", "Circle"
- Each template pre-places stickers with curated positions, rotations, and scales
- Users can modify everything after loading a template
- Save custom arrangements as user templates for reuse

### 13. Duplicate Sticker
- Long-press + drag to clone a sticker (ghost preview shows during drag)
- The duplicate spawns at a slight offset (+20dp, +20dp) from the original
- Inherits the same scale and rotation as the source
- Gets a new unique ID and z-index (placed on top)
- Useful for creating patterns or symmetrical compositions
- Add to history as a "duplicated" entry

### 14. Collaborative Canvas (Future)
- Real-time multiplayer canvas via WebSocket or Firebase Realtime DB
- Share a canvas link/code with friends
- See other users' cursors and sticker placements live
- Each user gets a color-coded cursor
- Conflict resolution: last-write-wins per sticker ID
- Chat overlay for coordination

---

## Technical

### 15. Multi-canvas / Pages
- Home screen showing a grid of saved canvases with thumbnail previews
- Create new canvas, rename, delete existing ones
- Each canvas has its own independent state (stickers, history, settings)
- Swipe between canvases or use a page indicator
- Default "My Canvas" created on first launch (current behavior)
- Canvas metadata: name, created date, last modified, sticker count

### 16. Sticker Layering UI
- Slide-out panel showing all stickers as a vertical list ordered by z-index
- Drag to reorder layers (updates z-index accordingly)
- Tap an item to highlight/select the sticker on canvas
- Eye icon to toggle sticker visibility (hide without deleting)
- Swipe-to-delete from the layer panel as an alternative delete method
- Shows sticker type icon + label + mini position info

### 17. Import from Photos
- "Import" button in the sticker tray opens the system photo picker
- Selected image is cropped to a rounded rectangle or custom shape
- Apply the die-cut white outline effect automatically
- Resize to a reasonable default (150dp) with pinch-zoom after placement
- Store imported images as Base64 in DataStore (small stickers only) or file references
- Support both camera capture and gallery selection

### 18. Performance Profiling & Optimization
- Lazy rendering: only compose stickers visible in the current viewport
- Reduce recomposition scope by extracting stable keys and using derivedStateOf
- Profile with ComposeProof recomposition tracking to identify hotspots
- Target 60fps during drag with 20+ stickers on screen
- Consider canvas-based rendering (DrawScope) for stickers beyond a threshold count

# Android Floating Overlay Manager

A clean and reusable Kotlin-based **Overlay Manager** that supports drawing views over other apps using **WindowManager** and **Material 3 components** — fully compatible from **Android 6 (API 23) to Android 15+**.

> 💬 Ideal for chat bubbles, floating widgets, developer tools, and system-wide UI enhancements.

---

## ✨ Features

* ✅ Overlay permission handling using `ActivityResultContract`
* ✅ Clean and testable `OverlayManager` class
* ✅ Bounce animation with `OvershootInterpolator`
* ✅ Drag-and-drop repositioning support
* ✅ Material 3-ready UI
* ✅ API-safe: handles both `TYPE_PHONE` and `TYPE_APPLICATION_OVERLAY`
* ✅ No duplicate overlays (safe lifecycle)

---

## 📉 Tech Stack

* Kotlin (idiomatic)
* ViewBinding
* Material 3
* WindowManager
* Custom `ActivityResultContract`

---

## 🚀 Usage

```kotlin
val overlayManager = OverlayManager(this, this, activityResultRegistry)
overlayManager.requestOverlayPermission { isGranted ->
    if (isGranted) {
        val binding = ViewFloatingBinding.inflate(layoutInflater)
        overlayManager.showOverlayView(binding)
    }
}
```

---

## 📸 Preview

*Add a short GIF or image showing the floating overlay in action*

---

## 🛎️ Roadmap

* [x] Basic floating view with drag
* [x] Overlay permission launcher with ActivityResult API
* [x] Bounce-in animation
* [ ] Animated fade-out on close
* [ ] Foreground service integration
* [ ] Dynamic updates via ViewModel channel

---

## 🤖 Author

**Sohaib** — Android Tech Lead | Modularization Advocate | Clean Code Champion

Connect on [LinkedIn](https://linkedin.com) or check out other projects!

---

## ☕ Support

Contributions are welcome! Feel free to:

* ✨ Star this repo
* 📄 Fork and expand it
* ❓ Open issues/feature requests

---

## ℹ️ License

MIT License. Use it freely, but don't forget to give credit where it's due ✨

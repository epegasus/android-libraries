## StraightenWheelView

Lightweight Android library: a **horizontal straightening wheel** (`StraightenWheelView`) for picking an angle around **center (zero)** with drag, fling, optional snap-to-marks, **snap-to-zero** with haptic feedback, and **hard end stops** (no wrap past the limits when end lock is on).

### Features

- **Single custom view** — `com.sohaib.wheelview.StraightenWheelView`
- **Gestures** — drag and fling; settles with deceleration
- **End lock (default)** — linear range **−2π … +2π** radians (clamped), like **−1 … 0 … +1** with borders; **`sw_endLock=false`** restores infinite wrap every **2π**
- **Snap to zero** — optional (default on): near center, snaps to **0** and triggers vibration / haptic (`VIBRATE` permission recommended in the app)
- **Snap to marks** — optional tick alignment after gesture
- **Styled via XML** — tick count, colors, active range, etc. (see table below)
- **Programmatic API** — radians, degrees, normalized fraction `completeTurnFraction`, `totalSpinnerRotation` scale

### Gradle setup (module names)

| Module        | Role                          |
|---------------|-------------------------------|
| **`:wheel`**  | Library (`com.sohaib.wheelview`) |
| **`:app`**    | Demo (`com.sohaib.wheelview.demo`) |

In your **app** module:

```kotlin
dependencies {
    implementation(project(":wheel"))
}
```

**Haptics:** declare vibration in the **app** manifest if you use snap-to-zero (included in the demo):

```xml
<uses-permission android:name="android.permission.VIBRATE" />
```

### Basic usage

#### 1. Add the view in XML

Use the `app` namespace for custom attributes (`xmlns:app="http://schemas.android.com/apk/res-auto"`).

```xml
<com.sohaib.wheelview.StraightenWheelView
    android:id="@+id/wheelView"
    android:layout_width="0dp"
    android:layout_height="80dp"
    android:layout_marginHorizontal="40dp"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintBottom_toBottomOf="parent"
    app:sw_activeColor="@color/red"
    app:sw_endLock="true"
    app:sw_snapToZero="true" />
```

#### 2. Listen for angle changes

```kotlin
import com.sohaib.wheelview.StraightenWheelView

binding.wheelView.setListener(object : StraightenWheelView.Listener() {
    override fun onRotationChanged(radians: Double, degreesAngle: Double) {
        // degreesAngle = radians * (totalSpinnerRotation / π)
        binding.angleLabel.text = String.format(Locale.US, "%.0f°", degreesAngle)
    }
})
```

#### 3. Configure scale (degrees vs radians)

`totalSpinnerRotation` maps one **π radians** of travel to this many **degrees** on your readout (default in code is **180**° per π rad if unset). Example for a **±60°**-style span over **±2π** rad (with end lock):

```kotlin
// 2π rad ↔ 60° total span → 30° per π rad
binding.wheelView.totalSpinnerRotation = 30f
```

#### 4. Programmatic value

```kotlin
binding.wheelView.degreesAngle = 15.0
// or
binding.wheelView.radiansAngle = someRadians
```

### XML attributes (`StraightenWheelView`)

| Attribute | Format | Description |
|-----------|--------|-------------|
| `sw_marksCount` | integer | Number of ticks (default **90**) |
| `sw_normalColor` | color | Inactive tick color |
| `sw_activeColor` | color | Center cursor / emphasis color |
| `sw_endLock` | boolean | **true** = clamp to travel range (default); **false** = wrap every 2π |
| `sw_snapToMarks` | boolean | Snap to nearest tick on release / fling end |
| `sw_showActiveRange` | boolean | Highlight active segment along the ruler |
| `sw_onlyPositiveValues` | boolean | **true** = travel **[0, 2π]** only |
| `sw_snapToZero` | boolean | Snap to center + haptic when near 0 (default **true**) |

### Demo app

The **`:app`** module showcases a labeled angle, image rotation tied to `degreesAngle`, and wheel styling. See `MainActivity.kt` and `activity_main.xml`.

### Video

https://github.com/user-attachments/assets/1f0fb01c-2af0-4517-a540-6dec2fd0fae9


---

## ⭐ Please Support This Project

If this project helped you, please give us a star on GitHub. 🙏🔥

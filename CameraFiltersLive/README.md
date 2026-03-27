# Live Camera Filters

Lightweight Android project to preview camera in real time and apply live GPU filters using a reusable `CameraView` module.

## Features

- **Live camera preview** with Camera2-backed custom `CameraView`
- **Real-time filter switching** (No filter, Sepia, Vignette, Hue, Contrast, and more)
- **Photo capture** from the preview stream
- **Front/back camera toggle** at runtime
- **Optional frame processing** hook for per-frame analysis pipelines
- **Gesture-ready camera controls** in the reusable library (zoom/focus/exposure hooks)

## Gradle Setup (Module Names)

- App module: `:app`
- Library module: `:cameraview`

In your app module:

```gradle
dependencies {
    implementation(project(":cameraview"))
}
```

## Basic Usage

### 1) Add `CameraView` in XML

```xml
<com.sohaib.cameraview.CameraView
    android:id="@+id/cameraView"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

### 2) Initialize and open camera

```kotlin
binding.cameraView.apply {
    setLifecycleOwner(this@CameraViewActivity)
    addCameraListener(object : CameraListener() {})
    open()
}
```

### 3) Apply a live filter

```kotlin
val filter = Filters.SEPIA.newInstance()
binding.cameraView.filter = filter
```

### 4) Capture picture

```kotlin
binding.cameraView.takePicture()
```

## Demo App

The `:app` module (`com.sohaib.cameraview.demo`) demonstrates:

- Runtime camera permission flow
- Live preview + filter cycling button
- Capture image button
- Front/back camera switch

Main demo screen: `CameraViewActivity`

## Permissions

Declared in demo app:

- `android.permission.CAMERA`
- `android.permission.RECORD_AUDIO`
- `android.permission.WRITE_EXTERNAL_STORAGE` (legacy declaration)

## Build and Run

### Android Studio

1. Open this project in Android Studio.
2. Select `app` configuration.
3. Run on a physical device (recommended for camera testing).

### Command line (Windows)

```bash
gradlew.bat assembleDebug
gradlew.bat installDebug
```

## Tech Stack

- Android Camera2
- Kotlin + Java
- ViewBinding + DataBinding
- Material Components
- OpenGL helper: `com.otaliastudios.opengl:egloo`

## Project Structure

```text
LiveCameraFilters/
├── app/         # Demo application
└── cameraview/  # Reusable camera + filter library
```

## Screenshots / GIFs

Add preview media here:

- `docs/images/camera-preview.png` (main camera screen)
- `docs/images/filter-cycling.gif` (switching filters in real time)
- `docs/images/capture-result.png` (captured photo sample)

Markdown placeholders:

```md
![Camera Preview](docs/images/camera-preview.png)
![Filter Cycling](docs/images/filter-cycling.gif)
![Capture Result](docs/images/capture-result.png)
```

## Available Filters

The app cycles these filters from `Filters.java`:

| Enum Key | Display Name |
| --- | --- |
| `NONE` | No Filter |
| `AUTO_FIX` | Auto Fix |
| `BLACK_AND_WHITE` | Black And White |
| `BRIGHTNESS` | Brightness |
| `CONTRAST` | Contrast |
| `CROSS_PROCESS` | Cross Process |
| `DOCUMENTARY` | Documentary |
| `DUOTONE` | Duotone |
| `FILL_LIGHT` | Fill Light |
| `GAMMA` | Gamma |
| `GRAIN` | Grain |
| `GRAYSCALE` | Grayscale |
| `HUE` | Hue |
| `INVERT_COLORS` | Invert Colors |
| `LOMOISH` | Lomoish |
| `POSTERIZE` | Posterize |
| `SATURATION` | Saturation |
| `SEPIA` | Sepia |
| `SHARPNESS` | Sharpness |
| `TEMPERATURE` | Temperature |
| `TINT` | Tint |
| `VIGNETTE` | Vignette |

## Contributing

Contributions are welcome.

1. Fork the repo.
2. Create a feature branch.
3. Make your changes with clear commit messages.
4. Open a pull request with a short demo (GIF/screenshot) if UI behavior changed.

If you are adding a new filter, also update:

- `cameraview/.../dev_filter/Filters.java`
- Demo UI flow in `CameraViewActivity`
- This README filter table

## ⭐ Please Support This Project

If this project helped you, please give it a star on GitHub. 🙏🔥

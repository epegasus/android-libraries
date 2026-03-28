# Material Carousel Slider

Sample Android app that demonstrates a horizontal image carousel using [Material Components](https://github.com/material-components/material-components-android) `CarouselLayoutManager`, `CarouselSnapHelper`, and `MaskableFrameLayout` items.

## Screenshot

<img src="screenshots/img.png" alt="Material carousel demo" width="300" />

## What it shows

- **RecyclerView** with `CarouselLayoutManager` for the carousel layout and peeking adjacent items.
- **CarouselSnapHelper** so items settle on discrete positions when scrolling ends.
- **MaskableFrameLayout** per item (see `item_carousel.xml`) with a listener that adjusts title position and alpha as the visible mask changes.

## Requirements

- Android Studio with AGP compatible with this project (see `gradle/libs.versions.toml`).
- **minSdk 24**, **targetSdk / compileSdk 36** (see `app/build.gradle.kts`).
- Kotlin, Java 17.

## Run the sample

1. Open the `MaterialCarouselSlider` directory in Android Studio (or the parent repo and select this module).
2. Sync Gradle, then run the **app** configuration on a device or emulator.

## Project layout

| Path | Role |
|------|------|
| `app/.../MainActivity.kt` | Sets up the carousel, adapter, and snap helper. |
| `app/.../AdapterImages.kt` | `ListAdapter` binding drawables and mask-driven title motion. |
| `app/src/main/res/layout/activity_main.xml` | Host `RecyclerView` with `CarouselLayoutManager`. |
| `app/src/main/res/layout/item_carousel.xml` | Single carousel cell layout. |

## License

See the [LICENSE](../LICENSE) file at the repository root.

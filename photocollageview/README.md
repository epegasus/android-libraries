## Photo Collage View (Sohaib)

Lightweight Android library to display and edit photo collages using a single custom view.

### Features

- **Single custom view** `com.sohaib.collageview.CollageView`
- **Multiple layouts** (2, 3, 4, 6 image shapes) selectable at runtime
- **Flexible inputs**: URIs, drawables, bitmaps, or resource ids
- **Gesture support**: pinch‑to‑zoom and drag per cell
- **Save to gallery** using the provided `ImageUtil`

### Gradle setup (module names)

- App module: `:app`
- Library module: `:collageview`

In your app module:

```kotlin
dependencies {
    implementation(project(":collageview"))
}
```

### Basic usage

#### 1. Add the view in XML

```xml
<com.sohaib.collageview.CollageView
    android:id="@+id/collageView"
    android:layout_width="match_parent"
    android:layout_height="0dp"
    android:background="@android:color/white"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintBottom_toBottomOf="parent" />
```

#### 2. Provide images

```kotlin
// From URIs
binding.collageView.setImagesFromUris(listOf(uri1, uri2, uri3))

// From drawable resources
binding.collageView.setImagesFromResources(
    listOf(R.drawable.image_1, R.drawable.image_2, R.drawable.image_3)
)
```

#### 3. Switch layouts (shapes)

```kotlin
binding.collageView.currentLayoutType =
    CollageViewFactory.CollageLayoutType.FOUR_IMAGE_0
```

You can expose these layouts in a `RecyclerView` using your own model (icon + `CollageLayoutType`) as done in the demo app.

#### 4. Save collage to gallery

```kotlin
ImageUtil().saveViewToGallery(
    activity = this,
    view = binding.collageView,
    listener = object : ImageUtil.ImageSavedListener {
        override fun onCollageSavedToGallery(isSaveSuccessful: Boolean, uri: Uri?) { /* ... */ }
        override fun onReadyToShareImage(uri: Uri?) { /* ... */ }
    }
)
```

### Demo app

The `:app` module (`com.sohaib.collageview.demo`) showcases:

- Shape picker with your collage icons in a horizontal `RecyclerView`
- Last layout selected by default
- Save‑to‑gallery flow with a Material confirmation dialog

### Video

A short video demo of:

- Selecting layouts from the icon list
- Zooming and panning individual cells
- Saving the collage to the gallery

is added in this section.


https://github.com/user-attachments/assets/a546cf5d-43d9-440b-bfdc-35b7d974d87c


---

## ⭐ Please Support This Project

If this project helped you, please give us a star on GitHub. 🙏🔥
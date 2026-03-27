# Story Viewer

A reusable Android library that provides an Instagram-like **story viewer** as a dialog with progress bars, swipe/click navigation, and optional pull-to-dismiss behavior.

---

## Features

- **`StoryView`** dialog-based story player
- **Progress indicators** with per-story duration control
- **Tap navigation** (previous/next) with **long-press pause/resume**
- **Swipe support** via `ViewPager2`
- **Optional story header** (title, subtitle, avatar)
- **Description support** per story item
- **RTL support**
- **Pull-to-dismiss**
- **Material-friendly UI** and easy theming

---

## Screenshot

![Screen_recording](https://github.com/user-attachments/assets/d6341e8d-740f-45f8-921a-e8992cbba482)

---

## Requirements

- **minSdk:** 23
- **compileSdk:** 36 (or match your app)
- **Java:** 17
- Host should be a **`FragmentActivity`** (e.g. `AppCompatActivity`)

---

## Installation (JitPack)

### 1) Add JitPack repository (project-level `settings.gradle`)

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### 2) Add dependency (app-level `build.gradle`)

```gradle
dependencies {
    implementation 'com.github.epegasus:Story-Viewer:latest.release'
}
```

Use the latest version from JitPack:
[https://jitpack.io/#epegasus/Story-Viewer](https://jitpack.io/#epegasus/Story-Viewer)

---

## Quick Start (Kotlin)

```kotlin
val stories = arrayListOf(
    MyStory(
        url = "https://images.pexels.com/photos/36029/aroni-arsa-children-little.jpg?auto=compress&cs=tinysrgb&dpr=1&w=500",
        description = "First story"
    ),
    MyStory(
        url = "https://media.istockphoto.com/id/517188688/photo/mountain-landscape.jpg?s=1024x1024&w=0&k=20&c=z8_rWaI8x4zApNEEG9DnWlGXyDIXe-OmsAyQ5fGPVV8=",
        description = "Second story"
    )
)

StoryView.Builder(supportFragmentManager)
    .setStoriesList(stories)
    .setStoryDuration(3000L)
    .setHeaderTitleText("Sohaib")
    .setHeaderSubtitleText("Just now")
    .setHeaderTitleLogoUrl("https://images.pexels.com/photos/36029/aroni-arsa-children-little.jpg?auto=compress&cs=tinysrgb&dpr=1&w=200")
    .build()
    .show()
```

---

## Public API (Builder)

- `setStoriesList(ArrayList<MyStory>)`
- `setStoryDuration(Long)`
- `setStartingIndex(Int)`
- `setHeaderTitleText(String?)`
- `setHeaderSubtitleText(String?)`
- `setHeaderTitleLogoUrl(String?)`
- `setHeadingInfoList(ArrayList<HeaderInfo>)`
- `setOnStoryClickListener(OnStoryClickListener?)`
- `setOnStoryChangeListener(OnStoryChangeListener?)`
- `setRtl(Boolean)`
- `build()`
- `show()`
- `dismiss()`

---

## Data Models

```kotlin
data class MyStory(
    val url: String?,
    val description: String? = null,
    val date: Date? = null
)
```

`HeaderInfo` supports title, subtitle, and title icon URL for the top header.

---

## Notes

- Ensure your app has internet permission:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

- Use reachable image URLs (HTTP fetch failures will skip to next story).

---

## ⭐ Please Support This Project

If this library helps your project, please consider:

- Giving this repo a **star** on GitHub
- Sharing it with your team/community
- Opening issues or PRs for improvements

Support link:
[https://github.com/epegasus/android-libraries](https://github.com/epegasus/android-libraries)

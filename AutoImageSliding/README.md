# AutoImageScroller

`AutoImageScroller` is an Android custom view library that renders a continuous, auto-scrolling horizontal image strip.

It can be used for animated backdrops, hero sections, or looping scenic banners in Android screens.

## Modules

- `:app` - Demo application
- `:autoimagescroller` - Library module

## Features

- Smooth frame-based horizontal auto-scroll
- Configurable `speed`
- Deterministic or random sequencing (`contiguous` / `randomness`)
- Supports a scene length (`sceneLength`) to control loop complexity
- Start state control (`initialState`)

## Usage

Add the view to your layout:

```xml
<com.sohaib.imagescroller.AutoImageScroller
    android:id="@+id/scrolling_background"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:imageId="@drawable/img_dummy"
    app:speed="60dp"
    app:contiguous="false" />
```

## XML Attributes

- `app:imageId` (`reference`) - Single drawable or drawable array reference
- `app:speed` (`dimension`) - Scroll speed in dp/sec
- `app:sceneLength` (`integer`) - Number of scene slots
- `app:randomness` (`reference`) - Int-array used as weighting per image
- `app:contiguous` (`boolean`) - Use ordered sequence instead of random
- `app:initialState` (`enum`) - `started` or `stopped`

## Demo

Original demo video:
https://user-images.githubusercontent.com/100923337/216246451-e3951c37-463d-4be4-8f10-bdbe7be006b9.mp4


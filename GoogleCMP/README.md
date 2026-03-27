# CMP

Sample Android app that integrates **Google’s User Messaging Platform (UMP)** for consent management (GDPR / privacy messaging) ahead of loading ads via **Google Mobile Ads**.

## Requirements

- **minSdk** 24 · **compileSdk** 36  
- [Play services Ads](https://developers.google.com/admob/android/quick-start) (`com.google.android.gms:play-services-ads`) — this project uses it via Version Catalog (see `gradle/libs.versions.toml`).

## Quick integration

### 1. Create a controller

Use an `Activity` context (same as the sample):

```kotlin
private val consentController by lazy { ConsentController(this) }
```

### 2. Initialize consent and handle callbacks

Pass your **debug test device hashed ID** only for debug builds (see [UMP debug geography](https://developers.google.com/admob/android/privacy/gdpr)); release builds ignore the debug-only wiring inside `ConsentController` and use standard request parameters.

```kotlin
consentController.initConsent("YOUR_DEBUG_DEVICE_HASHED_ID", object : ConsentCallback {
    override fun onConsentFormLoaded() {
        consentController.showConsentForm()
    }

    override fun onConsentFormDismissed() {
        // Form closed
    }

    override fun onAdsLoad(canRequestAd: Boolean) {
        if (canRequestAd) {
            // Load ads when allowed
        }
    }

    override fun onPolicyStatus(required: Boolean) {
        // e.g. enable “Privacy options” entry when required
    }
})
```

### 3. Check before loading ads

```kotlin
if (consentController.canRequestAds) {
    // Safe to request ads
}
```

### Optional: privacy options form

Open Google’s privacy options UI when required (see `MainActivity` for a button-driven example):

```kotlin
consentController.launchPrivacyForm { /* optional completion handler */ }
```

## Sample app

The runnable demo lives under `app/` — see `MainActivity` for a full flow with banner loading.

## Sample video

https://github.com/epegasus/CMP/assets/100923337/de02c989-a53e-42a0-af52-c73b6f560f5b

---

## Support this project

If this repo helps you ship consent and ads correctly, **please star it on GitHub** — it helps others discover the project and keeps maintenance sustainable. Thank you.

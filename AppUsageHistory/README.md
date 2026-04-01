# App Usage History

Android app that requests Usage Access permission and shows app usage data in two tabs using `ViewPager2`.

## Features

- Requests and validates Usage Access permission (`PACKAGE_USAGE_STATS`).
- Shows tabs only after permission is granted and device user is unlocked.
- Tab 1: recent usage events (latest first).
- Tab 2: installed launcher apps.
- Filters to launcher-visible apps (apps shown in home launcher).
- Supports optional system-app inclusion via API flags (default `false`).

## Architecture

- `MainActivity`: permission flow + tab setup.
- `UsageEventsFragment`: displays recent usage events list.
- `NonSystemAppsFragment`: displays installed apps list.
- `AppUsageManager`: central data/permission manager with clean APIs.

## AppUsageManager API

- `isPermissionGranted(): Boolean`
- `isUserUnlocked(): Boolean`
- `getUsageAccessSettingsIntent(): Intent`
- `getRecentUsageEvents(lastMinutes: Int = 10, includeSystemApps: Boolean = false): List<String>`
- `getInstalledApps(includeSystemApps: Boolean = false): List<String>`

## Notes

- Recent events are sorted by latest timestamp first.
- Duplicate visible rows are removed from usage events output.
- Installed apps list includes launcher entries and is alphabetically sorted.

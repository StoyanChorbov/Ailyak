# Ailyak

Android survival assistant focused on field awareness, nearby resources, and offline-friendly guidance.

## What It Does

Ailyak combines a live map, location-based resource discovery, and an in-app survival guide:

- Interactive map with user location puck and optional data overlays
- Nearby plants/mushrooms with safety hints and filters
- Nearby animals with taxonomy grouping and risk signal (IUCN category)
- Nearby water sources and shelters from OpenStreetMap/Overpass
- Cellular tower coverage overlays and nearby tower list (OpenCelliD)
- Built-in Survival Guide with step-by-step illustrated field procedures
- Settings for map style, tracking, radius, and emergency contact details

## Main Features

### 1) Bottom Navigation Experience

Current bottom tabs:

- `Guide`
- `Plants`
- `Water`
- `Map` (start destination, centered)
- `Animals`
- `Settings`

Navigation uses state restoration (`saveState`/`restoreState`) and single-top behavior to preserve each tab state.

### 2) Map Screen

The map (`Mapbox`) supports:

- User location puck (bearing-aware)
- Path polyline from locally stored path points
- Connection-lost marker from path history
- Toggleable overlays:
  - Water source markers
  - Shelter markers
  - Cellular coverage/tower circles
- Offline warning banner when connectivity is lost
- Area download action button on selected map point (triggers data prefetch methods)
- Emergency floating button with quick action dialog

### 3) Plants Screen

- Loads species near current location
- Pulls both `Plantae` and `Fungi`
- Search bar (scientific/common names)
- Type filters: all/plants/mushrooms
- Safety filters: edible/caution/toxic/unknown
- Expandable cards with summary loading
- Room cache with TTL and stale fallback on network failure

### 4) Water Screen

- Loads nearby water-related POIs via Overpass query
- Sorts by distance
- Displays source type and drinkability inference
- Room cache with TTL and stale fallback

### 5) Animals Screen

- Loads nearby GBIF occurrences
- Search and group filters (mammals, birds, reptiles, etc.)
- Distance-based listing with expandable details
- IUCN category parsing where available

### 6) Survival Guide

The guide is fully integrated in the `Guide` tab with menu + detail flow.

Sections included:

- Firecraft and warmth
- Water procurement
- Emergency shelter
- First aid and hazards
- Signaling and rescue

Each section includes multiple step-by-step items with matching images from `res/drawable` (`fire*`, `water*`, `shelter*`, `aid*`, `signal*`) and text from `strings.xml`.

### 7) Settings

- Map style selection (`outdoors`, `satellite`, `streets`, `light`, `dark`)
- Battery saver toggle
- Tracking interval slider
- Search radius slider
- Connection and GPS alert toggles
- Emergency contact name/phone fields

Emergency contact values are stored through encrypted preferences helpers.

## Data Sources

- iNaturalist API: nearby plants/fungi
- GBIF API: nearby animals
- Overpass API (OpenStreetMap): water + shelter points
- OpenCelliD API: nearby cell tower metadata
- Local Android location services: current device location

## Tech Stack

- Kotlin + Jetpack Compose + Material 3
- Navigation Compose
- Hilt (DI)
- Retrofit + OkHttp
- Room (local cache)
- DataStore (preferences)
- Mapbox Compose SDK
- Google Play Services Location

## Requirements

- Android Studio (latest stable recommended)
- JDK 17
- Android SDK (minSdk 26)
- A device/emulator with location services enabled
- Internet connection for live API data (cached fallback exists for some modules)

## Getting Started

### 1) Clone and Open

```powershell
git clone <your-repo-url>
cd Ailyak
```

Open the `Ailyak` Gradle project in Android Studio.

### 2) Ensure `local.properties`

You need an Android SDK path in `Ailyak/local.properties`:

```ini
sdk.dir=C:\\Users\\<you>\\AppData\\Local\\Android\\Sdk
```

### 3) Build

```powershell
cd Ailyak
.\gradlew.bat :app:assembleDebug
```

### 4) Run

Use Android Studio Run, or via Gradle + adb:

```powershell
cd Ailyak
.\gradlew.bat :app:installDebug
adb shell am start -n aubg.hack.ailyak/.MainActivity
```

## How To Use the App

### First Launch

1. Start the app.
2. Grant location permissions when prompted.
3. Land on the `Map` tab.

### Map Workflow

1. Open `Map`.
2. Use the layer button (top-left) to toggle overlays.
3. Tap map to choose an area point.
4. Press the download floating action button (`⬇`) to prefetch nearby map-related datasets.
5. Use the Emergency button for quick emergency actions (currently dialog actions are UI placeholders).

### Guide Workflow

1. Open `Guide` tab.
2. Select a section.
3. Read item-by-item instructions with images.
4. Use `Back` to return from detail to section list.

### Plants Workflow

1. Open `Plants`.
2. Search by name.
3. Filter by type and safety level.
4. Expand a card for details and summary.

### Water Workflow

1. Open `Water`.
2. Review nearby points sorted by distance.
3. Check inferred drinkability/type before field decisions.

### Animals Workflow

1. Open `Animals`.
2. Search and apply group filters.
3. Expand cards for more info and conservation category.

### Settings Workflow

1. Open `Settings`.
2. Choose map style.
3. Adjust tracking interval and search radius.
4. Configure alert toggles.
5. Save emergency contact details.

## Permissions Used

- `INTERNET`
- `ACCESS_FINE_LOCATION`
- `ACCESS_COARSE_LOCATION`
- `FOREGROUND_SERVICE`
- `FOREGROUND_SERVICE_LOCATION`
- `POST_NOTIFICATIONS`

Without location permission, core location-driven features cannot load nearby resources.

## Project Structure

Key paths:

- `Ailyak/app/src/main/java/aubg/hack/ailyak/navigation/AppNavGraph.kt`
- `Ailyak/app/src/main/java/aubg/hack/ailyak/ui/screens/`
- `Ailyak/app/src/main/java/aubg/hack/ailyak/ui/survivalguide/`
- `Ailyak/app/src/main/java/aubg/hack/ailyak/viewmodel/`
- `Ailyak/app/src/main/java/aubg/hack/ailyak/ui/survivalguide/data/`
- `Ailyak/app/src/main/res/values/strings.xml`
- `Ailyak/app/src/main/res/drawable/`

## Notes and Current Limitations

- Shelter route exists in navigation graph but is not currently exposed as a bottom-nav tab.
- Emergency dialog actions are currently UI-only placeholders (no direct call/SMS dispatch yet).
- Cached fallback behavior is implemented for some repositories (plants/water/cell towers), while others are live-fetch only.
- A Mapbox access token resource is currently committed in app resources; production deployments should move secrets to secure config.

## License

See `LICENSE`.

package aubg.hack.ailyak.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel


/*
## Map Layer Toggles Handoff

The top-right menu toggles are implemented and persisted in `MapLayersViewModel`:

- `showPlantsFoodSources`
- `showWaterSources`
- `showWildLife`
- `showSignalNearby`

Current scope is UI/state only. No service/map wiring yet.

### Integration contract

- Source of truth: `MapLayersViewModel`
- Read states from: `MapLayersViewModel` in `SurvivalGuideRoute` (or shared screen-level owner)
- Toggle behavior:
  - ON => corresponding layer/data should be visible on map
  - OFF => corresponding layer/data should be hidden/cleared from map

### Next wiring points

1. **Service fetch trigger**
   - When a toggle becomes `true`, call the matching service/repository load.
   - When `false`, stop updates if needed and clear/hide map annotations for that layer.

2. **Map rendering**
   - Connect each bool to map layer visibility in `SurvivalMap` / map rendering pipeline.
   - Prefer idempotent updates (re-applying same state should not duplicate markers/layers).

3. **Ownership**
   - Keep `MapLayersViewModel` as the single state owner.
   - Avoid duplicating toggle state in composables.

### Suggested mapping

- `showPlantsFoodSources` -> plants/food POIs layer
- `showWaterSources` -> water points layer
- `showWildLife` -> wildlife layer
- `showSignalNearby` -> signal coverage/nearby signal markers layer

### Done criteria

- Toggling each switch updates map layer visibility in real time.
- No duplicate markers/layers after repeated toggles.
- Turning toggle OFF removes/hides corresponding map content.
- App still works with all toggles OFF / all toggles ON.

 */


class MapLayersViewModel : ViewModel() {
    var showPlantsFoodSources by mutableStateOf(false)
        private set

    var showWaterSources by mutableStateOf(false)
        private set

    var showWildLife by mutableStateOf(false)
        private set

    var showSignalNearby by mutableStateOf(false)
        private set

    fun setPlantsFoodSources(enabled: Boolean) {
        showPlantsFoodSources = enabled
    }

    fun setWaterSources(enabled: Boolean) {
        showWaterSources = enabled
    }

    fun setWildLife(enabled: Boolean) {
        showWildLife = enabled
    }

    fun setSignalNearby(enabled: Boolean) {
        showSignalNearby = enabled
    }
}


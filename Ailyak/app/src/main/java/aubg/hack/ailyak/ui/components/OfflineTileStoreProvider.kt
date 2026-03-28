package aubg.hack.ailyak.ui.components

import com.mapbox.common.TileStore

object OfflineTileStoreProvider {
    val tileStore: TileStore by lazy {
        TileStore.create()
    }
}
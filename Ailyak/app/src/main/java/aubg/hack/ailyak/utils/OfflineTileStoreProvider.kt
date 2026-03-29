package aubg.hack.ailyak.utils

import com.mapbox.common.TileStore

object OfflineTileStoreProvider {
    val tileStore: TileStore by lazy {
        TileStore.create()
    }
}
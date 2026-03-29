package aubg.hack.ailyak.service

import android.content.Context
import com.mapbox.common.TileStore

object OfflineTileStore {
    lateinit var tileStore: TileStore

    fun init(context: Context) {
        tileStore = TileStore.create(
            context.filesDir.resolve("mapbox_tiles").absolutePath
        )
    }
}
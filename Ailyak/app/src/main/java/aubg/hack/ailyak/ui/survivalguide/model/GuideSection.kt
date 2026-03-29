package aubg.hack.ailyak.ui.survivalguide.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class GuideSection(
    @param:StringRes val titleRes: Int,
    @param:StringRes val bodyRes: Int,
    @param:StringRes val photoOneLabelRes: Int,
    @param:StringRes val photoTwoLabelRes: Int,
    val imageNamePrefix: String? = null,
    val icon: ImageVector? = null,
    val items: List<GuideItem> = emptyList()
)

data class GuideItem(
    @param:StringRes val titleRes: Int,
    @param:StringRes val imageLabelRes: Int,
    val stepResList: List<Int>
)


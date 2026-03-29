package aubg.hack.ailyak.ui.survivalguide.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class GuideSection(
    @StringRes val titleRes: Int,
    @StringRes val bodyRes: Int,
    @StringRes val photoOneLabelRes: Int,
    @StringRes val photoTwoLabelRes: Int,
    val imageNamePrefix: String? = null,
    val icon: ImageVector? = null,
    val items: List<GuideItem> = emptyList()
)

data class GuideItem(
    @StringRes val titleRes: Int,
    @StringRes val imageLabelRes: Int,
    val stepResList: List<Int>
)


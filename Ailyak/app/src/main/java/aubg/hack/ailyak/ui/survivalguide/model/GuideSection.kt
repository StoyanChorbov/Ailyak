package aubg.hack.ailyak.ui.survivalguide.model

import androidx.annotation.StringRes

data class GuideSection(
    @param:StringRes val titleRes: Int,
    @param:StringRes val bodyRes: Int,
    @param:StringRes val photoOneLabelRes: Int,
    @param:StringRes val photoTwoLabelRes: Int
)


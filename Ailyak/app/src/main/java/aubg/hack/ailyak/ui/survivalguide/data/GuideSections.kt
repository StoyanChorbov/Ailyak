package aubg.hack.ailyak.ui.survivalguide.data

import aubg.hack.ailyak.R
import aubg.hack.ailyak.ui.survivalguide.model.GuideSection

val guideSections: List<GuideSection> = listOf(
    GuideSection(
        titleRes = R.string.section_firecraft_title,
        bodyRes = R.string.section_firecraft_body,
        photoOneLabelRes = R.string.section_firecraft_photo_one,
        photoTwoLabelRes = R.string.section_firecraft_photo_two
    ),
    GuideSection(
        titleRes = R.string.section_water_title,
        bodyRes = R.string.section_water_body,
        photoOneLabelRes = R.string.section_water_photo_one,
        photoTwoLabelRes = R.string.section_water_photo_two
    ),
    GuideSection(
        titleRes = R.string.section_shelter_title,
        bodyRes = R.string.section_shelter_body,
        photoOneLabelRes = R.string.section_shelter_photo_one,
        photoTwoLabelRes = R.string.section_shelter_photo_two
    ),
    GuideSection(
        titleRes = R.string.section_first_aid_title,
        bodyRes = R.string.section_first_aid_body,
        photoOneLabelRes = R.string.section_first_aid_photo_one,
        photoTwoLabelRes = R.string.section_first_aid_photo_two
    ),
    GuideSection(
        titleRes = R.string.section_signaling_title,
        bodyRes = R.string.section_signaling_body,
        photoOneLabelRes = R.string.section_signaling_photo_one,
        photoTwoLabelRes = R.string.section_signaling_photo_two
    )
)


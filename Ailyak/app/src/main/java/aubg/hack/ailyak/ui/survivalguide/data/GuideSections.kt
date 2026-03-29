package aubg.hack.ailyak.ui.survivalguide.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CellTower
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.Roofing
import androidx.compose.material.icons.outlined.WaterDrop
import aubg.hack.ailyak.R
import aubg.hack.ailyak.ui.survivalguide.model.GuideItem
import aubg.hack.ailyak.ui.survivalguide.model.GuideSection

val guideSections: List<GuideSection> = listOf(
    GuideSection(
        titleRes = R.string.section_firecraft_warmth_title,
        bodyRes = R.string.section_firecraft_warmth_body,
        photoOneLabelRes = R.string.section_firecraft_warmth_photo_one,
        photoTwoLabelRes = R.string.section_firecraft_warmth_photo_two,
        imageNamePrefix = "fire",
        icon = Icons.Outlined.LocalFireDepartment,
        items = listOf(
            GuideItem(
                titleRes = R.string.fire_item_teepee_title,
                imageLabelRes = R.string.fire_item_teepee_image_label,
                stepResList = listOf(
                    R.string.fire_item_teepee_step_1,
                    R.string.fire_item_teepee_step_2,
                    R.string.fire_item_teepee_step_3
                )
            ),
            GuideItem(
                titleRes = R.string.fire_item_log_cabin_title,
                imageLabelRes = R.string.fire_item_log_cabin_image_label,
                stepResList = listOf(
                    R.string.fire_item_log_cabin_step_1,
                    R.string.fire_item_log_cabin_step_2,
                    R.string.fire_item_log_cabin_step_3
                )
            ),
            GuideItem(
                titleRes = R.string.fire_item_lean_to_title,
                imageLabelRes = R.string.fire_item_lean_to_image_label,
                stepResList = listOf(
                    R.string.fire_item_lean_to_step_1,
                    R.string.fire_item_lean_to_step_2,
                    R.string.fire_item_lean_to_step_3
                )
            ),
            GuideItem(
                titleRes = R.string.fire_item_ferro_rod_title,
                imageLabelRes = R.string.fire_item_ferro_rod_image_label,
                stepResList = listOf(
                    R.string.fire_item_ferro_rod_step_1,
                    R.string.fire_item_ferro_rod_step_2,
                    R.string.fire_item_ferro_rod_step_3
                )
            ),
            GuideItem(
                titleRes = R.string.fire_item_wet_tinder_title,
                imageLabelRes = R.string.fire_item_wet_tinder_image_label,
                stepResList = listOf(
                    R.string.fire_item_wet_tinder_step_1,
                    R.string.fire_item_wet_tinder_step_2,
                    R.string.fire_item_wet_tinder_step_3
                )
            )
        )
    ),
    GuideSection(
        titleRes = R.string.section_water_procurement_title,
        bodyRes = R.string.section_water_procurement_body,
        photoOneLabelRes = R.string.section_water_procurement_photo_one,
        photoTwoLabelRes = R.string.section_water_procurement_photo_two,
        imageNamePrefix = "water",
        icon = Icons.Outlined.WaterDrop,
        items = listOf(
            GuideItem(
                titleRes = R.string.water_item_transpiration_bag_title,
                imageLabelRes = R.string.water_item_transpiration_bag_image_label,
                stepResList = listOf(
                    R.string.water_item_transpiration_bag_step_1,
                    R.string.water_item_transpiration_bag_step_2,
                    R.string.water_item_transpiration_bag_step_3
                )
            ),
            GuideItem(
                titleRes = R.string.water_item_dew_sponge_title,
                imageLabelRes = R.string.water_item_dew_sponge_image_label,
                stepResList = listOf(
                    R.string.water_item_dew_sponge_step_1,
                    R.string.water_item_dew_sponge_step_2,
                    R.string.water_item_dew_sponge_step_3
                )
            ),
            GuideItem(
                titleRes = R.string.water_item_natural_filter_title,
                imageLabelRes = R.string.water_item_natural_filter_image_label,
                stepResList = listOf(
                    R.string.water_item_natural_filter_step_1,
                    R.string.water_item_natural_filter_step_2,
                    R.string.water_item_natural_filter_step_3
                )
            ),
            GuideItem(
                titleRes = R.string.water_item_boiling_rules_title,
                imageLabelRes = R.string.water_item_boiling_rules_image_label,
                stepResList = listOf(
                    R.string.water_item_boiling_rules_step_1,
                    R.string.water_item_boiling_rules_step_2,
                    R.string.water_item_boiling_rules_step_3
                )
            ),
            GuideItem(
                titleRes = R.string.water_item_tracking_hidden_water_title,
                imageLabelRes = R.string.water_item_tracking_hidden_water_image_label,
                stepResList = listOf(
                    R.string.water_item_tracking_hidden_water_step_1,
                    R.string.water_item_tracking_hidden_water_step_2,
                    R.string.water_item_tracking_hidden_water_step_3
                )
            )
        )
    ),
    GuideSection(
        titleRes = R.string.section_shelter_title,
        bodyRes = R.string.section_shelter_body,
        photoOneLabelRes = R.string.section_shelter_photo_one,
        photoTwoLabelRes = R.string.section_shelter_photo_two,
        imageNamePrefix = "shelter",
        icon = Icons.Outlined.Roofing,
        items = listOf(
            GuideItem(
                titleRes = R.string.shelter_item_safe_location_title,
                imageLabelRes = R.string.shelter_item_safe_location_image_label,
                stepResList = listOf(
                    R.string.shelter_item_safe_location_step_1,
                    R.string.shelter_item_safe_location_step_2,
                    R.string.shelter_item_safe_location_step_3
                )
            ),
            GuideItem(
                titleRes = R.string.shelter_item_ground_insulation_title,
                imageLabelRes = R.string.shelter_item_ground_insulation_image_label,
                stepResList = listOf(
                    R.string.shelter_item_ground_insulation_step_1,
                    R.string.shelter_item_ground_insulation_step_2,
                    R.string.shelter_item_ground_insulation_step_3
                )
            ),
            GuideItem(
                titleRes = R.string.shelter_item_lean_to_title,
                imageLabelRes = R.string.shelter_item_lean_to_image_label,
                stepResList = listOf(
                    R.string.shelter_item_lean_to_step_1,
                    R.string.shelter_item_lean_to_step_2,
                    R.string.shelter_item_lean_to_step_3
                )
            ),
            GuideItem(
                titleRes = R.string.shelter_item_debris_hut_title,
                imageLabelRes = R.string.shelter_item_debris_hut_image_label,
                stepResList = listOf(
                    R.string.shelter_item_debris_hut_step_1,
                    R.string.shelter_item_debris_hut_step_2,
                    R.string.shelter_item_debris_hut_step_3
                )
            ),
            GuideItem(
                titleRes = R.string.shelter_item_a_frame_tarp_title,
                imageLabelRes = R.string.shelter_item_a_frame_tarp_image_label,
                stepResList = listOf(
                    R.string.shelter_item_a_frame_tarp_step_1,
                    R.string.shelter_item_a_frame_tarp_step_2,
                    R.string.shelter_item_a_frame_tarp_step_3
                )
            )
        )
    ),
    GuideSection(
        titleRes = R.string.section_first_aid_title,
        bodyRes = R.string.section_first_aid_body,
        photoOneLabelRes = R.string.section_first_aid_photo_one,
        photoTwoLabelRes = R.string.section_first_aid_photo_two,
        imageNamePrefix = "aid",
        icon = Icons.Outlined.LocalHospital,
        items = listOf(
            GuideItem(
                titleRes = R.string.first_aid_item_stopping_bleeding_title,
                imageLabelRes = R.string.first_aid_item_stopping_bleeding_image_label,
                stepResList = listOf(
                    R.string.first_aid_item_stopping_bleeding_step_1,
                    R.string.first_aid_item_stopping_bleeding_step_2,
                    R.string.first_aid_item_stopping_bleeding_step_3
                )
            ),
            GuideItem(
                titleRes = R.string.first_aid_item_makeshift_splints_title,
                imageLabelRes = R.string.first_aid_item_makeshift_splints_image_label,
                stepResList = listOf(
                    R.string.first_aid_item_makeshift_splints_step_1,
                    R.string.first_aid_item_makeshift_splints_step_2,
                    R.string.first_aid_item_makeshift_splints_step_3
                )
            ),
            GuideItem(
                titleRes = R.string.first_aid_item_hypothermia_title,
                imageLabelRes = R.string.first_aid_item_hypothermia_image_label,
                stepResList = listOf(
                    R.string.first_aid_item_hypothermia_step_1,
                    R.string.first_aid_item_hypothermia_step_2,
                    R.string.first_aid_item_hypothermia_step_3
                )
            ),
            GuideItem(
                titleRes = R.string.first_aid_item_snake_bites_title,
                imageLabelRes = R.string.first_aid_item_snake_bites_image_label,
                stepResList = listOf(
                    R.string.first_aid_item_snake_bites_step_1,
                    R.string.first_aid_item_snake_bites_step_2,
                    R.string.first_aid_item_snake_bites_step_3
                )
            ),
            GuideItem(
                titleRes = R.string.first_aid_item_burns_title,
                imageLabelRes = R.string.first_aid_item_burns_image_label,
                stepResList = listOf(
                    R.string.first_aid_item_burns_step_1,
                    R.string.first_aid_item_burns_step_2,
                    R.string.first_aid_item_burns_step_3
                )
            )
        )
    ),
    GuideSection(
        titleRes = R.string.section_signaling_title,
        bodyRes = R.string.section_signaling_body,
        photoOneLabelRes = R.string.section_signaling_photo_one,
        photoTwoLabelRes = R.string.section_signaling_photo_two,
        imageNamePrefix = "signal",
        icon = Icons.Outlined.CellTower,
        items = listOf(
            GuideItem(
                titleRes = R.string.signaling_item_rule_of_three_title,
                imageLabelRes = R.string.signaling_item_rule_of_three_image_label,
                stepResList = listOf(
                    R.string.signaling_item_rule_of_three_step_1,
                    R.string.signaling_item_rule_of_three_step_2,
                    R.string.signaling_item_rule_of_three_step_3
                )
            ),
            GuideItem(
                titleRes = R.string.signaling_item_signal_mirror_title,
                imageLabelRes = R.string.signaling_item_signal_mirror_image_label,
                stepResList = listOf(
                    R.string.signaling_item_signal_mirror_step_1,
                    R.string.signaling_item_signal_mirror_step_2,
                    R.string.signaling_item_signal_mirror_step_3
                )
            ),
            GuideItem(
                titleRes = R.string.signaling_item_ground_markers_title,
                imageLabelRes = R.string.signaling_item_ground_markers_image_label,
                stepResList = listOf(
                    R.string.signaling_item_ground_markers_step_1,
                    R.string.signaling_item_ground_markers_step_2,
                    R.string.signaling_item_ground_markers_step_3
                )
            ),
            GuideItem(
                titleRes = R.string.signaling_item_smoky_day_fire_title,
                imageLabelRes = R.string.signaling_item_smoky_day_fire_image_label,
                stepResList = listOf(
                    R.string.signaling_item_smoky_day_fire_step_1,
                    R.string.signaling_item_smoky_day_fire_step_2,
                    R.string.signaling_item_smoky_day_fire_step_3
                )
            ),
            GuideItem(
                titleRes = R.string.signaling_item_high_ground_movement_title,
                imageLabelRes = R.string.signaling_item_high_ground_movement_image_label,
                stepResList = listOf(
                    R.string.signaling_item_high_ground_movement_step_1,
                    R.string.signaling_item_high_ground_movement_step_2,
                    R.string.signaling_item_high_ground_movement_step_3
                )
            )
        )
    )
)


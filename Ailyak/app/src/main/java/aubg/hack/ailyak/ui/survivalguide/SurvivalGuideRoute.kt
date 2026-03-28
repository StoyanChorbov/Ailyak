package aubg.hack.ailyak.ui.survivalguide

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import aubg.hack.ailyak.R
import aubg.hack.ailyak.ui.components.AppMenuItem
import aubg.hack.ailyak.ui.components.AppToggleItem
import aubg.hack.ailyak.ui.components.AppTopRightMenu
import aubg.hack.ailyak.ui.survivalguide.data.guideSections
import aubg.hack.ailyak.ui.survivalguide.screen.GuideDetailScreen
import aubg.hack.ailyak.ui.survivalguide.screen.GuideMenuScreen
import aubg.hack.ailyak.ui.survivalguide.screen.HomeScreen
import aubg.hack.ailyak.viewmodel.MapLayersViewModel

private sealed interface GuideScreen {
    data object Home : GuideScreen
    data object Menu : GuideScreen
    data class Detail(val sectionIndex: Int) : GuideScreen
}

@Composable
fun SurvivalGuideRoute(
    modifier: Modifier = Modifier,
    renderHomeContent: Boolean = true,
    showTopRightMenu: Boolean = true,
    startAtGuideMenu: Boolean = false
) {
    val mapLayersViewModel: MapLayersViewModel = viewModel()
    var currentScreen: GuideScreen by remember {
        mutableStateOf(if (startAtGuideMenu) GuideScreen.Menu else GuideScreen.Home)
    }
    var isAppMenuExpanded by remember { mutableStateOf(false) }

    val menuItems = listOf(
        AppMenuItem(label = stringResource(id = R.string.menu_item_survival_guide)) {
            isAppMenuExpanded = false
            currentScreen = GuideScreen.Menu
        }
    )

    val toggleItems = listOf(
        AppToggleItem(
            label = stringResource(id = R.string.menu_toggle_plants_food_sources),
            checked = mapLayersViewModel.showPlantsFoodSources,
            onCheckedChange = mapLayersViewModel::setPlantsFoodSources
        ),
        AppToggleItem(
            label = stringResource(id = R.string.menu_toggle_water_sources),
            checked = mapLayersViewModel.showWaterSources,
            onCheckedChange = mapLayersViewModel::setWaterSources
        ),
        AppToggleItem(
            label = stringResource(id = R.string.menu_toggle_wild_life),
            checked = mapLayersViewModel.showWildLife,
            onCheckedChange = mapLayersViewModel::setWildLife
        ),
        AppToggleItem(
            label = stringResource(id = R.string.menu_toggle_signal_nearby),
            checked = mapLayersViewModel.showSignalNearby,
            onCheckedChange = mapLayersViewModel::setSignalNearby
        )
    )

    BackHandler(enabled = isAppMenuExpanded || currentScreen != GuideScreen.Home) {
        if (isAppMenuExpanded) {
            isAppMenuExpanded = false
        } else {
            currentScreen = when (currentScreen) {
                is GuideScreen.Detail -> GuideScreen.Menu
                GuideScreen.Menu -> GuideScreen.Home
                GuideScreen.Home -> GuideScreen.Home
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (renderHomeContent) {
            HomeScreen(modifier = Modifier.fillMaxSize())
        }

        when (val screen = currentScreen) {
            GuideScreen.Home -> Unit
            GuideScreen.Menu -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.35f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {}
                        )
                )
                GuideMenuScreen(
                    sections = guideSections,
                    onBack = { currentScreen = GuideScreen.Home },
                    onSectionClick = { sectionIndex -> currentScreen = GuideScreen.Detail(sectionIndex) }
                )
            }

            is GuideScreen.Detail -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.35f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {}
                        )
                )
                GuideDetailScreen(
                    section = guideSections[screen.sectionIndex],
                    onBack = { currentScreen = GuideScreen.Menu }
                )
            }
        }

        if (showTopRightMenu) {
            AppTopRightMenu(
                isExpanded = isAppMenuExpanded,
                onToggle = { isAppMenuExpanded = !isAppMenuExpanded },
                onDismiss = { isAppMenuExpanded = false },
                items = menuItems,
                toggleItems = toggleItems,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
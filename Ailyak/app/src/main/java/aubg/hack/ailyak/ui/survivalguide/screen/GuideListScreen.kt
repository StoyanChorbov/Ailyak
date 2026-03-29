package aubg.hack.ailyak.ui.survivalguide.screen
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import aubg.hack.ailyak.ui.survivalguide.data.guideSections

private sealed interface GuideScreen {
    data object Menu : GuideScreen
    data class Detail(val sectionIndex: Int) : GuideScreen
}

@Composable
fun GuideListScreen() {
    var currentScreen: GuideScreen by remember { mutableStateOf(GuideScreen.Menu) }

    BackHandler(enabled = currentScreen is GuideScreen.Detail) {
        currentScreen = GuideScreen.Menu
    }

    when (val screen = currentScreen) {
        GuideScreen.Menu -> {
            GuideMenuScreen(
                sections = guideSections,
                onBack = {},
                onSectionClick = { sectionIndex ->
                    currentScreen = GuideScreen.Detail(sectionIndex)
                }
            )
        }

        is GuideScreen.Detail -> {
            GuideDetailScreen(
                section = guideSections[screen.sectionIndex],
                onBack = { currentScreen = GuideScreen.Menu }
            )
        }
    }
}

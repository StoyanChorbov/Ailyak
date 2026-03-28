package aubg.hack.ailyak.ui.survivalguide.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import aubg.hack.ailyak.ui.components.SurvivalMap

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    SurvivalMap(modifier = modifier.fillMaxSize())
}

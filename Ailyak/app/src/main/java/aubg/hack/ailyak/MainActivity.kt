package aubg.hack.ailyak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import aubg.hack.ailyak.ui.OfflineMapDownloadScreen
import aubg.hack.ailyak.ui.components.AppBottomNavigation
import aubg.hack.ailyak.ui.components.BottomNavDestination
import aubg.hack.ailyak.ui.survivalguide.SurvivalGuideRoute
import aubg.hack.ailyak.ui.theme.AilyakTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            AilyakTheme {
                var selectedDestination by rememberSaveable { mutableStateOf(BottomNavDestination.Map) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        AppBottomNavigation(
                            selectedDestination = selectedDestination,
                            onDestinationSelected = { selectedDestination = it }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (selectedDestination) {
                            BottomNavDestination.SurvivalGuide -> SurvivalGuideRoute(
                                modifier = Modifier.fillMaxSize(),
                                renderHomeContent = false
                            )
                            BottomNavDestination.Map -> {
                                OfflineMapDownloadScreen(modifier = Modifier.fillMaxSize())
                                SurvivalGuideRoute(
                                    modifier = Modifier.fillMaxSize(),
                                    renderHomeContent = false
                                )
                            }

                            BottomNavDestination.Settings -> SettingsPlaceholderPage()
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun SettingsPlaceholderPage() {
    Box(modifier = Modifier.fillMaxSize())
}
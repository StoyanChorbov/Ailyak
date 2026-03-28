package aubg.hack.ailyak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import aubg.hack.ailyak.ui.OfflineMapDownloadScreen
import aubg.hack.ailyak.ui.survivalguide.SurvivalGuideRoute
import aubg.hack.ailyak.ui.theme.AilyakTheme

private enum class BottomNavDestination {
    Placeholder,
    Map,
    Settings
}

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
                        NavigationBar {
                            BottomNavDestination.entries.forEach { destination ->
                                NavigationBarItem(
                                    selected = destination == selectedDestination,
                                    onClick = { selectedDestination = destination },
                                    icon = {
                                        Text(
                                            text = when (destination) {
                                                BottomNavDestination.Placeholder -> "L"
                                                BottomNavDestination.Map -> "M"
                                                BottomNavDestination.Settings -> "S"
                                            }
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = when (destination) {
                                                BottomNavDestination.Placeholder -> stringResource(id = R.string.bottom_nav_left_placeholder)
                                                BottomNavDestination.Map -> stringResource(id = R.string.bottom_nav_map)
                                                BottomNavDestination.Settings -> stringResource(id = R.string.bottom_nav_settings)
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (selectedDestination) {
                            BottomNavDestination.Placeholder -> LeftPlaceholderPage()
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
private fun LeftPlaceholderPage() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = stringResource(id = R.string.bottom_nav_left_placeholder))
    }
}

@Composable
private fun SettingsPlaceholderPage() {
    Box(modifier = Modifier.fillMaxSize())
}
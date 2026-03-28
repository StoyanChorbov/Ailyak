package aubg.hack.ailyak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import aubg.hack.ailyak.ui.OfflineMapDownloadScreen
import aubg.hack.ailyak.ui.components.StartLocationTrackingButton
import aubg.hack.ailyak.ui.components.StopLocationTrackingButton
import aubg.hack.ailyak.ui.components.SurvivalMap
import aubg.hack.ailyak.ui.survivalguide.SurvivalGuideRoute
import aubg.hack.ailyak.ui.theme.AilyakTheme

class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        enableEdgeToEdge()
//        setContent {
//            AilyakTheme {
//                Scaffold { paddingValues ->
//                    Column(modifier = Modifier.padding(paddingValues)) {
//                        StartLocationTrackingButton(Modifier.padding(top = 240.dp))
//                        StopLocationTrackingButton(Modifier.padding(bottom = 240.dp))
//                    }
//                }
//            }
//        }
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            AilyakTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        OfflineMapDownloadScreen(modifier = Modifier.fillMaxSize())
                        SurvivalGuideRoute(
                            modifier = Modifier.fillMaxSize(),
                            renderHomeContent = false
                        )
                    }
                }
            }
        }
    }
}
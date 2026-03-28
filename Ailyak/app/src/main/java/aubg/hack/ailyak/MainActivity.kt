package aubg.hack.ailyak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import aubg.hack.ailyak.ui.components.SurvivalMap
import aubg.hack.ailyak.ui.survivalguide.SurvivalGuideRoute
import aubg.hack.ailyak.ui.theme.AilyakTheme

class MainActivity : ComponentActivity() {
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
                        SurvivalMap(modifier = Modifier.fillMaxSize())
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


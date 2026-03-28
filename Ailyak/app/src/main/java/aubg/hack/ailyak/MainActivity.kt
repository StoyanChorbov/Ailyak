package aubg.hack.ailyak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import aubg.hack.ailyak.ui.survivalguide.SurvivalGuideRoute
import aubg.hack.ailyak.ui.theme.AilyakTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            AilyakTheme {
                SurvivalGuideRoute()
            }
        }
    }
}


package aubg.hack.ailyak
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import aubg.hack.ailyak.navigation.AppNavGraph
import aubg.hack.ailyak.ui.theme.AilyakTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AilyakTheme { AppNavGraph() } }
    }
}

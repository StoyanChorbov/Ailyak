package aubg.hack.ailyak

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import aubg.hack.ailyak.ui.components.SurvivalMap
import aubg.hack.ailyak.ui.theme.AilyakTheme
import com.mapbox.android.core.permissions.PermissionsManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            AilyakTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SurvivalMap(Modifier.fillMaxSize().padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun SomeFunction(modifier: Modifier) {}
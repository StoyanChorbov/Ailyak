package aubg.hack.ailyak.ui.theme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun AilyakTeam(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = lightColorScheme(), content = content)
}

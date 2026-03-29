package aubg.hack.ailyak.ui.survivalguide.screen
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun GuideListScreen() {
    // TODO: Load from assets/survival_guide.json — no network needed
    LazyColumn { item { Text("Offline Survival Guide") } }
}

package aubg.hack.ailyak.ui.components

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import aubg.hack.ailyak.R

enum class BottomNavDestination {
    Placeholder,
    Map,
    Settings
}

@Composable
fun AppBottomNavigation(
    selectedDestination: BottomNavDestination,
    onDestinationSelected: (BottomNavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        BottomNavDestination.entries.forEach { destination ->
            NavigationBarItem(
                selected = destination == selectedDestination,
                onClick = { onDestinationSelected(destination) },
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


package aubg.hack.ailyak.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun EmergencyMapOverlay(
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    FloatingActionButton(
        onClick = { showDialog = true },
        modifier = modifier.size(90.dp),
        shape = CircleShape,
        containerColor = Color(0xFFF4D35E),
        contentColor = Color(0xFF1F1F1F)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Emergency"
            )
            Text(
                text = "Emergency",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Emergency") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Choose an action:")
                    TextButton(onClick = { showDialog = false }, modifier = Modifier.fillMaxWidth()) {
                        Text("Send message to relatives")
                    }
                    TextButton(onClick = { showDialog = false }, modifier = Modifier.fillMaxWidth()) {
                        Text("Call relatives")
                    }
                    TextButton(onClick = { showDialog = false }, modifier = Modifier.fillMaxWidth()) {
                        Text("Cancel")
                    }
                }
            },
            confirmButton = {}
        )
    }
}

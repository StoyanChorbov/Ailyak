package aubg.hack.ailyak.ui.survivalguide.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import aubg.hack.ailyak.ui.survivalguide.component.PhotoPlaceholder
import aubg.hack.ailyak.ui.survivalguide.component.ScreenHeader
import aubg.hack.ailyak.ui.survivalguide.model.GuideSection

@Composable
fun GuideDetailScreen(
    section: GuideSection,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            ScreenHeader(
                title = stringResource(id = section.titleRes),
                onBack = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PhotoPlaceholder(label = stringResource(id = section.photoOneLabelRes))
            PhotoPlaceholder(label = stringResource(id = section.photoTwoLabelRes))
            Text(text = stringResource(id = section.bodyRes))
        }
    }
}


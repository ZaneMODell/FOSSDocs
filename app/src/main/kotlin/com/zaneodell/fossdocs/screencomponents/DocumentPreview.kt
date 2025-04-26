
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.zaneodell.fossdocs.screencomponents.DocumentThumbnail

@Composable
fun DocumentPreview(
    document: Document,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(6.dp)
            .clickable(onClick = onClick),
    ) {
        val docUri = document.path.toUri()

        // Just use DocumentThumbnail here
        DocumentThumbnail(
            context = LocalContext.current,
            fileUri = docUri,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = document.name,
            modifier = Modifier
                .padding(top = 4.dp)
                .align(Alignment.CenterHorizontally),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 12.sp
        )
    }
}

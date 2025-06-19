package au.com.tvmaze.browser.ui.show_detail

import android.text.Html
import android.widget.TextView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import au.com.tvmaze.browser.R.string
import au.com.tvmaze.browser.ui.model.TvShowUiModel
import au.com.tvmaze.browser.ui.theme.PurpleGrey80
import coil3.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvShowDetailScreen(
    show: TvShowUiModel,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(string.title_show_detail_screen),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    AsyncImage(
                        model = show.image,
                        contentDescription = show.title,
                        modifier = Modifier
                            .width(120.dp)
                            .height(180.dp)
                            .clip(MaterialTheme.shapes.medium),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Top
                    ) {
                        Text(
                            text = show.title,
                            style = MaterialTheme.typography.headlineSmall,
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = show.premieredYear,
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = show.scheduleDescription,
                            style = MaterialTheme.typography.bodySmall,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = show.genres,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                AndroidView(
                    factory = { context ->
                        TextView(context).apply {
                            text = Html.fromHtml(show.summary, Html.FROM_HTML_MODE_LEGACY)
                            textSize = 16f
                        }
                    },
                    update = {
                        it.text = Html.fromHtml(show.summary, Html.FROM_HTML_MODE_LEGACY)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (show.rating.isNotEmpty()) {
                HorizontalDivider(color = PurpleGrey80, thickness = 1.dp)
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(string.average_rating, show.rating),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400)
@Composable
fun TvShowDetailScreenPreview() {
    val sampleShow = TvShowUiModel(
        id = 1,
        title = "Sample Show Title",
        premieredYear = "2022",
        scheduleDescription = "Mondays 8:00 PM",
        image = "https://static.tvmaze.com/uploads/images/original_untouched/1/2668.jpg",
        genres = "Drama, Action, Sci-Fi",
        summary = "This is a sample summary for the show. It gives an overview of the plot and main characters.",
        rating = "8.5"
    )
    TvShowDetailScreen(
        show = sampleShow,
        onNavigateBack = {}
    )
}
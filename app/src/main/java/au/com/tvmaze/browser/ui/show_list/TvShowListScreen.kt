package au.com.tvmaze.browser.ui.show_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import au.com.tvmaze.browser.R.string
import au.com.tvmaze.browser.ui.model.TvShowUiModel
import au.com.tvmaze.browser.ui.theme.AppTheme
import coil3.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TvShowListScreen(
    modifier: Modifier = Modifier,
    viewModel: TvShowListViewModel,
    onShowSelected: (Int) -> Unit = {}
) {
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    val isSearchMode by viewModel.isSearchMode.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchMode) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = { Text(stringResource(string.menu_search_txt_placeholder)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                IconButton(onClick = {
                                    viewModel.clearSearchAndShowDefault()
                                    focusManager.clearFocus()
                                }) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = stringResource(string.menu_close_search_btn_a11y)
                                    )
                                }
                            },
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    viewModel.searchNow(searchQuery)
                                    focusManager.clearFocus()
                                }
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)

                        )
                    } else {
                        Text(
                            text = stringResource(string.app_name)
                        )
                    }
                },
                actions = {
                    if (!isSearchMode) {
                        IconButton(onClick = { viewModel.setSearchMode(true) }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = stringResource(string.menu_search_btn_a11y)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Gray.copy(alpha = 0.3f)
                )
            )
        }
    ) { innerPadding ->
        TvShowListScreen(
            modifier = modifier.padding(innerPadding),
            viewState = viewState,
            onRefresh = viewModel::refresh,
            onShowSelected = onShowSelected
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TvShowListScreen(
    modifier: Modifier = Modifier,
    viewState: TvShowListViewState,
    onRefresh: () -> Unit = {},
    onShowSelected: (Int) -> Unit = {}
) {
    PullToRefreshBox(
        modifier = modifier.fillMaxSize(),
        isRefreshing = viewState is TvShowListViewState.Loading,
        state = rememberPullToRefreshState(),
        onRefresh = onRefresh
    ) {
        when (viewState) {
            is TvShowListViewState.Loading -> {
                // Handled by pull-to-refresh
            }

            is TvShowListViewState.Ready -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(
                        viewState.shows,
                        key = { show ->
                            show.id
                        }
                    ) { show ->
                        TvShowItem(
                            show = show,
                            onShowSelected = { onShowSelected(show.id) }
                        )
                    }
                }
            }

            is TvShowListViewState.Failure -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text =  stringResource(string.error_msg))
                    Text(text = viewState.throwable.message ?: stringResource(string.unknown_error_msg))
                }
            }
        }
    }
}

@Composable
private fun TvShowItem(
    show: TvShowUiModel,
    onShowSelected: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onShowSelected)
            .padding(4.dp)
    ) {
        AsyncImage(
            modifier = Modifier.size(96.dp),
            model = show.image,
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = show.title,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = show.scheduleDescription,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    AppTheme {
        TvShowListScreen(
            viewState = TvShowListViewState.Ready(
                listOf(
                    TvShowUiModel(
                        id = 1,
                        title = "Under the Dome",
                        scheduleDescription = "Tuesday Nights",
                        image = "",
                        premieredYear = "2008",
                        genres = "Comedy",
                        summary = "<p>One anchor, several correspondents, zero credibility.</p>",
                        rating = "8",
                    ),
                    TvShowUiModel(
                        id = 2,
                        title = "Person of Interest",
                        scheduleDescription = "Thursday Afternoons",
                        image = "",
                        premieredYear = "2008",
                        genres = "Comedy",
                        summary = "<p>One anchor, several correspondents, zero credibility.</p>",
                        rating = "8",
                    )
                )
            ),
            onRefresh = {},
            onShowSelected = {}
        )
    }
}
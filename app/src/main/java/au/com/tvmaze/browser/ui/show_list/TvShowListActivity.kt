package au.com.tvmaze.browser.ui.show_list

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import au.com.tvmaze.browser.App
import au.com.mantelgroup.tvmazebrowser.AppContainer
import au.com.tvmaze.browser.ui.AppNavHost
import au.com.tvmaze.browser.ui.theme.AppTheme


class TvShowListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer: AppContainer = (application as App).appContainer

        enableEdgeToEdge()

        setContent {
            AppTheme {
                AppNavHost(appContainer.tvShowRepository)
            }
        }
    }
}

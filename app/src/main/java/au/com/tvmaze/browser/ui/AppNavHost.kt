package au.com.tvmaze.browser.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import au.com.mantelgroup.tvmazebrowser.data.TvShowRepository
import au.com.tvmaze.browser.ui.show_detail.TvShowDetailScreen
import au.com.tvmaze.browser.ui.show_list.TvShowListScreen
import au.com.tvmaze.browser.ui.show_list.TvShowListViewModel


sealed class Screen(val route: String) {
    object ShowList : Screen("main")
    object ShowDetail : Screen("show/{id}") {
        fun createRoute(id: Int) = "show/$id"
    }
}
@Composable
fun AppNavHost(repository: TvShowRepository) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "TvShows"
    ) {
        navigation(startDestination = Screen.ShowList.route, route = "TvShows") {
            composable(Screen.ShowList.route) { backStackEntry ->
                val sharedViewModel: TvShowListViewModel =
                    backStackEntry.sharedViewModel<TvShowListViewModel>(navController, repository)
                TvShowListScreen(
                    viewModel = sharedViewModel,
                    onShowSelected = { showId ->
                        navController.navigate(Screen.ShowDetail.createRoute(showId))
                    },
                )
            }
            composable(
                route = Screen.ShowDetail.route,
                arguments = listOf(
                    navArgument("id") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val sharedViewModel: TvShowListViewModel =
                    backStackEntry.sharedViewModel<TvShowListViewModel>(navController, repository)

                val showId = backStackEntry.arguments?.getInt("id") ?: return@composable
                val selectedShow = sharedViewModel.getShowById(showId)
                TvShowDetailScreen(
                    show = selectedShow,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavHostController, repository: TvShowRepository
): T {
    val navGraphRoute = destination.parent?.route ?: return viewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return viewModel(
        parentEntry,
        factory = TvShowListViewModel.TvShowListViewModelFactory(repository)
    )
}
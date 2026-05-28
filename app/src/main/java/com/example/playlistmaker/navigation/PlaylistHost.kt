package com.example.playlistmaker.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.playlistmaker.ui.activity.MainScreen
import com.example.playlistmaker.ui.playlists.PlaylistsScreen
import com.example.playlistmaker.ui.playlists.NewPlaylistScreen
import com.example.playlistmaker.ui.track.TrackDetailScreen
import com.example.playlistmaker.ui.favorites.FavoritesScreen
import com.example.playlistmaker.SettingsScreen
import com.example.playlistmaker.ui.search.SearchScreen
import com.example.playlistmaker.ui.search.SearchViewModel

@Composable
fun PlaylistHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Main.name
    ) {
        composable(Screen.Main.name) {
            MainScreen(
                onNavigateToSearch = {
                    navController.navigate(Screen.Search.name)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.name)
                },
                onNavigateToPlaylists = {
                    navController.navigate(Screen.Playlists.name)
                },
                onNavigateToFavorites = {
                    navController.navigate("Favorites")
                }
            )
        }

        composable(Screen.Playlists.name) {
            PlaylistsScreen(
                navigateBack = { navController.popBackStack() },
                addNewPlaylist = { navController.navigate("NewPlaylist") },
                navigateToPlaylist = {  } // TODO navigate to playlist detail
            )
        }

        composable("NewPlaylist") {
            NewPlaylistScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }

        composable("Favorites") {
            FavoritesScreen(
                onBackPressed = { navController.popBackStack() },
                onNavigateToTrack = { trackId ->
                    navController.navigate("TrackDetail/$trackId")
                }
            )
        }

        composable("TrackDetail/{trackId}") { backStackEntry ->
            val trackId = backStackEntry.arguments?.getString("trackId") ?: ""
            TrackDetailScreen(
                trackId = trackId,
                onBackPressed = { navController.popBackStack() }
            )
        }

        composable(Screen.Search.name) {
            val searchViewModel: SearchViewModel = viewModel(
                factory = SearchViewModel.getViewModelFactory()
            )
            SearchScreen(
                viewModel = searchViewModel,
                onBackPressed = { navController.popBackStack() },
                onNavigateToTrack = { trackId ->
                    navController.navigate("TrackDetail/$trackId")
                }
            )
        }

        composable(Screen.Settings.name) {
            SettingsScreen(
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
    }
}

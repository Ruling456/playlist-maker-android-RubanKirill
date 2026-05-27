package com.example.playlistmaker.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.playlistmaker.ui.activity.MainScreen
import com.example.playlistmaker.ui.search.SearchScreen
import com.example.playlistmaker.SettingsScreen
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
                }
            )
        }

        composable(Screen.Search.name) {
            val searchViewModel: SearchViewModel = viewModel(
                factory = SearchViewModel.getViewModelFactory()
            )
            SearchScreen(
                viewModel = searchViewModel,
                onBackPressed = { navController.popBackStack() }
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

package com.example.playlistmaker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.playlistmaker.MainScreen
import com.example.playlistmaker.SearchScreen
import com.example.playlistmaker.SettingsScreen

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
            SearchScreen(
                onBackPressed = {
                    navController.popBackStack()
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

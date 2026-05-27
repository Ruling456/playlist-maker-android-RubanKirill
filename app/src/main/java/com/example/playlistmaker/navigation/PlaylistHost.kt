package com.example.playlistmaker.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.playlistmaker.ui.activity.MainScreen
import com.example.playlistmaker.SearchScreen
import com.example.playlistmaker.SettingsScreen
import com.example.playlistmaker.ui.search.AllTracksActivity

@Composable
fun PlaylistHost(navController: NavHostController) {
    val context = LocalContext.current
    
    NavHost(
        navController = navController,
        startDestination = Screen.Main.name
    ) {
        composable(Screen.Main.name) {
            MainScreen(
                onNavigateToSearch = {
                    val intent = Intent(context, AllTracksActivity::class.java)
                    context.startActivity(intent)
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

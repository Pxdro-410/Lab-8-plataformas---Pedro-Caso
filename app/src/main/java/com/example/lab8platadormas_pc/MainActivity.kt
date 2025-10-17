// ----------------------------------------------------------------------------
// Pedro Caso
// 241286
// Lab 8 plataformas
// ----------------------------------------------------------------------------

package com.example.lab8platadormas_pc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lab8platadormas_pc.ui.theme.Lab6PlatadormasPCTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var darkTheme by remember { mutableStateOf(false) }

            Lab6PlatadormasPCTheme(darkTheme = darkTheme) {
                val navController = rememberNavController()

                NavHost(navController, startDestination = "home") {
                    composable("home") {
                        PexelsScreen(navController = navController)
                    }

                    composable(
                        route = "details/{photoId}",
                        arguments = listOf(navArgument("photoId") { type = NavType.LongType })
                    ) {
                        val photo = navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.get<PexelsPhoto>("photo")

                        photo?.let {
                            DetailsScreen(photo = it) {
                                navController.popBackStack()
                            }
                        }
                    }

                    composable("profile") {
                        ProfileScreen(
                            darkTheme = darkTheme,
                            onThemeChange = { darkTheme = it },
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}




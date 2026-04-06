package com.coupang.mobile.p.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.coupang.mobile.p.audio.SoundManager
import com.coupang.mobile.p.screens.GameScreen
import com.coupang.mobile.p.screens.HowToPlayScreen
import com.coupang.mobile.p.screens.LeaderboardScreen
import com.coupang.mobile.p.screens.LevelsScreen
import com.coupang.mobile.p.screens.MenuScreen
import com.coupang.mobile.p.screens.PrivacyPolicyScreen
import com.coupang.mobile.p.screens.SettingsScreen
import com.coupang.mobile.p.storage.GamePreferences

object Routes {
    const val MENU = "menu"
    const val LEVELS = "levels"
    const val GAME = "game/{level}"
    const val SETTINGS = "settings"
    const val HOW_TO_PLAY = "how_to_play"
    const val PRIVACY_POLICY = "privacy_policy"
    const val LEADERBOARD = "leaderboard"
    const val LOADING = "loading"
    const val CONNECT = "connect"

    fun game(level: Int) = "game/$level"
}

@Composable
fun AppNavGraph(
    navController: NavHostController,
    prefs: GamePreferences,
    soundManager: SoundManager,
    onExitApp: () -> Unit
) {
    NavHost(navController = navController, startDestination = Routes.MENU) {
        composable(Routes.MENU) {
            MenuScreen(
                onPlay = { navController.navigate(Routes.LEVELS) },
                onSettings = { navController.navigate(Routes.SETTINGS) },
                onLeaderboard = { navController.navigate(Routes.LEADERBOARD) },
                onExit = onExitApp
            )
        }
        composable(Routes.LEVELS) {
            LevelsScreen(
                prefs = prefs,
                onBack = { navController.popBackStack() },
                onHome = { navController.popBackStack(Routes.MENU, inclusive = false) },
                onLevelSelected = { level -> navController.navigate(Routes.game(level)) }
            )
        }
        composable(
            route = Routes.GAME,
            arguments = listOf(navArgument("level") { type = NavType.IntType })
        ) { backStackEntry ->
            val level = backStackEntry.arguments?.getInt("level") ?: 1
            GameScreen(
                level = level,
                prefs = prefs,
                soundManager = soundManager,
                onHome = { navController.popBackStack(Routes.MENU, inclusive = false) },
                onBack = { navController.popBackStack() },
                onNextLevel = { nextLevel ->
                    navController.popBackStack()
                    navController.navigate(Routes.game(nextLevel))
                },
                onReplay = {
                    navController.popBackStack()
                    navController.navigate(Routes.game(level))
                }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(
                prefs = prefs,
                soundManager = soundManager,
                onBack = { navController.popBackStack() },
                onHowToPlay = { navController.navigate(Routes.HOW_TO_PLAY) },
                onPrivacyPolicy = { navController.navigate(Routes.PRIVACY_POLICY) }
            )
        }
        composable(Routes.HOW_TO_PLAY) {
            HowToPlayScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.PRIVACY_POLICY) {
            PrivacyPolicyScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.LEADERBOARD) {
            LeaderboardScreen(
                prefs = prefs,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

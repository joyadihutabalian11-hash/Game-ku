package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object ProfileSelect : Screen("profile_select")
    object LevelSelect : Screen("level_select")
    object GameBoard : Screen("game_board/{levelId}") {
        fun createRoute(levelId: Int) = "game_board/$levelId"
    }
    object DailyMissions : Screen("daily_missions")
    object Leaderboard : Screen("leaderboard")
    object Badges : Screen("badges")
    object Settings : Screen("settings")
}

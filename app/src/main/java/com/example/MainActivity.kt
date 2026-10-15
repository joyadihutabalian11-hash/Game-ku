package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.badges.BadgesScreen
import com.example.ui.components.CelebrationDialog
import com.example.ui.components.JoyTopBar
import com.example.ui.game.GameBoardScreen
import com.example.ui.home.HomeScreen
import com.example.ui.leaderboard.LeaderboardScreen
import com.example.ui.level.LevelSelectScreen
import com.example.ui.missions.DailyMissionsScreen
import com.example.ui.navigation.Screen
import com.example.ui.profile.ProfileScreen
import com.example.ui.settings.SettingsScreen
import com.example.ui.theme.JoyGamingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JoyGamingTheme {
                JoyGamingApp()
            }
        }
    }
}

@Composable
fun JoyGamingApp(viewModel: MainViewModel = viewModel()) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

    val currentProfile by viewModel.currentProfile.collectAsState()
    val allProfiles by viewModel.allProfiles.collectAsState()
    val levelProgressList by viewModel.levelProgressList.collectAsState()
    val unlockedBadges by viewModel.unlockedBadges.collectAsState()
    val dailyMissions by viewModel.dailyMissions.collectAsState()

    val currentLevel by viewModel.currentLevel.collectAsState()
    val programBlocks by viewModel.programBlocks.collectAsState()
    val simulationFrames by viewModel.simulationFrames.collectAsState()
    val currentFrameIndex by viewModel.currentFrameIndex.collectAsState()
    val isRunningCode by viewModel.isRunningCode.collectAsState()
    val celebrationResult by viewModel.celebrationResult.collectAsState()

    val isSoundEnabled by viewModel.soundManager.isSoundEnabled.collectAsState()
    val isMusicEnabled by viewModel.soundManager.isMusicEnabled.collectAsState()

    val totalStars = remember(levelProgressList) {
        levelProgressList.sumOf { it.stars }
    }
    val completedLevelsCount = remember(levelProgressList) {
        levelProgressList.count { it.stars > 0 }
    }
    val unclaimedMissionsCount = remember(dailyMissions) {
        dailyMissions.count { it.isCompleted && !it.isClaimed }
    }

    // Secondary Screen Back Handling
    if (currentScreen != Screen.Home) {
        BackHandler {
            currentScreen = Screen.Home
        }
    }

    Scaffold(
        topBar = {
            JoyTopBar(
                title = when (currentScreen) {
                    Screen.Home -> "JOYGAMING"
                    Screen.ProfileSelect -> "PROFIL ANAK"
                    Screen.LevelSelect -> "100 LEVEL KODING"
                    Screen.GameBoard -> "LEVEL ${currentLevel.id}"
                    Screen.DailyMissions -> "MISI HARIAN"
                    Screen.Leaderboard -> "PAPAN SKOR JUARA"
                    Screen.Badges -> "LENCANA KODING"
                    Screen.Settings -> "PENGATURAN"
                },
                currentProfile = currentProfile,
                totalStars = totalStars,
                isSoundEnabled = isSoundEnabled,
                onSoundToggle = { viewModel.toggleSound() },
                onProfileClick = { currentScreen = Screen.ProfileSelect },
                onBackClick = if (currentScreen != Screen.Home) {
                    { currentScreen = Screen.Home }
                } else null
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.Home -> {
                    HomeScreen(
                        currentProfile = currentProfile,
                        totalStars = totalStars,
                        completedLevelsCount = completedLevelsCount,
                        unclaimedMissionsCount = unclaimedMissionsCount,
                        onStartGameClick = {
                            val nextLevel = (completedLevelsCount + 1).coerceAtMost(100)
                            viewModel.selectLevel(nextLevel)
                            currentScreen = Screen.GameBoard
                        },
                        onLevelSelectClick = { currentScreen = Screen.LevelSelect },
                        onDailyMissionsClick = { currentScreen = Screen.DailyMissions },
                        onLeaderboardClick = { currentScreen = Screen.Leaderboard },
                        onBadgesClick = { currentScreen = Screen.Badges },
                        onProfileClick = { currentScreen = Screen.ProfileSelect },
                        onSettingsClick = { currentScreen = Screen.Settings }
                    )
                }

                Screen.ProfileSelect -> {
                    ProfileScreen(
                        currentProfile = currentProfile,
                        allProfiles = allProfiles,
                        onSelectProfile = { profileId -> viewModel.switchProfile(profileId) },
                        onCreateProfile = { name, avatar -> viewModel.createProfile(name, avatar) },
                        onBackClick = { currentScreen = Screen.Home }
                    )
                }

                Screen.LevelSelect -> {
                    LevelSelectScreen(
                        levelProgressList = levelProgressList,
                        onLevelSelected = { levelId ->
                            viewModel.selectLevel(levelId)
                            currentScreen = Screen.GameBoard
                        },
                        onBackClick = { currentScreen = Screen.Home }
                    )
                }

                Screen.GameBoard -> {
                    GameBoardScreen(
                        level = currentLevel,
                        programBlocks = programBlocks,
                        simulationFrames = simulationFrames,
                        currentFrameIndex = currentFrameIndex,
                        isRunningCode = isRunningCode,
                        onAddBlock = { block -> viewModel.addBlock(block) },
                        onRemoveBlock = { index -> viewModel.removeBlockAt(index) },
                        onUpdateRepeatCount = { index, count -> viewModel.updateRepeatCount(index, count) },
                        onClearProgram = { viewModel.clearProgram() },
                        onRunCode = { viewModel.runCode() },
                        onStepForward = { viewModel.stepForward() },
                        onStopSimulation = { viewModel.stopSimulation() },
                        onSetSpeed = { speed -> viewModel.setSpeed(speed) }
                    )
                }

                Screen.DailyMissions -> {
                    DailyMissionsScreen(
                        dailyMissions = dailyMissions,
                        onClaimReward = { key -> viewModel.claimDailyMissionReward(key) },
                        onBackClick = { currentScreen = Screen.Home }
                    )
                }

                Screen.Leaderboard -> {
                    LeaderboardScreen(
                        getLeaderboardData = { callback -> viewModel.getLeaderboardEntries(callback) },
                        onBackClick = { currentScreen = Screen.Home }
                    )
                }

                Screen.Badges -> {
                    BadgesScreen(
                        unlockedBadges = unlockedBadges,
                        onBackClick = { currentScreen = Screen.Home }
                    )
                }

                Screen.Settings -> {
                    SettingsScreen(
                        isSoundEnabled = isSoundEnabled,
                        isMusicEnabled = isMusicEnabled,
                        onSoundToggle = { viewModel.toggleSound() },
                        onMusicToggle = { viewModel.toggleMusic() },
                        onResetProgressClick = { viewModel.resetAllProgress() },
                        onBackClick = { currentScreen = Screen.Home }
                    )
                }
            }

            // Level Completion Celebration Modal
            celebrationResult?.let { result ->
                CelebrationDialog(
                    levelId = result.levelId,
                    starsEarned = result.stars,
                    blocksUsed = result.blocksUsed,
                    targetBlocks = result.targetBlocks,
                    unlockedBadgeTitle = result.unlockedBadgeTitle,
                    onNextLevelClick = {
                        viewModel.dismissCelebration()
                        if (result.levelId < 100) {
                            viewModel.selectLevel(result.levelId + 1)
                        } else {
                            currentScreen = Screen.LevelSelect
                        }
                    },
                    onReplayClick = {
                        viewModel.dismissCelebration()
                        viewModel.selectLevel(result.levelId)
                    },
                    onLevelSelectClick = {
                        viewModel.dismissCelebration()
                        currentScreen = Screen.LevelSelect
                    }
                )
            }
        }
    }
}

package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.AppDatabase
import com.example.data.LevelRepository
import com.example.engine.GameEngine
import com.example.model.AchievementBadge
import com.example.model.CodingBlock
import com.example.model.DEFAULT_DAILY_MISSIONS
import com.example.model.DailyMission
import com.example.model.DailyMissionProgress
import com.example.model.GameFrameState
import com.example.model.LeaderboardEntry
import com.example.model.Level
import com.example.model.LevelProgress
import com.example.model.PlayerProfile
import com.example.model.SYSTEM_BADGES
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val profileDao = db.profileDao()
    private val progressDao = db.levelProgressDao()
    private val badgeDao = db.badgeDao()
    private val dailyMissionDao = db.dailyMissionDao()

    val soundManager = SoundManager(application)

    // Current Profile State
    val currentProfile: StateFlow<PlayerProfile?> = profileDao.getCurrentProfileFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val allProfiles: StateFlow<List<PlayerProfile>> = profileDao.getAllProfilesFlow()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Current Level & Workspace Program
    private val _currentLevelId = MutableStateFlow(1)
    val currentLevelId: StateFlow<Int> = _currentLevelId.asStateFlow()

    private val _currentLevel = MutableStateFlow<Level>(LevelRepository.getLevel(1))
    val currentLevel: StateFlow<Level> = _currentLevel.asStateFlow()

    private val _programBlocks = MutableStateFlow<List<CodingBlock>>(emptyList())
    val programBlocks: StateFlow<List<CodingBlock>> = _programBlocks.asStateFlow()

    // Simulation Execution State
    private val _simulationFrames = MutableStateFlow<List<GameFrameState>>(emptyList())
    val simulationFrames: StateFlow<List<GameFrameState>> = _simulationFrames.asStateFlow()

    private val _currentFrameIndex = MutableStateFlow(0)
    val currentFrameIndex: StateFlow<Int> = _currentFrameIndex.asStateFlow()

    private val _isRunningCode = MutableStateFlow(false)
    val isRunningCode: StateFlow<Boolean> = _isRunningCode.asStateFlow()

    private val _executionSpeed = MutableStateFlow(400L) // ms per step
    val executionSpeed: StateFlow<Long> = _executionSpeed.asStateFlow()

    private val _celebrationResult = MutableStateFlow<CelebrationState?>(null)
    val celebrationResult: StateFlow<CelebrationState?> = _celebrationResult.asStateFlow()

    private var simulationJob: Job? = null

    // Level Progress Map for Current Profile
    private val _levelProgressList = MutableStateFlow<List<LevelProgress>>(emptyList())
    val levelProgressList: StateFlow<List<LevelProgress>> = _levelProgressList.asStateFlow()

    // Unlocked Badges for Current Profile
    private val _unlockedBadges = MutableStateFlow<List<AchievementBadge>>(emptyList())
    val unlockedBadges: StateFlow<List<AchievementBadge>> = _unlockedBadges.asStateFlow()

    // Daily Missions State
    private val _dailyMissions = MutableStateFlow<List<DailyMission>>(emptyList())
    val dailyMissions: StateFlow<List<DailyMission>> = _dailyMissions.asStateFlow()

    private val todayKey: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    init {
        viewModelScope.launch {
            var profile = profileDao.getCurrentProfile()
            if (profile == null) {
                val defaultId = profileDao.insertOrUpdateProfile(
                    PlayerProfile(name = "Pemain Koding 🤖", avatarIcon = "mascot_joy", isCurrent = true)
                )
                profile = PlayerProfile(id = defaultId, name = "Pemain Koding 🤖", avatarIcon = "mascot_joy", isCurrent = true)
            }
            loadUserData(profile.id)
        }

        viewModelScope.launch {
            currentProfile.collect { profile ->
                if (profile != null) {
                    loadUserData(profile.id)
                }
            }
        }
    }

    private fun loadUserData(profileId: Long) {
        viewModelScope.launch {
            progressDao.getLevelProgressesFlow(profileId).collect { list ->
                _levelProgressList.value = list
            }
        }
        viewModelScope.launch {
            badgeDao.getBadgesFlow(profileId).collect { list ->
                _unlockedBadges.value = list
            }
        }
        viewModelScope.launch {
            dailyMissionDao.getMissionsFlow(profileId, todayKey).collect { dbMissions ->
                val map = dbMissions.associateBy { it.missionKey }
                _dailyMissions.value = DEFAULT_DAILY_MISSIONS.map { def ->
                    val dbRecord = map[def.key]
                    val currCount = dbRecord?.currentProgress ?: 0
                    val isDone = currCount >= def.targetCount
                    val isClaimed = dbRecord?.isClaimed ?: false

                    def.copy(
                        currentCount = currCount,
                        isCompleted = isDone,
                        isClaimed = isClaimed
                    )
                }
            }
        }
    }

    fun selectLevel(levelId: Int) {
        _currentLevelId.value = levelId
        _currentLevel.value = LevelRepository.getLevel(levelId)
        clearProgram()
        stopSimulation()
    }

    fun addBlock(block: CodingBlock) {
        soundManager.playClickSound()
        val current = _programBlocks.value.toMutableList()
        current.add(block.copyBlock())
        _programBlocks.value = current
    }

    fun removeBlockAt(index: Int) {
        soundManager.playClickSound()
        val current = _programBlocks.value.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            _programBlocks.value = current
        }
    }

    fun clearProgram() {
        soundManager.playClickSound()
        _programBlocks.value = emptyList()
        resetSimulationState()
    }

    fun updateRepeatCount(blockIndex: Int, newCount: Int) {
        soundManager.playClickSound()
        val current = _programBlocks.value.toMutableList()
        if (blockIndex in current.indices) {
            val target = current[blockIndex]
            if (target is CodingBlock.Repeat) {
                current[blockIndex] = target.copy(count = newCount.coerceIn(2, 6))
                _programBlocks.value = current
            }
        }
    }

    fun setSpeed(ms: Long) {
        _executionSpeed.value = ms
    }

    fun toggleSound() {
        soundManager.setSoundEnabled(!soundManager.isSoundEnabled.value)
    }

    fun toggleMusic() {
        soundManager.setMusicEnabled(!soundManager.isMusicEnabled.value)
    }

    fun runCode() {
        if (_isRunningCode.value) {
            stopSimulation()
            return
        }

        val level = _currentLevel.value
        val program = _programBlocks.value
        if (program.isEmpty()) return

        val frames = GameEngine.simulateProgram(level, program)
        _simulationFrames.value = frames
        _currentFrameIndex.value = 0
        _isRunningCode.value = true
        _celebrationResult.value = null

        simulationJob?.cancel()
        simulationJob = viewModelScope.launch {
            for (i in frames.indices) {
                _currentFrameIndex.value = i
                val frame = frames[i]

                if (frame.message.contains("maju")) soundManager.playMoveSound()
                else if (frame.message.contains("belok")) soundManager.playTurnSound()
                else if (frame.message.contains("melompat")) soundManager.playJumpSound()
                else if (frame.message.contains("permata")) soundManager.playCollectSound()

                if (frame.isWin) {
                    soundManager.playWinSound()
                    _isRunningCode.value = false
                    handleLevelWin(level, program, frames)
                    break
                }

                if (frame.isFailed) {
                    soundManager.playErrorSound()
                    _isRunningCode.value = false
                    break
                }

                delay(_executionSpeed.value)
            }
            _isRunningCode.value = false
        }
    }

    fun stepForward() {
        soundManager.playClickSound()
        if (_simulationFrames.value.isEmpty()) {
            val frames = GameEngine.simulateProgram(_currentLevel.value, _programBlocks.value)
            _simulationFrames.value = frames
            _currentFrameIndex.value = 0
        } else if (_currentFrameIndex.value < _simulationFrames.value.size - 1) {
            _currentFrameIndex.value++
            val frame = _simulationFrames.value[_currentFrameIndex.value]
            if (frame.isWin) {
                soundManager.playWinSound()
                handleLevelWin(_currentLevel.value, _programBlocks.value, _simulationFrames.value)
            } else if (frame.isFailed) {
                soundManager.playErrorSound()
            }
        }
    }

    fun stopSimulation() {
        simulationJob?.cancel()
        simulationJob = null
        _isRunningCode.value = false
        resetSimulationState()
    }

    private fun resetSimulationState() {
        _simulationFrames.value = emptyList()
        _currentFrameIndex.value = 0
        _celebrationResult.value = null
    }

    private fun handleLevelWin(level: Level, program: List<CodingBlock>, frames: List<GameFrameState>) {
        val profile = currentProfile.value ?: return
        val stars = GameEngine.calculateStars(level, program)
        val blocksUsed = GameEngine.countTotalBlocks(program)

        viewModelScope.launch {
            val existing = progressDao.getProgressForLevel(profile.id, level.id)
            val bestStars = maxOf(stars, existing?.stars ?: 0)
            val bestTime = minOf(10, existing?.bestTimeSeconds ?: 999)
            val minBlocks = minOf(blocksUsed, existing?.minBlocksUsed ?: 999)

            progressDao.saveProgress(
                LevelProgress(
                    profileId = profile.id,
                    levelNumber = level.id,
                    stars = bestStars,
                    bestTimeSeconds = bestTime,
                    minBlocksUsed = minBlocks,
                    isUnlocked = true
                )
            )

            // Unlock next level automatically
            if (level.id < 100) {
                val nextProgress = progressDao.getProgressForLevel(profile.id, level.id + 1)
                if (nextProgress == null || !nextProgress.isUnlocked) {
                    progressDao.saveProgress(
                        LevelProgress(
                            profileId = profile.id,
                            levelNumber = level.id + 1,
                            stars = 0,
                            bestTimeSeconds = 0,
                            minBlocksUsed = 0,
                            isUnlocked = true
                        )
                    )
                }
            }

            // Update Daily Missions Progress
            incrementDailyMission("mission_play_3", 1)
            if (stars == 3) {
                incrementDailyMission("mission_three_stars", 1)
            }
            val repeatBlocksUsed = program.filterIsInstance<CodingBlock.Repeat>().sumOf { it.count }
            if (repeatBlocksUsed > 0) {
                incrementDailyMission("mission_use_loop", repeatBlocksUsed)
            }
            val lastFrameGems = frames.lastOrNull()?.collectedGems?.size ?: 0
            if (lastFrameGems > 0) {
                incrementDailyMission("mission_collect_gems", lastFrameGems)
            }
            incrementDailyMission("mission_speed_coder", 1)

            // Check milestone badges
            val newBadgeTitle = checkAndGrantBadges(profile.id)

            _celebrationResult.value = CelebrationState(
                levelId = level.id,
                stars = stars,
                blocksUsed = blocksUsed,
                targetBlocks = level.maxBlocksFor3Stars,
                unlockedBadgeTitle = newBadgeTitle
            )
        }
    }

    private suspend fun incrementDailyMission(missionKey: String, delta: Int) {
        val profile = currentProfile.value ?: return
        val currentMissions = dailyMissionDao.getMissions(profile.id, todayKey).associateBy { it.missionKey }
        val targetDef = DEFAULT_DAILY_MISSIONS.find { it.key == missionKey } ?: return

        val existing = currentMissions[missionKey]
        val oldProg = existing?.currentProgress ?: 0
        val newProg = oldProg + delta
        val isDone = newProg >= targetDef.targetCount

        dailyMissionDao.saveMissionProgress(
            DailyMissionProgress(
                profileId = profile.id,
                dateKey = todayKey,
                missionKey = missionKey,
                currentProgress = newProg,
                isCompleted = isDone,
                isClaimed = existing?.isClaimed ?: false
            )
        )
    }

    fun claimDailyMissionReward(missionKey: String) {
        soundManager.playCollectSound()
        soundManager.playWinSound()
        val profile = currentProfile.value ?: return
        val targetDef = DEFAULT_DAILY_MISSIONS.find { it.key == missionKey } ?: return

        viewModelScope.launch {
            val existingList = dailyMissionDao.getMissions(profile.id, todayKey).associateBy { it.missionKey }
            val existing = existingList[missionKey] ?: return@launch

            if (existing.isCompleted && !existing.isClaimed) {
                // Mark as claimed
                dailyMissionDao.saveMissionProgress(
                    existing.copy(isClaimed = true)
                )

                // Add bonus stars as level progress bonus or badge
                if (targetDef.rewardBadgeKey != null) {
                    badgeDao.insertBadge(
                        AchievementBadge(
                            profileId = profile.id,
                            badgeKey = "daily_mission_hero",
                            title = "Pahlawan Misi Harian",
                            description = "Menyelesaikan seluruh misi tantangan harian koding!",
                            iconResName = "🎯"
                        )
                    )
                }
            }
        }
    }

    private suspend fun checkAndGrantBadges(profileId: Long): String? {
        val progresses = progressDao.getLevelProgresses(profileId)
        val totalStars = progresses.sumOf { it.stars }
        val completedLevels = progresses.count { it.stars > 0 }
        val existingBadges = badgeDao.getBadges(profileId).map { it.badgeKey }.toSet()

        var newlyGrantedTitle: String? = null

        for (info in SYSTEM_BADGES) {
            if (!existingBadges.contains(info.key)) {
                val levelCond = info.requiredLevels > 0 && completedLevels >= info.requiredLevels
                val starCond = info.requiredStars > 0 && totalStars >= info.requiredStars

                if (levelCond || starCond) {
                    badgeDao.insertBadge(
                        AchievementBadge(
                            profileId = profileId,
                            badgeKey = info.key,
                            title = info.title,
                            description = info.description,
                            iconResName = info.emoji
                        )
                    )
                    newlyGrantedTitle = info.title
                }
            }
        }
        return newlyGrantedTitle
    }

    fun dismissCelebration() {
        _celebrationResult.value = null
    }

    fun createProfile(name: String, avatar: String) {
        soundManager.playClickSound()
        viewModelScope.launch {
            profileDao.clearCurrentFlag()
            val newId = profileDao.insertOrUpdateProfile(
                PlayerProfile(name = name, avatarIcon = avatar, isCurrent = true)
            )
            progressDao.saveProgress(
                LevelProgress(profileId = newId, levelNumber = 1, stars = 0, bestTimeSeconds = 0, minBlocksUsed = 0, isUnlocked = true)
            )
        }
    }

    fun switchProfile(profileId: Long) {
        soundManager.playClickSound()
        viewModelScope.launch {
            profileDao.clearCurrentFlag()
            profileDao.setCurrentProfile(profileId)
        }
    }

    fun getLeaderboardEntries(callback: (List<LeaderboardEntry>) -> Unit) {
        viewModelScope.launch {
            val profiles = profileDao.getAllProfiles()
            val entries = mutableListOf<LeaderboardEntry>()

            for (p in profiles) {
                val progresses = progressDao.getLevelProgresses(p.id)
                val badges = badgeDao.getBadges(p.id)
                val totalStars = progresses.sumOf { it.stars }
                val levelsCompleted = progresses.count { it.stars > 0 }

                entries.add(
                    LeaderboardEntry(
                        profileId = p.id,
                        name = p.name,
                        avatarIcon = p.avatarIcon,
                        totalStars = totalStars,
                        levelsCompleted = levelsCompleted,
                        totalBadges = badges.size,
                        totalTimeSeconds = levelsCompleted * 15
                    )
                )
            }

            val sorted = entries.sortedWith(
                compareByDescending<LeaderboardEntry> { it.totalStars }
                    .thenByDescending { it.levelsCompleted }
            ).mapIndexed { index, entry -> entry.copy(rank = index + 1) }

            callback(sorted)
        }
    }

    fun resetAllProgress() {
        soundManager.playClickSound()
        val profile = currentProfile.value ?: return
        viewModelScope.launch {
            progressDao.clearProgressForProfile(profile.id)
            progressDao.saveProgress(
                LevelProgress(profileId = profile.id, levelNumber = 1, stars = 0, bestTimeSeconds = 0, minBlocksUsed = 0, isUnlocked = true)
            )
            selectLevel(1)
        }
    }
}

data class CelebrationState(
    val levelId: Int,
    val stars: Int,
    val blocksUsed: Int,
    val targetBlocks: Int,
    val unlockedBadgeTitle: String?
)

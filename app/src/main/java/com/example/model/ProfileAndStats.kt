package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_profiles")
data class PlayerProfile(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val avatarIcon: String = "mascot_joy", // mascot_joy, dino_robot, bunny_star, wizard_cat, rocket_dog
    val createdAt: Long = System.currentTimeMillis(),
    val isCurrent: Boolean = false
)

@Entity(tableName = "level_progress", primaryKeys = ["profileId", "levelNumber"])
data class LevelProgress(
    val profileId: Long,
    val levelNumber: Int,
    val stars: Int, // 0..3
    val bestTimeSeconds: Int,
    val minBlocksUsed: Int,
    val isUnlocked: Boolean,
    val completedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "achievement_badges", primaryKeys = ["profileId", "badgeKey"])
data class AchievementBadge(
    val profileId: Long,
    val badgeKey: String,
    val title: String,
    val description: String,
    val iconResName: String,
    val unlockedAt: Long = System.currentTimeMillis()
)

data class LeaderboardEntry(
    val profileId: Long,
    val name: String,
    val avatarIcon: String,
    val totalStars: Int,
    val levelsCompleted: Int,
    val totalBadges: Int,
    val totalTimeSeconds: Int,
    val rank: Int = 0
)

data class BadgeInfo(
    val key: String,
    val title: String,
    val description: String,
    val emoji: String,
    val requiredStars: Int = 0,
    val requiredLevels: Int = 0
)

val SYSTEM_BADGES = listOf(
    BadgeInfo("first_code", "Langkah Pertama", "Menyelesaikan level 1 koding pertama!", "🚀", requiredLevels = 1),
    BadgeInfo("valley_master", "Juara Lembah Logika", "Selesaikan 25 level di Lembah Logika!", "🏔️", requiredLevels = 25),
    BadgeInfo("loop_wizard", "Penyihir Perulangan", "Selesaikan 50 level dan kuasai Blok Loop!", "🔁", requiredLevels = 50),
    BadgeInfo("condition_hero", "Pahlawan Kondisi", "Selesaikan 75 level dengan logika cabang!", "⚡", requiredLevels = 75),
    BadgeInfo("joy_legend", "Legenda Koding JOYGAMING", "Selesaikan seluruh 100 level koding!", "👑", requiredLevels = 100),
    BadgeInfo("star_collector_50", "Bintang Gemerlap", "Kumpulkan 50 Bintang emas!", "⭐", requiredStars = 50),
    BadgeInfo("star_collector_150", "Raja Bintang", "Kumpulkan 150 Bintang emas!", "🌟", requiredStars = 150),
    BadgeInfo("star_collector_300", "Master 300 Bintang", "Kumpulkan 300 Bintang Sempurna!", "🏆", requiredStars = 300)
)

package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_missions", primaryKeys = ["profileId", "dateKey", "missionKey"])
data class DailyMissionProgress(
    val profileId: Long,
    val dateKey: String, // e.g. "2026-10-15"
    val missionKey: String,
    val currentProgress: Int,
    val isCompleted: Boolean,
    val isClaimed: Boolean
)

data class DailyMission(
    val key: String,
    val title: String,
    val description: String,
    val targetCount: Int,
    val currentCount: Int,
    val rewardStars: Int,
    val rewardBadgeKey: String? = null,
    val isCompleted: Boolean,
    val isClaimed: Boolean,
    val iconEmoji: String
)

val DEFAULT_DAILY_MISSIONS = listOf(
    DailyMission(
        key = "mission_play_3",
        title = "Pemain Rajin",
        description = "Selesaikan 3 Level Koding Hari Ini",
        targetCount = 3,
        currentCount = 0,
        rewardStars = 15,
        isCompleted = false,
        isClaimed = false,
        iconEmoji = "🎮"
    ),
    DailyMission(
        key = "mission_three_stars",
        title = "Master 3 Bintang",
        description = "Dapatkan 3 Bintang Sempurna di 2 Level",
        targetCount = 2,
        currentCount = 0,
        rewardStars = 20,
        isCompleted = false,
        isClaimed = false,
        iconEmoji = "🌟"
    ),
    DailyMission(
        key = "mission_use_loop",
        title = "Pakar Perulangan",
        description = "Gunakan Blok 'Ulangi' (Loop) 5 Kali",
        targetCount = 5,
        currentCount = 0,
        rewardStars = 15,
        isCompleted = false,
        isClaimed = false,
        iconEmoji = "🔁"
    ),
    DailyMission(
        key = "mission_collect_gems",
        title = "Pemburu Permata",
        description = "Kumpulkan 6 Permata Emas",
        targetCount = 6,
        currentCount = 0,
        rewardStars = 20,
        isCompleted = false,
        isClaimed = false,
        iconEmoji = "💎"
    ),
    DailyMission(
        key = "mission_speed_coder",
        title = "Juara Misi Harian",
        description = "Selesaikan Misi Koding tanpa Salah Melangkah",
        targetCount = 1,
        currentCount = 0,
        rewardStars = 30,
        rewardBadgeKey = "daily_hero",
        isCompleted = false,
        isClaimed = false,
        iconEmoji = "🎯"
    )
)

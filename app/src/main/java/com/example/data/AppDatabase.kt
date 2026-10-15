package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.model.AchievementBadge
import com.example.model.DailyMissionProgress
import com.example.model.LevelProgress
import com.example.model.PlayerProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerProfileDao {
    @Query("SELECT * FROM player_profiles ORDER BY createdAt ASC")
    fun getAllProfilesFlow(): Flow<List<PlayerProfile>>

    @Query("SELECT * FROM player_profiles ORDER BY createdAt ASC")
    suspend fun getAllProfiles(): List<PlayerProfile>

    @Query("SELECT * FROM player_profiles WHERE isCurrent = 1 LIMIT 1")
    fun getCurrentProfileFlow(): Flow<PlayerProfile?>

    @Query("SELECT * FROM player_profiles WHERE isCurrent = 1 LIMIT 1")
    suspend fun getCurrentProfile(): PlayerProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: PlayerProfile): Long

    @Query("UPDATE player_profiles SET isCurrent = 0")
    suspend fun clearCurrentFlag()

    @Query("UPDATE player_profiles SET isCurrent = 1 WHERE id = :profileId")
    suspend fun setCurrentProfile(profileId: Long)

    @Query("DELETE FROM player_profiles WHERE id = :profileId")
    suspend fun deleteProfile(profileId: Long)
}

@Dao
interface LevelProgressDao {
    @Query("SELECT * FROM level_progress WHERE profileId = :profileId ORDER BY levelNumber ASC")
    fun getLevelProgressesFlow(profileId: Long): Flow<List<LevelProgress>>

    @Query("SELECT * FROM level_progress WHERE profileId = :profileId ORDER BY levelNumber ASC")
    suspend fun getLevelProgresses(profileId: Long): List<LevelProgress>

    @Query("SELECT * FROM level_progress WHERE profileId = :profileId AND levelNumber = :levelNumber LIMIT 1")
    suspend fun getProgressForLevel(profileId: Long, levelNumber: Int): LevelProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: LevelProgress)

    @Query("DELETE FROM level_progress WHERE profileId = :profileId")
    suspend fun clearProgressForProfile(profileId: Long)
}

@Dao
interface AchievementBadgeDao {
    @Query("SELECT * FROM achievement_badges WHERE profileId = :profileId")
    fun getBadgesFlow(profileId: Long): Flow<List<AchievementBadge>>

    @Query("SELECT * FROM achievement_badges WHERE profileId = :profileId")
    suspend fun getBadges(profileId: Long): List<AchievementBadge>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadge(badge: AchievementBadge)
}

@Dao
interface DailyMissionDao {
    @Query("SELECT * FROM daily_missions WHERE profileId = :profileId AND dateKey = :dateKey")
    fun getMissionsFlow(profileId: Long, dateKey: String): Flow<List<DailyMissionProgress>>

    @Query("SELECT * FROM daily_missions WHERE profileId = :profileId AND dateKey = :dateKey")
    suspend fun getMissions(profileId: Long, dateKey: String): List<DailyMissionProgress>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMissionProgress(progress: DailyMissionProgress)
}

@Database(
    entities = [PlayerProfile::class, LevelProgress::class, AchievementBadge::class, DailyMissionProgress::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): PlayerProfileDao
    abstract fun levelProgressDao(): LevelProgressDao
    abstract fun badgeDao(): AchievementBadgeDao
    abstract fun dailyMissionDao(): DailyMissionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "joygaming_kids_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

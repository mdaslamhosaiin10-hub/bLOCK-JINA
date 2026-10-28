package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.BlockedAttempt
import com.example.data.model.CustomFilterRule
import com.example.data.model.DailyPledge
import com.example.data.model.UrgeLog
import kotlinx.coroutines.flow.Flow

@Dao
interface ShieldDao {
    // Blocked attempts
    @Query("SELECT * FROM blocked_attempts ORDER BY timestamp DESC LIMIT 100")
    fun getAllBlockedAttempts(): Flow<List<BlockedAttempt>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedAttempt(attempt: BlockedAttempt)

    @Query("DELETE FROM blocked_attempts WHERE id = :id")
    suspend fun deleteBlockedAttempt(id: Long)

    @Query("DELETE FROM blocked_attempts")
    suspend fun clearBlockedAttempts()

    @Query("SELECT COUNT(*) FROM blocked_attempts")
    fun getBlockedAttemptCount(): Flow<Int>

    // Custom Filters
    @Query("SELECT * FROM custom_filters ORDER BY createdAt DESC")
    fun getAllCustomFilters(): Flow<List<CustomFilterRule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomFilter(rule: CustomFilterRule)

    @Query("DELETE FROM custom_filters WHERE id = :id")
    suspend fun deleteCustomFilter(id: Long)

    @Query("UPDATE custom_filters SET isEnabled = :enabled WHERE id = :id")
    suspend fun toggleCustomFilter(id: Long, enabled: Boolean)

    // Daily Pledges
    @Query("SELECT * FROM daily_pledges ORDER BY timestamp DESC")
    fun getAllPledges(): Flow<List<DailyPledge>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPledge(pledge: DailyPledge)

    @Query("SELECT * FROM daily_pledges WHERE dateKey = :dateKey LIMIT 1")
    suspend fun getPledgeForDate(dateKey: String): DailyPledge?

    // Urge Logs
    @Query("SELECT * FROM urge_logs ORDER BY timestamp DESC LIMIT 50")
    fun getAllUrgeLogs(): Flow<List<UrgeLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUrgeLog(log: UrgeLog)
}

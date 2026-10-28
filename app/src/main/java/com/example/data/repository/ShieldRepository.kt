package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.engine.ContentSafetyEngine
import com.example.data.engine.SafetyInspectionResult
import com.example.data.local.ShieldDao
import com.example.data.model.BlockedAttempt
import com.example.data.model.CustomFilterRule
import com.example.data.model.DailyPledge
import com.example.data.model.ProtectionLevel
import com.example.data.model.ShieldState
import com.example.data.model.UrgeLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ShieldRepository(
    private val shieldDao: ShieldDao,
    context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("pureshield_prefs", Context.MODE_PRIVATE)

    private val _shieldState = MutableStateFlow(loadInitialShieldState())
    val shieldState: StateFlow<ShieldState> = _shieldState.asStateFlow()

    val blockedAttempts: Flow<List<BlockedAttempt>> = shieldDao.getAllBlockedAttempts()
    val customFilters: Flow<List<CustomFilterRule>> = shieldDao.getAllCustomFilters()
    val pledges: Flow<List<DailyPledge>> = shieldDao.getAllPledges()
    val urgeLogs: Flow<List<UrgeLog>> = shieldDao.getAllUrgeLogs()
    val blockedCount: Flow<Int> = shieldDao.getBlockedAttemptCount()

    private fun loadInitialShieldState(): ShieldState {
        val isActive = prefs.getBoolean("is_active", true)
        val levelName = prefs.getString("protection_level", ProtectionLevel.STRICT.name) ?: ProtectionLevel.STRICT.name
        val level = try { ProtectionLevel.valueOf(levelName) } catch (e: Exception) { ProtectionLevel.STRICT }
        val isStrictLocked = prefs.getBoolean("is_strict_locked", false)
        val pinHash = prefs.getString("pin_hash", null)
        val streakStart = prefs.getLong("streak_start", System.currentTimeMillis() - (86400000L * 3))
        val dnsType = prefs.getString("dns_type", "CleanBrowsing Family Filter") ?: "CleanBrowsing Family Filter"
        val isDnsConfigured = prefs.getBoolean("is_dns_configured", true)

        return ShieldState(
            isActive = isActive,
            protectionLevel = level,
            isStrictModeLocked = isStrictLocked,
            pinHash = pinHash,
            streakStartTimestamp = streakStart,
            totalInterceptedCount = 14,
            dnsActiveType = dnsType,
            isDnsConfigured = isDnsConfigured
        )
    }

    fun toggleProtection(active: Boolean) {
        prefs.edit().putBoolean("is_active", active).apply()
        _shieldState.value = _shieldState.value.copy(isActive = active)
    }

    fun setProtectionLevel(level: ProtectionLevel) {
        prefs.edit().putString("protection_level", level.name).apply()
        _shieldState.value = _shieldState.value.copy(protectionLevel = level)
    }

    fun setPin(pin: String) {
        val hash = pin.hashCode().toString()
        prefs.edit().putString("pin_hash", hash).apply()
        _shieldState.value = _shieldState.value.copy(pinHash = hash)
    }

    fun validatePin(pin: String): Boolean {
        val currentHash = _shieldState.value.pinHash ?: return true
        return pin.hashCode().toString() == currentHash
    }

    fun toggleStrictMode(locked: Boolean) {
        prefs.edit().putBoolean("is_strict_locked", locked).apply()
        _shieldState.value = _shieldState.value.copy(isStrictModeLocked = locked)
    }

    fun setDnsConfigured(configured: Boolean, dnsName: String = "CleanBrowsing Family Filter") {
        prefs.edit()
            .putBoolean("is_dns_configured", configured)
            .putString("dns_type", dnsName)
            .apply()
        _shieldState.value = _shieldState.value.copy(
            isDnsConfigured = configured,
            dnsActiveType = dnsName
        )
    }

    fun resetStreak() {
        val now = System.currentTimeMillis()
        prefs.edit().putLong("streak_start", now).apply()
        _shieldState.value = _shieldState.value.copy(streakStartTimestamp = now)
    }

    suspend fun inspectAndLog(input: String): SafetyInspectionResult {
        val currentFilters = customFilters.first()
        val result = ContentSafetyEngine.inspectContent(input, currentFilters)

        if (result.isBlocked) {
            shieldDao.insertBlockedAttempt(
                BlockedAttempt(
                    target = input,
                    category = result.category,
                    reason = result.explanation,
                    wasBlocked = true
                )
            )
        }
        return result
    }

    suspend fun addCustomFilter(pattern: String, isDomain: Boolean) {
        if (pattern.isNotBlank()) {
            shieldDao.insertCustomFilter(
                CustomFilterRule(
                    pattern = pattern.trim().lowercase(Locale.ROOT),
                    isDomain = isDomain
                )
            )
        }
    }

    suspend fun deleteCustomFilter(id: Long) {
        shieldDao.deleteCustomFilter(id)
    }

    suspend fun toggleCustomFilter(id: Long, enabled: Boolean) {
        shieldDao.toggleCustomFilter(id, enabled)
    }

    suspend fun clearBlockedAttempts() {
        shieldDao.clearBlockedAttempts()
    }

    suspend fun recordPledge(note: String = ""): Boolean {
        val todayKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val existing = shieldDao.getPledgeForDate(todayKey)
        if (existing != null) return false // already pledged today

        val start = _shieldState.value.streakStartTimestamp
        val days = ((System.currentTimeMillis() - start) / 86400000L).toInt().coerceAtLeast(1)

        shieldDao.insertPledge(
            DailyPledge(
                dateKey = todayKey,
                streakDayCount = days,
                note = note.ifBlank { "Committed to maintaining a pure, focused, and secure digital lifestyle today." }
            )
        )
        return true
    }

    suspend fun recordUrgeLog(
        triggerType: String,
        mood: String,
        intensity: Int,
        wasOvercome: Boolean,
        notes: String
    ) {
        shieldDao.insertUrgeLog(
            UrgeLog(
                triggerType = triggerType,
                mood = mood,
                intensity = intensity,
                wasOvercome = wasOvercome,
                notes = notes
            )
        )
    }
}

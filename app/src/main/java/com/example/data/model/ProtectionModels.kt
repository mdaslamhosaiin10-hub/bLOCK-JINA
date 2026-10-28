package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocked_attempts")
data class BlockedAttempt(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val target: String,
    val category: String,
    val reason: String,
    val wasBlocked: Boolean = true
)

@Entity(tableName = "custom_filters")
data class CustomFilterRule(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pattern: String,
    val isDomain: Boolean,
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_pledges")
data class DailyPledge(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateKey: String, // e.g., "2026-10-28"
    val timestamp: Long = System.currentTimeMillis(),
    val streakDayCount: Int,
    val note: String = ""
)

@Entity(tableName = "urge_logs")
data class UrgeLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val triggerType: String,
    val mood: String,
    val intensity: Int, // 1 to 5
    val wasOvercome: Boolean = true,
    val notes: String = ""
)

enum class ProtectionLevel(val title: String, val description: String) {
    BALANCED("Balanced", "Filters known adult domains and unsafe search terms."),
    STRICT("Strict Guardian", "Zero-tolerance: blocks all explicit sites, adult keywords, and restricts late-night triggers."),
    LOCKDOWN("Maximum Lockdown", "Highest security. Modifying settings requires PIN & 60-second cool-down.")
}

data class ShieldState(
    val isActive: Boolean = true,
    val protectionLevel: ProtectionLevel = ProtectionLevel.STRICT,
    val isStrictModeLocked: Boolean = false,
    val pinHash: String? = null,
    val streakStartTimestamp: Long = System.currentTimeMillis() - (86400000L * 3), // default 3 days active
    val totalInterceptedCount: Int = 14,
    val dnsActiveType: String = "CleanBrowsing Family Filter",
    val isDnsConfigured: Boolean = false
)

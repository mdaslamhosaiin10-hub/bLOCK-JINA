package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.BlockedAttempt
import com.example.data.model.CustomFilterRule
import com.example.data.model.DailyPledge
import com.example.data.model.UrgeLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        BlockedAttempt::class,
        CustomFilterRule::class,
        DailyPledge::class,
        UrgeLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PureShieldDatabase : RoomDatabase() {
    abstract fun shieldDao(): ShieldDao

    companion object {
        @Volatile
        private var INSTANCE: PureShieldDatabase? = null

        fun getDatabase(context: Context): PureShieldDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PureShieldDatabase::class.java,
                    "pureshield_database"
                )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Pre-populate with initial starter rules and example safe audit log
                        CoroutineScope(Dispatchers.IO).launch {
                            val dao = getDatabase(context).shieldDao()
                            val now = System.currentTimeMillis()
                            // Starter default custom blacklist filters
                            dao.insertCustomFilter(CustomFilterRule(pattern = "xxx", isDomain = false))
                            dao.insertCustomFilter(CustomFilterRule(pattern = "porn", isDomain = false))
                            dao.insertCustomFilter(CustomFilterRule(pattern = "adult-cam", isDomain = false))
                            dao.insertCustomFilter(CustomFilterRule(pattern = "nsfw", isDomain = false))
                            dao.insertCustomFilter(CustomFilterRule(pattern = "chaturbate.com", isDomain = true))

                            // Initial audit entries showing active 24/7 protection
                            dao.insertBlockedAttempt(
                                BlockedAttempt(
                                    timestamp = now - (3600000L * 2),
                                    target = "adult-content-stream.net",
                                    category = "Adult & Explicit Media",
                                    reason = "Matched adult domain database and blocked via Private DNS rule.",
                                    wasBlocked = true
                                )
                            )
                            dao.insertBlockedAttempt(
                                BlockedAttempt(
                                    timestamp = now - (3600000L * 14),
                                    target = "free explicit search query",
                                    category = "Adult Keyword Search",
                                    reason = "Flagged by safe search keyword filter.",
                                    wasBlocked = true
                                )
                            )
                            dao.insertBlockedAttempt(
                                BlockedAttempt(
                                    timestamp = now - (3600000L * 28),
                                    target = "live-cam-dating-site.xyz",
                                    category = "Adult Escort & Chat",
                                    reason = "Suspicious adult category blocked automatically.",
                                    wasBlocked = true
                                )
                            )

                            // Initial pledge for day 1
                            dao.insertPledge(
                                DailyPledge(
                                    dateKey = "2026-10-27",
                                    timestamp = now - 86400000L,
                                    streakDayCount = 1,
                                    note = "I commit to keeping my mind, habits, and digital spaces pure and focused."
                                )
                            )
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

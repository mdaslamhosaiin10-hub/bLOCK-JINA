package com.example.service

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.LockoutActivity
import com.example.data.engine.ContentSafetyEngine
import com.example.data.local.PureShieldDatabase
import com.example.data.model.BlockedAttempt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale

class PureShieldAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private var lastTriggerTime: Long = 0

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        isServiceRunning = true
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning = false
        instance = null
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        val now = System.currentTimeMillis()
        if (now - lastTriggerTime < 2000L) return

        val packageName = event.packageName?.toString() ?: ""
        val myPkg = applicationContext.packageName

        // CRITICAL FIX: Skip events originating from PureShield itself so our own UI text, test chips, and logs never trigger self-lockout!
        if (packageName == myPkg) {
            return
        }

        val textCandidates = mutableListOf<String>()

        event.text?.forEach { charSeq ->
            if (!charSeq.isNullOrBlank()) {
                textCandidates.add(charSeq.toString())
            }
        }

        event.contentDescription?.let { desc ->
            if (desc.isNotBlank()) textCandidates.add(desc.toString())
        }

        val source = event.source
        if (source != null) {
            val sourcePkg = source.packageName?.toString() ?: ""
            if (sourcePkg == myPkg) {
                source.recycle()
                return
            }
            extractTextFromNode(source, textCandidates)
            source.recycle()
        }

        val joinedScreenText = textCandidates.joinToString(" ").lowercase(Locale.ROOT)

        // 1. Check Anti-Tampering (Block Uninstall, DNS change, Accessibility off, Admin deactivate)
        val bypassReason = ContentSafetyEngine.detectSystemBypass(joinedScreenText, packageName)
        if (bypassReason != null) {
            lastTriggerTime = now
            enforceTamperLockdown(bypassReason)
            return
        }

        // 2. Check for Jina, Jua, Vulgar Dance, or Sexual Activity
        for (text in textCandidates) {
            val clean = text.trim()
            if (clean.length >= 2) {
                checkAndEnforceLockout(clean)
                break
            }
        }
    }

    private fun extractTextFromNode(node: AccessibilityNodeInfo, list: MutableList<String>, depth: Int = 0) {
        if (depth > 5) return
        val nodeText = node.text
        if (!nodeText.isNullOrBlank()) {
            list.add(nodeText.toString())
        }
        val desc = node.contentDescription
        if (!desc.isNullOrBlank()) {
            list.add(desc.toString())
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            extractTextFromNode(child, list, depth + 1)
            child.recycle()
        }
    }

    private fun enforceTamperLockdown(reason: String) {
        serviceScope.launch {
            try {
                val db = PureShieldDatabase.getDatabase(applicationContext)
                db.shieldDao().insertBlockedAttempt(
                    BlockedAttempt(
                        target = "System Anti-Tamper Barrier",
                        category = "Anti-Tamper & Anti-Uninstall",
                        reason = reason,
                        wasBlocked = true
                    )
                )

                performGlobalAction(GLOBAL_ACTION_HOME)
                vibrateAlert(pattern = longArrayOf(0, 500, 200, 500))

                val lockoutIntent = Intent(applicationContext, LockoutActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    putExtra(LockoutActivity.EXTRA_TRIGGER, reason)
                    putExtra(LockoutActivity.EXTRA_IS_TAMPER, true)
                }
                startActivity(lockoutIntent)
            } catch (e: Exception) {
                // safeguard
            }
        }
    }

    private fun checkAndEnforceLockout(text: String) {
        serviceScope.launch {
            try {
                val db = PureShieldDatabase.getDatabase(applicationContext)
                val customRules = db.shieldDao().getAllCustomFilters().first()

                val (isTriggered, trigger, category) = ContentSafetyEngine.isExplicitTrigger(text, customRules)

                if (isTriggered && trigger != null) {
                    lastTriggerTime = System.currentTimeMillis()

                    val isTamper = category == "Anti-Tamper Lockdown"

                    db.shieldDao().insertBlockedAttempt(
                        BlockedAttempt(
                            target = text.take(60),
                            category = category,
                            reason = "Immediate lockout enforced for moral safety: '$trigger'",
                            wasBlocked = true
                        )
                    )

                    performGlobalAction(GLOBAL_ACTION_HOME)
                    vibrateAlert()

                    val lockoutIntent = Intent(applicationContext, LockoutActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        putExtra(LockoutActivity.EXTRA_TRIGGER, "$category ($trigger)")
                        putExtra(LockoutActivity.EXTRA_QUERY, text)
                        putExtra(LockoutActivity.EXTRA_IS_TAMPER, isTamper)
                    }
                    startActivity(lockoutIntent)
                }
            } catch (e: Exception) {
                // safeguard
            }
        }
    }

    private fun vibrateAlert(pattern: LongArray? = null) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val vibrator = vibratorManager?.defaultVibrator
                vibrator?.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(500)
            }
        } catch (e: Exception) {
            // vibration fallback
        }
    }

    override fun onInterrupt() {
        // Interrupted
    }

    companion object {
        var isServiceRunning = false
        var instance: PureShieldAccessibilityService? = null

        fun isAccessibilitySettingsOn(context: Context): Boolean {
            var accessibilityEnabled = 0
            val service = "${context.packageName}/${PureShieldAccessibilityService::class.java.canonicalName}"
            try {
                accessibilityEnabled = Settings.Secure.getInt(
                    context.contentResolver,
                    Settings.Secure.ACCESSIBILITY_ENABLED
                )
            } catch (e: Settings.SettingNotFoundException) {
                return false
            }

            val stringColonSplitter = TextUtils.SimpleStringSplitter(':')
            if (accessibilityEnabled == 1) {
                val settingValue = Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                )
                if (settingValue != null) {
                    stringColonSplitter.setString(settingValue)
                    while (stringColonSplitter.hasNext()) {
                        val accessibilityService = stringColonSplitter.next()
                        if (accessibilityService.equals(service, ignoreCase = true)) {
                            return true
                        }
                    }
                }
            }
            return false
        }
    }
}

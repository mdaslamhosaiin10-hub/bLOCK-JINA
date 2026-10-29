package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.engine.SafetyInspectionResult
import com.example.data.local.PureShieldDatabase
import com.example.data.model.BlockedAttempt
import com.example.data.model.CustomFilterRule
import com.example.data.model.DailyPledge
import com.example.data.model.ProtectionLevel
import com.example.data.model.ShieldState
import com.example.data.model.UrgeLog
import com.example.data.repository.ShieldRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ShieldViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ShieldRepository

    init {
        val db = PureShieldDatabase.getDatabase(application)
        repository = ShieldRepository(db.shieldDao(), application)
    }

    val shieldState: StateFlow<ShieldState> = repository.shieldState

    val blockedAttempts: StateFlow<List<BlockedAttempt>> = repository.blockedAttempts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customFilters: StateFlow<List<CustomFilterRule>> = repository.customFilters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pledges: StateFlow<List<DailyPledge>> = repository.pledges
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val urgeLogs: StateFlow<List<UrgeLog>> = repository.urgeLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Sandbox / Test URL scanner state
    val scannerInput = MutableStateFlow("")
    val inspectionResult = MutableStateFlow<SafetyInspectionResult?>(null)
    val isScanning = MutableStateFlow(false)

    // Message events
    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    // Strict Mode / PIN / Cooldown State
    val isPinDialogOpen = MutableStateFlow(false)
    val isSetPinMode = MutableStateFlow(false)
    val pinError = MutableStateFlow<String?>(null)
    val cooldownSeconds = MutableStateFlow(0)
    private var cooldownJob: Job? = null

    // Urge Rescue State
    val isUrgeRescueOpen = MutableStateFlow(false)
    val breathingPhase = MutableStateFlow("Breathe In") // "Breathe In" (4s), "Hold" (7s), "Breathe Out" (8s)
    val breathingProgress = MutableStateFlow(0f)
    val breathingCycleCount = MutableStateFlow(0)
    private var breathingJob: Job? = null

    fun toggleProtection() {
        val currentState = shieldState.value
        if (currentState.isActive && currentState.isStrictModeLocked) {
            // Strict mode is on - require PIN or cool-down
            isPinDialogOpen.value = true
            isSetPinMode.value = false
            return
        }
        val newState = !currentState.isActive
        repository.toggleProtection(newState)
        emitMessage(if (newState) "24/7 Shield Activated" else "Shield Paused")
    }

    fun setProtectionLevel(level: ProtectionLevel) {
        val currentState = shieldState.value
        if (currentState.isStrictModeLocked && level != ProtectionLevel.LOCKDOWN) {
            isPinDialogOpen.value = true
            isSetPinMode.value = false
            return
        }
        repository.setProtectionLevel(level)
        emitMessage("Protection level updated to ${level.title}")
    }

    fun verifyPinAndExecute(pin: String, onSuccess: () -> Unit) {
        if (repository.validatePin(pin)) {
            pinError.value = null
            isPinDialogOpen.value = false
            onSuccess()
        } else {
            pinError.value = "Incorrect PIN. Cooldown triggered."
            startCooldown(30)
        }
    }

    fun saveNewPin(pin: String) {
        if (pin.length == 4) {
            repository.setPin(pin)
            repository.toggleStrictMode(true)
            isPinDialogOpen.value = false
            pinError.value = null
            emitMessage("Strict Guardian PIN configured and active!")
        } else {
            pinError.value = "PIN must be 4 digits"
        }
    }

    fun startCooldown(seconds: Int = 60) {
        cooldownJob?.cancel()
        cooldownSeconds.value = seconds
        cooldownJob = viewModelScope.launch {
            while (cooldownSeconds.value > 0) {
                delay(1000)
                cooldownSeconds.value -= 1
            }
        }
    }

    fun scanContent() {
        val input = scannerInput.value.trim()
        if (input.isEmpty()) return

        viewModelScope.launch {
            isScanning.value = true
            delay(400) // realistic scan animation
            val result = repository.inspectAndLog(input)
            inspectionResult.value = result
            isScanning.value = false
        }
    }

    fun clearScan() {
        scannerInput.value = ""
        inspectionResult.value = null
    }

    fun addCustomFilter(pattern: String, isDomain: Boolean) {
        viewModelScope.launch {
            repository.addCustomFilter(pattern, isDomain)
            emitMessage("Added '$pattern' to active blacklist.")
        }
    }

    fun deleteCustomFilter(id: Long) {
        viewModelScope.launch {
            repository.deleteCustomFilter(id)
            emitMessage("Filter rule removed.")
        }
    }

    fun toggleCustomFilter(id: Long, enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleCustomFilter(id, enabled)
        }
    }

    fun clearBlockedHistory() {
        viewModelScope.launch {
            repository.clearBlockedAttempts()
            emitMessage("Blocked activity log cleared.")
        }
    }

    fun markDnsConfigured(configured: Boolean, dnsName: String = "CleanBrowsing Family Filter") {
        repository.setDnsConfigured(configured, dnsName)
        emitMessage("Private DNS status updated.")
    }

    fun makeDailyPledge(note: String = "") {
        viewModelScope.launch {
            val success = repository.recordPledge(note)
            if (success) {
                emitMessage("Daily Pledge recorded! Stay strong today.")
            } else {
                emitMessage("You have already pledged for today.")
            }
        }
    }

    fun resetStreak() {
        repository.resetStreak()
        emitMessage("Streak tracker reset.")
    }

    fun startBreathingExercise() {
        isUrgeRescueOpen.value = true
        breathingCycleCount.value = 0
        breathingJob?.cancel()
        breathingJob = viewModelScope.launch {
            while (isUrgeRescueOpen.value && breathingCycleCount.value < 4) {
                // 4 seconds Breathe In
                breathingPhase.value = "Breathe In (4s)"
                for (i in 1..40) {
                    breathingProgress.value = i / 40f
                    delay(100)
                }

                // 7 seconds Hold
                breathingPhase.value = "Hold (7s)"
                for (i in 1..70) {
                    breathingProgress.value = 1f
                    delay(100)
                }

                // 8 seconds Breathe Out
                breathingPhase.value = "Exhale Slowly (8s)"
                for (i in 40 downTo 0) {
                    breathingProgress.value = i / 40f
                    delay(200)
                }

                breathingCycleCount.value += 1
            }
            breathingPhase.value = "Complete & Centered"
        }
    }

    fun stopBreathingExercise(wasHelpful: Boolean = true) {
        breathingJob?.cancel()
        isUrgeRescueOpen.value = false
        if (wasHelpful) {
            viewModelScope.launch {
                repository.recordUrgeLog(
                    triggerType = "Urge Surfing Breathing",
                    mood = "Calm & In Control",
                    intensity = 3,
                    wasOvercome = true,
                    notes = "Completed 4-7-8 breathing session."
                )
                emitMessage("Urge navigated successfully! Proud of your self-control.")
            }
        }
    }

    fun getExportConfigString(): String {
        val state = shieldState.value
        val filters = customFilters.value.map { it.pattern }
        val streakDays = ((System.currentTimeMillis() - state.streakStartTimestamp) / 86400000L).toInt().coerceAtLeast(1)

        return """
            ===========================================
            🛡️ PURESHIELD 24/7 ANDROID SECURITY PROFILE
            ===========================================
            Exported: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}
            
            [SYSTEM PROTECTION STATUS]
            - Protection Status: ${if (state.isActive) "ACTIVE 24/7" else "PAUSED"}
            - Protection Level: ${state.protectionLevel.title}
            - Strict Mode Locked: ${state.isStrictModeLocked}
            - Clean Streak Days: $streakDays Days
            - Intercepted Threats: ${state.totalInterceptedCount}
            
            [NETWORK & DNS SECURITY]
            - System-Wide Private DNS: ${state.dnsActiveType}
            - Private DNS Hostname: family-filter-dns.cleanbrowsing.org
            - Status: ${if (state.isDnsConfigured) "CONFIGURED & ACTIVE" else "PENDING SETUP"}
            
            [JINA KILLER & MORAL SECURITY]
            - Zina / Adult Keyword Interceptor: ACTIVE
            - Romance & Kissing Filter: ACTIVE
            - Figure / Curvy Attire Guard: ACTIVE
            - Vulgar Dance & Music Interceptor: ACTIVE
            - Sex Story & Erotica Blocker: ACTIVE
            - Visual Modesty Blur Guard: ACTIVE
            
            [4-TIER ANTI-BYPASS FORTRESS]
            - 1. Block App Uninstall: Device Admin + Sentinel Armed
            - 2. Block DNS Tampering: Settings Locked (5-Min Freeze)
            - 3. Block Accessibility Off: Intercepted (5-Min Freeze)
            - 4. Block Phone Admin Off: Intercepted (5-Min Freeze)
            
            [CUSTOM ACTIVE BLACKLIST (${filters.size})]
            ${filters.joinToString("\n") { "  * $it" }}
            
            ===========================================
            Install PureShield APK on your Android phone via AI Studio's
            Export menu (Download APK).
            ===========================================
        """.trimIndent()
    }

    private fun emitMessage(msg: String) {
        viewModelScope.launch {
            _userMessage.emit(msg)
        }
    }
}

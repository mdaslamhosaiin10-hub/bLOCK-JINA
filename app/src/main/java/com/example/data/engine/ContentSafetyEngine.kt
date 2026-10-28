package com.example.data.engine

import com.example.data.model.CustomFilterRule
import java.util.Locale

enum class RiskLevel {
    SAFE,
    SUSPICIOUS,
    HIGH_RISK,
    CRITICAL_ADULT_CONTENT,
    TAMPER_ATTEMPT,
    JINA_KILLER_LOCKDOWN
}

data class SafetyInspectionResult(
    val query: String,
    val isBlocked: Boolean,
    val riskLevel: RiskLevel,
    val category: String,
    val matchedPattern: String?,
    val explanation: String,
    val recommendedAction: String
)

object ContentSafetyEngine {

    // Common adult top-level domains and known explicit patterns
    private val ADULT_TLDS = listOf(
        ".xxx", ".adult", ".porn", ".sex", ".sexy", ".cam", ".dating", ".casino", ".bet"
    )

    // Jina / Zina / Hardcore Adult Sexual Triggers
    private val JINA_ADULT_KEYWORDS = listOf(
        "jina", "zina", "porn", "xxx", "nsfw", "hentai", "erotic", "sex", "camgirl", "chaturbate",
        "onlyfans", "redtube", "pornhub", "xvideos", "xnxx", "xhamster", "youporn",
        "playboy", "strip", "nude", "nudity", "hardcore", "softcore", "escort",
        "blowjob", "milf", "bdsm", "fetish", "erotica", "sensual massage", "adult game",
        "explicit", "webcam sex", "stripchat", "bongacams", "livejasmin", "rule34",
        "jav", "brazzers", "bangbros", "naughty", "slut", "incest", "deepfake porn",
        "sexy girl", "hot girl", "nude girl", "naked girl", "girl sex", "xxx girl",
        "call girl", "desisex", "bhabhi sex", "desi porn", "adult video", "adult film",
        "free hookup", "unprotected adult", "leak tape"
    )

    // Jua / Gambling / Betting Triggers
    private val JUA_GAMBLING_KEYWORDS = listOf(
        "jua", "gambling", "casino", "1xbet", "betting", "bet365", "poker", "slots",
        "roulette", "baccarat", "online casino", "betway", "melbet", "stake.com", "ipl betting"
    )

    // Romance, Kiss, and Suggestive Foreplay Triggers
    private val ROMANCE_KISS_KEYWORDS = listOf(
        "romance", "romantic", "romantic scene", "hot romance", "kiss", "kissing", "french kiss",
        "lip kiss", "love bite", "foreplay", "intimate scene", "love making", "bedroom romance",
        "hot hug", "cuddle hot", "romanchak"
    )

    // Body Figure, Revealing Attire & Oshalin Poshak Triggers
    private val FIGURE_BODY_KEYWORDS = listOf(
        "figer", "figure", "hot figure", "zero figure", "sexy figure", "curvy figure",
        "cleavage", "deep neck", "low waist", "bikini body", "swimsuit", "hot pose",
        "revealing dress", "oshalin", "oshalin poshak", "bikini", "lingerie", "tight dress hot"
    )

    // Vulgar Dance, Item Song, and Suggestive Media Triggers
    private val DANCE_SONG_KEYWORDS = listOf(
        "dance hot", "hot dance", "sexy dance", "item song", "hot song", "sexy song",
        "mujra", "belly dance", "twerk", "sensual dance", "rain dance", "rain song hot",
        "stage dance hot", "erotic dance", "vulgar dance", "reels hot dance"
    )

    // Sex Story, Erotica, and Choti Golpo Triggers
    private val SEX_STORY_CHOTI_KEYWORDS = listOf(
        "sex story", "choti", "choti golpo", "erotic story", "erotica", "adult story",
        "bhabhi story", "devar bhabhi", "adult novel", "hot scene", "bed scene",
        "sex scene", "viral mms", "leak mms", "leaked video", "sex audio", "choti kahani"
    )

    /**
     * Checks if the user is trying to bypass security via:
     * 1. Uninstalling the app
     * 2. Changing or turning off Private DNS
     * 3. Disabling the Accessibility Service
     * 4. Deactivating Device Administrator rights
     */
    fun detectSystemBypass(screenText: String, packageName: String? = null): String? {
        val clean = screenText.trim().lowercase(Locale.ROOT)
        val pkg = packageName?.lowercase(Locale.ROOT) ?: ""

        // Vector 1: Attempting to uninstall PureShield or clear its data/force stop
        if ((clean.contains("pureshield") || pkg.contains("packageinstaller")) &&
            (clean.contains("uninstall") || clean.contains("force stop") || clean.contains("clear storage") || clean.contains("delete app") || clean.contains("clear data"))) {
            return "App Uninstall / Force Stop Attempt Intercepted"
        }

        // Vector 2: Attempting to change or disable Private DNS
        if (clean.contains("private dns") &&
            (clean.contains("off") || clean.contains("turn off") || clean.contains("disable") || clean.contains("automatic") || clean.contains("select private dns"))) {
            return "Private DNS Tampering / Turn Off Attempt Intercepted"
        }

        // Vector 3: Attempting to turn off Accessibility Service for PureShield
        if ((clean.contains("pureshield") || clean.contains("accessibility")) &&
            (clean.contains("stop pureshield") || clean.contains("turn off pureshield") || clean.contains("stop service?") || clean.contains("disable pureshield") || (clean.contains("use pureshield") && clean.contains("off")))) {
            return "Accessibility Service Disabling Attempt Intercepted"
        }

        // Vector 4: Attempting to deactivate Device Administrator
        if ((clean.contains("device admin") || clean.contains("device administrator")) &&
            (clean.contains("deactivate") || clean.contains("remove device admin") || clean.contains("turn off") || clean.contains("deactivate this device admin app"))) {
            return "Device Admin Deactivation Attempt Intercepted"
        }

        return null
    }

    fun isTamperAttempt(screenText: String, packageName: String? = null): Boolean {
        return detectSystemBypass(screenText, packageName) != null
    }

    /**
     * Ultra-Strict Jina Killer check for real-time keystroke and screen monitoring.
     * Intercepts:
     * - Romance & Kissing
     * - Figure / Body curves
     * - Vulgar Dance & Songs
     * - Sex Story & Choti Golpo
     * - Jina & Adult triggers
     * - Jua & Gambling
     * - Anti-Tamper & Bypass
     */
    fun isExplicitTrigger(
        input: String,
        customFilters: List<CustomFilterRule> = emptyList()
    ): Triple<Boolean, String?, String> {
        val cleanInput = input.trim().lowercase(Locale.ROOT)
        if (cleanInput.length < 2) return Triple(false, null, "")

        // 1. Check custom user blacklist first
        for (filter in customFilters.filter { it.isEnabled }) {
            val pattern = filter.pattern.trim().lowercase(Locale.ROOT)
            if (pattern.isNotEmpty() && cleanInput.contains(pattern)) {
                return Triple(true, filter.pattern, "Custom Filter")
            }
        }

        // 2. Check System Anti-Bypass
        val bypassReason = detectSystemBypass(cleanInput)
        if (bypassReason != null) {
            return Triple(true, bypassReason, "Anti-Tamper Lockdown")
        }

        // 3. Check Sex Story & Choti Golpo
        for (story in SEX_STORY_CHOTI_KEYWORDS) {
            if (cleanInput.contains(story)) {
                return Triple(true, story, "Sex Story & Erotica Guard")
            }
        }

        // 4. Check Vulgar Dance & Song
        for (dance in DANCE_SONG_KEYWORDS) {
            if (cleanInput.contains(dance)) {
                return Triple(true, dance, "Vulgar Dance & Song Guard")
            }
        }

        // 5. Check Body Figure & Revealing Poshak
        for (fig in FIGURE_BODY_KEYWORDS) {
            if (cleanInput.contains(fig)) {
                return Triple(true, fig, "Modesty & Figure Guard")
            }
        }

        // 6. Check Romance & Kissing
        for (rom in ROMANCE_KISS_KEYWORDS) {
            if (cleanInput.contains(rom)) {
                return Triple(true, rom, "Romance & Sensual Filter")
            }
        }

        // 7. Check Jua / Gambling
        for (jua in JUA_GAMBLING_KEYWORDS) {
            if (cleanInput.contains(jua)) {
                return Triple(true, jua, "Jua & Gambling")
            }
        }

        // 8. Check Adult TLDs
        for (tld in ADULT_TLDS) {
            if (cleanInput.contains(tld)) {
                return Triple(true, tld, "Adult Domain")
            }
        }

        // 9. Check Jina & Adult Keywords
        for (keyword in JINA_ADULT_KEYWORDS) {
            if (cleanInput.contains(keyword)) {
                return Triple(true, keyword, "Jina & Adult Content")
            }
        }

        // 10. Word-token level checks for short terms
        val tokens = cleanInput.split(Regex("[^a-zA-Z0-9]+"))
        for (token in tokens) {
            if (token == "xxx" || token == "sex" || token == "jua" || token == "porn" ||
                token == "zina" || token == "jina" || token == "sexy" || token == "hot" ||
                token == "kiss" || token == "choti" || token == "figer") {
                return Triple(true, token, "Lust & Jina Trigger")
            }
        }

        // 11. Combinations with "girl", "bhabhi", "aunty", "model", "dance", "song"
        val subjectTokens = setOf("girl", "bhabhi", "boudi", "aunty", "model", "actress", "dance", "song")
        val suggestiveModifiers = setOf(
            "hot", "sexy", "nude", "naked", "sex", "cam", "xxx", "dating", "escort",
            "dance", "bikini", "poshak", "romance", "romantic", "figure", "figer",
            "curvy", "kiss", "clip", "leak", "mms", "viral", "bed", "scene", "story"
        )

        for (token in tokens) {
            if (subjectTokens.contains(token)) {
                // Check if any other token is suggestive
                for (other in tokens) {
                    if (other != token && suggestiveModifiers.contains(other)) {
                        return Triple(true, "$token + $other", "Jina Killer Trigger")
                    }
                }
            }
        }

        return Triple(false, null, "")
    }

    /**
     * Deep inspection for sandbox and audit logging.
     */
    fun inspectContent(
        input: String,
        customFilters: List<CustomFilterRule> = emptyList()
    ): SafetyInspectionResult {
        val (isTriggered, trigger, category) = isExplicitTrigger(input, customFilters)

        if (isTriggered && trigger != null) {
            val risk = if (category == "Anti-Tamper Lockdown") RiskLevel.TAMPER_ATTEMPT else RiskLevel.JINA_KILLER_LOCKDOWN
            val action = if (category == "Anti-Tamper Lockdown") {
                "System Anti-Bypass barrier activated! 5-minute phone lockout initiated to block uninstall, DNS modification, or service deactivation."
            } else {
                "Jina Killer Protocol triggered: Screen locked instantly to guard eyes, heart, and purity."
            }

            return SafetyInspectionResult(
                query = input,
                isBlocked = true,
                riskLevel = risk,
                category = category,
                matchedPattern = trigger,
                explanation = "Ultra-Strict Jina Killer Policy violated ($category): '$trigger'. Access strictly barred.",
                recommendedAction = action
            )
        }

        return SafetyInspectionResult(
            query = input,
            isBlocked = false,
            riskLevel = RiskLevel.SAFE,
            category = "Safe & Pure",
            matchedPattern = null,
            explanation = "Content verified clean, pure, and free of suggestive, romantic, or adult triggers.",
            recommendedAction = "Safe to browse."
        )
    }
}

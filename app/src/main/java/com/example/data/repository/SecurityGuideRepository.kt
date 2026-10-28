package com.example.data.repository

data class SecurityGuideTopic(
    val id: String,
    val title: String,
    val category: String,
    val summary: String,
    val severityLevel: String, // "CRITICAL", "RECOMMENDED", "PROACTIVE"
    val steps: List<String>,
    val settingsAction: String? = null, // Intent action for Android settings
    val iconName: String
)

object SecurityGuideCatalog {
    val topics = listOf(
        SecurityGuideTopic(
            id = "private_dns_shield",
            title = "Activate System-Wide Private DNS Shield",
            category = "Network Level Protection",
            summary = "Block all adult websites, explicit images, and pornographic networks before any browser can access them.",
            severityLevel = "CRITICAL",
            steps = listOf(
                "Copy CleanBrowsing Family Hostname: family-filter-dns.cleanbrowsing.org",
                "Open Android Settings > Network & internet > Private DNS.",
                "Choose 'Private DNS provider hostname' and paste the CleanBrowsing address.",
                "Tap Save. Protection applies to Wi-Fi and mobile data 24/7 without battery drain."
            ),
            settingsAction = "android.settings.NETWORK_OPERATOR_SETTINGS",
            iconName = "Security"
        ),
        SecurityGuideTopic(
            id = "safesearch_lock",
            title = "Enforce SafeSearch on Search Engines",
            category = "Search Engine Filters",
            summary = "Prevent explicit visual previews, pornographic suggestions, and adult links in search queries.",
            severityLevel = "CRITICAL",
            steps = listOf(
                "In Google App / Chrome: Go to Settings > SafeSearch > Select 'Filter'.",
                "On YouTube: Tap Profile > Settings > General > Turn on 'Restricted Mode'.",
                "On Bing: Go to Settings > SafeSearch > Set to 'Strict'.",
                "With CleanBrowsing DNS active, SafeSearch is permanently forced even if toggled off locally."
            ),
            settingsAction = null,
            iconName = "Search"
        ),
        SecurityGuideTopic(
            id = "incognito_guard",
            title = "Restricting Incognito & Private Browsing",
            category = "Browser Privacy & Safety",
            summary = "Private browsing often acts as a blind spot for impulsive adult viewing. Learn how to secure it.",
            severityLevel = "RECOMMENDED",
            steps = listOf(
                "Install a digital wellbeing filter or use Google Family Link to block Incognito mode.",
                "Alternatively, in Chrome Enterprise/Work profile, set 'IncognitoModeAvailability' policy to Disabled.",
                "Set browser history to auto-sync with your accountability contact."
            ),
            settingsAction = "android.settings.SECURITY_SETTINGS",
            iconName = "VisibilityOff"
        ),
        SecurityGuideTopic(
            id = "late_night_bedtime",
            title = "Late-Night Vulnerability & Bedtime Lockdown",
            category = "Habit & Psychology",
            summary = "82% of adult content relapses occur in bed between 11 PM and 5 AM. Establish digital boundaries.",
            severityLevel = "RECOMMENDED",
            steps = listOf(
                "Enable Android 'Bedtime Mode' (Grayscale display + Do Not Disturb).",
                "Grayscale display reduces dopamine arousal from bright, vivid colors.",
                "Keep phone charger outside the bedroom or at least 10 feet away from your bed.",
                "If feeling an urge late at night, immediately open the Urge Rescue tool for a 2-minute reset."
            ),
            settingsAction = "android.settings.USAGE_ACCESS_SETTINGS",
            iconName = "NightsStay"
        ),
        SecurityGuideTopic(
            id = "permission_audit",
            title = "Camera & Microphone Privacy Guard",
            category = "Device Privacy",
            summary = "Protect yourself from malicious adult webcam blackmail scams (sextortion) and spyware.",
            severityLevel = "CRITICAL",
            steps = listOf(
                "Check Android 'Permission Manager' for apps with active Camera or Microphone access.",
                "Revoke access for unknown browsers, APKs installed outside Play Store, or sketchy tools.",
                "Enable the Android 12+ green camera/microphone privacy indicators in notification shade.",
                "Never share intimate media or engage in video calls with unverified contacts."
            ),
            settingsAction = "android.settings.PRIVACY_SETTINGS",
            iconName = "PrivacyTip"
        ),
        SecurityGuideTopic(
            id = "accountability_barrier",
            title = "Cool-Down Pause & Strict Guardian Lock",
            category = "Impulse Breaking",
            summary = "Impulses usually pass within 3 to 10 minutes if an artificial friction barrier is introduced.",
            severityLevel = "PROACTIVE",
            steps = listOf(
                "Enable Strict Mode in PureShield with a 4-digit master PIN.",
                "PureShield enforces a 60-second breathing timer before settings can be loosened.",
                "This crucial 60-second window reactivates your logical prefrontal cortex, diffusing emotional impulse."
            ),
            settingsAction = null,
            iconName = "Lock"
        )
    )
}

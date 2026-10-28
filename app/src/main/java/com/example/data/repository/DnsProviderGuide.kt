package com.example.data.repository

data class DnsProvider(
    val id: String,
    val name: String,
    val hostname: String,
    val description: String,
    val features: List<String>,
    val recommendedBadge: Boolean = false
)

object DnsProviderCatalog {
    val providers = listOf(
        DnsProvider(
            id = "cleanbrowsing",
            name = "CleanBrowsing Family Filter",
            hostname = "family-filter-dns.cleanbrowsing.org",
            description = "The gold standard for blocking adult sites, explicit search results, and enforcing SafeSearch system-wide.",
            features = listOf(
                "Blocks all adult websites and pornography",
                "Enforces Google, Bing, and DuckDuckGo SafeSearch",
                "Forces YouTube Restricted Mode automatically",
                "Blocks phishing, malware, and malicious domains"
            ),
            recommendedBadge = true
        ),
        DnsProvider(
            id = "adguard_family",
            name = "AdGuard Family Protection",
            hostname = "family.adguard-dns.com",
            description = "High-performance privacy DNS that blocks adult content, explicit ads, and tracking scripts.",
            features = listOf(
                "Blocks adult & explicit websites",
                "Filters intrusive adult banner popups",
                "Enforces safe search algorithms",
                "No logs, strict privacy policy"
            )
        ),
        DnsProvider(
            id = "cloudflare_family",
            name = "Cloudflare 1.1.1.3 Family",
            hostname = "security.cloudflare-dns.com",
            description = "Ultra-fast global Anycast network blocking malware and adult content simultaneously.",
            features = listOf(
                "Blocks adult domain categories",
                "Cloudflare edge DDoS and malware defense",
                "Minimal latency for fast browsing",
                "Zero tracking"
            )
        )
    )

    val stepByStepInstructions = listOf(
        "Open your phone's Android 'Settings' app.",
        "Navigate to 'Network & internet' (or 'Connections' on Samsung devices).",
        "Tap on 'Private DNS' (or search 'Private DNS' in the top search bar).",
        "Select the option 'Private DNS provider hostname'.",
        "Paste the hostname from PureShield (e.g. family-filter-dns.cleanbrowsing.org).",
        "Tap 'Save'. Your entire device is now 24/7 protected from adult content across all browsers and apps!"
    )
}

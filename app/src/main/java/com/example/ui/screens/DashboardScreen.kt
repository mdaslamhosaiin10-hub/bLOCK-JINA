package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ProtectionLevel
import com.example.ui.components.ShieldRadarPulse
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyberCyanLight
import com.example.ui.theme.CyberCyanSecondary
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.SecurityGold
import com.example.ui.theme.ShieldGreenDark
import com.example.ui.theme.ShieldGreenLight
import com.example.ui.theme.ShieldGreenPrimary
import com.example.ui.viewmodel.ShieldViewModel

@Composable
fun DashboardScreen(
    viewModel: ShieldViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val shieldState by viewModel.shieldState.collectAsStateWithLifecycle()
    val blockedAttempts by viewModel.blockedAttempts.collectAsStateWithLifecycle()

    val streakDays = ((System.currentTimeMillis() - shieldState.streakStartTimestamp) / 86400000L)
        .toInt().coerceAtLeast(1)

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Top Bar Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "PureShield 24/7",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Text(
                    text = "Personal Safety & Adult Content Blocker",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (shieldState.isActive) ShieldGreenDark.copy(alpha = 0.4f)
                        else AlertRed.copy(alpha = 0.3f)
                    )
                    .border(
                        1.dp,
                        if (shieldState.isActive) ShieldGreenPrimary else AlertRed,
                        RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (shieldState.isActive) ShieldGreenLight else AlertRed)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (shieldState.isActive) "24/7 ON" else "PAUSED",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (shieldState.isActive) ShieldGreenLight else AlertRed
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Center Pulsating Shield
        ShieldRadarPulse(
            isActive = shieldState.isActive,
            isStrictModeLocked = shieldState.isStrictModeLocked,
            uptimeText = "Uptime: ${streakDays * 24}h continuous",
            onToggleClick = { viewModel.toggleProtection() }
        )

        // 24/7 Phone Activity Sentinel Card (User's specific core request)
        val isSentinelOn = com.example.service.PureShieldAccessibilityService.isAccessibilitySettingsOn(context)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .testTag("phone_sentinel_card"),
            colors = CardDefaults.cardColors(containerColor = if (isSentinelOn) Color(0xFF0F261F) else Color(0xFF291B18)),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                if (isSentinelOn) ShieldGreenLight else SecurityGold
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (isSentinelOn) ShieldGreenLight else SecurityGold)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "24/7 Live Phone Activity Sentinel",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSentinelOn) ShieldGreenDark else SecurityGold.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (isSentinelOn) "ACTIVE SENTINEL" else "SETUP SENTINEL",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSentinelOn) ShieldGreenLight else SecurityGold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Monitors text and search queries across your phone 24/7. If explicit searches like 'xxx', 'sex', or adult triggers are detected in any browser or app, PureShield instantly kicks you out and locks the screen for 60 seconds.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSentinelOn) DarkSurfaceElevated else SecurityGold
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("enable_sentinel_button")
                    ) {
                        Text(
                            text = if (isSentinelOn) "Sentinel Settings" else "Turn On Sentinel",
                            fontSize = 11.5.sp,
                            color = if (isSentinelOn) Color.White else Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            val lockoutIntent = Intent(context, com.example.LockoutActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                putExtra(com.example.LockoutActivity.EXTRA_TRIGGER, "xxx / sex (Test Trigger)")
                                putExtra(com.example.LockoutActivity.EXTRA_QUERY, "test search xxx")
                            }
                            context.startActivity(lockoutIntent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("test_lockout_button")
                    ) {
                        Text("Test Instant Block", fontSize = 11.5.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Urge Surfing Emergency Safe Space Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable { viewModel.startBreathingExercise() }
                .testTag("open_urge_rescue_card"),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF162544)
            ),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, CyberCyanSecondary)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(CyberCyanSecondary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SelfImprovement,
                            contentDescription = null,
                            tint = CyberCyanLight,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Urge Rescue & Safe Space",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Feeling triggered? Tap for 2-min calm breathing",
                            fontSize = 12.sp,
                            color = CyberCyanLight
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Filled.OpenInNew,
                    contentDescription = null,
                    tint = CyberCyanLight,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Download APK for Android Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .testTag("download_apk_card"),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131D36)),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberCyanSecondary)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Download,
                        contentDescription = null,
                        tint = CyberCyanLight,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Download APK for Your Android Phone",
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Install PureShield on your real phone: Open AI Studio's top menu (Export icon in top right) -> Select 'Export' -> 'Download APK'. Transfer or open on Android to install.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        val exportString = viewModel.getExportConfigString()
                        val sendIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "PureShield_Security_Profile.txt")
                            putExtra(Intent.EXTRA_TEXT, exportString)
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Download / Export PureShield Profile"))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberCyanSecondary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("export_config_file_button")
                ) {
                    Icon(Icons.Filled.Share, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export & Download Security Config File", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Metrics Grid (Clean Days, Interceptions, Level)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Card 1: Clean Days
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Pure Streak", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$streakDays Days",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = ShieldGreenLight
                    )
                    Text(text = "Safe & Active", fontSize = 10.sp, color = Color.Gray)
                }
            }

            // Card 2: Blocked Interceptions
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Intercepted", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${blockedAttempts.size + shieldState.totalInterceptedCount}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = SecurityGold
                    )
                    Text(text = "Adult Threats Blocked", fontSize = 10.sp, color = Color.Gray)
                }
            }

            // Card 3: DNS Filter
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "DNS Shield", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (shieldState.isDnsConfigured) "Active" else "Setup",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = if (shieldState.isDnsConfigured) ShieldGreenLight else AlertRed
                    )
                    Text(text = "Family Filter", fontSize = 10.sp, color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Protection Level Selection
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Protection Mode",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 15.sp
                    )
                    Text(
                        text = shieldState.protectionLevel.title,
                        fontWeight = FontWeight.SemiBold,
                        color = ShieldGreenLight,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ProtectionLevel.values().forEach { level ->
                        val isSelected = shieldState.protectionLevel == level
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setProtectionLevel(level) },
                            label = {
                                Text(
                                    text = level.title,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ShieldGreenDark,
                                selectedLabelColor = Color.White,
                                containerColor = DarkSurfaceVariant,
                                labelColor = Color.LightGray
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("level_chip_${level.name}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = shieldState.protectionLevel.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Quick DNS Configuration Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Dns, contentDescription = null, tint = ShieldGreenLight)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "CleanBrowsing Private DNS",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Android native Private DNS blocks adult content system-wide across all browsers, incognito sessions, and apps without VPN battery consumption.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0F172A))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "family-filter-dns.cleanbrowsing.org",
                        fontSize = 12.sp,
                        color = CyberCyanLight,
                        fontWeight = FontWeight.Medium
                    )

                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("DNS Hostname", "family-filter-dns.cleanbrowsing.org")
                            clipboard.setPrimaryClip(clip)
                            viewModel.markDnsConfigured(true)
                        },
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("copy_dns_button")
                    ) {
                        Icon(
                            Icons.Filled.ContentCopy,
                            contentDescription = "Copy DNS",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            try {
                                context.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                            } catch (e: Exception) {
                                context.startActivity(Intent(Settings.ACTION_SETTINGS))
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ShieldGreenDark),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("open_dns_settings_button")
                    ) {
                        Text("Open Settings", fontSize = 12.sp, color = Color.White)
                    }

                    Button(
                        onClick = {
                            viewModel.isSetPinMode.value = true
                            viewModel.isPinDialogOpen.value = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2B47)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("setup_pin_button")
                    ) {
                        Icon(Icons.Filled.Lock, contentDescription = null, modifier = Modifier.size(14.dp), tint = SecurityGold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Lock PIN", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

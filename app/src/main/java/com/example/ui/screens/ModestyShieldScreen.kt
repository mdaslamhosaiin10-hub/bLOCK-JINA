package com.example.ui.screens

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.LockoutActivity
import com.example.receiver.PureShieldAdminReceiver
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

data class ProtectedAppCategory(
    val id: String,
    val name: String,
    val appNames: String,
    val description: String,
    val iconName: String
)

@Composable
fun ModestyShieldScreen(
    viewModel: ShieldViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Device Admin status
    val dpm = remember { context.getSystemService(Context.DEVICE_POLICY_SERVICE) as? DevicePolicyManager }
    val adminComponent = remember { ComponentName(context, PureShieldAdminReceiver::class.java) }
    var isAdminActive by remember { mutableStateOf(dpm?.isAdminActive(adminComponent) == true) }

    // Modesty Blur State
    var isBlurShieldEnabled by remember { mutableStateOf(true) }
    var blurRadius by remember { mutableFloatStateOf(24f) }
    var isSampleRevealed by remember { mutableStateOf(false) }

    val protectedApps = listOf(
        ProtectedAppCategory(
            id = "browser",
            name = "Web Browsers",
            appNames = "Google Chrome, Samsung Internet, Firefox, Opera",
            description = "Enforces adult domain blocks, strict SafeSearch, and blocks Private DNS bypass.",
            iconName = "Security"
        ),
        ProtectedAppCategory(
            id = "ai_apps",
            name = "AI & Chat Assistants",
            appNames = "Google Gemini, ChatGPT, Claude, Character.AI",
            description = "Filters prompts and responses against Jina, sexual roleplay, and vulgar talk.",
            iconName = "SmartToy"
        ),
        ProtectedAppCategory(
            id = "video_dance",
            name = "Video & Social Feeds",
            appNames = "YouTube, Facebook, Instagram, TikTok",
            description = "Restricts explicit video thumbnails, vulgar item dance, and suggestive reels.",
            iconName = "Visibility"
        ),
        ProtectedAppCategory(
            id = "gambling",
            name = "Jua & Betting Platforms",
            appNames = "1xBet, Bet365, Online Casinos, Poker, Slots",
            description = "100% hard block on gambling, lottery, and betting web links.",
            iconName = "Lock"
        )
    )

    val appStatusMap = remember {
        mutableStateMapOf(
            "browser" to true,
            "ai_apps" to true,
            "video_dance" to true,
            "gambling" to true
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Modesty & Anti-Tamper Shield",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Text(
                text = "Protects against Jina, Jua, vulgar dance, and locks settings against uninstallation.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Anti-Uninstall Device Administrator Enforcer
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isAdminActive) Color(0xFF0F261F) else Color(0xFF291B18)
                ),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (isAdminActive) ShieldGreenLight else AlertRed
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.AdminPanelSettings,
                                contentDescription = null,
                                tint = if (isAdminActive) ShieldGreenLight else AlertRed,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Anti-Uninstall Lock (5-Min Freeze)",
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isAdminActive) ShieldGreenDark else AlertRed.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (isAdminActive) "ARMED" else "UNLOCKED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAdminActive) ShieldGreenLight else AlertRed
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "4-Tier System Anti-Bypass Fortress prevents disabling your protection:",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    val barriers = listOf(
                        "🚫 Block App Uninstall: Device Admin + Sentinel immediately blocks package uninstall.",
                        "🚫 Block DNS Change: Settings locked if Private DNS is set to Off or altered.",
                        "🚫 Block Accessibility Off: Intercepts and denies disabling PureShield Sentinel.",
                        "🚫 Block Phone Admin Deactivation: Prevents removing administrator authorization."
                    )

                    barriers.forEach { barrier ->
                        Text(
                            text = barrier,
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                                    putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
                                    putExtra(
                                        DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                                        "PureShield Anti-Uninstall Protection prevents accidental or impulsive deletion to maintain your 24/7 moral security."
                                    )
                                }
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAdminActive) DarkSurfaceElevated else ShieldGreenPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (isAdminActive) "Device Admin Active" else "Activate Device Admin",
                                fontSize = 11.5.sp,
                                color = if (isAdminActive) Color.White else Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = {
                                val lockoutIntent = Intent(context, LockoutActivity::class.java).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    putExtra(LockoutActivity.EXTRA_TRIGGER, "Tampering / Uninstall Attempt (Demo)")
                                    putExtra(LockoutActivity.EXTRA_IS_TAMPER, true)
                                    putExtra(LockoutActivity.EXTRA_IS_TEST, true)
                                }
                                context.startActivity(lockoutIntent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Filled.PowerSettingsNew, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Test 5-Min Lock", fontSize = 11.5.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Visual Modesty & Photo/Video Blur Guard Simulator
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.BlurOn, contentDescription = null, tint = CyberCyanLight)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Visual Modesty Blur Shield",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Switch(
                            checked = isBlurShieldEnabled,
                            onCheckedChange = { isBlurShieldEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ShieldGreenPrimary,
                                checkedTrackColor = ShieldGreenDark
                            )
                        )
                    }

                    Text(
                        text = "Blurs immodest photos, vulgar dance videos, and revealing attire (oshalin poshak) across feeds to protect your vision.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    // Blur Radius Slider
                    Text(
                        text = "Curtain Blur Intensity: ${blurRadius.toInt()}px",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyanLight
                    )

                    Slider(
                        value = blurRadius,
                        onValueChange = { blurRadius = it },
                        valueRange = 8f..50f,
                        colors = SliderDefaults.colors(
                            thumbColor = ShieldGreenPrimary,
                            activeTrackColor = ShieldGreenLight,
                            inactiveTrackColor = DarkSurfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Live Interactive Visual Preview Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFF831843),
                                        Color(0xFF4C0519),
                                        Color(0xFF1E1B4B)
                                    )
                                )
                            )
                            .border(1.dp, CyberCyanSecondary.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Blurred content simulation
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .then(
                                    if (isBlurShieldEnabled && !isSampleRevealed) {
                                        Modifier.blur(blurRadius.dp)
                                    } else {
                                        Modifier
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "💃 [ MEDIA STREAM WITH IMMODEST ATTIRE ]",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "Item Song / Vulgar Dance / Revealing Video",
                                    color = SecurityGold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Shield Overlay Indicator
                        if (isBlurShieldEnabled && !isSampleRevealed) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.Black.copy(alpha = 0.7f))
                                    .border(1.dp, ShieldGreenLight, RoundedCornerShape(20.dp))
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.VisibilityOff, contentDescription = null, tint = ShieldGreenLight, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "PROTECTED: MODESTY BLUR APPLIED",
                                        color = ShieldGreenLight,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isSampleRevealed) "⚠️ Warning: Preview Unblurred" else "🛡️ Eyes Protected from Indecency",
                            fontSize = 11.5.sp,
                            color = if (isSampleRevealed) AlertRed else ShieldGreenLight
                        )

                        TextButton(onClick = { isSampleRevealed = !isSampleRevealed }) {
                            Text(
                                text = if (isSampleRevealed) "Re-apply Blur" else "Peek Preview",
                                fontSize = 11.sp,
                                color = CyberCyanLight
                            )
                        }
                    }
                }
            }
        }

        // Protected App Matrix
        item {
            Text(
                text = "Protected App Matrix (Chrome, Gemini, YT, FB)",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(protectedApps) { appCategory ->
            val isEnabled = appStatusMap[appCategory.id] ?: true

            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isEnabled) ShieldGreenDark.copy(alpha = 0.3f) else DarkSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (appCategory.iconName) {
                                    "SmartToy" -> Icons.Filled.SmartToy
                                    "Visibility" -> Icons.Filled.Visibility
                                    "Lock" -> Icons.Filled.Lock
                                    else -> Icons.Filled.Security
                                },
                                contentDescription = null,
                                tint = if (isEnabled) ShieldGreenLight else Color.Gray,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = appCategory.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = appCategory.appNames,
                                fontSize = 11.sp,
                                color = CyberCyanLight,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = appCategory.description,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { appStatusMap[appCategory.id] = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = ShieldGreenPrimary,
                            checkedTrackColor = ShieldGreenDark
                        )
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

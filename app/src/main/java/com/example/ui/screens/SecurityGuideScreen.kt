package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.DnsProviderCatalog
import com.example.data.repository.SecurityGuideCatalog
import com.example.data.repository.SecurityGuideTopic
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
fun SecurityGuideScreen(
    viewModel: ShieldViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val expandedTopics = remember { mutableStateMapOf<String, Boolean>() }
    var selectedDnsProvider by remember { mutableStateOf(DnsProviderCatalog.providers.first()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "24/7 Security & Privacy Guide",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Text(
                text = "Actionable device hardening to eliminate adult content access and safeguard personal privacy.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Family DNS Profile Selector
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Security, contentDescription = null, tint = ShieldGreenLight)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Verified Adult-Blocking DNS Profiles",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Text(
                        text = "Private DNS works 24/7 across all Wi-Fi & cellular networks without third-party VPN apps.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                    )

                    // Provider Tabs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        DnsProviderCatalog.providers.forEach { provider ->
                            val isSelected = selectedDnsProvider.id == provider.id
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) ShieldGreenDark else DarkSurfaceVariant)
                                    .clickable { selectedDnsProvider = provider }
                                    .padding(vertical = 8.dp, horizontal = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (provider.id == "cleanbrowsing") "CleanBrowsing" else if (provider.id == "adguard_family") "AdGuard" else "Cloudflare",
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else Color.LightGray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = selectedDnsProvider.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Hostname Copy Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F172A))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "DNS Hostname", fontSize = 10.sp, color = Color.Gray)
                            Text(
                                text = selectedDnsProvider.hostname,
                                fontSize = 12.sp,
                                color = CyberCyanLight,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("DNS Hostname", selectedDnsProvider.hostname)
                                clipboard.setPrimaryClip(clip)
                                viewModel.markDnsConfigured(true, selectedDnsProvider.name)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ShieldGreenPrimary),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.testTag("copy_selected_dns")
                        ) {
                            Icon(Icons.Filled.ContentCopy, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            try {
                                context.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                            } catch (e: Exception) {
                                context.startActivity(Intent(Settings.ACTION_SETTINGS))
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Open Android Private DNS Settings", color = Color.White, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                    }
                }
            }
        }

        item {
            Text(
                text = "Comprehensive Security Pillars",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(top = 6.dp)
            )
        }

        items(SecurityGuideCatalog.topics, key = { it.id }) { topic ->
            val isExpanded = expandedTopics[topic.id] ?: false

            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedTopics[topic.id] = !isExpanded }
                    .testTag("topic_card_${topic.id}")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (topic.severityLevel) {
                                            "CRITICAL" -> AlertRed.copy(alpha = 0.2f)
                                            "RECOMMENDED" -> SecurityGold.copy(alpha = 0.2f)
                                            else -> ShieldGreenPrimary.copy(alpha = 0.2f)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                val iconVector = when (topic.iconName) {
                                    "Security" -> Icons.Filled.Security
                                    "Search" -> Icons.Filled.Search
                                    "VisibilityOff" -> Icons.Filled.VisibilityOff
                                    "NightsStay" -> Icons.Filled.NightsStay
                                    "PrivacyTip" -> Icons.Filled.PrivacyTip
                                    else -> Icons.Filled.Lock
                                }
                                Icon(
                                    imageVector = iconVector,
                                    contentDescription = null,
                                    tint = when (topic.severityLevel) {
                                        "CRITICAL" -> AlertRed
                                        "RECOMMENDED" -> SecurityGold
                                        else -> ShieldGreenLight
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = topic.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = topic.category,
                                    fontSize = 11.sp,
                                    color = CyberCyanLight
                                )
                            }
                        }

                        Icon(
                            imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = topic.summary,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    AnimatedVisibility(visible = isExpanded) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            Text(
                                text = "Actionable Steps:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ShieldGreenLight
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            topic.steps.forEachIndexed { idx, step ->
                                Row(
                                    modifier = Modifier.padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = "${idx + 1}.",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CyberCyanSecondary,
                                        modifier = Modifier.width(18.dp)
                                    )
                                    Text(
                                        text = step,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 16.sp
                                    )
                                }
                            }

                            if (topic.settingsAction != null) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = {
                                        try {
                                            context.startActivity(Intent(topic.settingsAction))
                                        } catch (e: Exception) {
                                            context.startActivity(Intent(Settings.ACTION_SETTINGS))
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ShieldGreenDark),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Open Relevant Android Setting", fontSize = 12.sp, color = Color.White)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(Icons.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Milestone(
    val days: Int,
    val title: String,
    val description: String
)

@Composable
fun StreakJournalScreen(
    viewModel: ShieldViewModel,
    modifier: Modifier = Modifier
) {
    val shieldState by viewModel.shieldState.collectAsStateWithLifecycle()
    val pledges by viewModel.pledges.collectAsStateWithLifecycle()
    val urgeLogs by viewModel.urgeLogs.collectAsStateWithLifecycle()

    val streakDays = ((System.currentTimeMillis() - shieldState.streakStartTimestamp) / 86400000L)
        .toInt().coerceAtLeast(1)

    val milestones = listOf(
        Milestone(1, "First Day", "24 hours clean & focused"),
        Milestone(3, "3-Day Shield", "Dopamine stabilization"),
        Milestone(7, "1 Week Pure", "Prefrontal cortex strengthening"),
        Milestone(14, "2 Weeks Free", "Reduced impulse triggers"),
        Milestone(30, "1 Month Pure", "Rewired neural pathways"),
        Milestone(90, "Reboot Master", "Full digital wellness mastery")
    )

    var showResetDialog by remember { mutableStateOf(false) }
    var pledgeNote by remember { mutableStateOf("") }

    val todayKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val hasPledgedToday = pledges.any { it.dateKey == todayKey }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Pure Path & Accountability",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Track your freedom streak, daily pledges & journal",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = { showResetDialog = true },
                    modifier = Modifier.testTag("reset_streak_button")
                ) {
                    Icon(Icons.Filled.RestartAlt, contentDescription = "Reset", tint = Color.Gray)
                }
            }
        }

        // Circular Streak Counter Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { ((streakDays % 7) / 7f).coerceIn(0.15f, 1f) },
                            modifier = Modifier.fillMaxSize(),
                            color = ShieldGreenPrimary,
                            strokeWidth = 10.dp,
                            trackColor = DarkSurfaceVariant
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$streakDays",
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = if (streakDays == 1) "CLEAN DAY" else "CLEAN DAYS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ShieldGreenLight,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "✨ Every day free from explicit material sharpens your clarity, memory, and self-respect.",
                        fontSize = 12.5.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Milestone Badges Carousel
        item {
            Column {
                Text(
                    text = "Milestone Achievements",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(milestones) { milestone ->
                        val isUnlocked = streakDays >= milestone.days
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isUnlocked) Color(0xFF162544) else DarkSurface
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = if (isUnlocked) androidx.compose.foundation.BorderStroke(1.dp, ShieldGreenLight) else null,
                            modifier = Modifier.width(135.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isUnlocked) SecurityGold.copy(alpha = 0.2f)
                                            else Color.DarkGray.copy(alpha = 0.3f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isUnlocked) Icons.Filled.EmojiEvents else Icons.Filled.Lock,
                                        contentDescription = null,
                                        tint = if (isUnlocked) SecurityGold else Color.Gray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = milestone.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUnlocked) Color.White else Color.Gray
                                )
                                Text(
                                    text = milestone.description,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Daily Commitment Pledge Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (hasPledgedToday) Icons.Filled.CheckCircle else Icons.Filled.Star,
                            contentDescription = null,
                            tint = if (hasPledgedToday) ShieldGreenLight else SecurityGold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (hasPledgedToday) "Today's Pledge Sealed" else "Daily Commitment Pledge",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    if (hasPledgedToday) {
                        Text(
                            text = "You've signed your daily pledge for today! Keep your shield active and your mind clear.",
                            fontSize = 12.sp,
                            color = ShieldGreenLight
                        )
                    } else {
                        Text(
                            text = "\"I commit to protecting my mind, habits, and digital spaces from adult content today.\"",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = CyberCyanLight
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = pledgeNote,
                            onValueChange = { pledgeNote = it },
                            placeholder = { Text("Add personal affirmation or goal (optional)") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ShieldGreenPrimary,
                                unfocusedBorderColor = DarkSurfaceVariant,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { viewModel.makeDailyPledge(pledgeNote) },
                            colors = ButtonDefaults.buttonColors(containerColor = ShieldGreenPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("sign_pledge_button")
                        ) {
                            Text("Sign Today's Pledge", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Overcome Urges Journal Section
        item {
            Text(
                text = "Resilience & Urge Log (${urgeLogs.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        if (urgeLogs.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Filled.SelfImprovement, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = "No Urges Logged Yet", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = "When triggers happen, use the Urge Rescue tool on the home screen to log victories.", color = Color.Gray, fontSize = 11.sp)
                    }
                }
            }
        } else {
            items(urgeLogs) { log ->
                val timeStr = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(log.timestamp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(ShieldGreenPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Favorite, contentDescription = null, tint = ShieldGreenLight, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Overcame: ${log.triggerType}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "State: ${log.mood} • $timeStr",
                                fontSize = 11.sp,
                                color = ShieldGreenLight
                            )
                            if (log.notes.isNotBlank()) {
                                Text(
                                    text = log.notes,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = DarkSurface,
            title = {
                Text("Reset Streak?", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Relapses are lessons, not the end. Resetting will start your clean streak from Day 1. Remember: recovery is a marathon, not a sprint.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetStreak()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AlertRed)
                ) {
                    Text("Reset Streak", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}

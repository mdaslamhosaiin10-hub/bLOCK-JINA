package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberCyanLight
import com.example.ui.theme.CyberCyanSecondary
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.SecurityGold
import com.example.ui.theme.ShieldGreenLight
import com.example.ui.theme.ShieldGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrgeSurfingSheet(
    isOpen: Boolean,
    breathingPhase: String,
    breathingProgress: Float,
    cycleCount: Int,
    onDismiss: (wasHelpful: Boolean) -> Unit
) {
    if (!isOpen) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTab by remember { mutableIntStateOf(0) }

    ModalBottomSheet(
        onDismissRequest = { onDismiss(true) },
        sheetState = sheetState,
        containerColor = DarkSurface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.SelfImprovement,
                        contentDescription = "Safe Space",
                        tint = ShieldGreenLight,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Urge Rescue & Safe Space",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                IconButton(
                    onClick = { onDismiss(true) },
                    modifier = Modifier.testTag("close_urge_sheet")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = Color.Gray
                    )
                }
            }

            Text(
                text = "Urges are like ocean waves: they peak and fade within 3-5 minutes. Ride this wave safely.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
                textAlign = TextAlign.Start
            )

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF1E2B47),
                contentColor = ShieldGreenLight,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .padding(bottom = 16.dp)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("4-7-8 Breathing", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Filled.Air, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("5-4-3-2-1 Grounding", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Filled.Favorite, contentDescription = null) }
                )
            }

            if (selectedTab == 0) {
                // Breathing Exercise View
                val animatedScale by animateFloatAsState(
                    targetValue = 0.8f + (breathingProgress * 0.45f),
                    animationSpec = tween(durationMillis = 200),
                    label = "breathScale"
                )

                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer pulsating orb
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(animatedScale)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        CyberCyanSecondary.copy(alpha = 0.5f),
                                        ShieldGreenPrimary.copy(alpha = 0.15f)
                                    )
                                )
                            )
                            .border(2.dp, ShieldGreenLight, CircleShape)
                    )

                    // Inner info
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = breathingPhase,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Cycle ${cycleCount + 1} of 4",
                            fontSize = 12.sp,
                            color = CyberCyanLight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2B47)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "💡 Neuro-Calm Technique",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SecurityGold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Inhale through nose for 4s, hold breath gently for 7s, exhale completely through mouth for 8s. This triggers your parasympathetic vagus nerve, calming dopamine restlessness.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                }
            } else {
                // 5-4-3-2-1 Sensory Grounding View
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2B47)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Sensory Re-connection",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val groundingSteps = listOf(
                            "👁️ 5 Things you can SEE around the room right now.",
                            "✋ 4 Things you can physically TOUCH (chair, desk, cloth).",
                            "👂 3 Things you can HEAR (fan, breathing, distant traffic).",
                            "👃 2 Things you can SMELL (fresh air, coffee, paper).",
                            "👅 1 Thing you can TASTE (sip of cold water)."
                        )

                        groundingSteps.forEach { step ->
                            Text(
                                text = step,
                                fontSize = 12.5.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { onDismiss(true) },
                colors = ButtonDefaults.buttonColors(containerColor = ShieldGreenPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("complete_urge_button")
            ) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "I Overcame The Urge",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyberCyanSecondary
import com.example.ui.theme.SecurityGold
import com.example.ui.theme.ShieldGreenLight
import com.example.ui.theme.ShieldGreenPrimary

@Composable
fun ShieldRadarPulse(
    isActive: Boolean,
    isStrictModeLocked: Boolean,
    uptimeText: String,
    onToggleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val pulseScale1 by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse1"
    )

    val pulseAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha1"
    )

    val pulseScale2 by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, delayMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse2"
    )

    val pulseAlpha2 by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, delayMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha2"
    )

    val primaryColor = if (isActive) ShieldGreenPrimary else AlertRed
    val glowColor = if (isActive) ShieldGreenLight else Color(0xFFF87171)

    Box(
        modifier = modifier
            .size(240.dp)
            .testTag("shield_radar_box"),
        contentAlignment = Alignment.Center
    ) {
        // Pulse ring 2
        if (isActive) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(pulseScale2)
            ) {
                drawCircle(
                    color = glowColor.copy(alpha = pulseAlpha2),
                    style = Stroke(width = 3.dp.toPx())
                )
            }

            // Pulse ring 1
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(pulseScale1)
            ) {
                drawCircle(
                    color = primaryColor.copy(alpha = pulseAlpha1),
                    style = Stroke(width = 4.dp.toPx())
                )
            }
        }

        // Center clickable Shield Orb
        Box(
            modifier = Modifier
                .size(165.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = if (isActive) {
                            listOf(
                                Color(0xFF064E3B),
                                Color(0xFF0A0F1D)
                            )
                        } else {
                            listOf(
                                Color(0xFF7F1D1D),
                                Color(0xFF0A0F1D)
                            )
                        }
                    )
                )
                .border(
                    width = 2.5.dp,
                    color = if (isActive) primaryColor else AlertRed,
                    shape = CircleShape
                )
                .clickable { onToggleClick() }
                .testTag("shield_toggle_button"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp)
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Icon(
                        imageVector = if (isActive) Icons.Filled.Shield else Icons.Outlined.Shield,
                        contentDescription = "Shield Status Icon",
                        tint = if (isActive) ShieldGreenLight else AlertRed,
                        modifier = Modifier.size(54.dp)
                    )
                    if (isStrictModeLocked) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(SecurityGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = "Locked",
                                tint = Color.Black,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isActive) "24/7 ACTIVE" else "PAUSED",
                    color = if (isActive) ShieldGreenLight else AlertRed,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )

                Text(
                    text = if (isActive) uptimeText else "Tap to Resume",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

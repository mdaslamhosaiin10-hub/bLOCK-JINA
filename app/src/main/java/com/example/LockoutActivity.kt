package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PureShieldDatabase
import com.example.data.repository.ShieldRepository
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyberCyanLight
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SecurityGold
import com.example.ui.theme.ShieldGreenLight
import com.example.ui.theme.ShieldGreenPrimary
import kotlinx.coroutines.delay

class LockoutActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val triggerWord = intent.getStringExtra(EXTRA_TRIGGER) ?: "Explicit Content"
        val queryText = intent.getStringExtra(EXTRA_QUERY) ?: ""
        val isTamper = intent.getBooleanExtra(EXTRA_IS_TAMPER, false)
        val initialDuration = if (isTamper) 300 else 60 // 5 minutes (300s) for tamper attempt

        val db = PureShieldDatabase.getDatabase(this)
        val repository = ShieldRepository(db.shieldDao(), this)

        setContent {
            MyApplicationTheme(darkTheme = true) {
                LockoutScreen(
                    trigger = triggerWord,
                    query = queryText,
                    isTamper = isTamper,
                    totalSeconds = initialDuration,
                    onUnlockSuccess = { finish() },
                    repository = repository
                )
            }
        }
    }

    companion object {
        const val EXTRA_TRIGGER = "EXTRA_TRIGGER"
        const val EXTRA_QUERY = "EXTRA_QUERY"
        const val EXTRA_IS_TAMPER = "EXTRA_IS_TAMPER"
    }
}

@Composable
fun LockoutScreen(
    trigger: String,
    query: String,
    isTamper: Boolean,
    totalSeconds: Int,
    onUnlockSuccess: () -> Unit,
    repository: ShieldRepository
) {
    // Intercept back button completely during high security lockdown
    BackHandler(enabled = true) {
        // Locked
    }

    var countdownSeconds by remember { mutableIntStateOf(totalSeconds) }
    var isPinMode by remember { mutableStateOf(false) }
    var enteredPin by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        while (countdownSeconds > 0) {
            delay(1000)
            countdownSeconds -= 1
        }
    }

    val minutes = countdownSeconds / 60
    val seconds = countdownSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_alert")
    val alertScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alert_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isTamper) Color.Black else DarkBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Warning Icon
        Box(
            modifier = Modifier
                .size(110.dp)
                .scale(alertScale)
                .clip(CircleShape)
                .background(AlertRed.copy(alpha = 0.25f))
                .border(2.5.dp, AlertRed, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isTamper) Icons.Filled.PowerSettingsNew else Icons.Filled.Block,
                contentDescription = "Threat Blocked",
                tint = AlertRed,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (isTamper) "5-MINUTE TAMPER LOCKDOWN" else "INSTANT SHIELD LOCKDOWN",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = AlertRed,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (isTamper) "App Uninstall / DNS Disable Attempt Blocked" else "Protected From Moral & Spiritual Harm",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF2E1015))
                .border(1.dp, AlertRed.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = "Interception Reason: $trigger",
                fontSize = 13.sp,
                color = AlertRed,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Countdown Timer Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = if (isTamper) Color(0xFF111116) else DarkSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (countdownSeconds > 0) {
                    Box(
                        modifier = Modifier.size(110.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { countdownSeconds.toFloat() / totalSeconds.toFloat() },
                            modifier = Modifier.fillMaxSize(),
                            color = AlertRed,
                            strokeWidth = 7.dp,
                            trackColor = DarkSurfaceVariant
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = timeFormatted,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = if (isTamper) "SYSTEM LOCKED" else "COOL-DOWN",
                                fontSize = 9.sp,
                                color = AlertRed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (isTamper) {
                            "Security barrier active. Attempting to bypass DNS, force-stop, or uninstall PureShield triggers this 5-minute freeze to ensure self-control."
                        } else {
                            "Take slow deep breaths. This momentary pause disrupts compulsive dopamine arousal and restores prefrontal control."
                        },
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = ShieldGreenLight,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Lockout Duration Expired",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ShieldGreenLight
                    )
                    Text(
                        text = "You are safe to resume healthy, pure digital habits.",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onUnlockSuccess,
                        colors = ButtonDefaults.buttonColors(containerColor = ShieldGreenPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("dismiss_lockdown_button")
                    ) {
                        Text("Return to Clean Screen", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Accountability PIN override option
        if (countdownSeconds > 0) {
            if (isPinMode) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Accountability Partner PIN Bypass",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SecurityGold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = enteredPin,
                            onValueChange = { if (it.length <= 4) enteredPin = it },
                            label = { Text("4-digit PIN") },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            isError = pinError != null,
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ShieldGreenPrimary,
                                unfocusedBorderColor = Color.Gray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (pinError != null) {
                            Text(
                                text = pinError!!,
                                fontSize = 11.sp,
                                color = AlertRed,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (repository.validatePin(enteredPin)) {
                                        onUnlockSuccess()
                                    } else {
                                        pinError = "Incorrect PIN. Lockout continues."
                                    }
                                },
                                enabled = enteredPin.length == 4,
                                colors = ButtonDefaults.buttonColors(containerColor = SecurityGold),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Unlock", color = Color.Black, fontWeight = FontWeight.Bold)
                            }

                            TextButton(
                                onClick = { isPinMode = false },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel", color = Color.Gray)
                            }
                        }
                    }
                }
            } else {
                TextButton(
                    onClick = { isPinMode = true },
                    modifier = Modifier.testTag("pin_override_toggle")
                ) {
                    Icon(Icons.Filled.Lock, contentDescription = null, tint = SecurityGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Accountability PIN Override", color = SecurityGold, fontSize = 12.sp)
                }
            }
        }
    }
}

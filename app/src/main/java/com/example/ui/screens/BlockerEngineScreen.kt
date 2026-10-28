package com.example.ui.screens

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.engine.RiskLevel
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

@Composable
fun BlockerEngineScreen(
    viewModel: ShieldViewModel,
    modifier: Modifier = Modifier
) {
    var selectedSection by remember { mutableIntStateOf(0) }
    val scannerInput by viewModel.scannerInput.collectAsStateWithLifecycle()
    val inspectionResult by viewModel.inspectionResult.collectAsStateWithLifecycle()
    val isScanning by viewModel.isScanning.collectAsStateWithLifecycle()

    val blockedAttempts by viewModel.blockedAttempts.collectAsStateWithLifecycle()
    val customFilters by viewModel.customFilters.collectAsStateWithLifecycle()

    var newFilterText by remember { mutableStateOf("") }
    var newFilterIsDomain by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        // Top Header
        Text(
            text = "Active Blocker Engine",
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
        Text(
            text = "Sandbox Scanner & Strict Content Filtering",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Tabs: 0: URL / Query Inspector, 1: Blocked Logs, 2: Custom Blacklist
        TabRow(
            selectedTabIndex = selectedSection,
            containerColor = DarkSurface,
            contentColor = ShieldGreenLight,
            modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedSection == 0,
                onClick = { selectedSection = 0 },
                text = { Text("Scanner", fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedSection == 1,
                onClick = { selectedSection = 1 },
                text = { Text("Blocked (${blockedAttempts.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Filled.History, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedSection == 2,
                onClick = { selectedSection = 2 },
                text = { Text("Custom Rules", fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Filled.FilterList, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        when (selectedSection) {
            0 -> {
                // SANDBOX SCANNER TAB
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Inspect Link, Domain or Search Term",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Test how PureShield intercepts explicit keywords, adult domains, and scams in real-time.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                                )

                                OutlinedTextField(
                                    value = scannerInput,
                                    onValueChange = { viewModel.scannerInput.value = it },
                                    placeholder = { Text("e.g. adult-stream.xxx or query phrase") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(onDone = { viewModel.scanContent() }),
                                    trailingIcon = {
                                        if (scannerInput.isNotEmpty()) {
                                            IconButton(onClick = { viewModel.clearScan() }) {
                                                Icon(Icons.Filled.Clear, contentDescription = "Clear", tint = Color.Gray)
                                            }
                                        }
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = ShieldGreenPrimary,
                                        unfocusedBorderColor = DarkSurfaceVariant,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("scanner_input_field")
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    androidx.compose.foundation.lazy.LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        val testWords = listOf("romance", "hot", "figer", "dance", "sexy song", "girl", "sex story", "xxx", "jua")
                                        items(testWords) { testWord ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Color(0xFF22151B))
                                                    .border(1.dp, AlertRed.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                                    .clickable {
                                                        viewModel.scannerInput.value = testWord
                                                        viewModel.scanContent()
                                                    }
                                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                                            ) {
                                                Text(
                                                    text = testWord,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = AlertRed
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = { viewModel.scanContent() },
                                    enabled = scannerInput.isNotBlank() && !isScanning,
                                    colors = ButtonDefaults.buttonColors(containerColor = ShieldGreenPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("scan_button")
                                ) {
                                    if (isScanning) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            color = Color.Black,
                                            strokeWidth = 2.5.dp
                                        )
                                    } else {
                                        Icon(Icons.Filled.Security, contentDescription = null, tint = Color.Black)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Test & Inspect Content", color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // INSPECTION RESULT
                    if (inspectionResult != null) {
                        item {
                            val res = inspectionResult!!
                            val isBlocked = res.isBlocked
                            val accentColor = if (isBlocked) AlertRed else ShieldGreenLight

                            Card(
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                                shape = RoundedCornerShape(14.dp),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, accentColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("inspection_result_card")
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = if (isBlocked) Icons.Filled.Block else Icons.Filled.CheckCircle,
                                                contentDescription = null,
                                                tint = accentColor,
                                                modifier = Modifier.size(28.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = if (isBlocked) "STRONGLY BLOCKED" else "VERIFIED SAFE",
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = accentColor
                                                )
                                                Text(
                                                    text = "Category: ${res.category}",
                                                    fontSize = 12.sp,
                                                    color = Color.White
                                                )
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(accentColor.copy(alpha = 0.2f))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = res.riskLevel.name,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = accentColor
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = res.explanation,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 18.sp
                                    )

                                    if (res.matchedPattern != null) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Trigger Match: '${res.matchedPattern}'",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SecurityGold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF0F172A))
                                            .padding(10.dp)
                                    ) {
                                        Text(
                                            text = "🛡️ Action: ${res.recommendedAction}",
                                            fontSize = 11.5.sp,
                                            color = CyberCyanLight
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // BLOCKED ATTEMPTS LOG TAB
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "24/7 Activity Interception Log",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        if (blockedAttempts.isNotEmpty()) {
                            IconButton(onClick = { viewModel.clearBlockedHistory() }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Clear Log", tint = AlertRed)
                            }
                        }
                    }

                    if (blockedAttempts.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = ShieldGreenLight,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "No Threat Activity Logged",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Your browsing environment is clean and safe.",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(blockedAttempts, key = { it.id }) { item ->
                                val dateStr = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                                    .format(Date(item.timestamp))

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
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(AlertRed.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Block,
                                                contentDescription = null,
                                                tint = AlertRed,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = item.target,
                                                fontSize = 13.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = "${item.category} • $dateStr",
                                                fontSize = 11.sp,
                                                color = AlertRed
                                            )
                                            Text(
                                                text = item.reason,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // CUSTOM BLACKLIST & TRIGGER WORDS TAB
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurface),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Add Custom Filter / Trigger Word",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Add specific trigger terms or domains you want PureShield to strictly block.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = 10.dp)
                                )

                                OutlinedTextField(
                                    value = newFilterText,
                                    onValueChange = { newFilterText = it },
                                    placeholder = { Text("e.g. trigger-site.com or keyword") },
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

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (newFilterIsDomain) "Type: Domain URL" else "Type: Trigger Word",
                                            fontSize = 12.sp,
                                            color = CyberCyanLight
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Switch(
                                            checked = newFilterIsDomain,
                                            onCheckedChange = { newFilterIsDomain = it },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = ShieldGreenPrimary,
                                                checkedTrackColor = ShieldGreenDark
                                            )
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            if (newFilterText.isNotBlank()) {
                                                viewModel.addCustomFilter(newFilterText, newFilterIsDomain)
                                                newFilterText = ""
                                            }
                                        },
                                        enabled = newFilterText.isNotBlank(),
                                        colors = ButtonDefaults.buttonColors(containerColor = ShieldGreenPrimary),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Filled.Add, contentDescription = null, tint = Color.Black)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Add Rule", color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "Active Custom Rules (${customFilters.size})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    items(customFilters, key = { it.id }) { filter ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = filter.pattern,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (filter.isEnabled) Color.White else Color.Gray
                                    )
                                    Text(
                                        text = if (filter.isDomain) "Domain Blocklist" else "Trigger Keyword",
                                        fontSize = 11.sp,
                                        color = CyberCyanLight
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Switch(
                                        checked = filter.isEnabled,
                                        onCheckedChange = { viewModel.toggleCustomFilter(filter.id, it) },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = ShieldGreenPrimary,
                                            checkedTrackColor = ShieldGreenDark
                                        )
                                    )
                                    IconButton(onClick = { viewModel.deleteCustomFilter(filter.id) }) {
                                        Icon(
                                            Icons.Filled.Delete,
                                            contentDescription = "Delete",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

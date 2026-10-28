package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.PinLockDialog
import com.example.ui.components.UrgeSurfingSheet
import com.example.ui.screens.BlockerEngineScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.SecurityGuideScreen
import com.example.ui.screens.StreakJournalScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ShieldGreenDark
import com.example.ui.theme.ShieldGreenLight
import com.example.ui.theme.ShieldGreenPrimary
import com.example.ui.viewmodel.ShieldViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class NavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

class MainActivity : ComponentActivity() {

    private val viewModel: ShieldViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme(darkTheme = true) {
                PureShieldApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun PureShieldApp(viewModel: ShieldViewModel) {
    var selectedScreenIndex by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val navItems = listOf(
        NavItem("Shield", Icons.Filled.Shield, Icons.Outlined.Shield, "nav_shield"),
        NavItem("Modesty", Icons.Filled.VisibilityOff, Icons.Outlined.VisibilityOff, "nav_modesty"),
        NavItem("Blocker", Icons.Filled.Block, Icons.Outlined.Block, "nav_blocker"),
        NavItem("Guide", Icons.Filled.MenuBook, Icons.Outlined.MenuBook, "nav_guide"),
        NavItem("Pure Path", Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents, "nav_streak")
    )

    // Handle back button on sub-screens to return to Dashboard
    BackHandler(enabled = selectedScreenIndex != 0) {
        selectedScreenIndex = 0
    }

    // Observe snackbar messages
    LaunchedEffect(Unit) {
        viewModel.userMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    // Urge Rescue State
    val isUrgeRescueOpen by viewModel.isUrgeRescueOpen.collectAsStateWithLifecycle()
    val breathingPhase by viewModel.breathingPhase.collectAsStateWithLifecycle()
    val breathingProgress by viewModel.breathingProgress.collectAsStateWithLifecycle()
    val breathingCycleCount by viewModel.breathingCycleCount.collectAsStateWithLifecycle()

    // PIN State
    val isPinDialogOpen by viewModel.isPinDialogOpen.collectAsStateWithLifecycle()
    val isSetPinMode by viewModel.isSetPinMode.collectAsStateWithLifecycle()
    val pinError by viewModel.pinError.collectAsStateWithLifecycle()
    val cooldownSeconds by viewModel.cooldownSeconds.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        containerColor = DarkBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar")
            ) {
                navItems.forEachIndexed { index, item ->
                    val isSelected = selectedScreenIndex == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedScreenIndex = index },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title,
                                tint = if (isSelected) ShieldGreenLight else Color.Gray
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) ShieldGreenLight else Color.Gray
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = ShieldGreenDark.copy(alpha = 0.5f),
                            selectedIconColor = ShieldGreenLight,
                            selectedTextColor = ShieldGreenLight,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag(item.testTag)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedScreenIndex) {
                0 -> DashboardScreen(viewModel = viewModel)
                1 -> com.example.ui.screens.ModestyShieldScreen(viewModel = viewModel)
                2 -> BlockerEngineScreen(viewModel = viewModel)
                3 -> SecurityGuideScreen(viewModel = viewModel)
                4 -> StreakJournalScreen(viewModel = viewModel)
            }
        }
    }

    // Urge Rescue Bottom Sheet
    UrgeSurfingSheet(
        isOpen = isUrgeRescueOpen,
        breathingPhase = breathingPhase,
        breathingProgress = breathingProgress,
        cycleCount = breathingCycleCount,
        onDismiss = { wasHelpful -> viewModel.stopBreathingExercise(wasHelpful) }
    )

    // Master PIN Dialog
    PinLockDialog(
        isOpen = isPinDialogOpen,
        isSetMode = isSetPinMode,
        errorMessage = pinError,
        cooldownSeconds = cooldownSeconds,
        onConfirm = { pin ->
            if (isSetPinMode) {
                viewModel.saveNewPin(pin)
            } else {
                viewModel.verifyPinAndExecute(pin) {
                    viewModel.toggleProtection()
                }
            }
        },
        onDismiss = {
            viewModel.isPinDialogOpen.value = false
            viewModel.pinError.value = null
        }
    )
}

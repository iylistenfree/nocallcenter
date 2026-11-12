package com.nocallcenter.app.ui.main

import android.Manifest
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PhoneCallback
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nocallcenter.app.R
import com.nocallcenter.app.data.AppState
import com.nocallcenter.app.data.model.CallScreeningOutcome
import com.nocallcenter.app.data.model.ScreenedCall
import com.nocallcenter.app.util.CountryPrefixHelper
import com.nocallcenter.app.util.UpdateCheckResult
import com.nocallcenter.app.util.UpdateInfo
import java.text.SimpleDateFormat
import java.util.*

enum class AppTab {
    DASHBOARD,
    HISTORY,
    SETTINGS,
    ABOUT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainScreenViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.appState.collectAsStateWithLifecycle()
    var currentTab by remember { mutableStateOf(AppTab.DASHBOARD) }
    var showGuideDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Role Manager Launcher for Call Screening
    val roleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.refreshScreeningRoleStatus()
    }

    // Permission launcher for SMS, Contacts, Notifications
    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        viewModel.reload()
    }

    DisposableEffect(Unit) {
        viewModel.reload()
        onDispose { }
    }

    val versionName = remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.2.0"
        } catch (e: Exception) {
            "1.2.0"
        }
    }

    LaunchedEffect(Unit) {
        viewModel.checkForUpdates(versionName, isManual = false)
    }

    // If first launch, show Onboarding landing screen!
    if (state.isFirstLaunch) {
        OnboardingScreen(
            onComplete = { viewModel.completeFirstLaunch() }
        )
        return
    }

    if (showGuideDialog) {
        PermissionGuideDialog(onDismiss = { showGuideDialog = false })
    }

    if (viewModel.showUpdateDialog && viewModel.updateCheckResult is UpdateCheckResult.Available) {
        val info = (viewModel.updateCheckResult as UpdateCheckResult.Available).info
        UpdateAvailableDialog(
            info = info,
            onDownload = {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(info.downloadUrl)).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(browserIntent)
                viewModel.dismissUpdateDialog()
            },
            onDismiss = { viewModel.dismissUpdateDialog() }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = stringResource(R.string.app_name),
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentTab == AppTab.DASHBOARD,
                    onClick = { currentTab = AppTab.DASHBOARD },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_dashboard)) }
                )
                NavigationBarItem(
                    selected = currentTab == AppTab.HISTORY,
                    onClick = { currentTab = AppTab.HISTORY },
                    icon = { Icon(Icons.Default.Call, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_history)) }
                )
                NavigationBarItem(
                    selected = currentTab == AppTab.SETTINGS,
                    onClick = { currentTab = AppTab.SETTINGS },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_settings)) }
                )
                NavigationBarItem(
                    selected = currentTab == AppTab.ABOUT,
                    onClick = { currentTab = AppTab.ABOUT },
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_about)) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (currentTab) {
                AppTab.DASHBOARD -> DashboardContent(
                    state = state,
                    onToggleService = { viewModel.setScreeningEnabled(it) },
                    onRequestRole = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val roleManager = context.getSystemService(RoleManager::class.java)
                            if (roleManager?.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING) == true) {
                                val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
                                roleLauncher.launch(intent)
                            }
                        }
                    }
                )
                AppTab.HISTORY -> HistoryContent(
                    calls = state.screenedCalls,
                    onClearHistory = { viewModel.clearCallHistory() }
                )
                AppTab.SETTINGS -> SettingsContent(
                    state = state,
                    viewModel = viewModel,
                    onRequestPermissions = {
                        val perms = mutableListOf(
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.SEND_SMS
                        )
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            perms.add(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        permissionsLauncher.launch(perms.toTypedArray())
                    }
                )
                AppTab.ABOUT -> AboutContent(
                    versionName = versionName,
                    viewModel = viewModel,
                    onShowPermissionGuide = { showGuideDialog = true }
                )
            }
        }
    }
}

/**
 * Onboarding landing screen shown ONLY on first launch
 */
@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Icon(
            imageVector = Icons.Default.Call,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(72.dp)
        )

        Text(
            text = stringResource(R.string.onboarding_welcome_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = stringResource(R.string.onboarding_welcome_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // How it works card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = stringResource(R.string.onboarding_how_it_works_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = stringResource(R.string.onboarding_step1_title),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(R.string.onboarding_step1_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider()

                Text(
                    text = stringResource(R.string.onboarding_step2_title),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(R.string.onboarding_step2_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                HorizontalDivider()

                Text(
                    text = stringResource(R.string.onboarding_step3_title),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(R.string.onboarding_step3_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Android 14/15 Permission unlock guide
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.onboarding_perm_guide_title),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Text(
                    text = stringResource(R.string.onboarding_perm_guide_desc),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(text = stringResource(R.string.onboarding_perm_step1), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                Text(text = stringResource(R.string.onboarding_perm_step2), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                Text(text = stringResource(R.string.onboarding_perm_step3), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                Text(text = stringResource(R.string.onboarding_perm_step4), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedButton(
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.parse("package:" + context.packageName)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.btn_open_app_settings))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onComplete,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = stringResource(R.string.btn_get_started),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun DashboardContent(
    state: AppState,
    onToggleService: (Boolean) -> Unit,
    onRequestRole: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (state.isScreeningEnabled)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (state.isScreeningEnabled)
                            stringResource(R.string.status_active)
                        else
                            stringResource(R.string.status_inactive),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (state.isScreeningEnabled)
                            stringResource(R.string.status_active_desc)
                        else
                            stringResource(R.string.status_inactive_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = state.isScreeningEnabled,
                    onCheckedChange = onToggleService
                )
            }
        }

        // Call Screening Default App Banner (if not granted)
        if (!state.isScreeningRoleHeld && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.role_required_title),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.role_required_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onRequestRole,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(stringResource(R.string.btn_set_screening_role))
                    }
                }
            }
        }

        // Smart SMS Budget Card
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.budget_card_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (!state.isBudgetingEnabled) {
                        SuggestionChip(
                            onClick = { },
                            label = { Text(stringResource(R.string.budget_unlimited_badge)) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (state.isBudgetingEnabled) {
                    val progress = (state.smsSentThisCycle.toFloat() / state.smsBudgetLimit.toFloat())
                        .coerceIn(0f, 1f)
                    val isExhausted = state.smsSentThisCycle >= state.smsBudgetLimit

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = if (isExhausted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(
                                R.string.budget_usage_format,
                                state.smsSentThisCycle,
                                state.smsBudgetLimit
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = stringResource(R.string.budget_resets_on, state.billingCycleDay),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (isExhausted) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.budget_exhausted_warning),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                } else {
                    Text(
                        text = stringResource(R.string.budget_unlimited_usage_format, state.smsSentThisCycle),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.budget_resets_on, state.billingCycleDay),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Stats Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = stringResource(R.string.stat_blocked_total),
                value = state.totalBlockedCalls.toString(),
                icon = Icons.Default.Call,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = stringResource(R.string.stat_sms_sent),
                value = state.smsSentThisCycle.toString(),
                icon = Icons.AutoMirrored.Filled.Send,
                modifier = Modifier.weight(1f)
            )
        }

        // PayPal Donation Card (Replaces the old recent calls preview on Dashboard)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "☕ " + stringResource(R.string.coffee_card_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Text(
                    text = stringResource(R.string.coffee_card_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = {
                        val browserIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(state.paypalUrl)
                        ).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(browserIntent)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF003087) // PayPal Navy Blue
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.btn_buy_coffee_paypal),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun HistoryContent(
    calls: List<ScreenedCall>,
    onClearHistory: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.history_clear)) },
            text = { Text(stringResource(R.string.history_clear_confirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearHistory()
                        showDialog = false
                    }
                ) {
                    Text("OK", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f, fill = false)) {
                Text(
                    text = stringResource(R.string.history_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                if (calls.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.history_subtitle_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (calls.isNotEmpty()) {
                IconButton(onClick = { showDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.history_clear),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (calls.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.history_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(calls) { call ->
                    ScreenedCallItem(call = call)
                }
            }
        }
    }
}

@Composable
fun ScreenedCallItem(
    call: ScreenedCall,
    onCallClick: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val dateFormat = remember { SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()) }
    val formattedDate = remember(call.timestamp) { dateFormat.format(Date(call.timestamp)) }
    val isDialable = remember(call.phoneNumber) { call.phoneNumber.any { it.isDigit() } }

    val handleCall = {
        if (isDialable) {
            if (onCallClick != null) {
                onCallClick(call.phoneNumber)
            } else {
                try {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${Uri.encode(call.phoneNumber.trim())}")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                } catch (_: Exception) {
                }
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isDialable) {
                    Modifier.clickable(
                        role = Role.Button,
                        onClickLabel = stringResource(R.string.action_call_number, call.phoneNumber),
                        onClick = handleCall
                    )
                } else Modifier
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Call,
                contentDescription = null,
                tint = when (call.outcome) {
                    CallScreeningOutcome.BLOCKED_SMS_SENT -> MaterialTheme.colorScheme.primary
                    CallScreeningOutcome.BLOCKED_ANTI_FLOOD -> Color(0xFFE65100)
                    CallScreeningOutcome.BLOCKED_BUDGET_EXCEEDED -> MaterialTheme.colorScheme.error
                    CallScreeningOutcome.BLOCKED_FOREIGN_PREFIX -> Color(0xFF6A1B9A)
                    else -> MaterialTheme.colorScheme.outline
                },
                modifier = Modifier.padding(end = 12.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = call.callerName ?: call.phoneNumber,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                if (call.callerName != null) {
                    Text(
                        text = call.phoneNumber,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                OutcomeBadge(outcome = call.outcome)
                if (isDialable) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.PhoneCallback,
                            contentDescription = stringResource(R.string.action_call_back),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = stringResource(R.string.action_call_back),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OutcomeBadge(outcome: CallScreeningOutcome) {
    val (label, containerColor, contentColor) = when (outcome) {
        CallScreeningOutcome.BLOCKED_SMS_SENT -> Triple(
            stringResource(R.string.status_blocked_sms_sent),
            Color(0xFFE8F5E9),
            Color(0xFF2E7D32)
        )
        CallScreeningOutcome.BLOCKED_ANTI_FLOOD -> Triple(
            stringResource(R.string.status_blocked_sms_skipped_flood),
            Color(0xFFFFF3E0),
            Color(0xFFE65100)
        )
        CallScreeningOutcome.BLOCKED_BUDGET_EXCEEDED -> Triple(
            stringResource(R.string.status_blocked_sms_skipped_budget),
            Color(0xFFFFEBEE),
            Color(0xFFC62828)
        )
        CallScreeningOutcome.BLOCKED_SMS_DISABLED -> Triple(
            stringResource(R.string.status_blocked_sms_disabled),
            Color(0xFFF5F5F5),
            Color(0xFF616161)
        )
        CallScreeningOutcome.BLOCKED_FOREIGN_PREFIX -> Triple(
            stringResource(R.string.status_blocked_foreign_prefix),
            Color(0xFFEDE7F6),
            Color(0xFF512DA8)
        )
        CallScreeningOutcome.ALLOWED_CONTACT -> Triple(
            stringResource(R.string.badge_contacts),
            Color(0xFFE3F2FD),
            Color(0xFF1565C0)
        )
    }

    Surface(
        color = containerColor,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(start = 8.dp)
    ) {
        Text(
            text = label,
            color = contentColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun SettingsContent(
    state: AppState,
    viewModel: MainScreenViewModel,
    onRequestPermissions: () -> Unit
) {
    val context = LocalContext.current
    var editingTemplate by remember(state.smsTemplate) { mutableStateOf(state.smsTemplate) }
    var showEuCountriesDialog by remember { mutableStateOf(false) }

    if (showEuCountriesDialog) {
        EuCountriesDialog(
            selectedPrefixes = state.excludedEuPrefixes,
            onTogglePrefix = { viewModel.toggleExcludedEuPrefix(it) },
            onSelectAll = {
                viewModel.setExcludedEuPrefixes(CountryPrefixHelper.EU_COUNTRIES.map { it.dialCode }.toSet())
            },
            onDeselectAll = {
                viewModel.setExcludedEuPrefixes(emptySet())
            },
            onDismiss = { showEuCountriesDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        // Section: Call Screening
        Text(
            text = stringResource(R.string.settings_screening_section),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Card(shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.setting_block_non_contacts),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = stringResource(R.string.setting_block_non_contacts_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = state.blockNonContacts,
                        onCheckedChange = { viewModel.setBlockNonContacts(it) }
                    )
                }
            }
        }

        // Section: Foreign & International Calls
        Text(
            text = stringResource(R.string.settings_foreign_section),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Card(shape = RoundedCornerShape(16.dp)) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Switch Extra-UE
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.setting_block_extra_eu),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = stringResource(R.string.setting_block_extra_eu_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = state.blockExtraEuCalls,
                        onCheckedChange = { viewModel.setBlockExtraEuCalls(it) }
                    )
                }

                HorizontalDivider()

                // Switch EU
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.setting_block_eu),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = stringResource(R.string.setting_block_eu_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = state.blockEuCalls,
                        onCheckedChange = { viewModel.setBlockEuCalls(it) }
                    )
                }

                // If blockEuCalls is enabled, show the row to manage allowed exceptions
                AnimatedVisibility(visible = state.blockEuCalls) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.setting_manage_eu_exceptions),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = stringResource(
                                        R.string.setting_manage_eu_exceptions_desc,
                                        state.excludedEuPrefixes.size,
                                        CountryPrefixHelper.EU_COUNTRIES.size
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            OutlinedButton(onClick = { showEuCountriesDialog = true }) {
                                Text(stringResource(R.string.btn_manage))
                            }
                        }
                    }
                }
            }
        }

        // Section: SMS Settings
        Text(
            text = stringResource(R.string.settings_sms_section),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Card(shape = RoundedCornerShape(16.dp)) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Toggle: Auto-SMS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.setting_enable_sms),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = stringResource(R.string.setting_enable_sms_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = state.isAutoSmsEnabled,
                        onCheckedChange = { viewModel.setAutoSmsEnabled(it) }
                    )
                }

                HorizontalDivider()

                // Toggle: Smart SMS Budgeting (Salva-Credito)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.setting_enable_budget),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = stringResource(R.string.setting_enable_budget_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = state.isBudgetingEnabled,
                        onCheckedChange = { viewModel.setBudgetingEnabled(it) }
                    )
                }

                // If budgeting enabled: configure limit and billing renewal day
                AnimatedVisibility(visible = state.isBudgetingEnabled) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "${stringResource(R.string.setting_budget_max)}: ${state.smsBudgetLimit} SMS",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Slider(
                            value = state.smsBudgetLimit.toFloat(),
                            onValueChange = { viewModel.setSmsBudgetLimit(it.toInt()) },
                            valueRange = 10f..200f,
                            steps = 18
                        )

                        Text(
                            text = "${stringResource(R.string.setting_billing_day)}: ${stringResource(R.string.billing_day_format, state.billingCycleDay)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Slider(
                            value = state.billingCycleDay.toFloat(),
                            onValueChange = { viewModel.setBillingCycleDay(it.toInt()) },
                            valueRange = 1f..31f,
                            steps = 29
                        )
                    }
                }

                HorizontalDivider()

                // Anti-Flood Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.setting_anti_flood),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = stringResource(R.string.setting_anti_flood_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = state.isAntiFloodEnabled,
                        onCheckedChange = { viewModel.setAntiFloodEnabled(it) }
                    )
                }
            }
        }

        // SMS Template Card
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.setting_sms_template),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    TextButton(onClick = {
                        viewModel.resetSmsTemplate()
                        editingTemplate = context.getString(R.string.default_sms_template)
                    }) {
                        Text(stringResource(R.string.btn_reset_default))
                    }
                }

                OutlinedTextField(
                    value = editingTemplate,
                    onValueChange = {
                        editingTemplate = it
                        viewModel.setSmsTemplate(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 6
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.setting_sms_template_desc),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${editingTemplate.length}/160",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (editingTemplate.length > 160) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Section: Updates
        Text(
            text = stringResource(R.string.btn_check_updates),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Card(shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.setting_auto_check_updates),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = stringResource(R.string.setting_auto_check_updates_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = state.isAutoCheckUpdatesEnabled,
                        onCheckedChange = { viewModel.setAutoCheckUpdatesEnabled(it) }
                    )
                }
            }
        }

        // Permissions Section
        Text(
            text = stringResource(R.string.settings_permissions_section),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Button(
            onClick = onRequestPermissions,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.btn_check_grant_permissions))
        }
    }
}

@Composable
fun AboutContent(
    versionName: String,
    viewModel: MainScreenViewModel,
    onShowPermissionGuide: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Icon(
            imageVector = Icons.Default.Call,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        )

        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(R.string.app_tagline),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = stringResource(R.string.about_version, versionName),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Update Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.btn_check_updates),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = stringResource(R.string.about_internet_notice),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = { viewModel.checkForUpdates(versionName, isManual = true) },
                    enabled = !viewModel.isCheckingUpdate,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (viewModel.isCheckingUpdate) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.update_checking))
                    } else {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.btn_check_updates))
                    }
                }

                // Feedback message after check
                when (viewModel.updateCheckResult) {
                    is UpdateCheckResult.UpToDate -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = stringResource(R.string.update_already_latest),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    is UpdateCheckResult.Error -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = stringResource(R.string.update_check_failed),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    else -> {}
                }
            }
        }

        // Privacy First card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.about_privacy_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.about_privacy_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // How it works card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.onboarding_how_it_works_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "• " + stringResource(R.string.onboarding_step1_desc),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• " + stringResource(R.string.onboarding_step2_desc),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "• " + stringResource(R.string.onboarding_step3_desc),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Button to re-open Android 14/15 Permission Guide
        OutlinedButton(
            onClick = onShowPermissionGuide,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Info, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.about_view_guide))
        }
    }
}

@Composable
fun UpdateAvailableDialog(
    info: UpdateInfo,
    onDownload: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.SystemUpdate,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = stringResource(R.string.update_dialog_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.update_dialog_msg, info.tagName),
                    style = MaterialTheme.typography.bodyMedium
                )

                if (info.releaseNotes.isNotBlank()) {
                    Text(
                        text = stringResource(R.string.update_dialog_notes_title),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = info.releaseNotes,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .padding(10.dp)
                                .heightIn(max = 140.dp)
                                .verticalScroll(rememberScrollState())
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDownload,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(stringResource(R.string.btn_download_update))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.btn_later))
            }
        }
    )
}

@Composable
fun PermissionGuideDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.onboarding_perm_guide_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.onboarding_perm_guide_desc),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = stringResource(R.string.onboarding_perm_step1), style = MaterialTheme.typography.bodySmall)
                Text(text = stringResource(R.string.onboarding_perm_step2), style = MaterialTheme.typography.bodySmall)
                Text(text = stringResource(R.string.onboarding_perm_step3), style = MaterialTheme.typography.bodySmall)
                Text(text = stringResource(R.string.onboarding_perm_step4), style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse("package:" + context.packageName)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(intent)
                    onDismiss()
                }
            ) {
                Text(stringResource(R.string.btn_open_app_settings))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.btn_close))
            }
        }
    )
}

@Composable
fun EuCountriesDialog(
    selectedPrefixes: Set<String>,
    onTogglePrefix: (String) -> Unit,
    onSelectAll: () -> Unit,
    onDeselectAll: () -> Unit,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    val filteredCountries = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            CountryPrefixHelper.EU_COUNTRIES
        } else {
            val q = searchQuery.trim().lowercase()
            CountryPrefixHelper.EU_COUNTRIES.filter { country ->
                val countryName = context.getString(country.nameResId).lowercase()
                countryName.contains(q) || country.dialCode.contains(q) || country.isoCode.lowercase().contains(q)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.dialog_select_eu_prefixes_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
            ) {
                Text(
                    text = stringResource(R.string.dialog_select_eu_prefixes_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(stringResource(R.string.search_country_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = null)
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onSelectAll) {
                        Text(stringResource(R.string.action_select_all), fontSize = 12.sp)
                    }
                    TextButton(onClick = onDeselectAll) {
                        Text(stringResource(R.string.action_deselect_all), fontSize = 12.sp)
                    }
                }

                HorizontalDivider()

                if (filteredCountries.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_countries_found),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(filteredCountries, key = { it.dialCode }) { country ->
                            val isSelected = selectedPrefixes.contains(country.dialCode)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onTogglePrefix(country.dialCode) }
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = country.flagEmoji,
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = stringResource(country.nameResId),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = country.dialCode,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { onTogglePrefix(country.dialCode) }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.btn_close))
            }
        }
    )
}

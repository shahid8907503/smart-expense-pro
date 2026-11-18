package com.smart.expense.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smart.expense.data.currency.CurrencyManager
import com.smart.expense.data.local.entities.TransactionEntity
import com.smart.expense.data.repository.AuthRepository
import com.smart.expense.data.repository.ExpenseRepository
import com.smart.expense.ui.components.*
import com.smart.expense.ui.screens.voice.VoiceConfirmationDialog
import com.smart.expense.ui.screens.voice.VoiceListeningDialog
import com.smart.expense.ui.theme.*
import com.smart.expense.util.BankSmsParser
import com.smart.expense.util.DataExporter
import com.smart.expense.voice.ParsedVoiceTransaction
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * World-Class Fintech Dashboard
 * Combining Apple simplicity, Revolut depth, and Linear visual precision.
 */
@Composable
fun HomeScreen(
    authRepository: AuthRepository,
    expenseRepository: ExpenseRepository,
    onNavigateToActivity: () -> Unit,
    onNavigateToBudgets: () -> Unit,
    onNavigateToVoiceQuery: () -> Unit,
    onNavigateToAddManual: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val user = authRepository.currentUser.collectAsState().value
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val allTransactions by expenseRepository.getAllTransactionsFlow(user.userId).collectAsState(initial = emptyList())
    val recentTransactions by expenseRepository.getRecentTransactionsFlow(user.userId, limit = 5).collectAsState(initial = emptyList())

    val cal = Calendar.getInstance()
    val currentMonth = cal.get(Calendar.MONTH) + 1
    val currentYear = cal.get(Calendar.YEAR)
    val hourOfDay = cal.get(Calendar.HOUR_OF_DAY)
    val monthName = SimpleDateFormat("MMMM yyyy", Locale.US).format(Date())

    val timeGreeting = when (hourOfDay) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..21 -> "Good evening"
        else -> "Good night"
    }

    val budgets by expenseRepository.getBudgetsFlow(user.userId, currentMonth, currentYear).collectAsState(initial = emptyList())

    // Calculations
    val totalIncome = allTransactions.filter { it.type == "income" }.sumOf { it.amount }
    val totalExpense = allTransactions.filter { it.type == "expense" }.sumOf { it.amount }
    val availableBalance = totalIncome - totalExpense

    // Dialog States
    var showListeningDialog by remember { mutableStateOf(false) }
    var parsedVoiceTx by remember { mutableStateOf<ParsedVoiceTransaction?>(null) }
    var showDebtTracker by remember { mutableStateOf(false) }
    var showCalendarDialog by remember { mutableStateOf(false) }
    var showRecurringDialog by remember { mutableStateOf(false) }
    var showSavingsGoalsDialog by remember { mutableStateOf(false) }
    var showSplitBillDialog by remember { mutableStateOf(false) }
    var showAutoTrackDialog by remember { mutableStateOf(false) }
    var showSecurityAuditDialog by remember { mutableStateOf(false) }

    // Auto-track bank alert ingestion on home screen load
    LaunchedEffect(user.userId) {
        if (BankSmsParser.isAutoLogEnabled(context)) {
            val pending = BankSmsParser.getPendingAlerts(context)
            if (pending.isNotEmpty()) {
                pending.forEach { alert ->
                    expenseRepository.addTransaction(
                        userId = user.userId,
                        amount = alert.amount,
                        type = alert.type,
                        category = alert.category,
                        description = "${alert.merchant} (${alert.sourceApp})",
                        timestamp = alert.timestamp,
                        paymentMethod = "UPI",
                        paymentType = "EXTERNAL",
                        source = "auto_track"
                    )
                }
                BankSmsParser.clearPendingAlerts(context)
            }
        }
    }

    // Modal Dialogs
    if (showSavingsGoalsDialog) {
        SavingsGoalsDialog(
            currencyCode = user.preferredCurrency,
            onDismiss = { showSavingsGoalsDialog = false }
        )
    }

    if (showAutoTrackDialog) {
        AutoTrackDialog(
            currencyCode = user.preferredCurrency,
            onLogAlert = { alert ->
                scope.launch {
                    expenseRepository.addTransaction(
                        userId = user.userId,
                        amount = alert.amount,
                        type = alert.type,
                        category = alert.category,
                        description = "${alert.merchant} (${alert.sourceApp})",
                        timestamp = alert.timestamp,
                        paymentMethod = "UPI",
                        paymentType = "EXTERNAL",
                        source = "auto_track"
                    )
                }
            },
            onDismiss = { showAutoTrackDialog = false }
        )
    }

    if (showSecurityAuditDialog) {
        SecurityAuditDialog(
            onDismiss = { showSecurityAuditDialog = false }
        )
    }

    if (showSplitBillDialog) {
        SplitBillDialog(
            currencyCode = user.preferredCurrency,
            onAddToDebt = { personName, amount ->
                val prefs = context.getSharedPreferences("smart_expense_debts", android.content.Context.MODE_PRIVATE)
                val gson = com.google.gson.Gson()
                val type = object : com.google.gson.reflect.TypeToken<MutableList<DebtRecord>>() {}.type
                val existing: MutableList<DebtRecord> = try {
                    gson.fromJson(prefs.getString("debts_list", null) ?: "[]", type) ?: mutableListOf()
                } catch (e: Exception) { mutableListOf() }
                existing.add(
                    DebtRecord(
                        personName = personName,
                        amount = amount,
                        isOwedToMe = true,
                        note = "Split bill (auto-added)"
                    )
                )
                prefs.edit().putString("debts_list", gson.toJson(existing)).apply()
            },
            onLogMyShare = { amount, title, category ->
                scope.launch {
                    expenseRepository.addTransaction(
                        userId = user.userId,
                        amount = amount,
                        type = "expense",
                        category = category,
                        description = "My share: $title",
                        paymentMethod = user.defaultPaymentMethod,
                        paymentType = "EXTERNAL",
                        source = "split_bill"
                    )
                }
            },
            onDismiss = { showSplitBillDialog = false }
        )
    }

    if (showDebtTracker) {
        DebtTrackerDialog(
            currencyCode = user.preferredCurrency,
            onDismiss = { showDebtTracker = false }
        )
    }

    if (showCalendarDialog) {
        CalendarExpenseDialog(
            allTransactions = allTransactions,
            currencyCode = user.preferredCurrency,
            onDismiss = { showCalendarDialog = false }
        )
    }

    if (showRecurringDialog) {
        RecurringBillsDialog(
            currencyCode = user.preferredCurrency,
            onPayAndLog = { bill ->
                scope.launch {
                    expenseRepository.addTransaction(
                        userId = user.userId,
                        amount = bill.amount,
                        type = "expense",
                        category = bill.category,
                        description = "${bill.title} (${bill.cycle})",
                        paymentMethod = user.defaultPaymentMethod,
                        paymentType = "EXTERNAL",
                        source = "recurring"
                    )
                }
            },
            onDismiss = { showRecurringDialog = false }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            AdBannerView()
        },
        floatingActionButton = {
            // Starling Mint Floating Action Button
            FloatingActionButton(
                onClick = onNavigateToAddManual,
                containerColor = StarlingMintAccent,
                contentColor = Color(0xFF021B1F),
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Transaction",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // =================================================================
            // 1. TOP HEADER: GREETING, MONTH & AVATAR
            // =================================================================
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$timeGreeting, ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = user.name.split(" ").firstOrNull() ?: "Shahid",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = monthName,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    // Top Action Icons: Security Shield & Avatar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Security Center Quick Access
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                .clickable { showSecurityAuditDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Security Audit",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // User Avatar with Refined Primary Ring
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                .clickable { onNavigateToProfile() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (user.name.firstOrNull() ?: 'U').toString().uppercase(),
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            // =================================================================
            // 2. HERO BALANCE CARD (Dynamic Multi-Currency & Sheen)
            // =================================================================
            item {
                val curr = CurrencyManager.getCurrency(user.preferredCurrency)
                FinancialSummaryCard(
                    balance = availableBalance * curr.exchangeRateFromInr,
                    totalIncome = totalIncome * curr.exchangeRateFromInr,
                    totalExpense = totalExpense * curr.exchangeRateFromInr,
                    currencySymbol = curr.symbol
                )
            }

            // =================================================================
            // 3. AI FINANCIAL PULSE & INSIGHTS BANNER
            // =================================================================
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToVoiceQuery() }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "AI FINANCIAL INSIGHT",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.1.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(FintechEmerald)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            val insightText = if (totalExpense > 0 && totalIncome > 0) {
                                val ratio = ((totalExpense / totalIncome) * 100).toInt()
                                if (ratio > 75) "Notice: Expenses reached $ratio% of income. Tap to review saving tips."
                                else "Healthy trend: You retained ${100 - ratio}% of earnings this month."
                            } else {
                                "Tap to ask voice assistant: \"How much did I spend this week?\""
                            }
                            Text(
                                text = insightText,
                                fontSize = 12.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 17.sp
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // =================================================================
            // 4. INTELLIGENT VOICE ENTRY HERO INTERACTION
            // =================================================================
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .clickable { showListeningDialog = true },
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    val isDarkTheme = isSystemInDarkTheme()
                    val voiceBannerColors = if (isDarkTheme) {
                        listOf(
                            Color(0xFF063B44), // Starling Dark Sea Pine
                            Color(0xFF021B1F)  // Abyssal Pine
                        )
                    } else {
                        listOf(
                            Color(0xFF0A4753), // Starling Oceanic Pine
                            Color(0xFF03282E)  // Deep Oceanic Pine
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Brush.horizontalGradient(voiceBannerColors))
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(22.dp))
                            .padding(horizontal = 20.dp, vertical = 18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(FintechEmeraldLight)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "VOICE EXPENSE ENTRY",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.5.sp,
                                            letterSpacing = 1.2.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        ),
                                        color = Color(0xFFC7D2FE)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Log Transactions with Voice",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "Say \"Spent ₹250 on lunch\" in English, Hindi, or Telugu",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }

                            // Glowing Microphone Orb
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f))
                                    .border(1.5.dp, Color.White.copy(alpha = 0.4f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Voice microphone",
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }
            }

            // =================================================================
            // 5. QUICK ACTIONS APPS & FINANCIAL TOOL MATRIX
            // =================================================================
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Smart Financial Tools",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "All Tools Active",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Modern 4-column quick action grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionPill(
                        title = "Bank SMS",
                        icon = Icons.Default.MarkChatUnread,
                        iconTint = FintechCyan,
                        modifier = Modifier.weight(1f),
                        onClick = { showAutoTrackDialog = true }
                    )
                    QuickActionPill(
                        title = "Split Bill",
                        icon = Icons.Default.Group,
                        iconTint = FintechVioletLight,
                        modifier = Modifier.weight(1f),
                        onClick = { showSplitBillDialog = true }
                    )
                    QuickActionPill(
                        title = "Savings",
                        icon = Icons.Default.Savings,
                        iconTint = FintechEmerald,
                        modifier = Modifier.weight(1f),
                        onClick = { showSavingsGoalsDialog = true }
                    )
                    QuickActionPill(
                        title = "Calendar",
                        icon = Icons.Default.CalendarMonth,
                        iconTint = FintechAmber,
                        modifier = Modifier.weight(1f),
                        onClick = { showCalendarDialog = true }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionPill(
                        title = "Recurring",
                        icon = Icons.Default.Autorenew,
                        iconTint = Color(0xFF60A5FA),
                        modifier = Modifier.weight(1f),
                        onClick = { showRecurringDialog = true }
                    )
                    QuickActionPill(
                        title = "Debts/Loans",
                        icon = Icons.Default.AccountBalanceWallet,
                        iconTint = Color(0xFFF472B6),
                        modifier = Modifier.weight(1f),
                        onClick = { showDebtTracker = true }
                    )
                    QuickActionPill(
                        title = "Export CSV",
                        icon = Icons.Default.FileDownload,
                        iconTint = FintechEmeraldLight,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            DataExporter.exportTransactionsToCsv(
                                context = context,
                                transactions = allTransactions,
                                currencyCode = user.preferredCurrency,
                                dateFormatPattern = user.dateFormat
                            )
                        }
                    )
                    QuickActionPill(
                        title = "Budgets",
                        icon = Icons.Default.AccountBalanceWallet,
                        iconTint = FintechElectricViolet,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToBudgets
                    )
                }
            }

            // =================================================================
            // 6. MULTI-TIMELINE BREAKDOWN CARD
            // =================================================================
            item {
                MultiTimelineBreakdownCard(
                    allTransactions = allTransactions,
                    currencyCode = user.preferredCurrency,
                    onNavigateToBudgets = onNavigateToBudgets,
                    onNavigateToActivity = onNavigateToActivity,
                    onOpenCalendar = { showCalendarDialog = true },
                    onOpenRecurring = { showRecurringDialog = true },
                    onOpenSavingsGoals = { showSavingsGoalsDialog = true },
                    onOpenSplitBill = { showSplitBillDialog = true },
                    onOpenDebtTracker = { showDebtTracker = true },
                    onOpenAutoTrack = { showAutoTrackDialog = true },
                    onOpenSecurityAudit = { showSecurityAuditDialog = true },
                    onExportCsv = {
                        DataExporter.exportTransactionsToCsv(
                            context = context,
                            transactions = allTransactions,
                            currencyCode = user.preferredCurrency,
                            dateFormatPattern = user.dateFormat
                        )
                    }
                )
            }

            // =================================================================
            // 7. AI FINANCIAL HEALTH SCORE
            // =================================================================
            item {
                FinancialHealthCard(
                    totalIncome = totalIncome,
                    totalExpense = totalExpense,
                    transactions = allTransactions
                )
            }

            // =================================================================
            // 8. MONTHLY BUDGET SUMMARY (IF CONFIGURED)
            // =================================================================
            if (budgets.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Monthly Budgets",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        TextButton(onClick = onNavigateToBudgets) {
                            Text(
                                text = "View all",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                val topBudget = budgets.first()
                val spent = allTransactions
                    .filter { it.type == "expense" && it.category.equals(topBudget.category, ignoreCase = true) }
                    .sumOf { it.amount }

                item {
                    BudgetProgressItem(
                        category = topBudget.category,
                        monthlyLimit = topBudget.monthlyLimit,
                        spent = spent
                    )
                }
            }

            // =================================================================
            // 9. RECENT TRANSACTIONS ACTIVITY FEED
            // =================================================================
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Transactions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    TextButton(onClick = onNavigateToActivity) {
                        Text(
                            text = "See all (${allTransactions.size})",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (recentTransactions.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp, horizontal = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = null,
                                tint = FintechTextMuted,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "No transactions yet",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap the microphone above or the + button to record your first expense",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(recentTransactions) { tx ->
                    TransactionRowItem(
                        transaction = tx,
                        currencyCode = user.preferredCurrency,
                        onClick = onNavigateToActivity
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    // Voice Listening Modal Dialog
    if (showListeningDialog) {
        VoiceListeningDialog(
            preferredLanguage = user.preferredLanguage,
            onDismiss = { showListeningDialog = false },
            onTransactionParsed = { parsed ->
                showListeningDialog = false
                parsedVoiceTx = parsed
            }
        )
    }

    // Voice Confirmation & Edit Modal Dialog
    parsedVoiceTx?.let { parsed ->
        VoiceConfirmationDialog(
            initialData = parsed,
            onDismiss = { parsedVoiceTx = null },
            onConfirm = { amount, type, category, desc, timestamp, paymentMethod, paymentType ->
                scope.launch {
                    expenseRepository.addTransaction(
                        userId = user.userId,
                        amount = amount,
                        type = type,
                        category = category,
                        description = desc,
                        timestamp = timestamp,
                        paymentMethod = paymentMethod,
                        paymentType = paymentType,
                        source = "voice",
                        rawVoiceText = parsed.rawText
                    )
                }
                parsedVoiceTx = null
            }
        )
    }
}

/**
 * Sleek Linear-style Quick Action Pill
 */
@Composable
private fun QuickActionPill(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier.height(76.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(19.dp)
                )
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}

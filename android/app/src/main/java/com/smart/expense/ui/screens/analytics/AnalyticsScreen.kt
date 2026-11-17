package com.smart.expense.ui.screens.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smart.expense.data.currency.CurrencyManager
import com.smart.expense.data.repository.AuthRepository
import com.smart.expense.data.repository.ExpenseRepository
import com.smart.expense.ui.components.*
import com.smart.expense.ui.theme.*
import com.smart.expense.util.DataExporter
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AnalyticsScreen(
    authRepository: AuthRepository,
    expenseRepository: ExpenseRepository,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val user = authRepository.currentUser.collectAsState().value
    val curr = CurrencyManager.getCurrency(user.preferredCurrency)
    val allTransactions by expenseRepository.getAllTransactionsFlow(user.userId).collectAsState(initial = emptyList())

    // Category Breakdown Calculation
    val categorySpendItems = remember(allTransactions) {
        val expenseMap = mutableMapOf<String, Double>()
        allTransactions.filter { it.type == "expense" }.forEach { tx ->
            expenseMap[tx.category] = (expenseMap[tx.category] ?: 0.0) + tx.amount
        }
        expenseMap.map { (cat, amt) ->
            CategorySpendItem(
                category = cat,
                amount = amt,
                color = getCategoryColor(cat)
            )
        }.sortedByDescending { it.amount }
    }

    // Monthly Trends Calculation (Last 5 Months)
    val monthlyTrends = remember(allTransactions) {
        val list = mutableListOf<MonthlyTrendItem>()
        for (i in 4 downTo 0) {
            val c = Calendar.getInstance()
            c.add(Calendar.MONTH, -i)
            val m = c.get(Calendar.MONTH)
            val y = c.get(Calendar.YEAR)
            val mName = SimpleDateFormat("MMM", Locale.US).format(c.time)

            val inc = allTransactions.filter {
                val d = Calendar.getInstance().apply { timeInMillis = it.transactionDate }
                it.type == "income" && d.get(Calendar.MONTH) == m && d.get(Calendar.YEAR) == y
            }.sumOf { it.amount }

            val exp = allTransactions.filter {
                val d = Calendar.getInstance().apply { timeInMillis = it.transactionDate }
                it.type == "expense" && d.get(Calendar.MONTH) == m && d.get(Calendar.YEAR) == y
            }.sumOf { it.amount }

            list.add(MonthlyTrendItem(mName, inc, exp))
        }
        list
    }

    // Summary calculations
    val calToday = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
    }
    val todaySpending = allTransactions.filter { it.type == "expense" && it.transactionDate >= calToday.timeInMillis }.sumOf { it.amount }

    val calWeek = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -7) }
    val weekSpending = allTransactions.filter { it.type == "expense" && it.transactionDate >= calWeek.timeInMillis }.sumOf { it.amount }

    val calMonth = Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }
    val monthSpending = allTransactions.filter { it.type == "expense" && it.transactionDate >= calMonth.timeInMillis }.sumOf { it.amount }

    val totalIncome = allTransactions.filter { it.type == "income" }.sumOf { it.amount }
    val totalExpense = allTransactions.filter { it.type == "expense" }.sumOf { it.amount }
    val savingsRate = if (totalIncome > 0) (((totalIncome - totalExpense) / totalIncome) * 100).toInt() else 0

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Analytics & Insights",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Download Statement / Export Button
                IconButton(
                    onClick = {
                        DataExporter.exportTransactionsToCsv(
                            context = context,
                            transactions = allTransactions,
                            currencyCode = user.preferredCurrency,
                            dateFormatPattern = user.dateFormat
                        )
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = "Export Report",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Spacer(modifier = Modifier.height(2.dp))

            // 1. Metric Trio Cards (Today, Week, Month)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FintechMetricPill(
                    label = "TODAY",
                    amount = "${curr.symbol}${String.format(Locale.US, "%,.0f", todaySpending)}",
                    accentColor = FintechCyan,
                    modifier = Modifier.weight(1f)
                )
                FintechMetricPill(
                    label = "7 DAYS",
                    amount = "${curr.symbol}${String.format(Locale.US, "%,.0f", weekSpending)}",
                    accentColor = FintechElectricViolet,
                    modifier = Modifier.weight(1f)
                )
                FintechMetricPill(
                    label = "THIS MONTH",
                    amount = "${curr.symbol}${String.format(Locale.US, "%,.0f", monthSpending)}",
                    accentColor = FintechCoral,
                    modifier = Modifier.weight(1f)
                )
            }

            // 2. Savings Rate & Cash Retention Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "NET SAVINGS RATE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = FintechTextMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (totalIncome > 0) "$savingsRate% of Earnings" else "No Income Recorded",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (savingsRate >= 20) FintechEmerald else FintechAmber
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (savingsRate >= 20) FintechEmerald.copy(alpha = 0.18f) else FintechAmber.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (savingsRate >= 20) Icons.Default.Savings else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (savingsRate >= 20) FintechEmerald else FintechAmber,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // 3. Category Breakdown Donut Chart
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(22.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Category Distribution",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${categorySpendItems.size} Categories",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    CategorySpendingChart(items = categorySpendItems)
                }
            }

            // 4. Monthly Dual Bar Trend Chart
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(22.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Monthly Cashflow (Income vs Expense)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    MonthlyTrendChart(trends = monthlyTrends)
                }
            }

            // 5. Smart AI Spending Observation Card
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AI SPENDING RECOMMENDATION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.1.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        val topCategory = categorySpendItems.firstOrNull()?.category ?: "Food"
                        Text(
                            text = "Your largest expenditure is in $topCategory. Setting a category budget limit under the Budgets tab will help you retain ₹2,500 more this cycle.",
                            fontSize = 12.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun FintechMetricPill(
    label: String,
    amount: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                letterSpacing = 1.1.sp,
                fontWeight = FontWeight.Bold,
                color = FintechTextMuted
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = amount,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor
            )
        }
    }
}

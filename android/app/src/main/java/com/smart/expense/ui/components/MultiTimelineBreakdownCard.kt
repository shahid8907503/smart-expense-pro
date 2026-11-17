package com.smart.expense.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smart.expense.data.currency.CurrencyManager
import com.smart.expense.data.local.entities.TransactionEntity
import com.smart.expense.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

data class TimelinePeriodData(
    val title: String,
    val dateRangeLabel: String,
    val income: Double,
    val expense: Double,
    val balance: Double
)

/**
 * Multi-Timeline Financial Breakdown Card & Quick Action Matrix
 * Refined with Linear-inspired dark cards and crisp typography.
 */
@Composable
fun MultiTimelineBreakdownCard(
    allTransactions: List<TransactionEntity>,
    currencyCode: String = "INR",
    onNavigateToBudgets: () -> Unit,
    onNavigateToActivity: () -> Unit,
    onOpenCalendar: () -> Unit = {},
    onOpenRecurring: () -> Unit = {},
    onOpenSavingsGoals: () -> Unit = {},
    onOpenSplitBill: () -> Unit = {},
    onOpenDebtTracker: () -> Unit = {},
    onOpenAutoTrack: () -> Unit = {},
    onOpenSecurityAudit: () -> Unit = {},
    onExportCsv: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val cal = Calendar.getInstance()
    val nowMs = System.currentTimeMillis()

    // 1. Today's start
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    val todayStartMs = cal.timeInMillis

    // 2. This Week's start (Monday)
    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    val weekStartMs = cal.timeInMillis

    // 3. This Month's start
    cal.set(Calendar.DAY_OF_MONTH, 1)
    val monthStartMs = cal.timeInMillis

    // 4. Year to Date start (Jan 1)
    cal.set(Calendar.DAY_OF_YEAR, 1)
    val yearStartMs = cal.timeInMillis

    fun computePeriod(startMs: Long): Pair<Double, Double> {
        val filtered = allTransactions.filter { it.transactionDate in startMs..nowMs }
        val income = filtered.filter { it.type == "income" }.sumOf { it.amount }
        val expense = filtered.filter { it.type == "expense" }.sumOf { it.amount }
        return Pair(income, expense)
    }

    val todayPair = computePeriod(todayStartMs)
    val weekPair = computePeriod(weekStartMs)
    val monthPair = computePeriod(monthStartMs)
    val yearPair = computePeriod(yearStartMs)

    val dateFormat = SimpleDateFormat("MM-dd", Locale.US)
    val todayDateStr = SimpleDateFormat("yyyy-MM-dd EEE", Locale.US).format(Date(nowMs))
    val weekRangeStr = "${dateFormat.format(Date(weekStartMs))} - ${dateFormat.format(Date(nowMs))}"
    val monthRangeStr = "${dateFormat.format(Date(monthStartMs))} - ${dateFormat.format(Date(nowMs))}"
    val yearRangeStr = "${SimpleDateFormat("yyyy-01-01", Locale.US).format(Date(nowMs))} - Today"

    val periods = listOf(
        TimelinePeriodData("Today", todayDateStr, todayPair.first, todayPair.second, todayPair.first - todayPair.second),
        TimelinePeriodData("This Week", weekRangeStr, weekPair.first, weekPair.second, weekPair.first - weekPair.second),
        TimelinePeriodData("This Month", monthRangeStr, monthPair.first, monthPair.second, monthPair.first - monthPair.second),
        TimelinePeriodData("Year to Date", yearRangeStr, yearPair.first, yearPair.second, yearPair.first - yearPair.second)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {

            // Breakdown Table Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Timeline Breakdown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "Income / Expense / Net",
                    style = MaterialTheme.typography.labelSmall,
                    color = FintechTextMuted
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Timeline Rows (Today, This Week, This Month, Year to Date)
            periods.forEachIndexed { index, p ->
                TimelineRowItem(
                    period = p,
                    currencyCode = currencyCode,
                    onClick = onNavigateToActivity
                )
                if (index < periods.size - 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun TimelineRowItem(
    period: TimelinePeriodData,
    currencyCode: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1.3f)) {
            Text(
                text = period.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = period.dateRangeLabel,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = FintechTextMuted
            )
        }

        // Income & Expense
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.weight(1.1f)
        ) {
            Text(
                text = "+${CurrencyManager.format(period.income, currencyCode)}",
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = FintechEmerald
            )
            Text(
                text = "-${CurrencyManager.format(period.expense, currencyCode)}",
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = FintechCoral
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Net Balance
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.weight(1.1f)
        ) {
            Text(
                text = "Net Cashflow",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.5.sp),
                color = FintechTextMuted
            )
            Text(
                text = CurrencyManager.format(period.balance, currencyCode),
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (period.balance >= 0) FintechCyan else FintechCoral
            )
        }
    }
}

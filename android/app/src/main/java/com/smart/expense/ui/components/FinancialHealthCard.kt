package com.smart.expense.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smart.expense.data.local.entities.TransactionEntity
import com.smart.expense.ui.theme.*
import java.util.Calendar

/**
 * World-Class AI Financial Health & Velocity Card
 * Midnight Navy surfaces, Electric Violet & Cyan glowing progress bar.
 */
@Composable
fun FinancialHealthCard(
    totalIncome: Double,
    totalExpense: Double,
    transactions: List<TransactionEntity>,
    modifier: Modifier = Modifier
) {
    val cal = Calendar.getInstance()
    val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH).coerceAtLeast(1)
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

    // Algorithmic Velocity Metrics
    val dailyBurnRate = totalExpense / dayOfMonth
    val projectedMonthEndExpense = dailyBurnRate * daysInMonth
    val projectedSavings = (totalIncome - projectedMonthEndExpense).coerceAtLeast(0.0)

    // Top spending category calculation
    val expenseTx = transactions.filter { it.type == "expense" }
    val categoryTotals = mutableMapOf<String, Double>()
    expenseTx.forEach { tx ->
        categoryTotals[tx.category] = (categoryTotals[tx.category] ?: 0.0) + tx.amount
    }
    val topEntry = categoryTotals.maxByOrNull { it.value }
    val topCatName = topEntry?.key ?: "Expenses"
    val topCatPercent = if (totalExpense > 0 && topEntry != null) {
        ((topEntry.value / totalExpense) * 100).toInt()
    } else 0

    // Algorithmic Financial Health Score (0-100)
    val savingsRatio = if (totalIncome > 0) {
        ((totalIncome - totalExpense) / totalIncome).coerceIn(0.0, 1.0)
    } else 0.5

    val rawScore = (savingsRatio * 70.0 + (if (projectedSavings > 0) 25.0 else 5.0)).toInt()
    val healthScore = rawScore.coerceIn(20, 98)

    val (statusLabel, statusColor, statusBg) = when {
        healthScore >= 80 -> Triple("EXCELLENT", FintechEmerald, FintechEmeraldContainer)
        healthScore >= 65 -> Triple("HEALTHY", FintechCyan, FintechCyanContainer)
        healthScore >= 50 -> Triple("MODERATE", FintechAmber, FintechAmberContainer)
        else -> Triple("NEEDS ATTENTION", FintechCoral, FintechCoralContainer)
    }

    val animatedProgress by animateFloatAsState(
        targetValue = healthScore / 100f,
        animationSpec = tween(durationMillis = 800),
        label = "healthScoreAnim"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row: AI Badge + Health Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AI FINANCIAL HEALTH",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusBg,
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = statusLabel,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Score Display Row: "88/100" + Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$healthScore",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = " / 100",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = statusColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Velocity Metrics Grid: Daily Burn Rate & Projected Savings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Daily Burn Rate",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "₹${dailyBurnRate.toInt()} / day",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Projected Savings",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "₹${projectedSavings.toInt()}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = FintechEmerald
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dynamic AI Algorithmic Insight
            val aiTip = if (topCatPercent > 0) {
                "At this velocity, you'll retain ₹${projectedSavings.toInt()} this month. $topCatName accounts for $topCatPercent% of expenses."
            } else {
                "At this velocity, you'll retain ₹${projectedSavings.toInt()} this month. Keep logging to optimize savings."
            }

            Text(
                text = "💡 $aiTip",
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

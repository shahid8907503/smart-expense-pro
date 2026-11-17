package com.smart.expense.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smart.expense.ui.theme.*

/**
 * World-Class Fintech Balance Hero Card
 * Inspired by Apple Card & Revolut Ultra:
 * - Dynamic Midnight/Electric Violet gradient with subtle sheen
 * - Bold tabular currency typography
 * - Savings rate metric pill
 * - Income & Expense sub-counters with translucent badges
 * - Visual cashflow health bar
 */
@Composable
fun FinancialSummaryCard(
    balance: Double,
    totalIncome: Double,
    totalExpense: Double,
    currencySymbol: String = "₹",
    modifier: Modifier = Modifier
) {
    val savings = (totalIncome - totalExpense).coerceAtLeast(0.0)
    val savingsRate = if (totalIncome > 0) ((savings / totalIncome) * 100).toInt() else 0
    val expenseRatio = if (totalIncome > 0) ((totalExpense / totalIncome).coerceIn(0.0, 1.0)).toFloat() else 0f

    // Smooth Revolut-style Animated Rolling Balance
    val animatedBalance by animateFloatAsState(
        targetValue = balance.toFloat(),
        animationSpec = tween(durationMillis = 950, easing = FastOutSlowInEasing),
        label = "animated_balance"
    )

    val isDark = isSystemInDarkTheme()
    // Signature Coinpay Royal Cobalt Gradient Hero Card
    val cardColors = if (isDark) {
        listOf(
            Color(0xFF1E3A8A), // Deep Cobalt Navy
            Color(0xFF1E293B), // Matte Slate
            Color(0xFF0F172A)  // Deep Obsidian
        )
    } else {
        listOf(
            Color(0xFF1D4ED8), // Vibrant Royal Cobalt 700
            Color(0xFF2563EB), // Electric Royal Blue 600
            Color(0xFF1E40AF)  // Deep Royal Slate
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(cardColors))
            .border(
                width = 1.dp,
                color = Color(0x33FFFFFF),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(22.dp)
    ) {
        Column {
            // Header Row: Label & Savings Rate Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF67E8F9)) // Soft Cyan Dot
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TOTAL AVAILABLE BALANCE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            letterSpacing = 1.4.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color(0xFFE2E8F0)
                    )
                }

                // Savings Pill Badge (Coinpay Solar Accent)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x2EFFFFFF),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x40FFFFFF))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = Color(0xFFFDE68A),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (totalIncome > 0) "$savingsRate% Saved" else "Active",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFEF08A)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Large Hero Tabular Balance Amount
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currencySymbol,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 26.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color(0xFFBFDBFE)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = String.format(java.util.Locale.US, "%,.2f", animatedBalance.toDouble()),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.SansSerif,
                        letterSpacing = (-0.5).sp
                    ),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Visual Cashflow Bar (Expense vs Retained)
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Cashflow Retention",
                        fontSize = 11.sp,
                        color = Color(0xFFBFDBFE)
                    )
                    Text(
                        text = if (totalIncome > 0) "${(100 - (expenseRatio * 100)).toInt()}% Retained" else "Ready",
                        fontSize = 11.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0x33FFFFFF))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = (1f - expenseRatio).coerceIn(0.05f, 1f))
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF60A5FA), Color(0xFFFDE68A))
                                )
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Income, Expense & Net Savings Metric Cards Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x24FFFFFF))
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp))
                    .padding(vertical = 12.dp, horizontal = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Income Sub-counter
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x3310B981)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Income",
                            tint = Color(0xFF6EE7B7),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Income",
                            fontSize = 11.sp,
                            color = Color(0xFFE2E8F0)
                        )
                        Text(
                            text = "+$currencySymbol${String.format(java.util.Locale.US, "%,.0f", totalIncome)}",
                            fontSize = 13.5.sp,
                            color = Color(0xFFA7F3D0),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Divider
                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .width(1.dp)
                        .background(Color(0x33FFFFFF))
                )

                // 2. Expense Sub-counter
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x33EF4444)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Expense",
                            tint = Color(0xFFFCA5A5),
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Expenses",
                            fontSize = 11.sp,
                            color = Color(0xFFE2E8F0)
                        )
                        Text(
                            text = "-$currencySymbol${String.format(java.util.Locale.US, "%,.0f", totalExpense)}",
                            fontSize = 13.5.sp,
                            color = Color(0xFFFECACA),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Divider
                Box(
                    modifier = Modifier
                        .height(28.dp)
                        .width(1.dp)
                        .background(Color(0x33FFFFFF))
                )

                // 3. Net Savings Sub-counter
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x33F59E0B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Savings,
                            contentDescription = "Savings",
                            tint = Color(0xFFFDE68A),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Net Saved",
                            fontSize = 11.sp,
                            color = Color(0xFFE2E8F0)
                        )
                        Text(
                            text = "$currencySymbol${String.format(java.util.Locale.US, "%,.0f", savings)}",
                            fontSize = 13.5.sp,
                            color = Color(0xFFFEF08A),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

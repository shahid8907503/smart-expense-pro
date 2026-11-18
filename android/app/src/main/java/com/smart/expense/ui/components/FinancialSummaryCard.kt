package com.smart.expense.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
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
 * Starling Bank × Wealthsimple Signature Neobank Hero Card
 * Inspired by Starling Bank's iconic Oceanic Pine canvas & dual spending spaces:
 * - Rich Abyssal Pine gradient with atmospheric glass highlight
 * - Interactive Balance Visibility Toggle (Tap eye to mask balance)
 * - Starling Dual Spaces: "In Spending Spaces" & "Spent This Month"
 * - High-contrast tabular numerals with currency display
 * - Cashflow health indicator bar with Electric Jade accent
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

    var isBalanceHidden by remember { mutableStateOf(false) }

    // Smooth Neobank Animated Rolling Balance
    val animatedBalance by animateFloatAsState(
        targetValue = balance.toFloat(),
        animationSpec = tween(durationMillis = 950, easing = FastOutSlowInEasing),
        label = "animated_balance"
    )

    val isDark = isSystemInDarkTheme()
    // Starling Bank Signature Oceanic Pine & Deep Sea Teal Gradient
    val cardGradient = if (isDark) {
        listOf(
            Color(0xFF021B1F), // Abyssal Sea Pine
            Color(0xFF073841), // Deep Oceanic Teal
            Color(0xFF0C4D59)  // Starling Signature Teal
        )
    } else {
        listOf(
            Color(0xFF03282E), // Deep Oceanic Pine
            Color(0xFF0A4753), // Starling Sea Pine
            Color(0xFF105F6E)  // Vibrant Sea Teal
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(Brush.linearGradient(cardGradient))
            .border(
                width = 1.dp,
                color = Color(0x3300E676), // Subtle electric jade sheen border
                shape = RoundedCornerShape(26.dp)
            )
            .padding(22.dp)
    ) {
        Column {
            // Header Row: Starling Account Badge & Privacy Eye Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Starling Neobank Pill Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0x26FFFFFF),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00E676)) // Live Starling Active Mint Dot
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "PERSONAL ACCOUNT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.5.sp,
                                letterSpacing = 1.2.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color(0xFFE2E8F0)
                        )
                    }
                }

                // Right: Eye Privacy Toggle + Savings Rate Pill
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Eye Privacy Icon
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x20FFFFFF))
                            .clickable { isBalanceHidden = !isBalanceHidden },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isBalanceHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle Balance Visibility",
                            tint = Color(0xFFE2E8F0),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Savings Pill Badge (Electric Mint)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0x2E00E676),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x4000E676))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = null,
                                tint = Color(0xFF6EE7B7),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (totalIncome > 0) "$savingsRate% Saved" else "Active",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFA7F3D0)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Starling Large Hero Tabular Balance Amount
            Column {
                Text(
                    text = "Total Balance",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color(0xFF94A3B8)
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isBalanceHidden) {
                        Text(
                            text = "$currencySymbol • • • • • •",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            ),
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = currencySymbol,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontSize = 26.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color(0xFF99F6E4) // Sea glass cyan
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
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Starling Dual Spaces (Seen at 0:28s in your video)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Box 1: In Spending Spaces / Budget
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x1FFFFFFF))
                        .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 9.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF38BDF8))
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Spending Spaces",
                                fontSize = 10.5.sp,
                                color = Color(0xFFBAE6FD),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = if (isBalanceHidden) "••••" else "$currencySymbol${String.format(java.util.Locale.US, "%,.0f", savings)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Box 2: Total Spent This Month
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x1FFFFFFF))
                        .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 9.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF87171))
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Spent This Month",
                                fontSize = 10.5.sp,
                                color = Color(0xFFFECDD3),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = if (isBalanceHidden) "••••" else "$currencySymbol${String.format(java.util.Locale.US, "%,.0f", totalExpense)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Visual Cashflow Retention Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Cashflow Retention",
                        fontSize = 11.sp,
                        color = Color(0xFF99F6E4)
                    )
                    Text(
                        text = if (totalIncome > 0) "${(100 - (expenseRatio * 100)).toInt()}% Retained" else "Ready",
                        fontSize = 11.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(5.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0x29FFFFFF))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = (1f - expenseRatio).coerceIn(0.05f, 1f))
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF00E676), Color(0xFF38BDF8))
                                )
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3-Column Glass Metric Pill Row (Income, Expense, Net Saved)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x22FFFFFF))
                    .border(1.dp, Color(0x26FFFFFF), RoundedCornerShape(16.dp))
                    .padding(vertical = 10.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Income
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0x3310B981)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Income",
                            tint = Color(0xFF6EE7B7),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "Income",
                            fontSize = 10.5.sp,
                            color = Color(0xFFE2E8F0)
                        )
                        Text(
                            text = if (isBalanceHidden) "••••" else "+$currencySymbol${String.format(java.util.Locale.US, "%,.0f", totalIncome)}",
                            fontSize = 13.sp,
                            color = Color(0xFFA7F3D0),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Divider
                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .width(1.dp)
                        .background(Color(0x26FFFFFF))
                )

                // 2. Expenses
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0x33EF4444)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Expense",
                            tint = Color(0xFFFCA5A5),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "Expenses",
                            fontSize = 10.5.sp,
                            color = Color(0xFFE2E8F0)
                        )
                        Text(
                            text = if (isBalanceHidden) "••••" else "-$currencySymbol${String.format(java.util.Locale.US, "%,.0f", totalExpense)}",
                            fontSize = 13.sp,
                            color = Color(0xFFFECACA),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Divider
                Box(
                    modifier = Modifier
                        .height(24.dp)
                        .width(1.dp)
                        .background(Color(0x26FFFFFF))
                )

                // 3. Net Saved
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0x3300E676)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Savings,
                            contentDescription = "Savings",
                            tint = Color(0xFF6EE7B7),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "Net Saved",
                            fontSize = 10.5.sp,
                            color = Color(0xFFE2E8F0)
                        )
                        Text(
                            text = if (isBalanceHidden) "••••" else "$currencySymbol${String.format(java.util.Locale.US, "%,.0f", savings)}",
                            fontSize = 13.sp,
                            color = Color(0xFF6EE7B7),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

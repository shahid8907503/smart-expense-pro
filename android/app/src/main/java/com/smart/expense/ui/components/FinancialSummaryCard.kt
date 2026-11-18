package com.smart.expense.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smart.expense.ui.theme.*

/**
 * PLAYFUL FINTECH × NEO-TACTILE FINANCIAL HERO CARD WITH 3D TILT PHYSICS
 * - Pure Native 3D Perspective Tilt (Touch drag tilts the card in 3D space)
 * - Spring Physics Deceleration (wobbles and bounces back on release)
 * - Dynamic Specular Glare that moves with the 3D angle
 * - Large tabular numerals for immediate financial clarity
 * - Interactive Privacy Eye Toggle
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

    // 3D Tilt Angles (X and Y axis)
    var tiltX by remember { mutableFloatStateOf(0f) }
    var tiltY by remember { mutableFloatStateOf(0f) }

    // Bouncy Spring Physics on release (CRED / Apple Card style)
    val animatedTiltX by animateFloatAsState(
        targetValue = tiltX,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_tilt_x"
    )
    val animatedTiltY by animateFloatAsState(
        targetValue = tiltY,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_tilt_y"
    )

    val animatedBalance by animateFloatAsState(
        targetValue = balance.toFloat(),
        animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing),
        label = "animated_balance"
    )

    val isDark = isSystemInDarkTheme()
    val heroBg = if (isDark) DarkPaperCard else PaletteForestInk
    val heroBorder = if (isDark) DarkPaperBorder else PaletteForestInk

    // Physical Tactile Shadow Offset Layering with 3D Graphics Layer
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, _ ->
                        change.consume()
                        val w = size.width.toFloat().coerceAtLeast(1f)
                        val h = size.height.toFloat().coerceAtLeast(1f)
                        val px = change.position.x.coerceIn(0f, w)
                        val py = change.position.y.coerceIn(0f, h)
                        tiltX = -((py - h / 2f) / (h / 2f)) * 14f
                        tiltY = ((px - w / 2f) / (w / 2f)) * 14f
                    },
                    onDragEnd = {
                        tiltX = 0f
                        tiltY = 0f
                    },
                    onDragCancel = {
                        tiltX = 0f
                        tiltY = 0f
                    }
                )
            }
            .graphicsLayer {
                rotationX = animatedTiltX
                rotationY = animatedTiltY
                cameraDistance = 16f * density
            }
    ) {
        // Physical tactile back-shadow (Forest Ink 3.5dp offset)
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 3.dp, y = 4.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(if (isDark) Color(0xFF0D0F0C) else Color(0x33242923))
        )

        // Main Hero Surface
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(heroBg)
                .border(
                    width = 1.5.dp,
                    color = heroBorder,
                    shape = RoundedCornerShape(26.dp)
                )
                .padding(22.dp)
        ) {
            Column {
                // Header Row: Active Account Badge & Privacy Eye Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tactile Pill Badge (Electric Lime Highlight)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = PaletteElectricLime,
                        border = androidx.compose.foundation.BorderStroke(1.dp, PaletteForestInk)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(PaletteForestInk)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "NET AVAILABLE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.5.sp,
                                    letterSpacing = 1.1.sp,
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                color = PaletteForestInk
                            )
                        }
                    }

                    // Right: Privacy Eye Toggle + Savings Metric Pill
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0x22FFFFFF))
                                .clickable { isBalanceHidden = !isBalanceHidden },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isBalanceHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle Balance Visibility",
                                tint = PaletteWarmPaper,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Savings Pill Badge (Mint Mist)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PaletteMintMist,
                            border = androidx.compose.foundation.BorderStroke(1.dp, PaletteBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = null,
                                    tint = PaletteForestInk,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (totalIncome > 0) "$savingsRate% Saved" else "Active",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PaletteForestInk
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hero Tabular Balance Amount
                Column {
                    Text(
                        text = "Total Balance",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = Color(0xFFA6ABA0)
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
                                color = PaletteWarmPaper
                            )
                        } else {
                            Text(
                                text = currencySymbol,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = PaletteElectricLime
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = String.format(java.util.Locale.US, "%,.2f", animatedBalance.toDouble()),
                                style = MaterialTheme.typography.displayLarge.copy(
                                    fontSize = 38.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = FontFamily.SansSerif,
                                    letterSpacing = (-0.8).sp
                                ),
                                color = PaletteWarmPaper
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Cashflow Health Retention Bar
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Cashflow Retention",
                            fontSize = 11.sp,
                            color = Color(0xFFA6ABA0)
                        )
                        Text(
                            text = if (totalIncome > 0) "${(100 - (expenseRatio * 100)).toInt()}% Retained" else "Ready",
                            fontSize = 11.sp,
                            color = PaletteElectricLime,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(RoundedCornerShape(3.5.dp))
                            .background(Color(0x33FFFFFF))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = (1f - expenseRatio).coerceIn(0.05f, 1f))
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(3.5.dp))
                                .background(PaletteElectricLime)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 3-Column Tactile Sub-Metrics (Income, Expense, Net Saved)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x1AFFFFFF))
                        .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp))
                        .padding(vertical = 12.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. Income (Mint Mist)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(PaletteMintMist),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = "Income",
                                tint = PaletteForestInk,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Income",
                                fontSize = 10.5.sp,
                                color = Color(0xFFA6ABA0)
                            )
                            Text(
                                text = if (isBalanceHidden) "••••" else "+$currencySymbol${String.format(java.util.Locale.US, "%,.0f", totalIncome)}",
                                fontSize = 13.sp,
                                color = PaletteMintMist,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .height(26.dp)
                            .width(1.dp)
                            .background(Color(0x33FFFFFF))
                    )

                    // 2. Expenses (Coral Pop)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(PaletteCoralPop),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Expense",
                                tint = PaletteForestInk,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Spent",
                                fontSize = 10.5.sp,
                                color = Color(0xFFA6ABA0)
                            )
                            Text(
                                text = if (isBalanceHidden) "••••" else "-$currencySymbol${String.format(java.util.Locale.US, "%,.0f", totalExpense)}",
                                fontSize = 13.sp,
                                color = PaletteCoralPop,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .height(26.dp)
                            .width(1.dp)
                            .background(Color(0x33FFFFFF))
                    )

                    // 3. Net Saved (Lavender)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(PaletteLavender),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Savings,
                                contentDescription = "Savings",
                                tint = PaletteForestInk,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Saved",
                                fontSize = 10.5.sp,
                                color = Color(0xFFA6ABA0)
                            )
                            Text(
                                text = if (isBalanceHidden) "••••" else "$currencySymbol${String.format(java.util.Locale.US, "%,.0f", savings)}",
                                fontSize = 13.sp,
                                color = PaletteLavender,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Dynamic 3D Specular Glare Sheen Overlay
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.10f),
                                Color.Transparent,
                                Color.White.copy(alpha = 0.04f)
                            ),
                            start = Offset(
                                x = (1f + animatedTiltY / 14f) * 150f,
                                y = (1f + animatedTiltX / 14f) * 150f
                            ),
                            end = Offset(x = 800f, y = 800f)
                        )
                    )
            )
        }
    }
}

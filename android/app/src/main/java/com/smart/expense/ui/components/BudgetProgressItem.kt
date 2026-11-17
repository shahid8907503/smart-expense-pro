package com.smart.expense.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smart.expense.ui.theme.*

@Composable
fun BudgetProgressItem(
    category: String,
    monthlyLimit: Double,
    spent: Double,
    modifier: Modifier = Modifier
) {
    val ratio = if (monthlyLimit > 0) (spent / monthlyLimit).toFloat() else 0f
    val progress by animateFloatAsState(
        targetValue = ratio.coerceIn(0f, 1f),
        animationSpec = tween(700),
        label = "budget_progress"
    )

    val remaining = (monthlyLimit - spent).coerceAtLeast(0.0)
    val percentageUsed = (ratio * 100).toInt()

    val status = when {
        ratio >= 1.0f -> "EXCEEDED"
        ratio >= 0.8f -> "80% USED"
        else -> "ON TRACK"
    }

    val statusColor = when (status) {
        "EXCEEDED" -> FintechCoral
        "80% USED" -> FintechAmber
        else -> FintechEmerald
    }

    val catColor = getCategoryColor(category)

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row: Category Icon + Name & Status Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(catColor.copy(alpha = 0.16f))
                            .border(1.dp, catColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(category),
                            contentDescription = category,
                            tint = catColor,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$percentageUsed% of monthly budget",
                            fontSize = 11.sp,
                            color = FintechTextMuted
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.16f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = status,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Animated Linear Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.horizontalGradient(
                                if (ratio >= 1.0f) listOf(FintechCoral, Color(0xFFEF4444))
                                else if (ratio >= 0.8f) listOf(FintechAmber, Color(0xFFF59E0B))
                                else listOf(MaterialTheme.colorScheme.primary, FintechEmerald)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Spending and Remaining labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Spent: ₹${String.format(java.util.Locale.US, "%,.0f", spent)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Left: ₹${String.format(java.util.Locale.US, "%,.0f", remaining)}",
                    fontSize = 12.sp,
                    color = if (remaining > 0) FintechEmerald else FintechCoral,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

package com.smart.expense.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.smart.expense.data.currency.CurrencyManager
import com.smart.expense.data.local.entities.TransactionEntity
import com.smart.expense.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * World-Class Interactive Visual Calendar Expense Ledger
 * Bottom-sheet style with Midnight Navy surfaces and Electric Violet/Cyan accents.
 */
@Composable
fun CalendarExpenseDialog(
    allTransactions: List<TransactionEntity>,
    currencyCode: String = "INR",
    onDismiss: () -> Unit
) {
    val todayCal = remember { Calendar.getInstance() }
    var displayYear by remember { mutableIntStateOf(todayCal.get(Calendar.YEAR)) }
    var displayMonth by remember { mutableIntStateOf(todayCal.get(Calendar.MONTH)) }
    var selectedDay by remember { mutableIntStateOf(todayCal.get(Calendar.DAY_OF_MONTH)) }

    val monthCal = remember(displayYear, displayMonth) {
        Calendar.getInstance().apply {
            set(Calendar.YEAR, displayYear)
            set(Calendar.MONTH, displayMonth)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }

    val daysInMonth = remember(displayYear, displayMonth) {
        monthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    val firstDayOfWeekOffset = remember(displayYear, displayMonth) {
        val dow = monthCal.get(Calendar.DAY_OF_WEEK)
        (dow - Calendar.MONDAY + 7) % 7
    }

    val monthStartMs = remember(displayYear, displayMonth) { monthCal.timeInMillis }
    val monthEndMs = remember(displayYear, displayMonth) {
        Calendar.getInstance().apply {
            timeInMillis = monthStartMs
            set(Calendar.DAY_OF_MONTH, daysInMonth)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
        }.timeInMillis
    }

    val txByDay = remember(allTransactions, displayYear, displayMonth) {
        val map = mutableMapOf<Int, MutableList<TransactionEntity>>()
        allTransactions.filter { it.transactionDate in monthStartMs..monthEndMs }.forEach { tx ->
            val c = Calendar.getInstance().apply { timeInMillis = tx.transactionDate }
            val day = c.get(Calendar.DAY_OF_MONTH)
            map.getOrPut(day) { mutableListOf() }.add(tx)
        }
        map
    }

    val selectedDayTransactions = remember(selectedDay, txByDay) {
        txByDay[selectedDay] ?: emptyList()
    }

    val monthTitle = remember(displayYear, displayMonth) {
        SimpleDateFormat("MMMM yyyy", Locale.US).format(monthCal.time)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() },
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.92f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* prevent dismiss */ },
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Drag Handle
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(42.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(FintechTextMuted.copy(alpha = 0.4f))
                        )
                    }

                    // Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF1E1B4B), Color(0xFF0F172A))
                                )
                            )
                            .padding(horizontal = 20.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(FintechElectricViolet.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = null,
                                        tint = FintechCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Calendar Expense View",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = FintechTextPrimary
                                    )
                                    Text(
                                        text = "Interactive Day-by-Day Cashflow",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = FintechTextSecondary
                                    )
                                }
                            }

                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = FintechTextSecondary)
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 18.dp, vertical = 12.dp)
                    ) {
                        // Month Selector Bar
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = FintechCardBg,
                            border = BorderStroke(1.dp, FintechCardBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 6.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        if (displayMonth == 0) {
                                            displayMonth = 11
                                            displayYear--
                                        } else {
                                            displayMonth--
                                        }
                                        selectedDay = 1
                                    }
                                ) {
                                    Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month", tint = FintechCyan)
                                }

                                Text(
                                    text = monthTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = FintechTextPrimary
                                )

                                IconButton(
                                    onClick = {
                                        if (displayMonth == 11) {
                                            displayMonth = 0
                                            displayYear++
                                        } else {
                                            displayMonth++
                                        }
                                        selectedDay = 1
                                    }
                                ) {
                                    Icon(Icons.Default.ChevronRight, contentDescription = "Next Month", tint = FintechCyan)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Weekday Labels (Mon - Sun)
                        Row(modifier = Modifier.fillMaxWidth()) {
                            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { dayName ->
                                Text(
                                    text = dayName,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = FintechTextMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Calendar Grid (up to 6 weeks)
                        val totalCells = firstDayOfWeekOffset + daysInMonth
                        val numRows = (totalCells + 6) / 7
                        val curr = CurrencyManager.getCurrency(currencyCode)

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            for (row in 0 until numRows) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    for (col in 0 until 7) {
                                        val cellIndex = row * 7 + col
                                        val dayNum = cellIndex - firstDayOfWeekOffset + 1

                                        if (dayNum in 1..daysInMonth) {
                                            val dayTxs = txByDay[dayNum] ?: emptyList()
                                            val dayExp = dayTxs.filter { it.type == "expense" }.sumOf { it.amount }
                                            val dayInc = dayTxs.filter { it.type == "income" }.sumOf { it.amount }
                                            val isSelected = selectedDay == dayNum
                                            val isToday = dayNum == todayCal.get(Calendar.DAY_OF_MONTH) &&
                                                    displayMonth == todayCal.get(Calendar.MONTH) &&
                                                    displayYear == todayCal.get(Calendar.YEAR)

                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(48.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(
                                                        when {
                                                            isSelected -> FintechElectricViolet
                                                            isToday -> FintechCyan.copy(alpha = 0.15f)
                                                            dayTxs.isNotEmpty() -> FintechCardBg
                                                            else -> Color.Transparent
                                                        }
                                                    )
                                                    .border(
                                                        width = if (isSelected) 1.5.dp else if (isToday) 1.dp else 0.5.dp,
                                                        color = if (isSelected) FintechCyan else if (isToday) FintechCyan else FintechCardBorder.copy(alpha = 0.5f),
                                                        shape = RoundedCornerShape(10.dp)
                                                    )
                                                    .clickable { selectedDay = dayNum }
                                                    .padding(2.dp),
                                                contentAlignment = Alignment.TopCenter
                                            ) {
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.SpaceBetween,
                                                    modifier = Modifier.fillMaxSize()
                                                ) {
                                                    Text(
                                                        text = "$dayNum",
                                                        fontSize = 11.sp,
                                                        fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Medium,
                                                        color = if (isSelected) Color.White else if (isToday) FintechCyan else FintechTextPrimary
                                                    )

                                                    if (dayExp > 0) {
                                                        val convertedExp = dayExp * curr.exchangeRateFromInr
                                                        val compact = if (convertedExp >= 1000) "${(convertedExp / 1000).toInt()}k" else "${convertedExp.toInt()}"
                                                        Text(
                                                            text = "-${curr.symbol}$compact",
                                                            fontSize = 8.5.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isSelected) Color(0xFFFCA5A5) else FintechCoral,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Clip
                                                        )
                                                    } else if (dayInc > 0) {
                                                        val convertedInc = dayInc * curr.exchangeRateFromInr
                                                        val compact = if (convertedInc >= 1000) "${(convertedInc / 1000).toInt()}k" else "${convertedInc.toInt()}"
                                                        Text(
                                                            text = "+${curr.symbol}$compact",
                                                            fontSize = 8.5.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isSelected) Color(0xFF6EE7B7) else FintechEmerald,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Clip
                                                        )
                                                    }
                                                }
                                            }
                                        } else {
                                            Spacer(modifier = Modifier.weight(1f).height(48.dp))
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = FintechCardBorder)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Selected Day Transactions Header
                        val selectedDayExp = selectedDayTransactions.filter { it.type == "expense" }.sumOf { it.amount }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$selectedDay ${monthTitle.split(" ").first()} (${selectedDayTransactions.size} entries)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = FintechTextPrimary
                            )
                            if (selectedDayExp > 0) {
                                Text(
                                    text = "Spent: ${CurrencyManager.format(selectedDayExp, currencyCode)}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = FintechCoral
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (selectedDayTransactions.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No transactions on $selectedDay ${monthTitle.split(" ").first()}.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = FintechTextMuted
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(selectedDayTransactions) { tx ->
                                    TransactionRowItem(
                                        transaction = tx,
                                        currencyCode = currencyCode
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

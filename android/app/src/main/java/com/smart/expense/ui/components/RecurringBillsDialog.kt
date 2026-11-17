package com.smart.expense.ui.components

import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.smart.expense.data.currency.CurrencyManager
import com.smart.expense.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

data class RecurringBill(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val amount: Double,
    val category: String = "Bills & Utilities",
    val cycle: String = "Monthly", // Monthly, Weekly, Yearly
    val dueDay: Int = 5,
    val lastPaidMonthKey: String? = null
)

/**
 * World-Class Recurring Bills & Subscriptions Manager
 * Bottom-sheet style with Midnight Navy surfaces and Electric Violet/Cyan accents.
 */
@Composable
fun RecurringBillsDialog(
    currencyCode: String = "INR",
    onPayAndLog: (RecurringBill) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("smart_expense_pro_recurring", Context.MODE_PRIVATE) }
    val gson = remember { Gson() }
    val currentMonthKey = remember { SimpleDateFormat("yyyy-MM", Locale.US).format(Date()) }
    val currentDay = remember { Calendar.getInstance().get(Calendar.DAY_OF_MONTH) }

    fun loadBills(): List<RecurringBill> {
        val json = prefs.getString("bills_list", null) ?: return emptyList()
        val type = object : TypeToken<List<RecurringBill>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveBills(list: List<RecurringBill>) {
        prefs.edit().putString("bills_list", gson.toJson(list)).apply()
    }

    var bills by remember { mutableStateOf(loadBills()) }
    var isAddingNew by remember { mutableStateOf(false) }

    var titleInput by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }
    var dueDayInput by remember { mutableStateOf("5") }
    var selectedCategory by remember { mutableStateOf("Bills & Utilities") }
    var selectedCycle by remember { mutableStateOf("Monthly") }

    val totalMonthlyCommitment = bills.sumOf {
        when (it.cycle) {
            "Weekly" -> it.amount * 4
            "Yearly" -> it.amount / 12.0
            else -> it.amount
        }
    }
    val pendingCount = bills.count { it.lastPaidMonthKey != currentMonthKey }

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
                                        imageVector = Icons.Default.Repeat,
                                        contentDescription = null,
                                        tint = FintechCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Recurring Bills & Subscriptions",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = FintechTextPrimary
                                    )
                                    Text(
                                        text = "Rent, Wi-Fi, EMIs & Streaming",
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
                            .padding(horizontal = 20.dp, vertical = 14.dp)
                    ) {
                        // Monthly Commitment Summary Banner
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = FintechCardBg,
                                border = BorderStroke(1.dp, FintechCardBorder),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("Monthly Commitment", style = MaterialTheme.typography.labelSmall, color = FintechTextSecondary)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = CurrencyManager.format(totalMonthlyCommitment, currencyCode),
                                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp),
                                        fontWeight = FontWeight.ExtraBold,
                                        color = FintechCyan
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = FintechCardBg,
                                border = BorderStroke(1.dp, if (pendingCount > 0) FintechCoralLight.copy(alpha = 0.3f) else FintechEmeraldLight.copy(alpha = 0.3f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "Due This Month",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (pendingCount > 0) FintechCoralLight else FintechEmeraldLight,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (pendingCount > 0) "$pendingCount Pending" else "All Paid ✓",
                                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp),
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (pendingCount > 0) FintechCoral else FintechEmerald
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        if (isAddingNew) {
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = FintechCardBg,
                                border = BorderStroke(1.dp, FintechBorderGlow),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("New Recurring Bill", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = FintechTextPrimary)
                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = titleInput,
                                        onValueChange = { titleInput = it },
                                        label = { Text("Bill Name (e.g. Netflix, Wi-Fi, Rent)", color = FintechTextSecondary) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = FintechCyan,
                                            unfocusedBorderColor = FintechCardBorder,
                                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = amountInput,
                                            onValueChange = { amountInput = it },
                                            label = { Text("Amount (${CurrencyManager.getCurrency(currencyCode).symbol})", color = FintechTextSecondary) },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            singleLine = true,
                                            modifier = Modifier.weight(1.3f),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = FintechCyan,
                                                unfocusedBorderColor = FintechCardBorder,
                                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                                            )
                                        )
                                        OutlinedTextField(
                                            value = dueDayInput,
                                            onValueChange = { if (it.length <= 2) dueDayInput = it },
                                            label = { Text("Due Day (1-28)", color = FintechTextSecondary) },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            singleLine = true,
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = FintechCyan,
                                                unfocusedBorderColor = FintechCardBorder,
                                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text("Billing Cycle", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = FintechTextPrimary)
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        listOf("Monthly", "Weekly", "Yearly").forEach { cycle ->
                                            FilterChip(
                                                selected = selectedCycle == cycle,
                                                onClick = { selectedCycle = cycle },
                                                label = { Text(cycle, fontSize = 12.sp) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = FintechElectricViolet,
                                                    selectedLabelColor = Color.White,
                                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                                ),
                                                border = BorderStroke(1.dp, if (selectedCycle == cycle) FintechElectricViolet else FintechCardBorder)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text("Category", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = FintechTextPrimary)
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        listOf("Bills & Utilities", "Rent", "Entertainment", "Other").forEach { cat ->
                                            FilterChip(
                                                selected = selectedCategory == cat,
                                                onClick = { selectedCategory = cat },
                                                label = { Text(cat.split(" ").first(), fontSize = 11.sp) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = FintechCyan,
                                                    selectedLabelColor = Color.White,
                                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                                ),
                                                border = BorderStroke(1.dp, if (selectedCategory == cat) FintechCyan else FintechCardBorder)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { isAddingNew = false },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp),
                                            border = BorderStroke(1.dp, FintechCardBorder),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = FintechTextSecondary)
                                        ) {
                                            Text("Cancel")
                                        }
                                        Button(
                                            onClick = {
                                                val amt = amountInput.toDoubleOrNull() ?: 0.0
                                                val day = (dueDayInput.toIntOrNull() ?: 5).coerceIn(1, 28)
                                                if (titleInput.isNotBlank() && amt > 0) {
                                                    val newBill = RecurringBill(
                                                        title = titleInput.trim(),
                                                        amount = amt,
                                                        category = selectedCategory,
                                                        cycle = selectedCycle,
                                                        dueDay = day
                                                    )
                                                    val updated = bills + newBill
                                                    bills = updated
                                                    saveBills(updated)
                                                    titleInput = ""
                                                    amountInput = ""
                                                    isAddingNew = false
                                                    Toast.makeText(context, "Added recurring bill!", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = FintechElectricViolet),
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text("Save Bill", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Scheduled Bills (${bills.size})",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = FintechTextPrimary
                                )
                                TextButton(onClick = { isAddingNew = true }) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = FintechCyan)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Bill", fontSize = 12.5.sp, color = FintechCyan, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (bills.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "No recurring bills added yet.\nTrack monthly Rent, Wi-Fi, Netflix, or EMIs.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = FintechTextMuted,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(14.dp))
                                        OutlinedButton(
                                            onClick = {
                                                val samples = listOf(
                                                    RecurringBill(title = "Home / Hostel Rent", amount = 8500.0, category = "Rent", cycle = "Monthly", dueDay = 5),
                                                    RecurringBill(title = "Jio Fiber Wi-Fi", amount = 699.0, category = "Bills & Utilities", cycle = "Monthly", dueDay = 12),
                                                    RecurringBill(title = "Netflix Subscription", amount = 199.0, category = "Entertainment", cycle = "Monthly", dueDay = 20)
                                                )
                                                bills = samples
                                                saveBills(samples)
                                            },
                                            shape = RoundedCornerShape(12.dp),
                                            border = BorderStroke(1.dp, FintechBorderGlow),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = FintechCyan)
                                        ) {
                                            Text("Load Sample Bills", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(bills) { bill ->
                                        val isPaid = bill.lastPaidMonthKey == currentMonthKey
                                        val isOverdue = !isPaid && currentDay > bill.dueDay

                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(16.dp),
                                            color = FintechCardBg,
                                            border = BorderStroke(1.dp, FintechCardBorder)
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(14.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(bill.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = FintechTextPrimary)
                                                    Text(
                                                        text = "${bill.cycle} • Due Day ${bill.dueDay} • ${bill.category}",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = FintechTextSecondary
                                                    )
                                                    Spacer(modifier = Modifier.height(6.dp))
                                                    Surface(
                                                        color = when {
                                                            isPaid -> FintechEmeraldContainer
                                                            isOverdue -> FintechCoralContainer
                                                            else -> FintechCardSurface
                                                        },
                                                        shape = RoundedCornerShape(6.dp)
                                                    ) {
                                                        Text(
                                                            text = when {
                                                                isPaid -> "PAID THIS MONTH"
                                                                isOverdue -> "OVERDUE"
                                                                else -> "UPCOMING"
                                                            },
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = when {
                                                                isPaid -> FintechEmeraldLight
                                                                isOverdue -> FintechCoralLight
                                                                else -> FintechCyan
                                                            },
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                        )
                                                    }
                                                }

                                                Column(horizontalAlignment = Alignment.End) {
                                                    Text(
                                                        text = CurrencyManager.format(bill.amount, currencyCode),
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = FintechTextPrimary
                                                    )
                                                    Spacer(modifier = Modifier.height(6.dp))
                                                    if (!isPaid) {
                                                        Button(
                                                            onClick = {
                                                                val updated = bills.map {
                                                                    if (it.id == bill.id) it.copy(lastPaidMonthKey = currentMonthKey) else it
                                                                }
                                                                bills = updated
                                                                saveBills(updated)
                                                                onPayAndLog(bill)
                                                                Toast.makeText(context, "Paid & logged ${bill.title}!", Toast.LENGTH_SHORT).show()
                                                            },
                                                            colors = ButtonDefaults.buttonColors(containerColor = FintechElectricViolet),
                                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                                            modifier = Modifier.height(32.dp),
                                                            shape = RoundedCornerShape(8.dp)
                                                        ) {
                                                            Text("Pay & Log", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                                        }
                                                    } else {
                                                        IconButton(
                                                            onClick = {
                                                                val updated = bills.filter { it.id != bill.id }
                                                                bills = updated
                                                                saveBills(updated)
                                                            },
                                                            modifier = Modifier.size(28.dp)
                                                        ) {
                                                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = FintechCoral, modifier = Modifier.size(18.dp))
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

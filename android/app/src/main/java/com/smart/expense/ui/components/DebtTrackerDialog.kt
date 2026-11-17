package com.smart.expense.ui.components

import android.content.Context
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.smart.expense.data.currency.CurrencyManager
import com.smart.expense.ui.theme.*
import java.util.*

data class DebtRecord(
    val id: String = UUID.randomUUID().toString(),
    val personName: String,
    val amount: Double,
    val isOwedToMe: Boolean, // true = Lent (They owe me), false = Borrowed (I owe them)
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isSettled: Boolean = false
)

/**
 * World-Class Debt & IOU Manager
 * Bottom-sheet style with Midnight Navy surfaces and Electric Violet/Cyan accents.
 */
@Composable
fun DebtTrackerDialog(
    currencyCode: String = "INR",
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("smart_expense_debts", Context.MODE_PRIVATE) }
    val gson = remember { Gson() }

    fun loadDebts(): List<DebtRecord> {
        val json = prefs.getString("debts_list", null) ?: return emptyList()
        val type = object : TypeToken<List<DebtRecord>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveDebts(list: List<DebtRecord>) {
        prefs.edit().putString("debts_list", gson.toJson(list)).apply()
    }

    var debts by remember { mutableStateOf(loadDebts()) }
    var isAddingNew by remember { mutableStateOf(false) }

    var personNameInput by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }
    var isOwedToMeInput by remember { mutableStateOf(true) }
    var noteInput by remember { mutableStateOf("") }

    val activeDebts = debts.filter { !it.isSettled }
    val totalOwedToMe = activeDebts.filter { it.isOwedToMe }.sumOf { it.amount }
    val totalIOwe = activeDebts.filter { !it.isOwedToMe }.sumOf { it.amount }

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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(FintechElectricViolet.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Payments,
                                        contentDescription = null,
                                        tint = FintechCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Debt & IOU Tracker",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = FintechTextPrimary
                                    )
                                    Text(
                                        text = "Manage Lent & Borrowed Funds",
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
                        // Summary Row: You are owed vs You owe
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                color = FintechCardBg,
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, FintechEmeraldLight.copy(alpha = 0.3f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("They Owe Me", style = MaterialTheme.typography.labelSmall, color = FintechEmeraldLight, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = CurrencyManager.format(totalOwedToMe, currencyCode),
                                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp),
                                        fontWeight = FontWeight.ExtraBold,
                                        color = FintechEmerald
                                    )
                                }
                            }

                            Surface(
                                color = FintechCardBg,
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, FintechCoralLight.copy(alpha = 0.3f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text("I Owe Them", style = MaterialTheme.typography.labelSmall, color = FintechCoralLight, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = CurrencyManager.format(totalIOwe, currencyCode),
                                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp),
                                        fontWeight = FontWeight.ExtraBold,
                                        color = FintechCoral
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (isAddingNew) {
                            // Add New Form
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = FintechCardBg,
                                border = BorderStroke(1.dp, FintechBorderGlow),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("New IOU Record", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = FintechTextPrimary)
                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = personNameInput,
                                        onValueChange = { personNameInput = it },
                                        label = { Text("Person Name", color = FintechTextSecondary) },
                                        placeholder = { Text("e.g. Rahul, Sneha", color = FintechTextMuted) },
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

                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = amountInput,
                                        onValueChange = { amountInput = it },
                                        label = { Text("Amount (${CurrencyManager.getCurrency(currencyCode).symbol})", color = FintechTextSecondary) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        FilterChip(
                                            selected = isOwedToMeInput,
                                            onClick = { isOwedToMeInput = true },
                                            label = { Text("I Lent Money", fontSize = 12.sp) },
                                            modifier = Modifier.weight(1f),
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = FintechEmerald,
                                                selectedLabelColor = Color.White,
                                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                            ),
                                            border = BorderStroke(1.dp, if (isOwedToMeInput) FintechEmerald else FintechCardBorder)
                                        )
                                        FilterChip(
                                            selected = !isOwedToMeInput,
                                            onClick = { isOwedToMeInput = false },
                                            label = { Text("I Borrowed", fontSize = 12.sp) },
                                            modifier = Modifier.weight(1f),
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = FintechCoral,
                                                selectedLabelColor = Color.White,
                                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                            ),
                                            border = BorderStroke(1.dp, if (!isOwedToMeInput) FintechCoral else FintechCardBorder)
                                        )
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
                                                if (personNameInput.isNotBlank() && amt > 0) {
                                                    val newRecord = DebtRecord(
                                                        personName = personNameInput.trim(),
                                                        amount = amt,
                                                        isOwedToMe = isOwedToMeInput,
                                                        note = noteInput.trim()
                                                    )
                                                    val updated = debts + newRecord
                                                    debts = updated
                                                    saveDebts(updated)
                                                    personNameInput = ""
                                                    amountInput = ""
                                                    isAddingNew = false
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = FintechElectricViolet),
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text("Save Record", fontWeight = FontWeight.Bold)
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
                                    "Active Records (${activeDebts.size})",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = FintechTextPrimary
                                )
                                TextButton(onClick = { isAddingNew = true }) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = FintechCyan)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Record", fontSize = 12.5.sp, color = FintechCyan, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (activeDebts.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = FintechTextMuted,
                                            modifier = Modifier.size(44.dp)
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "All debts settled!",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = FintechTextSecondary,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Tap '+ Add Record' to track money lent or borrowed.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = FintechTextMuted,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(activeDebts) { record ->
                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(14.dp),
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
                                                Column {
                                                    Text(record.personName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = FintechTextPrimary)
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = if (record.isOwedToMe) "Owes you • Lent" else "You owe • Borrowed",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = if (record.isOwedToMe) FintechEmerald else FintechCoral
                                                    )
                                                }

                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = CurrencyManager.format(record.amount, currencyCode),
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = FontWeight.ExtraBold,
                                                        color = if (record.isOwedToMe) FintechEmerald else FintechCoral
                                                    )
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    IconButton(
                                                        onClick = {
                                                            val updated = debts.map {
                                                                if (it.id == record.id) it.copy(isSettled = true) else it
                                                            }
                                                            debts = updated
                                                            saveDebts(updated)
                                                        },
                                                        modifier = Modifier.size(32.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.CheckCircle,
                                                            contentDescription = "Settle",
                                                            tint = FintechEmerald,
                                                            modifier = Modifier.size(22.dp)
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
                }
            }
        }
    }
}

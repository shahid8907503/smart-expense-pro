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
import java.util.*

data class SavingsGoal(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val targetAmount: Double,
    val savedAmount: Double = 0.0,
    val targetNote: String = "Target 2026"
)

/**
 * World-Class Savings Goals ("Piggy Bank")
 * Bottom-sheet style with Midnight Navy surfaces, Electric Violet progress, and Cyan accents.
 */
@Composable
fun SavingsGoalsDialog(
    currencyCode: String = "INR",
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("smart_expense_pro_goals", Context.MODE_PRIVATE) }
    val gson = remember { Gson() }

    fun loadGoals(): List<SavingsGoal> {
        val json = prefs.getString("goals_list", null) ?: return emptyList()
        val type = object : TypeToken<List<SavingsGoal>>() {}.type
        return try {
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveGoals(list: List<SavingsGoal>) {
        prefs.edit().putString("goals_list", gson.toJson(list)).apply()
    }

    var goals by remember { mutableStateOf(loadGoals()) }
    var isAddingNew by remember { mutableStateOf(false) }

    var titleInput by remember { mutableStateOf("") }
    var targetInput by remember { mutableStateOf("") }
    var initialSavedInput by remember { mutableStateOf("0") }
    var noteInput by remember { mutableStateOf("Dec 2026") }

    var depositGoal by remember { mutableStateOf<SavingsGoal?>(null) }
    var customDepositText by remember { mutableStateOf("500") }

    val totalSaved = goals.sumOf { it.savedAmount }
    val totalTarget = goals.sumOf { it.targetAmount }
    val overallPct = if (totalTarget > 0) ((totalSaved / totalTarget) * 100).toInt().coerceIn(0, 100) else 0

    // Deposit popup for selected goal
    if (depositGoal != null) {
        AlertDialog(
            onDismissRequest = { depositGoal = null },
            containerColor = FintechCardBg,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    "Deposit to ${depositGoal!!.title}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = FintechTextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Current: ${CurrencyManager.format(depositGoal!!.savedAmount, currencyCode)} / ${CurrencyManager.format(depositGoal!!.targetAmount, currencyCode)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = FintechTextSecondary
                    )
                    OutlinedTextField(
                        value = customDepositText,
                        onValueChange = { customDepositText = it },
                        label = { Text("Deposit Amount (${CurrencyManager.getCurrency(currencyCode).symbol})", color = FintechTextSecondary) },
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
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("500", "1000", "2000", "5000").forEach { quick ->
                            FilterChip(
                                selected = customDepositText == quick,
                                onClick = { customDepositText = quick },
                                label = { Text("+$quick", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = FintechElectricViolet,
                                    selectedLabelColor = Color.White,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                border = BorderStroke(1.dp, if (customDepositText == quick) FintechElectricViolet else FintechCardBorder)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = customDepositText.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            val updated = goals.map {
                                if (it.id == depositGoal!!.id) it.copy(savedAmount = it.savedAmount + amt) else it
                            }
                            goals = updated
                            saveGoals(updated)
                            Toast.makeText(context, "Added ${CurrencyManager.format(amt, currencyCode)} to ${depositGoal!!.title}!", Toast.LENGTH_SHORT).show()
                        }
                        depositGoal = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FintechElectricViolet),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Save Deposit", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { depositGoal = null }) {
                    Text("Cancel", color = FintechTextSecondary)
                }
            }
        )
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
                                        imageVector = Icons.Default.Savings,
                                        contentDescription = null,
                                        tint = FintechEmerald,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Savings Goals (Piggy Bank)",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = FintechTextPrimary
                                    )
                                    Text(
                                        text = "Build Wealth & Track Targets",
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
                        // Summary Banner
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = FintechCardBg,
                            border = BorderStroke(1.dp, FintechBorderGlow),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Total Saved Across Goals", style = MaterialTheme.typography.labelSmall, color = FintechTextSecondary)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = CurrencyManager.format(totalSaved, currencyCode),
                                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                                        fontWeight = FontWeight.ExtraBold,
                                        color = FintechEmerald
                                    )
                                }
                                Surface(
                                    color = FintechElectricViolet,
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text(
                                        text = "$overallPct% Overall",
                                        color = Color.White,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
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
                                    Text("Create New Savings Goal", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = FintechTextPrimary)
                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = titleInput,
                                        onValueChange = { titleInput = it },
                                        label = { Text("Goal Name (e.g. New Laptop, Emergency Fund)", color = FintechTextSecondary) },
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
                                            value = targetInput,
                                            onValueChange = { targetInput = it },
                                            label = { Text("Target (${CurrencyManager.getCurrency(currencyCode).symbol})", color = FintechTextSecondary) },
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
                                        OutlinedTextField(
                                            value = initialSavedInput,
                                            onValueChange = { initialSavedInput = it },
                                            label = { Text("Already Saved", color = FintechTextSecondary) },
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

                                    OutlinedTextField(
                                        value = noteInput,
                                        onValueChange = { noteInput = it },
                                        label = { Text("Target Timeline (e.g. Dec 2026)", color = FintechTextSecondary) },
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
                                                val target = targetInput.toDoubleOrNull() ?: 0.0
                                                val initial = initialSavedInput.toDoubleOrNull() ?: 0.0
                                                if (titleInput.isNotBlank() && target > 0) {
                                                    val newGoal = SavingsGoal(
                                                        title = titleInput.trim(),
                                                        targetAmount = target,
                                                        savedAmount = initial,
                                                        targetNote = noteInput.trim().ifEmpty { "2026 Goal" }
                                                    )
                                                    val updated = goals + newGoal
                                                    goals = updated
                                                    saveGoals(updated)
                                                    titleInput = ""
                                                    targetInput = ""
                                                    initialSavedInput = "0"
                                                    isAddingNew = false
                                                    Toast.makeText(context, "Goal created!", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = FintechElectricViolet),
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text("Save Goal", fontWeight = FontWeight.Bold)
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
                                    "Your Goals (${goals.size})",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = FintechTextPrimary
                                )
                                TextButton(onClick = { isAddingNew = true }) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = FintechCyan)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("New Goal", fontSize = 12.5.sp, color = FintechCyan, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (goals.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "No savings goals yet.\nCreate goals for gadgets, trips, or emergency fund.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = FintechTextMuted,
                                            textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(14.dp))
                                        OutlinedButton(
                                            onClick = {
                                                val samples = listOf(
                                                    SavingsGoal(title = "Emergency Fund", targetAmount = 50000.0, savedAmount = 18500.0, targetNote = "Safety Net"),
                                                    SavingsGoal(title = "New Laptop Upgrade", targetAmount = 65000.0, savedAmount = 32000.0, targetNote = "Dec 2026"),
                                                    SavingsGoal(title = "Goa Trip with Friends", targetAmount = 15000.0, savedAmount = 12000.0, targetNote = "Next Month")
                                                )
                                                goals = samples
                                                saveGoals(samples)
                                            },
                                            shape = RoundedCornerShape(12.dp),
                                            border = BorderStroke(1.dp, FintechBorderGlow),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = FintechCyan)
                                        ) {
                                            Text("Load Sample Goals", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(goals) { goal ->
                                        val progress = if (goal.targetAmount > 0) (goal.savedAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f) else 0f
                                        val pct = (progress * 100).toInt()
                                        val isCompleted = goal.savedAmount >= goal.targetAmount

                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(16.dp),
                                            color = FintechCardBg,
                                            border = BorderStroke(1.dp, if (isCompleted) FintechEmeraldLight.copy(alpha = 0.4f) else FintechCardBorder)
                                        ) {
                                            Column(modifier = Modifier.padding(14.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(goal.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = FintechTextPrimary)
                                                        Text(
                                                            text = "${goal.targetNote} • $pct% Completed",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = if (isCompleted) FintechEmerald else FintechTextSecondary
                                                        )
                                                    }

                                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                        if (!isCompleted) {
                                                            Button(
                                                                onClick = {
                                                                    customDepositText = "500"
                                                                    depositGoal = goal
                                                                },
                                                                colors = ButtonDefaults.buttonColors(containerColor = FintechElectricViolet),
                                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                                                modifier = Modifier.height(32.dp),
                                                                shape = RoundedCornerShape(8.dp)
                                                            ) {
                                                                Text("+ Deposit", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                                            }
                                                        } else {
                                                            Surface(
                                                                color = FintechEmeraldContainer,
                                                                shape = RoundedCornerShape(8.dp)
                                                            ) {
                                                                Text(
                                                                    text = "ACHIEVED ✓",
                                                                    color = FintechEmeraldLight,
                                                                    fontSize = 10.sp,
                                                                    fontWeight = FontWeight.Bold,
                                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                                )
                                                            }
                                                        }
                                                        IconButton(
                                                            onClick = {
                                                                val updated = goals.filter { it.id != goal.id }
                                                                goals = updated
                                                                saveGoals(updated)
                                                            },
                                                            modifier = Modifier.size(28.dp)
                                                        ) {
                                                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = FintechCoral, modifier = Modifier.size(18.dp))
                                                        }
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(10.dp))

                                                LinearProgressIndicator(
                                                    progress = { progress },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(8.dp)
                                                        .clip(RoundedCornerShape(4.dp)),
                                                    color = if (isCompleted) FintechEmerald else FintechCyan,
                                                    trackColor = FintechSecondaryBg
                                                )

                                                Spacer(modifier = Modifier.height(8.dp))

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = "Saved: ${CurrencyManager.format(goal.savedAmount, currencyCode)}",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = FintechCyan
                                                    )
                                                    Text(
                                                        text = "Target: ${CurrencyManager.format(goal.targetAmount, currencyCode)}",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = FintechTextSecondary
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

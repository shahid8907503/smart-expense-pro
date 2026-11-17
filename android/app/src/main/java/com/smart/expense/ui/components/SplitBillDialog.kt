package com.smart.expense.ui.components

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import kotlin.math.abs
import java.util.Locale

// ─── Data Models ────────────────────────────────────────────────────────────

data class SplitPerson(
    val id: String = java.util.UUID.randomUUID().toString(),
    var name: String = "",
    var customAmount: Double? = null,
    var paid: Boolean = false
)

data class SplitBillRecord(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val totalAmount: Double,
    val people: List<SplitPerson>,
    val splitMode: String,   // "equal" | "custom"
    val category: String,
    val createdAt: Long = System.currentTimeMillis()
)

// ─── Persistence ─────────────────────────────────────────────────────────────

private fun getSplitBillPrefs(context: Context): SharedPreferences =
    context.getSharedPreferences("split_bill_records", Context.MODE_PRIVATE)

private fun loadSplitRecords(context: Context): MutableList<SplitBillRecord> {
    val json = getSplitBillPrefs(context).getString("records", null) ?: return mutableListOf()
    val type = object : TypeToken<MutableList<SplitBillRecord>>() {}.type
    return try { Gson().fromJson(json, type) } catch (e: Exception) { mutableListOf() }
}

private fun saveSplitRecords(context: Context, records: List<SplitBillRecord>) {
    getSplitBillPrefs(context).edit()
        .putString("records", Gson().toJson(records))
        .apply()
}

// ─── Main Dialog ─────────────────────────────────────────────────────────────

private val CATEGORIES = listOf(
    "Food & Dining", "Travel", "Entertainment", "Shopping",
    "Accommodation", "Utilities", "Others"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitBillDialog(
    currencyCode: String = "INR",
    onAddToDebt: (personName: String, amount: Double) -> Unit = { _, _ -> },
    onLogMyShare: (amount: Double, title: String, category: String) -> Unit = { _, _, _ -> },
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val currency = CurrencyManager.getCurrency(currencyCode)

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("New Split", "History")

    var title by remember { mutableStateOf("") }
    var totalAmountStr by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(CATEGORIES[0]) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var splitMode by remember { mutableStateOf("equal") }
    var people by remember {
        mutableStateOf(
            listOf(
                SplitPerson(name = "Me"),
                SplitPerson(name = "")
            )
        )
    }
    var newPersonName by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }
    var successMsg by remember { mutableStateOf("") }

    var history by remember { mutableStateOf(loadSplitRecords(context)) }

    val totalAmount = totalAmountStr.toDoubleOrNull() ?: 0.0
    val perPerson = if (people.isNotEmpty() && totalAmount > 0) totalAmount / people.size else 0.0

    fun computeShare(person: SplitPerson): Double =
        if (splitMode == "equal") perPerson else (person.customAmount ?: perPerson)

    val customTotal = people.sumOf { it.customAmount ?: 0.0 }
    val customDiff = if (splitMode == "custom") totalAmount - customTotal else 0.0

    fun doSplitAndLog() {
        if (title.isBlank()) { errorMsg = "Enter a bill title"; return }
        if (totalAmount <= 0) { errorMsg = "Enter a valid total amount"; return }
        if (people.size < 2) { errorMsg = "Add at least 2 people to split"; return }
        if (splitMode == "custom" && abs(customDiff) > 0.5) {
            errorMsg = "Custom amounts don't add up to total (diff: ${currency.symbol}${String.format(Locale.US, "%.2f", abs(customDiff))})"
            return
        }

        val record = SplitBillRecord(
            title = title,
            totalAmount = totalAmount,
            people = people.toList(),
            splitMode = splitMode,
            category = selectedCategory
        )
        val updated = loadSplitRecords(context).also { it.add(0, record) }
        saveSplitRecords(context, updated)
        history = updated

        val myShare = computeShare(people.first())
        onLogMyShare(myShare, title, selectedCategory)

        people.drop(1).forEach { person ->
            if (person.name.isNotBlank()) {
                val share = computeShare(person)
                onAddToDebt(person.name, share)
            }
        }

        successMsg = "Split done! Your share: ${currency.symbol}${String.format(Locale.US, "%.2f", myShare)} logged. ${people.size - 1} IOU(s) tracked."
        showSuccess = true
        errorMsg = ""

        title = ""
        totalAmountStr = ""
        people = listOf(SplitPerson(name = "Me"), SplitPerson(name = ""))
        splitMode = "equal"
        selectedCategory = CATEGORIES[0]
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
                                        imageVector = Icons.Default.GroupAdd,
                                        contentDescription = null,
                                        tint = FintechVioletLight,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Split Bill with Friends",
                                        color = FintechTextPrimary,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Fair Share • Auto IOU Logging",
                                        color = FintechTextSecondary,
                                        fontSize = 11.5.sp
                                    )
                                }
                            }
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = FintechTextSecondary)
                            }
                        }
                    }

                    // Tabs
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = FintechSecondaryBg,
                        contentColor = FintechCyan
                    ) {
                        tabs.forEachIndexed { index, label ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index; errorMsg = ""; showSuccess = false },
                                text = {
                                    Text(
                                        text = label,
                                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selectedTab == index) FintechCyan else FintechTextSecondary
                                    )
                                }
                            )
                        }
                    }

                    // Tab Content
                    when (selectedTab) {
                        0 -> NewSplitTab(
                            title = title,
                            onTitleChange = { title = it; errorMsg = "" },
                            totalAmountStr = totalAmountStr,
                            onTotalChange = { totalAmountStr = it; errorMsg = "" },
                            currency = currency.symbol,
                            selectedCategory = selectedCategory,
                            onCategoryChange = { selectedCategory = it },
                            categoryExpanded = categoryExpanded,
                            onCategoryExpandChange = { categoryExpanded = it },
                            splitMode = splitMode,
                            onSplitModeChange = { splitMode = it },
                            people = people,
                            onPeopleChange = { people = it },
                            newPersonName = newPersonName,
                            onNewPersonNameChange = { newPersonName = it },
                            perPerson = perPerson,
                            customDiff = customDiff,
                            errorMsg = errorMsg,
                            showSuccess = showSuccess,
                            successMsg = successMsg,
                            onDismissSuccess = { showSuccess = false },
                            onSplit = { doSplitAndLog() }
                        )
                        1 -> HistoryTab(
                            history = history,
                            currency = currency.symbol,
                            onClear = {
                                saveSplitRecords(context, emptyList())
                                history = mutableListOf()
                            }
                        )
                    }
                }
            }
        }
    }
}

// ─── New Split Tab ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewSplitTab(
    title: String, onTitleChange: (String) -> Unit,
    totalAmountStr: String, onTotalChange: (String) -> Unit,
    currency: String,
    selectedCategory: String, onCategoryChange: (String) -> Unit,
    categoryExpanded: Boolean, onCategoryExpandChange: (Boolean) -> Unit,
    splitMode: String, onSplitModeChange: (String) -> Unit,
    people: List<SplitPerson>, onPeopleChange: (List<SplitPerson>) -> Unit,
    newPersonName: String, onNewPersonNameChange: (String) -> Unit,
    perPerson: Double,
    customDiff: Double,
    errorMsg: String,
    showSuccess: Boolean, successMsg: String,
    onDismissSuccess: () -> Unit,
    onSplit: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        if (showSuccess) {
            item {
                Surface(
                    color = FintechEmeraldContainer,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, FintechEmeraldLight.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(successMsg, color = FintechEmeraldLight, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        IconButton(onClick = onDismissSuccess, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp), tint = FintechEmeraldLight)
                        }
                    }
                }
            }
        }

        // Bill Title
        item {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Bill Description", color = FintechTextSecondary) },
                placeholder = { Text("e.g. Dinner with Friends", color = FintechTextMuted) },
                leadingIcon = { Icon(Icons.Default.Receipt, contentDescription = null, tint = FintechCyan) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FintechCyan,
                    unfocusedBorderColor = FintechCardBorder,
                    focusedTextColor = FintechTextPrimary,
                    unfocusedTextColor = FintechTextPrimary,
                    focusedContainerColor = FintechCardBg,
                    unfocusedContainerColor = FintechCardBg
                ),
                singleLine = true
            )
        }

        // Total Amount + Category row
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = totalAmountStr,
                    onValueChange = onTotalChange,
                    label = { Text("Total Bill", color = FintechTextSecondary) },
                    leadingIcon = { Text(currency, color = FintechCyan, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(start = 12.dp)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1.1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FintechCyan,
                        unfocusedBorderColor = FintechCardBorder,
                        focusedTextColor = FintechTextPrimary,
                        unfocusedTextColor = FintechTextPrimary,
                        focusedContainerColor = FintechCardBg,
                        unfocusedContainerColor = FintechCardBg
                    ),
                    singleLine = true
                )

                // Category Dropdown
                Box(modifier = Modifier.weight(1f)) {
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = onCategoryExpandChange
                    ) {
                        OutlinedTextField(
                            value = selectedCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category", color = FintechTextSecondary) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = FintechCyan,
                                unfocusedBorderColor = FintechCardBorder,
                                focusedTextColor = FintechTextPrimary,
                                unfocusedTextColor = FintechTextPrimary,
                                focusedContainerColor = FintechCardBg,
                                unfocusedContainerColor = FintechCardBg
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { onCategoryExpandChange(false) },
                            modifier = Modifier.background(FintechCardBg)
                        ) {
                            CATEGORIES.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat, color = FintechTextPrimary) },
                                    onClick = { onCategoryChange(cat); onCategoryExpandChange(false) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Split Mode Toggle
        item {
            Text("Split Mode", fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp, color = FintechTextPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("equal" to "Equal Split", "custom" to "Custom Amounts").forEach { (mode, label) ->
                    FilterChip(
                        selected = splitMode == mode,
                        onClick = { onSplitModeChange(mode) },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FintechElectricViolet,
                            selectedLabelColor = Color.White,
                            containerColor = FintechCardBg,
                            labelColor = FintechTextSecondary
                        ),
                        border = BorderStroke(1.dp, if (splitMode == mode) FintechElectricViolet else FintechCardBorder)
                    )
                }
            }
        }

        // Per-person preview (Equal mode)
        if (splitMode == "equal" && perPerson > 0) {
            item {
                Surface(
                    color = FintechCardBg,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, FintechBorderGlow)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Each person pays:", color = FintechTextSecondary, fontSize = 13.sp)
                        Text(
                            text = "$currency${String.format(Locale.US, "%,.2f", perPerson)}",
                            fontWeight = FontWeight.ExtraBold,
                            color = FintechCyan,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        // Custom total warning
        if (splitMode == "custom" && (totalAmountStr.toDoubleOrNull() ?: 0.0) > 0) {
            item {
                val diff = customDiff
                val color = if (abs(diff) < 0.5) FintechEmerald else FintechCoral
                val msg = when {
                    abs(diff) < 0.5 -> "✓ Amounts balance perfectly"
                    diff > 0 -> "⚠ Unassigned: $currency${String.format(Locale.US, "%.2f", diff)}"
                    else -> "⚠ Over-assigned by: $currency${String.format(Locale.US, "%.2f", -diff)}"
                }
                Text(msg, color = color, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }

        // People list header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Members (${people.size})", fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp, color = FintechTextPrimary)
                Text(
                    text = "First member is You",
                    fontSize = 11.5.sp,
                    color = FintechTextMuted
                )
            }
        }

        // People rows
        itemsIndexed(people) { index, person ->
            PersonRow(
                person = person,
                index = index,
                isMe = index == 0,
                splitMode = splitMode,
                currency = currency,
                perPerson = perPerson,
                onNameChange = { newName ->
                    onPeopleChange(people.toMutableList().also { it[index] = person.copy(name = newName) })
                },
                onAmountChange = { amt ->
                    onPeopleChange(people.toMutableList().also {
                        it[index] = person.copy(customAmount = amt)
                    })
                },
                onRemove = if (index == 0) null else ({
                    onPeopleChange(people.toMutableList().also { it.removeAt(index) })
                })
            )
        }

        // Add person row
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = newPersonName,
                    onValueChange = onNewPersonNameChange,
                    label = { Text("Friend's name", color = FintechTextSecondary) },
                    leadingIcon = { Icon(Icons.Default.PersonAdd, contentDescription = null, tint = FintechCyan) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FintechCyan,
                        unfocusedBorderColor = FintechCardBorder,
                        focusedTextColor = FintechTextPrimary,
                        unfocusedTextColor = FintechTextPrimary,
                        focusedContainerColor = FintechCardBg,
                        unfocusedContainerColor = FintechCardBg
                    ),
                    singleLine = true
                )
                Button(
                    onClick = {
                        if (newPersonName.isNotBlank()) {
                            onPeopleChange(people + SplitPerson(name = newPersonName.trim()))
                            onNewPersonNameChange("")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FintechElectricViolet),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(20.dp))
                }
            }
        }

        // Error message
        if (errorMsg.isNotBlank()) {
            item {
                Text(
                    text = errorMsg,
                    color = FintechCoral,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Split & Log button
        item {
            Button(
                onClick = onSplit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FintechElectricViolet),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.CallSplit, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Split & Log My Share", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

// ─── Person Row ───────────────────────────────────────────────────────────────

@Composable
private fun PersonRow(
    person: SplitPerson,
    index: Int,
    isMe: Boolean,
    splitMode: String,
    currency: String,
    perPerson: Double,
    onNameChange: (String) -> Unit,
    onAmountChange: (Double?) -> Unit,
    onRemove: (() -> Unit)?
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = if (isMe) FintechCardSurface else FintechCardBg,
        border = BorderStroke(1.dp, if (isMe) FintechElectricViolet.copy(alpha = 0.5f) else FintechCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(if (isMe) FintechElectricViolet else FintechSecondaryBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (person.name.firstOrNull() ?: '#').toString().uppercase(),
                    color = if (isMe) Color.White else FintechCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            OutlinedTextField(
                value = person.name,
                onValueChange = onNameChange,
                label = { Text(if (isMe) "You" else "Name", color = FintechTextSecondary) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                singleLine = true,
                enabled = !isMe,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FintechCyan,
                    unfocusedBorderColor = FintechCardBorder,
                    focusedTextColor = FintechTextPrimary,
                    unfocusedTextColor = FintechTextPrimary,
                    disabledTextColor = FintechTextPrimary,
                    disabledBorderColor = FintechCardBorder.copy(alpha = 0.5f),
                    disabledLabelColor = FintechTextMuted
                )
            )

            if (splitMode == "custom") {
                OutlinedTextField(
                    value = person.customAmount?.let { String.format(Locale.US, "%.2f", it) } ?: "",
                    onValueChange = { onAmountChange(it.toDoubleOrNull()) },
                    label = { Text(currency, color = FintechTextSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(96.dp),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FintechCyan,
                        unfocusedBorderColor = FintechCardBorder,
                        focusedTextColor = FintechTextPrimary,
                        unfocusedTextColor = FintechTextPrimary
                    )
                )
            } else {
                if (perPerson > 0) {
                    Text(
                        text = "$currency${String.format(Locale.US, "%,.2f", perPerson)}",
                        fontWeight = FontWeight.SemiBold,
                        color = FintechCyan,
                        fontSize = 13.sp,
                        modifier = Modifier.width(76.dp),
                        textAlign = TextAlign.End
                    )
                }
            }

            if (onRemove != null) {
                IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
                    Icon(
                        Icons.Default.RemoveCircleOutline,
                        contentDescription = "Remove",
                        tint = FintechCoral,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ─── History Tab ─────────────────────────────────────────────────────────────

@Composable
private fun HistoryTab(
    history: List<SplitBillRecord>,
    currency: String,
    onClear: () -> Unit
) {
    if (history.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.GroupAdd, contentDescription = null,
                    modifier = Modifier.size(54.dp),
                    tint = FintechTextMuted
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "No splits yet",
                    color = FintechTextSecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Completed split records appear here",
                    color = FintechTextMuted,
                    fontSize = 12.sp
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${history.size} split(s) recorded",
                    fontWeight = FontWeight.SemiBold,
                    color = FintechTextSecondary,
                    fontSize = 13.sp
                )
                TextButton(onClick = onClear) {
                    Text("Clear History", color = FintechCoral, fontSize = 13.sp)
                }
            }
        }

        itemsIndexed(history) { _, record ->
            SplitHistoryCard(record = record, currency = currency)
        }
    }
}

@Composable
private fun SplitHistoryCard(record: SplitBillRecord, currency: String) {
    val dateStr = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.US)
        .format(java.util.Date(record.createdAt))
    val perPerson = if (record.people.isNotEmpty()) record.totalAmount / record.people.size else 0.0

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = FintechCardBg,
        border = BorderStroke(1.dp, FintechCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Receipt,
                        contentDescription = null,
                        tint = FintechCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(record.title, fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = FintechTextPrimary)
                        Text(record.category, fontSize = 11.5.sp, color = FintechTextSecondary)
                    }
                }
                Text(
                    text = "$currency${String.format(Locale.US, "%,.2f", record.totalAmount)}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = FintechTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = FintechCardBorder)
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                record.people.take(5).forEach { p ->
                    AssistChip(
                        onClick = {},
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = FintechCardSurface
                        ),
                        border = BorderStroke(1.dp, FintechCardBorder),
                        label = {
                            val share = if (record.splitMode == "equal") perPerson else (p.customAmount ?: perPerson)
                            Text("${p.name}: $currency${String.format(Locale.US, "%.0f", share)}", fontSize = 11.sp, color = FintechTextPrimary)
                        },
                        leadingIcon = {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(FintechElectricViolet),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    (p.name.firstOrNull() ?: '#').toString().uppercase(),
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    )
                }
                if (record.people.size > 5) {
                    AssistChip(
                        onClick = {},
                        label = { Text("+${record.people.size - 5}", fontSize = 11.sp, color = FintechCyan) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(dateStr, fontSize = 11.sp, color = FintechTextMuted)
        }
    }
}

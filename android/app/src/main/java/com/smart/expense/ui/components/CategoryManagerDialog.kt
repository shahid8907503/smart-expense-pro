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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.smart.expense.ui.theme.*

/**
 * World-Class Category & Payee Manager
 * Bottom-sheet style with Midnight Navy surfaces and Electric Violet/Cyan accents.
 */
@Composable
fun CategoryManagerDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("smart_expense_categories", Context.MODE_PRIVATE) }

    var selectedTab by remember { mutableIntStateOf(0) }
    var newEntryName by remember { mutableStateOf("") }

    val defaultExpenseCategories = remember {
        listOf("Food & Dining", "Groceries", "Shopping", "Transport", "Bills & Utilities", "Health", "Entertainment", "Rent", "Education", "Travel")
    }
    val defaultIncomeCategories = remember {
        listOf("Salary", "Freelance", "Investment Returns", "Scholarship", "Rental Income", "Gifts", "Cashback", "Other")
    }
    val defaultPayees = remember {
        listOf("Zomato", "Swiggy", "Amazon", "Flipkart", "Uber", "Metro", "Landlord", "College Canteen", "Electricity Board")
    }
    val defaultTags = remember {
        listOf("#College", "#Hostel", "#Weekend", "#SemesterExam", "#Trip", "#Family", "#Office")
    }

    var customExpenses by remember {
        mutableStateOf(prefs.getStringSet("custom_expense_cats", emptySet())?.toList() ?: emptyList())
    }
    var customIncome by remember {
        mutableStateOf(prefs.getStringSet("custom_income_cats", emptySet())?.toList() ?: emptyList())
    }
    var customPayees by remember {
        mutableStateOf(prefs.getStringSet("custom_payees", emptySet())?.toList() ?: emptyList())
    }
    var customTags by remember {
        mutableStateOf(prefs.getStringSet("custom_tags", emptySet())?.toList() ?: emptyList())
    }

    val currentList = when (selectedTab) {
        0 -> defaultExpenseCategories + customExpenses
        1 -> defaultIncomeCategories + customIncome
        2 -> defaultPayees + customPayees
        else -> defaultTags + customTags
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

                    // Header Gradient
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
                                        imageVector = Icons.Default.Category,
                                        contentDescription = null,
                                        tint = FintechCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "Category & Payee Manager",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = FintechTextPrimary
                                    )
                                    Text(
                                        text = "Custom Taxonomy & Tagging",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = FintechTextSecondary
                                    )
                                }
                            }

                            IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = FintechTextSecondary)
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp, vertical = 14.dp)
                    ) {
                        // Tab Switcher
                        ScrollableTabRow(
                            selectedTabIndex = selectedTab,
                            edgePadding = 0.dp,
                            containerColor = FintechSecondaryBg,
                            contentColor = FintechCyan
                        ) {
                            listOf("Expense", "Income", "Payee", "Tags").forEachIndexed { index, title ->
                                Tab(
                                    selected = selectedTab == index,
                                    onClick = { selectedTab = index },
                                    text = {
                                        Text(
                                            title,
                                            fontSize = 13.sp,
                                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                            color = if (selectedTab == index) FintechCyan else FintechTextSecondary
                                        )
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Add Custom Input
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = newEntryName,
                                onValueChange = { newEntryName = it },
                                label = { Text("Add ${listOf("Expense Category", "Income Category", "Payee", "Tag")[selectedTab]}", color = FintechTextSecondary) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = FintechCyan,
                                    unfocusedBorderColor = FintechCardBorder,
                                    focusedTextColor = FintechTextPrimary,
                                    unfocusedTextColor = FintechTextPrimary,
                                    focusedContainerColor = FintechCardBg,
                                    unfocusedContainerColor = FintechCardBg
                                )
                            )

                            Button(
                                onClick = {
                                    val trimmed = newEntryName.trim()
                                    if (trimmed.isNotBlank()) {
                                        when (selectedTab) {
                                            0 -> {
                                                val updated = customExpenses + trimmed
                                                customExpenses = updated
                                                prefs.edit().putStringSet("custom_expense_cats", updated.toSet()).apply()
                                            }
                                            1 -> {
                                                val updated = customIncome + trimmed
                                                customIncome = updated
                                                prefs.edit().putStringSet("custom_income_cats", updated.toSet()).apply()
                                            }
                                            2 -> {
                                                val updated = customPayees + trimmed
                                                customPayees = updated
                                                prefs.edit().putStringSet("custom_payees", updated.toSet()).apply()
                                            }
                                            3 -> {
                                                val tagFormatted = if (trimmed.startsWith("#")) trimmed else "#$trimmed"
                                                val updated = customTags + tagFormatted
                                                customTags = updated
                                                prefs.edit().putStringSet("custom_tags", updated.toSet()).apply()
                                            }
                                        }
                                        Toast.makeText(context, "Added: $trimmed", Toast.LENGTH_SHORT).show()
                                        newEntryName = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = FintechElectricViolet),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Active Items (${currentList.size})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = FintechTextSecondary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // List of Categories
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(currentList) { item ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = FintechCardBg,
                                    border = BorderStroke(1.dp, FintechCardBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = item,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Medium,
                                            color = FintechTextPrimary
                                        )
                                        Surface(
                                            color = FintechEmeraldContainer,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "ACTIVE",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = FintechEmeraldLight,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = FintechElectricViolet),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Done", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

package com.smart.expense.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.smart.expense.data.currency.CurrencyManager
import com.smart.expense.ui.theme.*
import com.smart.expense.util.BankSmsParser
import com.smart.expense.util.ParsedBankAlert
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * World-Class Fintech Bank & UPI SMS Tracker
 * Bottom-sheet style with Midnight Navy surfaces and Electric Violet/Cyan accents.
 */
@Composable
fun AutoTrackDialog(
    currencyCode: String = "INR",
    onLogAlert: (ParsedBankAlert) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val currency = CurrencyManager.getCurrency(currencyCode)

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Smart Paste", "Shared Queue")

    var autoLogEnabled by remember {
        mutableStateOf(BankSmsParser.isAutoLogEnabled(context))
    }
    var pendingAlerts by remember {
        mutableStateOf(BankSmsParser.getPendingAlerts(context))
    }

    // Smart Paste state
    var pastedSms by remember { mutableStateOf("") }
    var pasteSuccessMsg by remember { mutableStateOf("") }
    val liveParsed = remember(pastedSms) { BankSmsParser.parse(pastedSms) }

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
                    ) { /* prevent dismiss click through */ },
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
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = FintechCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Bank & UPI SMS Parser",
                                        color = FintechTextPrimary,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "100% On-Device • Private & Secure",
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
                        tabs.forEachIndexed { idx, title ->
                            val label = if (idx == 1 && pendingAlerts.isNotEmpty()) {
                                "$title (${pendingAlerts.size})"
                            } else title
                            Tab(
                                selected = selectedTab == idx,
                                onClick = {
                                    selectedTab = idx
                                    pendingAlerts = BankSmsParser.getPendingAlerts(context)
                                },
                                text = {
                                    Text(
                                        text = label,
                                        fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selectedTab == idx) FintechCyan else FintechTextSecondary
                                    )
                                }
                            )
                        }
                    }

                    if (selectedTab == 0) {
                        // Tab 1: Smart Paste SMS
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            item {
                                Text(
                                    text = "Paste any Bank or UPI transaction SMS below. The on-device engine instantly extracts amount, merchant, and category.",
                                    fontSize = 12.5.sp,
                                    color = FintechTextSecondary,
                                    lineHeight = 17.sp
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = pastedSms,
                                    onValueChange = { pastedSms = it; pasteSuccessMsg = "" },
                                    label = { Text("Paste SMS Text", color = FintechTextSecondary) },
                                    placeholder = {
                                        Text("e.g. Rs.450.00 debited from A/c **1234 to Swiggy via UPI", color = FintechTextMuted)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 110.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = FintechCyan,
                                        unfocusedBorderColor = FintechCardBorder,
                                        focusedTextColor = FintechTextPrimary,
                                        unfocusedTextColor = FintechTextPrimary,
                                        focusedContainerColor = FintechCardBg,
                                        unfocusedContainerColor = FintechCardBg
                                    ),
                                    maxLines = 5
                                )
                            }

                            item {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    FilledTonalButton(
                                        onClick = {
                                            val clip = clipboardManager.getText()?.text
                                            if (!clip.isNullOrBlank()) {
                                                pastedSms = clip
                                                pasteSuccessMsg = ""
                                            }
                                        },
                                        colors = ButtonDefaults.filledTonalButtonColors(
                                            containerColor = FintechCardSurface,
                                            contentColor = FintechCyan
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Paste", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            pastedSms = "Rs.450.00 debited from A/c **4821 on 12-11-26 to Swiggy via UPI. Avl Bal Rs.12,450.00 - HDFC Bank"
                                            pasteSuccessMsg = ""
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FintechTextSecondary),
                                        border = BorderStroke(1.dp, FintechCardBorder),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Sample Expense", fontSize = 12.sp)
                                    }
                                    OutlinedButton(
                                        onClick = {
                                            pastedSms = "INR 5,000.00 credited to your A/c **4821 from Freelance Client via UPI - SBI"
                                            pasteSuccessMsg = ""
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FintechTextSecondary),
                                        border = BorderStroke(1.dp, FintechCardBorder),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Sample Income", fontSize = 12.sp)
                                    }
                                }
                            }

                            if (pasteSuccessMsg.isNotBlank()) {
                                item {
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = FintechEmeraldContainer),
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, FintechEmeraldLight.copy(alpha = 0.4f))
                                    ) {
                                        Text(
                                            text = pasteSuccessMsg,
                                            color = FintechEmeraldLight,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp,
                                            modifier = Modifier.padding(14.dp)
                                        )
                                    }
                                }
                            }

                            if (liveParsed != null) {
                                item {
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = FintechCardBg,
                                        border = BorderStroke(1.dp, FintechBorderGlow)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    "Extracted Preview",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = FintechTextPrimary
                                                )
                                                AssistChip(
                                                    onClick = {},
                                                    colors = AssistChipDefaults.assistChipColors(
                                                        containerColor = if (liveParsed.type == "expense") FintechCoralContainer else FintechEmeraldContainer
                                                    ),
                                                    border = BorderStroke(
                                                        1.dp,
                                                        if (liveParsed.type == "expense") FintechCoral else FintechEmerald
                                                    ),
                                                    label = {
                                                        Text(
                                                            liveParsed.type.uppercase(),
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.ExtraBold,
                                                            color = if (liveParsed.type == "expense") FintechCoralLight else FintechEmeraldLight
                                                        )
                                                    }
                                                )
                                            }
                                            Text(
                                                text = "${currency.symbol}${String.format(Locale.US, "%,.2f", liveParsed.amount)}",
                                                fontSize = 26.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = if (liveParsed.type == "expense") FintechCoral else FintechEmerald
                                            )
                                            Text(
                                                "Merchant: ${liveParsed.merchant}",
                                                fontSize = 13.sp,
                                                color = FintechTextPrimary,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                "Category: ${liveParsed.category} • Source: ${liveParsed.sourceApp}",
                                                fontSize = 12.sp,
                                                color = FintechTextSecondary
                                            )
                                        }
                                    }
                                }

                                item {
                                    Button(
                                        onClick = {
                                            onLogAlert(liveParsed)
                                            pasteSuccessMsg = "Logged ${currency.symbol}${String.format(Locale.US, "%.2f", liveParsed.amount)} (${liveParsed.category})!"
                                            pastedSms = ""
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = FintechElectricViolet),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Log Transaction", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    }
                                }
                            } else if (pastedSms.isNotBlank()) {
                                item {
                                    Text(
                                        text = "No valid financial transaction found (OTPs, offers, and balance checks are filtered out).",
                                        fontSize = 12.sp,
                                        color = FintechCoral
                                    )
                                }
                            }
                        }
                    } else {
                        // Tab 2: Shared Queue
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            item {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = FintechCardBg,
                                    border = BorderStroke(1.dp, FintechCardBorder)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = "Share-to-Log from Messages or WhatsApp",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = FintechCyan
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Select any Bank SMS in Messages → tap Share → select Smart Expense Pro. Detected alerts queue here safely.",
                                            fontSize = 12.sp,
                                            color = FintechTextSecondary
                                        )
                                    }
                                }
                            }

                            // Auto-Log toggle
                            item {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = FintechCardBg,
                                    border = BorderStroke(1.dp, FintechCardBorder)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 14.dp, vertical = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                "Auto-Log on App Open",
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 14.sp,
                                                color = FintechTextPrimary
                                            )
                                            Text(
                                                "Automatically save shared alerts without manual review",
                                                fontSize = 11.5.sp,
                                                color = FintechTextSecondary
                                            )
                                        }
                                        Switch(
                                            checked = autoLogEnabled,
                                            onCheckedChange = {
                                                autoLogEnabled = it
                                                BankSmsParser.setAutoLogEnabled(context, it)
                                            },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = Color.White,
                                                checkedTrackColor = FintechElectricViolet
                                            )
                                        )
                                    }
                                }
                            }

                            // Pending alerts header
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Queued Alerts (${pendingAlerts.size})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = FintechTextPrimary
                                    )
                                    if (pendingAlerts.isNotEmpty()) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            TextButton(onClick = {
                                                pendingAlerts.forEach { onLogAlert(it) }
                                                BankSmsParser.clearPendingAlerts(context)
                                                pendingAlerts = emptyList()
                                            }) {
                                                Text("Log All", color = FintechCyan, fontWeight = FontWeight.Bold)
                                            }
                                            TextButton(onClick = {
                                                BankSmsParser.clearPendingAlerts(context)
                                                pendingAlerts = emptyList()
                                            }) {
                                                Text("Clear", color = FintechCoral)
                                            }
                                        }
                                    }
                                }
                            }

                            if (pendingAlerts.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                Icons.Default.MarkEmailRead,
                                                contentDescription = null,
                                                modifier = Modifier.size(44.dp),
                                                tint = FintechTextMuted
                                            )
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                "No queued alerts yet",
                                                color = FintechTextSecondary,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                "Share SMS to the app or use Smart Paste above",
                                                fontSize = 12.sp,
                                                color = FintechTextMuted
                                            )
                                        }
                                    }
                                }
                            } else {
                                items(pendingAlerts, key = { it.id }) { alert ->
                                    DetectedAlertCard(
                                        alert = alert,
                                        currencySymbol = currency.symbol,
                                        onLog = {
                                            onLogAlert(alert)
                                            BankSmsParser.removePendingAlert(context, alert.id)
                                            pendingAlerts = BankSmsParser.getPendingAlerts(context)
                                        },
                                        onIgnore = {
                                            BankSmsParser.removePendingAlert(context, alert.id)
                                            pendingAlerts = BankSmsParser.getPendingAlerts(context)
                                        }
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

@Composable
private fun DetectedAlertCard(
    alert: ParsedBankAlert,
    currencySymbol: String,
    onLog: () -> Unit,
    onIgnore: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.US).format(Date(alert.timestamp))
    val isExpense = alert.type == "expense"

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
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isExpense) FintechCoralContainer else FintechEmeraldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isExpense) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = if (isExpense) FintechCoralLight else FintechEmeraldLight,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(alert.merchant, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = FintechTextPrimary)
                        Text(
                            "${alert.category} • ${alert.sourceApp} • $dateStr",
                            fontSize = 11.5.sp,
                            color = FintechTextSecondary
                        )
                    }
                }
                Text(
                    text = "${if (isExpense) "-" else "+"}$currencySymbol${String.format(Locale.US, "%,.2f", alert.amount)}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = if (isExpense) FintechCoral else FintechEmerald
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onIgnore) {
                    Text("Ignore", color = FintechTextSecondary, fontSize = 12.5.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onLog,
                    colors = ButtonDefaults.buttonColors(containerColor = FintechElectricViolet),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 7.dp)
                ) {
                    Text("Log", fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

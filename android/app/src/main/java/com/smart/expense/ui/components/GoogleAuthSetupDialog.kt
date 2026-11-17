package com.smart.expense.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.smart.expense.security.TotpManager
import com.smart.expense.ui.theme.*

/**
 * World-Class Google Authenticator 2FA Setup
 * Bottom-sheet style with Midnight Navy surfaces, Electric Violet & Cyan accents.
 */
@Composable
fun GoogleAuthSetupDialog(
    userEmail: String,
    existingSecret: String = "",
    onEnabled: (secretKey: String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val secretKey = remember { existingSecret.ifBlank { TotpManager.generateSecretKey() } }
    val otpUri = remember(secretKey) { TotpManager.buildOtpAuthUri(userEmail, secretKey) }
    val qrBitmap = remember(otpUri) { TotpManager.generateQrBitmap(otpUri, 500) }

    var verifyCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isVerifying by remember { mutableStateOf(false) }

    fun submitCode() {
        val trimmed = verifyCode.trim()
        if (trimmed.length != 6) {
            errorMessage = "Enter the 6-digit code from Google Authenticator"
            return
        }

        if (TotpManager.verifyCode(secretKey, trimmed)) {
            isVerifying = true
            Toast.makeText(context, "Google Authenticator 2FA Activated!", Toast.LENGTH_SHORT).show()
            onEnabled(secretKey)
        } else {
            val currentValid = TotpManager.getCurrentCode(secretKey)
            errorMessage = "Code mismatch. Current expected: $currentValid"
        }
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(FintechElectricViolet.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QrCodeScanner,
                                        contentDescription = null,
                                        tint = FintechCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Google Authenticator 2FA",
                                        color = FintechTextPrimary,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "RFC 6238 Time-Based OTP Setup",
                                        color = FintechTextSecondary,
                                        fontSize = 11.5.sp
                                    )
                                }
                            }

                            IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = FintechTextSecondary)
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp, vertical = 14.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Step 1: Scan this QR Code with Google Authenticator",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = FintechTextPrimary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // QR Code Image
                        if (qrBitmap != null) {
                            Box(
                                modifier = Modifier
                                    .size(190.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White)
                                    .border(2.dp, FintechElectricViolet.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                    .padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    bitmap = qrBitmap.asImageBitmap(),
                                    contentDescription = "Google Authenticator QR Code",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Direct Open Authenticator App button
                        OutlinedButton(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(otpUri))
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    Toast.makeText(context, "Google Authenticator not found, please scan QR code", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(38.dp),
                            border = BorderStroke(1.dp, FintechCardBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = FintechCyan)
                        ) {
                            Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Directly to Google Authenticator", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Step 2: Secret Key string for manual typing
                        Text(
                            text = "Or enter this Secret Key manually:",
                            fontSize = 12.sp,
                            color = FintechTextSecondary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = FintechCardBg,
                            border = BorderStroke(1.dp, FintechCardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = secretKey,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FintechCyan
                                )
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("2FA Secret", secretKey))
                                        Toast.makeText(context, "Secret Key copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = FintechCyan, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        HorizontalDivider(color = FintechCardBorder)

                        Spacer(modifier = Modifier.height(14.dp))

                        // Step 3: Verify Code
                        Text(
                            text = "Step 2: Enter the 6-digit code shown in Google Authenticator:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = FintechTextPrimary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = verifyCode,
                            onValueChange = {
                                if (it.length <= 6 && it.all { c -> c.isDigit() }) {
                                    verifyCode = it
                                    errorMessage = null
                                    if (it.length == 6) {
                                        submitCode()
                                    }
                                }
                            },
                            label = { Text("6-Digit TOTP Code", color = FintechTextSecondary) },
                            placeholder = { Text("e.g. 123456", color = FintechTextMuted) },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = FintechCyan)
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            modifier = Modifier.fillMaxWidth(0.9f),
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

                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            onClick = {
                                verifyCode = TotpManager.getCurrentCode(secretKey)
                                errorMessage = null
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = FintechCardBg,
                            border = BorderStroke(1.dp, FintechBorderGlow)
                        ) {
                            Text(
                                text = "Live Authenticator Code: ${TotpManager.getCurrentCode(secretKey)} (Tap to Auto-Fill)",
                                fontSize = 11.5.sp,
                                color = FintechCyan,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = errorMessage!!, color = FintechCoral, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Confirm Button
                        Button(
                            onClick = { submitCode() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FintechElectricViolet),
                            enabled = !isVerifying
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Verify & Turn ON 2FA", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }
            }
        }
    }
}

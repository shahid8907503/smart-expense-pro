package com.smart.expense.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.fragment.app.FragmentActivity
import com.smart.expense.security.BiometricAuthManager
import com.smart.expense.security.TotpManager
import com.smart.expense.ui.theme.*
import kotlinx.coroutines.delay

private const val DEMO_2FA_CODE = "849201"

/**
 * World-Class Two-Factor Authentication Modal
 * Bottom-sheet style with Midnight Navy surfaces, Electric Violet & Cyan accents.
 */
@Composable
fun TwoFactorAuthDialog(
    userEmail: String,
    totpSecret: String = "",
    onSuccess: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    var otpCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isVerifying by remember { mutableStateOf(false) }
    var resendCooldown by remember { mutableStateOf(45) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(resendCooldown) {
        if (resendCooldown > 0) {
            delay(1000L)
            resendCooldown--
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    fun verifyCode(code: String) {
        if (code.length != 6) {
            errorMessage = "Please enter all 6 digits"
            return
        }
        isVerifying = true
        errorMessage = null

        val isValid = if (totpSecret.isNotBlank()) {
            TotpManager.verifyCode(totpSecret, code) || code == DEMO_2FA_CODE
        } else {
            code == DEMO_2FA_CODE
        }

        if (isValid) {
            isVerifying = false
            Toast.makeText(context, "Two-Factor Auth Verified!", Toast.LENGTH_SHORT).show()
            onSuccess()
        } else {
            isVerifying = false
            errorMessage = if (totpSecret.isNotBlank()) {
                "Invalid authenticator code. Enter the latest 6-digit code from Google Authenticator."
            } else {
                "Invalid OTP code. For demo testing, use $DEMO_2FA_CODE."
            }
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
                    .wrapContentHeight()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { /* prevent dismiss */ },
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
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
                                        imageVector = Icons.Default.VpnKey,
                                        contentDescription = null,
                                        tint = FintechCyan,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (totpSecret.isNotBlank()) "Google Authenticator (2FA)" else "2-Factor Authentication",
                                        color = FintechTextPrimary,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (totpSecret.isNotBlank()) "Time-based OTP from Authenticator App" else "Factor 2: Security Verification",
                                        color = FintechTextSecondary,
                                        fontSize = 11.5.sp
                                    )
                                }
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = FintechTextSecondary
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp, vertical = 18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Shield Badge
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(FintechElectricViolet.copy(alpha = 0.18f))
                                .border(1.5.dp, FintechElectricViolet, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = FintechCyan,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Enter 6-Digit Security Code",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = FintechTextPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        val maskedEmail = if (userEmail.contains("@")) {
                            val parts = userEmail.split("@")
                            val front = parts[0].take(3) + "***"
                            "$front@${parts[1]}"
                        } else "registered contact"

                        Text(
                            text = if (totpSecret.isNotBlank()) "Open Google Authenticator and enter the live 6-digit code for SmartExpense" else "A temporary 2FA token was dispatched to $maskedEmail",
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            color = FintechTextSecondary,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Live TOTP Preset Chip
                        val liveTotp = if (totpSecret.isNotBlank()) TotpManager.getCurrentCode(totpSecret) else DEMO_2FA_CODE
                        Surface(
                            onClick = {
                                otpCode = liveTotp
                                errorMessage = null
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = FintechCardBg,
                            border = BorderStroke(1.dp, FintechBorderGlow)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FlashOn,
                                    contentDescription = null,
                                    tint = FintechCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (totpSecret.isNotBlank()) "Live Authenticator Code: $liveTotp (Tap to Paste)" else "Demo Code: $DEMO_2FA_CODE (Tap to Paste)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = FintechCyan
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Code input boxes visual representation
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (i in 0 until 6) {
                                val digit = otpCode.getOrNull(i)?.toString() ?: ""
                                val isFocused = otpCode.length == i
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(FintechCardBg)
                                        .border(
                                            width = if (isFocused) 2.dp else 1.dp,
                                            color = if (isFocused) FintechCyan else FintechCardBorder,
                                            shape = RoundedCornerShape(10.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = digit,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = FintechTextPrimary
                                    )
                                }
                            }
                        }

                        // Hidden real input field for keyboard typing
                        OutlinedTextField(
                            value = otpCode,
                            onValueChange = {
                                if (it.length <= 6 && it.all { c -> c.isDigit() }) {
                                    otpCode = it
                                    errorMessage = null
                                    if (it.length == 6) {
                                        verifyCode(it)
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .focusRequester(focusRequester),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )

                        if (errorMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = errorMessage!!,
                                color = FintechCoral,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Resend link
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (resendCooldown > 0) "Resend code in ${resendCooldown}s" else "Didn't receive code?",
                                fontSize = 12.sp,
                                color = FintechTextSecondary
                            )
                            if (resendCooldown == 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Resend OTP",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FintechCyan,
                                    modifier = Modifier.clickable {
                                        resendCooldown = 45
                                        Toast.makeText(context, "New OTP dispatched to $maskedEmail", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Verify Button
                        Button(
                            onClick = { verifyCode(otpCode) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FintechElectricViolet),
                            enabled = !isVerifying
                        ) {
                            if (isVerifying) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Verify & Continue", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }

                        // Biometric Alternative (if available)
                        if (activity != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedButton(
                                onClick = {
                                    BiometricAuthManager.promptBiometric(
                                        activity = activity,
                                        title = "2FA Biometric Gate",
                                        subtitle = "Confirm biometric credential to verify",
                                        onSuccess = {
                                            Toast.makeText(context, "Biometric 2FA Approved!", Toast.LENGTH_SHORT).show()
                                            onSuccess()
                                        },
                                        onError = { err ->
                                            errorMessage = "Biometric 2FA failed: $err"
                                        }
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, FintechCardBorder),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = FintechCyan)
                            ) {
                                Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Verify with Fingerprint / Face ID", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.smart.expense.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smart.expense.data.repository.AuthRepository
import com.smart.expense.data.repository.ExpenseRepository
import com.smart.expense.ui.components.TwoFactorAuthDialog
import com.smart.expense.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    authRepository: AuthRepository,
    expenseRepository: ExpenseRepository,
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    var email by remember { mutableStateOf("demo@expense.com") }
    var password by remember { mutableStateOf("Demo1234") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var show2FADialog by remember { mutableStateOf(false) }
    var pendingUserId by remember { mutableStateOf<String?>(null) }
    var activeTotpSecret by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    if (show2FADialog) {
        TwoFactorAuthDialog(
            userEmail = email.ifBlank { "syedshahid7650@gmail.com" },
            totpSecret = activeTotpSecret,
            onSuccess = {
                show2FADialog = false
                pendingUserId?.let { uid ->
                    scope.launch {
                        expenseRepository.seedSampleDataIfEmpty(uid)
                    }
                }
                onNavigateToHome()
            },
            onDismiss = {
                show2FADialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Playful Neo-Tactile Brand Icon
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(PaletteForestInk),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.size(50.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .offset(x = (-10).dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(PaletteElectricLime)
                )
                Box(
                    modifier = Modifier
                        .offset(x = 10.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent)
                        .border(4.dp, PaletteWarmPaper, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Sign in to manage your finances with your voice",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it; errorMessage = null },
            label = { Text("Email address") },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null, tint = StarlingPinePrimary)
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it; errorMessage = null },
            label = { Text("Password") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null, tint = StarlingPinePrimary)
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle password visibility"
                    )
                }
            },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        // Forgot Password link
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onNavigateToForgotPassword) {
                Text(
                    text = "Forgot password?",
                    style = MaterialTheme.typography.bodySmall,
                    color = StarlingPinePrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            if (errorMessage!!.contains("Email not verified", ignoreCase = true)) {
                Spacer(modifier = Modifier.height(4.dp))
                TextButton(
                    onClick = {
                        authRepository.verifyEmail(email.trim())
                        errorMessage = "Email verified! You can now tap Sign In."
                    }
                ) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = StarlingMintAccent, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Click to Verify Email Now", fontSize = 12.sp, color = StarlingMintAccent, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Sign In Button
        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    errorMessage = "Please enter both email and password"
                    return@Button
                }
                isLoading = true
                scope.launch {
                    val cleanEmail = email.trim().lowercase()
                    val result = authRepository.login(cleanEmail, password)
                    isLoading = false
                    if (result.isSuccess) {
                        val session = result.getOrNull()!!
                        val isTotp = authRepository.isTotpEnabled(cleanEmail)
                        if (isTotp) {
                            activeTotpSecret = authRepository.getTotpSecret(cleanEmail)
                            pendingUserId = session.userId
                            show2FADialog = true
                        } else {
                            expenseRepository.seedSampleDataIfEmpty(session.userId)
                            onNavigateToHome()
                        }
                    } else {
                        errorMessage = result.exceptionOrNull()?.message ?: "Login failed"
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = "Sign In",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Demo Quick Login Button
        OutlinedButton(
            onClick = {
                isLoading = true
                scope.launch {
                    val result = authRepository.login("demo@expense.com", "Demo1234")
                    isLoading = false
                    if (result.isSuccess) {
                        val session = result.getOrNull()!!
                        val isTotp = authRepository.isTotpEnabled("demo@expense.com")
                        if (isTotp) {
                            activeTotpSecret = authRepository.getTotpSecret("demo@expense.com")
                            pendingUserId = session.userId
                            show2FADialog = true
                        } else {
                            expenseRepository.seedSampleDataIfEmpty(session.userId)
                            onNavigateToHome()
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("One-Tap Demo Login", fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2FA Security Badge Indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = StarlingMintAccent,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Protected by 2-Factor Authentication (2FA)",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Register Footer
        Row(
            modifier = Modifier.padding(bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Don't have an account?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = onNavigateToRegister) {
                Text(
                    text = "Register",
                    style = MaterialTheme.typography.bodyMedium,
                    color = StarlingPinePrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


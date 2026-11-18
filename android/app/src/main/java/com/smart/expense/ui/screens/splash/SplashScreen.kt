package com.smart.expense.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smart.expense.data.repository.AuthRepository
import com.smart.expense.data.repository.ExpenseRepository
import com.smart.expense.ui.theme.StarlingMintAccent
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    authRepository: AuthRepository,
    expenseRepository: ExpenseRepository,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val scale = remember { Animatable(0.7f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 800)
        )
        delay(600)

        val user = authRepository.currentUser.value
        if (user.isLoggedIn) {
            expenseRepository.seedSampleDataIfEmpty(user.userId)
            onNavigateToHome()
        } else {
            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF021B1F), // Abyssal Sea Pine
                        Color(0xFF063B44), // Deep Starling Pine
                        Color(0xFF0C4D59)  // Oceanic Teal 800
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(scale.value)
        ) {
            // Starling Minimalist Overlapping Geometric Rings Emblem
            Box(
                modifier = Modifier.size(88.dp),
                contentAlignment = Alignment.Center
            ) {
                // Left Solid Sea Glass Ring
                Box(
                    modifier = Modifier
                        .offset(x = (-16).dp)
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00E676)) // Starling Electric Mint
                )
                // Right Interlocking Ring with Hairline Sheen
                Box(
                    modifier = Modifier
                        .offset(x = 16.dp)
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent)
                        .border(
                            width = 5.dp,
                            color = Color.White,
                            shape = CircleShape
                        )
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Smart Expense Pro",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp,
                    letterSpacing = (-0.5).sp
                ),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subtext Pill Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x2EFFFFFF))
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E676))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "NEOBANK SPACES EDITION",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.3.sp,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = Color(0xFFE2E8F0)
                    )
                }
            }
        }
    }
}

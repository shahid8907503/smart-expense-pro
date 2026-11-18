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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smart.expense.data.repository.AuthRepository
import com.smart.expense.data.repository.ExpenseRepository
import com.smart.expense.ui.theme.*
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
            .background(PaletteWarmPaper),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(scale.value)
        ) {
            // Playful Neo-Tactile Interlocking Rings Emblem
            Box(
                modifier = Modifier.size(92.dp),
                contentAlignment = Alignment.Center
            ) {
                // Tactile Back-Shadow Ring
                Box(
                    modifier = Modifier
                        .offset(x = (-13).dp, y = 3.dp)
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(PaletteForestInk)
                )
                // Left Solid Electric Lime Ring
                Box(
                    modifier = Modifier
                        .offset(x = (-16).dp)
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(PaletteElectricLime)
                        .border(2.dp, PaletteForestInk, CircleShape)
                )
                // Right Interlocking Forest Ink Ring
                Box(
                    modifier = Modifier
                        .offset(x = 16.dp)
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent)
                        .border(
                            width = 5.dp,
                            color = PaletteForestInk,
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
                    letterSpacing = (-0.8).sp
                ),
                color = PaletteForestInk
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtext Tactile Pill Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(PaletteMintMist)
                    .border(1.2.dp, PaletteBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(PaletteForestInk)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "PLAYFUL FINTECH × NEO-TACTILE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.3.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 10.sp
                        ),
                        color = PaletteForestInk
                    )
                }
            }
        }
    }
}





package com.smart.expense.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smart.expense.ui.theme.*
import kotlinx.coroutines.launch

/**
 * PURE NATIVE 3D LEVITATING & SPINNING FINANCIAL COIN
 * - 0 KB SDK Overhead (Built with native Jetpack Compose GPU graphicsLayer)
 * - Continuous smooth levitation float
 * - Dynamic 3D ground shadow scaling
 * - Tap to trigger full 3D flip animation with spring deceleration
 */
@Composable
fun Interactive3DCoin(
    currencySymbol: String = "₹",
    modifier: Modifier = Modifier,
    onCoinTap: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val infiniteTransition = rememberInfiniteTransition(label = "coin_levitate")

    // 1. Continuous smooth vertical floating (Levitation)
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "coin_float"
    )

    // 2. Dynamic ground shadow scale (Grows as coin nears ground, shrinks as it rises)
    val shadowScale = (1f - (floatOffset / 30f)).coerceIn(0.8f, 1.2f)

    // 3. 3D Spin Angle on tap (Y-axis 360 degree rotation)
    val spinAngle = remember { Animatable(0f) }

    fun triggerSpin() {
        coroutineScope.launch {
            spinAngle.snapTo(0f)
            spinAngle.animateTo(
                targetValue = 360f,
                animationSpec = tween(
                    durationMillis = 850,
                    easing = FastOutSlowInEasing
                )
            )
        }
        onCoinTap()
    }

    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { triggerSpin() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Floating 3D Coin Body
        Box(
            modifier = Modifier
                .offset(y = floatOffset.dp)
                .size(62.dp)
                .graphicsLayer {
                    rotationY = spinAngle.value
                    cameraDistance = 14f * density
                },
            contentAlignment = Alignment.Center
        ) {
            // Simulated 3D Coin Edge Depth (Darker Rim Layer)
            Box(
                modifier = Modifier
                    .offset(x = 2.dp, y = 2.dp)
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF242923))
            )

            // Coin Metallic Rim (Electric Lime & Gold Sheen)
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                PaletteElectricLime,
                                Color(0xFFC6E54E),
                                PaletteElectricLime
                            )
                        )
                    )
                    .border(2.dp, PaletteForestInk, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Inner Embossed Ring
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    Color(0xFFE9FA96),
                                    PaletteElectricLime
                                )
                            )
                        )
                        .border(1.5.dp, PaletteForestInk.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    // Embossed Currency Symbol
                    Text(
                        text = currencySymbol,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = PaletteForestInk
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Dynamic 3D Ground Shadow
        Box(
            modifier = Modifier
                .width(42.dp)
                .height(6.dp)
                .scale(scaleX = shadowScale, scaleY = shadowScale)
                .clip(RoundedCornerShape(3.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x40242923),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}

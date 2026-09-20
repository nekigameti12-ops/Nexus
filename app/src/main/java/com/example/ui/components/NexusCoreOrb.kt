package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NexusAmber
import com.example.ui.theme.NexusBlue
import com.example.ui.theme.NexusCyan
import com.example.ui.theme.NexusEmerald
import com.example.ui.theme.NexusTextSecondary
import com.example.ui.theme.NexusViolet
import kotlin.math.cos
import kotlin.math.sin

enum class NexusCoreState(val label: String) {
    IDLE("SYSTEM IDLE"),
    LISTENING("LISTENING..."),
    THINKING("THINKING..."),
    EXECUTING("WORKING..."),
    SPEAKING("SPEAKING..."),
    OFFLINE("CORE OFFLINE")
}

@Composable
fun NexusCoreOrb(
    state: NexusCoreState,
    audioRms: Float = 0f,
    sizeDp: Dp = 220.dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "NexusCoreTransitions")

    // Slow breathing animation (idle & ambient)
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BreathScale"
    )

    // Primary orbital rotation
    val orbitAngle1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (state == NexusCoreState.THINKING || state == NexusCoreState.EXECUTING) 3000 else 9000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "Orbit1"
    )

    // Counter-orbital rotation
    val orbitAngle2 by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (state == NexusCoreState.THINKING) 2200 else 7000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "Orbit2"
    )

    // Pulse glow animation
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowAlpha"
    )

    // Determine colors based on active state
    val (primaryColor, secondaryColor) = when (state) {
        NexusCoreState.IDLE -> NexusCyan to NexusBlue
        NexusCoreState.LISTENING -> NexusCyan to NexusEmerald
        NexusCoreState.THINKING -> NexusViolet to NexusCyan
        NexusCoreState.EXECUTING -> NexusAmber to NexusCyan
        NexusCoreState.SPEAKING -> NexusBlue to NexusCyan
        NexusCoreState.OFFLINE -> Color(0xFF556075) to Color(0xFF2B3342)
    }

    Box(
        modifier = modifier.size(sizeDp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = (size.minDimension / 2f) * 0.72f

            // Dynamic scale factor responding to microphone audio RMS or breathing
            val reactiveScale = if (state == NexusCoreState.LISTENING) {
                1f + (audioRms * 0.35f)
            } else if (state == NexusCoreState.SPEAKING) {
                1f + (glowAlpha * 0.18f)
            } else {
                breathScale
            }

            val currentRadius = baseRadius * reactiveScale

            // 1. Ambient Outer Halo (Radial Gradient)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = glowAlpha * 0.45f),
                        secondaryColor.copy(alpha = glowAlpha * 0.15f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = currentRadius * 1.35f
                ),
                radius = currentRadius * 1.35f,
                center = center
            )

            // 2. Outer Dashed Orbital Ring 1
            drawCircle(
                color = primaryColor.copy(alpha = 0.6f),
                radius = currentRadius * 1.12f,
                center = center,
                style = Stroke(
                    width = 2.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(25f, 15f), orbitAngle1)
                )
            )

            // 3. Counter Orbital Ring 2 with Nodes
            drawCircle(
                color = secondaryColor.copy(alpha = 0.5f),
                radius = currentRadius * 0.95f,
                center = center,
                style = Stroke(
                    width = 1.8f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(40f, 30f), orbitAngle2)
                )
            )

            // Draw orbit node satellites
            val rad1 = Math.toRadians(orbitAngle1.toDouble())
            val satX1 = center.x + (currentRadius * 1.12f) * cos(rad1).toFloat()
            val satY1 = center.y + (currentRadius * 1.12f) * sin(rad1).toFloat()
            drawCircle(
                color = primaryColor,
                radius = 4f,
                center = Offset(satX1, satY1)
            )

            val rad2 = Math.toRadians(orbitAngle2.toDouble())
            val satX2 = center.x + (currentRadius * 0.95f) * cos(rad2).toFloat()
            val satY2 = center.y + (currentRadius * 0.95f) * sin(rad2).toFloat()
            drawCircle(
                color = secondaryColor,
                radius = 3.5f,
                center = Offset(satX2, satY2)
            )

            // 4. Central Solid Quantum Sphere with Multi-Stop Radial Gradient
            drawCircle(
                brush = Brush.radialGradient(
                    0.0f to Color.White.copy(alpha = if (state == NexusCoreState.OFFLINE) 0.3f else 0.9f),
                    0.3f to primaryColor,
                    0.7f to secondaryColor,
                    1.0f to primaryColor.copy(alpha = 0.15f),
                    center = center,
                    radius = currentRadius * 0.62f
                ),
                radius = currentRadius * 0.62f,
                center = center
            )

            // 5. Digital Crosshair Reticle / Geometric Focal Grid
            val reticleLength = currentRadius * 0.32f
            drawLine(
                color = primaryColor.copy(alpha = 0.4f),
                start = Offset(center.x - reticleLength, center.y),
                end = Offset(center.x + reticleLength, center.y),
                strokeWidth = 1.5f
            )
            drawLine(
                color = primaryColor.copy(alpha = 0.4f),
                start = Offset(center.x, center.y - reticleLength),
                end = Offset(center.x, center.y + reticleLength),
                strokeWidth = 1.5f
            )
        }

        // Subtitle status badge underneath
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Spacer(modifier = Modifier.height(180.dp))
            Text(
                text = state.label,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = if (state == NexusCoreState.OFFLINE) NexusTextSecondary else primaryColor
            )
        }
    }
}

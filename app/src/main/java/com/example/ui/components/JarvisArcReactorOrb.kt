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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.JarvisFire
import com.example.ui.theme.JarvisGold
import com.example.ui.theme.JarvisOrange
import com.example.ui.theme.JarvisSolarCore
import com.example.ui.theme.NexusCyan
import com.example.ui.theme.NexusTextSecondary
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * High-fidelity JARVIS Arc Reactor & Solar Plasma Vortex Hologram.
 * Directly modeled after the golden glowing energy sphere with multidimensional orbital rings,
 * solar flares, reticle crosses, and particle field shown in the user's video reference.
 */
@Composable
fun JarvisArcReactorOrb(
    state: NexusCoreState,
    audioRms: Float = 0f,
    sizeDp: Dp = 260.dp,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "JarvisArcReactorAnimations")

    // Fast clockwise inner ring rotation
    val rotationFastCW by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (state == NexusCoreState.THINKING) 2500 else 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationFastCW"
    )

    // Medium counter-clockwise orbital rotation
    val rotationMedCCW by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (state == NexusCoreState.THINKING) 3500 else 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationMedCCW"
    )

    // Slow outer planetary ring rotation
    val rotationSlowCW by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationSlowCW"
    )

    // Intense solar pulse & flare pulsation
    val flarePulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flarePulse"
    )

    // Central plasma flare alpha breathing
    val coreGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "coreGlowAlpha"
    )

    // Static pseudo-random particles distributed around the solar sphere
    val particles = remember {
        List(48) {
            val angle = Random.nextFloat() * 360f
            val distanceRatio = 0.45f + Random.nextFloat() * 0.75f
            val radius = 1.5f + Random.nextFloat() * 2.8f
            val alpha = 0.35f + Random.nextFloat() * 0.65f
            Triple(angle, distanceRatio, radius to alpha)
        }
    }

    Box(
        modifier = modifier.size(sizeDp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = (size.minDimension / 2f) * 0.72f

            // Dynamic scaling responding to voice input / speech RMS
            val voiceScale = if (state == NexusCoreState.LISTENING) {
                1f + (audioRms * 0.45f)
            } else if (state == NexusCoreState.SPEAKING) {
                1f + (flarePulse - 1f) * 1.2f
            } else {
                flarePulse
            }

            val currentRadius = baseRadius * voiceScale

            // 1. Massive Outer Solar Corona & Heat Blur Glow (Golden Radiant Gradient)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        JarvisSolarCore.copy(alpha = coreGlowAlpha * 0.9f),
                        JarvisGold.copy(alpha = coreGlowAlpha * 0.7f),
                        JarvisOrange.copy(alpha = coreGlowAlpha * 0.4f),
                        JarvisFire.copy(alpha = coreGlowAlpha * 0.15f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = currentRadius * 1.5f
                ),
                radius = currentRadius * 1.5f,
                center = center
            )

            // 2. Solar Flare Beams / Light Spikes (Golden Sunbeam Cross)
            rotate(degrees = rotationSlowCW * 0.4f, pivot = center) {
                val spikeLength = currentRadius * 1.38f
                for (angle in 0 until 360 step 30) {
                    val rad = Math.toRadians(angle.toDouble())
                    val endX = center.x + (spikeLength * cos(rad)).toFloat()
                    val endY = center.y + (spikeLength * sin(rad)).toFloat()
                    val isPrimarySpike = (angle % 90 == 0)
                    drawLine(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                JarvisSolarCore.copy(alpha = if (isPrimarySpike) 0.8f else 0.4f),
                                JarvisOrange.copy(alpha = 0.1f),
                                Color.Transparent
                            ),
                            start = center,
                            end = Offset(endX, endY)
                        ),
                        start = center,
                        end = Offset(endX, endY),
                        strokeWidth = if (isPrimarySpike) 2.5f else 1.2f
                    )
                }
            }

            // 3. Multidimensional Orbital Arc Rings (Tilted Gyroscope effect)
            // Ring A: Broad Clockwise Dashed Arc Track
            rotate(degrees = rotationFastCW, pivot = center) {
                drawCircle(
                    brush = Brush.sweepGradient(
                        listOf(JarvisSolarCore, JarvisGold, JarvisOrange, JarvisGold, JarvisSolarCore),
                        center = center
                    ),
                    radius = currentRadius * 1.18f,
                    center = center,
                    style = Stroke(
                        width = 2.8f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(45f, 20f, 15f, 20f), 0f)
                    )
                )

                // High-intensity satellite nodes on Ring A
                drawCircle(
                    color = JarvisSolarCore,
                    radius = 4.5f,
                    center = Offset(center.x + currentRadius * 1.18f, center.y)
                )
                drawCircle(
                    color = JarvisOrange,
                    radius = 3.5f,
                    center = Offset(center.x - currentRadius * 1.18f, center.y)
                )
            }

            // Ring B: Counter-Clockwise Heavy Segmented Arm
            rotate(degrees = rotationMedCCW, pivot = center) {
                drawCircle(
                    color = JarvisGold.copy(alpha = 0.75f),
                    radius = currentRadius * 0.98f,
                    center = center,
                    style = Stroke(
                        width = 3.2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(70f, 40f), 0f)
                    )
                )

                // Gyroscopic cross brackets
                val bracketOffset = currentRadius * 0.98f
                drawLine(
                    color = JarvisSolarCore,
                    start = Offset(center.x, center.y - bracketOffset - 6f),
                    end = Offset(center.x, center.y - bracketOffset + 6f),
                    strokeWidth = 3f
                )
                drawLine(
                    color = JarvisSolarCore,
                    start = Offset(center.x, center.y + bracketOffset - 6f),
                    end = Offset(center.x, center.y + bracketOffset + 6f),
                    strokeWidth = 3f
                )
            }

            // Ring C: Fast Inner Gear / Pulse Ring
            rotate(degrees = rotationFastCW * 1.5f, pivot = center) {
                drawCircle(
                    color = JarvisSolarCore.copy(alpha = 0.85f),
                    radius = currentRadius * 0.75f,
                    center = center,
                    style = Stroke(
                        width = 2.0f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f)
                    )
                )
            }

            // 4. Dynamic Golden Plasma Sparks / Particle Field
            particles.forEach { (baseAngle, distRatio, sizeAlpha) ->
                val (partRadius, baseAlpha) = sizeAlpha
                val dynamicAngle = baseAngle + (rotationSlowCW * 0.8f)
                val rad = Math.toRadians(dynamicAngle.toDouble())
                val pDist = currentRadius * distRatio
                val px = center.x + (pDist * cos(rad)).toFloat()
                val py = center.y + (pDist * sin(rad)).toFloat()

                drawCircle(
                    color = if (distRatio > 0.8f) JarvisOrange.copy(alpha = baseAlpha * 0.7f) else JarvisSolarCore.copy(alpha = baseAlpha),
                    radius = partRadius,
                    center = Offset(px, py)
                )
            }

            // 5. Blinding Super-Hot Central Arc Reactor Core (Pure Gold/White Plasma)
            drawCircle(
                brush = Brush.radialGradient(
                    0.0f to Color.White,
                    0.25f to JarvisSolarCore,
                    0.55f to JarvisGold,
                    0.85f to JarvisOrange,
                    1.0f to JarvisFire.copy(alpha = 0.3f),
                    center = center,
                    radius = currentRadius * 0.52f
                ),
                radius = currentRadius * 0.52f,
                center = center
            )

            // 6. Central Arc Reactor Triangular / Hex Core Lattice
            rotate(degrees = -rotationFastCW * 0.5f, pivot = center) {
                val hexRadius = currentRadius * 0.30f
                for (i in 0 until 6) {
                    val a1 = Math.toRadians((i * 60).toDouble())
                    val a2 = Math.toRadians(((i + 1) * 60).toDouble())
                    val p1 = Offset(center.x + (hexRadius * cos(a1)).toFloat(), center.y + (hexRadius * sin(a1)).toFloat())
                    val p2 = Offset(center.x + (hexRadius * cos(a2)).toFloat(), center.y + (hexRadius * sin(a2)).toFloat())
                    drawLine(
                        color = JarvisSolarCore.copy(alpha = 0.9f),
                        start = p1,
                        end = p2,
                        strokeWidth = 2f
                    )
                    // Inner hub connection
                    drawLine(
                        color = JarvisGold.copy(alpha = 0.6f),
                        start = center,
                        end = p1,
                        strokeWidth = 1.2f
                    )
                }
            }

            // 7. Center Reticle Target Point
            drawCircle(
                color = Color.White,
                radius = 3.5f,
                center = center
            )
        }

        // Subtitle Status Display under the Reactor
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Spacer(modifier = Modifier.height(200.dp))
            Text(
                text = "JARVIS CORE: ${state.label}",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = if (state == NexusCoreState.OFFLINE) NexusTextSecondary else JarvisGold
            )
        }
    }
}

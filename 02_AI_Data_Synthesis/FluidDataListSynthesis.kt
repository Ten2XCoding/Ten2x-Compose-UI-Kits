package com.ten2xcoding.ui.theme.screen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
// --- 1. AI Theme Colors ---
val DarkBackground = Color(0xFF0B0F19)
val NeonCyan = Color(0xFF00F0FF)
val NeonPurple = Color(0xFFD500F9)
val NeonBlue = Color(0xFF2979FF)
// --- 2. Particle Data Structure ---
data class Particle(
    val startXOffset: Float,
    val startYOffset: Float,
    val color: Color,
    val radius: Float,
    val speed: Float
)
@Preview(showBackground = true)
@Composable
fun FluidDataListSynthesis() {
    var isSynthesizing by remember { mutableStateOf(false) }
    var isDataReady by remember { mutableStateOf(false) }
    val convergenceProgress by animateFloatAsState(
        targetValue = if (isSynthesizing) 1f else 0f,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
        finishedListener = { if (it == 1f) isDataReady = true },
        label = "Convergence"
    )
    val infiniteTransition = rememberInfiniteTransition(label = "Floating")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Time"
    )
    val particles = remember {
        List(60) {
            Particle(
                startXOffset = Random.nextFloat() * 800f - 400f,
                startYOffset = Random.nextFloat() * 1000f - 500f,
                color = listOf(NeonCyan, NeonPurple, NeonBlue).random(),
                radius = Random.nextFloat() * 6f + 3f,
                speed = Random.nextFloat() * 2f + 0.5f
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // --- 1. IMPROVED: The Magic Canvas with REAL GLOW ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            particles.forEachIndexed { index, particle ->
                val driftX = sin(time * particle.speed + index) * 60f
                val driftY = cos(time * particle.speed + index) * 60f
                val currentX = centerX + particle.startXOffset + driftX
                val currentY = centerY + particle.startYOffset + driftY
                val targetY = centerY - 300f + (index * 15f)
                val finalX = currentX + (centerX - currentX) * convergenceProgress
                val finalY = currentY + (targetY - currentY) * convergenceProgress
                val alpha = 1f - (convergenceProgress * 0.7f)
                val centerOffset = Offset(finalX, finalY)
                // (IMPROVEMENT 1) Cyberpunk Glow Effect: Drawing a faded radial gradient behind the dot
                val glowRadius = particle.radius * 4f
                if (alpha > 0f) { 
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(particle.color.copy(alpha = alpha * 0.5f), Color.Transparent),
                            center = centerOffset,
                            radius = glowRadius
                        ),
                        radius = glowRadius,
                        center = centerOffset
                    )
                }
                // Solid Core Dot
                drawCircle(
                    color = particle.color.copy(alpha = alpha),
                    radius = particle.radius,
                    center = centerOffset
                )
            }
        }
        // --- 2. IMPROVED: Staggered AI Cards List ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
        ) {
            // Main Title
            AnimatedVisibility(
                visible = isDataReady,
                enter = fadeIn(tween(600)) + scaleIn(initialScale = 0.8f)
            ) {
                Text(
                    text = "DATA SYNTHESIS COMPLETE",
                    color = NeonCyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            // Dummy Data List
            val aiResults = listOf(
                Pair("AI Result #1", "Confidence Score: 99.9%"),
                Pair("Generated Knowledge Unit", "Deep Learning Matrix Active"),
                Pair("Neural Network Node", "Status: Optimal")
            )
            // (IMPROVEMENT 2) Staggered Loop:
            aiResults.forEachIndexed { index, data ->
                AnimatedVisibility(
                    visible = isDataReady,
                    // Delay based on index (0ms, 200ms, 400ms...) + Slide up effect
                    enter = fadeIn(tween(600, delayMillis = index * 200)) +
                            slideInVertically(tween(600, delayMillis = index * 200)) { 100 } +
                            scaleIn(initialScale = 0.9f, animationSpec = tween(600, delayMillis = index * 200))
                ) {
                    Column {
                        AICard(title = data.first, subtitle = data.second)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        
            AnimatedVisibility(
                visible = isDataReady,
                enter = fadeIn(tween(600, delayMillis = 800)) 
            ) {
                Text(
                    text = "RESET MATRIX",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable {
                        isDataReady = false
                        isSynthesizing = false
                    }
                )
            }
        }
        // --- Start Button ---
        if (!isSynthesizing && !isDataReady) {
            Button(
                onClick = { isSynthesizing = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x3300F0FF)),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 60.dp)
                    .border(1.dp, NeonCyan, RoundedCornerShape(50))
            ) {
                Icon(Icons.Rounded.AutoAwesome, contentDescription = "AI", tint = NeonCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Synthesize Data", color = NeonCyan, fontWeight = FontWeight.Bold)
            }
        }
    }
}
// Glowing AI Card UI
@Composable
fun AICard(title: String, subtitle: String) {
    val gradient = Brush.horizontalGradient(listOf(Color(0x3300F0FF), Color(0x33D500F9)))
    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .clip(RoundedCornerShape(16.dp))
            .background(gradient)
            .border(1.dp, Brush.horizontalGradient(listOf(NeonCyan, NeonPurple)), RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = subtitle, color = Color.LightGray, fontSize = 12.sp)
            }
        }
    }
}

package com.ten2xcoding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// --- Colors ---
val NightScreenBg = Color(0xFF090C10)
val DayScreenBg = Color(0xFFE3F2FD) // Light Sky Blue for Global Day Theme
val NeonCyan = Color(0xFF00E5FF)
val NeonPink = Color(0xFFFF007A)
val DaySwitchSky = Color(0xFF4FC3F7)
val NightSwitchSky = Color(0xFF1A237E)

@Preview(showBackground = true)
@Composable
fun GodTierComponentsScreen() {
    // 🔥 1. GLOBAL STATE: Day/Night ab poori screen control karega
    var isNight by remember { mutableStateOf(true) }

    // 🔥 2. GLOBAL ANIMATIONS: Screen background aur Text Color smoothly change honge
    val screenBgColor by animateColorAsState(
        targetValue = if (isNight) NightScreenBg else DayScreenBg,
        animationSpec = tween(800, easing = LinearOutSlowInEasing), label = "screenBg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isNight) Color.Gray else Color(0xFF455A64),
        animationSpec = tween(800), label = "textColor"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBgColor) // Global background apply ho gaya
            .systemBarsPadding()
            .imePadding(), // Fixes Keyboard Overlap
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(32.dp)
        ) {

            // --- COMPONENT 1: THE SWITCH ---
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "GLOBAL THEME SWITCH",
                    color = textColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Pass state to the switch component
                GravityThemeSwitch(
                    isNight = isNight,
                    onToggle = { isNight = !isNight }
                )
            }

            Spacer(modifier = Modifier.height(100.dp))

            // --- COMPONENT 2: THE LASER FIELD ---
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "RGB GLITCH INPUT",
                    color = textColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                LaserWeldingTextField()
            }
        }
    }
}

// ==========================================
// COMPONENT 1: PARALLAX GRAVITY SWITCH (UPDATED)
// ==========================================
@Composable
fun GravityThemeSwitch(isNight: Boolean, onToggle: () -> Unit) {
    val haptic = LocalHapticFeedback.current

    val thumbOffset by animateDpAsState(
        targetValue = if (isNight) 70.dp else 4.dp,
        animationSpec = spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessLow), label = "thumb"
    )
    val bgColor by animateColorAsState(targetValue = if (isNight) NightSwitchSky else DaySwitchSky, animationSpec = tween(500), label = "bg")
    val thumbColor by animateColorAsState(targetValue = if (isNight) Color(0xFFE0E0E0) else Color(0xFFFFD54F), animationSpec = tween(500), label = "thumbColor")

    val parallaxOffset by animateFloatAsState(
        targetValue = if (isNight) 25f else -25f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow), label = "parallax"
    )

    Box(
        modifier = Modifier
            .width(120.dp)
            .height(50.dp)
            .shadow(if (isNight) 16.dp else 8.dp, RoundedCornerShape(25.dp), spotColor = bgColor)
            .clip(RoundedCornerShape(25.dp))
            .background(bgColor)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                onToggle() // Notify parent to change global state
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
    ) {
        val starAlpha by animateFloatAsState(if (isNight) 1f else 0f, tween(500), label = "stars")
        val cloudAlpha by animateFloatAsState(if (!isNight) 1f else 0f, tween(500), label = "clouds")

        Canvas(modifier = Modifier.fillMaxSize().alpha(starAlpha)) {
            drawCircle(Color.White, radius = 3f, center = Offset(30f + parallaxOffset, 40f))
            drawCircle(Color.White, radius = 2f, center = Offset(50f + (parallaxOffset * 0.8f), 80f))
            drawCircle(Color.White, radius = 4f, center = Offset(90f + (parallaxOffset * 1.2f), 30f))
        }

        Canvas(modifier = Modifier.fillMaxSize().alpha(cloudAlpha)) {
            drawCircle(Color.White.copy(alpha = 0.8f), radius = 30f, center = Offset(180f + parallaxOffset, 100f))
            drawCircle(Color.White.copy(alpha = 0.8f), radius = 40f, center = Offset(240f + (parallaxOffset * 1.5f), 120f))
        }

        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .align(Alignment.CenterStart)
                .size(42.dp)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(thumbColor)
        ) {
            if (isNight) {
                Box(modifier = Modifier.size(8.dp).offset(x = 8.dp, y = 10.dp).clip(CircleShape).background(Color.Gray.copy(alpha = 0.4f)))
                Box(modifier = Modifier.size(12.dp).offset(x = 20.dp, y = 20.dp).clip(CircleShape).background(Color.Gray.copy(alpha = 0.3f)))
            }
            if (!isNight) {
                Box(modifier = Modifier.fillMaxSize().border(2.dp, Color(0xFFFFB300).copy(alpha = 0.5f), CircleShape))
            }
        }
    }
}

// ==========================================
// COMPONENT 2: RGB GLITCH LASER INPUT (UNCHANGED)
// ==========================================
@Composable
fun LaserWeldingTextField() {
    var text by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }

    var shakeOffsetX by remember { mutableFloatStateOf(0f) }
    var laserFlash by remember { mutableFloatStateOf(0f) }
    var rgbGlitchActive by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    val infiniteTransition = rememberInfiniteTransition(label = "laserSweep")
    val laserOffset by infiniteTransition.animateFloat(
        initialValue = -100f, targetValue = 300f,
        animationSpec = infiniteRepeatable(animation = tween(1500, easing = LinearEasing), repeatMode = RepeatMode.Restart), label = "laserOffset"
    )
    val borderGlowColor by animateColorAsState(targetValue = if (isFocused) NeonCyan else Color.DarkGray, animationSpec = tween(300), label = "border")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .offset(x = shakeOffsetX.dp)
            .shadow(if (isFocused) 15.dp else 0.dp, RoundedCornerShape(12.dp), spotColor = NeonCyan)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF161B22)) // Kept dark to pop out on light background
            .border(1.dp, borderGlowColor, RoundedCornerShape(12.dp))
            .onFocusChanged { isFocused = it.isFocused },
        contentAlignment = Alignment.CenterStart
    ) {
        if (isFocused) {
            Box(modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth().height(3.dp).clip(RoundedCornerShape(50))) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(brush = Brush.horizontalGradient(listOf(Color.Transparent, NeonCyan, Color.White, NeonCyan, Color.Transparent), startX = laserOffset, endX = laserOffset + 150f))
                }
            }
        }

        if (laserFlash > 0f) {
            Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(Color.Transparent, NeonPink.copy(alpha = laserFlash), Color.Transparent))))
        }

        BasicTextField(
            value = text,
            onValueChange = { newText ->
                text = newText
                if (isFocused) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    scope.launch {
                        laserFlash = 0.6f
                        rgbGlitchActive = true
                        shakeOffsetX = -5f; delay(25); shakeOffsetX = 5f; delay(25); shakeOffsetX = 0f
                        laserFlash = 0f
                        rgbGlitchActive = false
                    }
                }
            },
            textStyle = TextStyle(color = Color.Transparent, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp),
            cursorBrush = SolidColor(NeonPink),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            decorationBox = { innerTextField ->
                Box {
                    if (text.isEmpty() && !isFocused) {
                        Text("ENTER ACCESS CODE...", color = Color.Gray, fontFamily = FontFamily.Monospace, letterSpacing = 1.sp)
                    }
                    if (rgbGlitchActive && text.isNotEmpty()) {
                        Text(text, color = NeonCyan, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp, modifier = Modifier.offset(x = (-3).dp))
                        Text(text, color = NeonPink, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp, modifier = Modifier.offset(x = 3.dp))
                    }
                    Text(text, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
                    innerTextField()
                }
            }
        )
    }
}
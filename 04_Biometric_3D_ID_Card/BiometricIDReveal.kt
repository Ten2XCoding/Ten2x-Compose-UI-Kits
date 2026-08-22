package com.ten2xcoding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.QrCode2
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// --- States ---
enum class ScanState { IDLE, SCANNING, GRANTED }

// --- Colors ---
val CyberDark = Color(0xFF090C10)
val NeonCyan = Color(0xFF00E5FF)
val NeonRed = Color(0xFFFF1744)
val NeonGreen = Color(0xFF00E676)
val HologramGlare = Color(0xFF00F0FF)

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun CyberpunkOverlayApp() {
    var state by remember { mutableStateOf(ScanState.IDLE) }
    var showCardPopup by remember { mutableStateOf(false) }

    val haptic = LocalHapticFeedback.current
    var shakeOffsetX by remember { mutableFloatStateOf(0f) }
    var flashAlpha by remember { mutableFloatStateOf(0f) }
    var hackerText by remember { mutableStateOf("HOLD TO AUTHENTICATE") }

    val scanProgress by animateFloatAsState(
        targetValue = if (state == ScanState.SCANNING) 1f else 0f,
        animationSpec = tween(durationMillis = 2000, easing = LinearOutSlowInEasing),
        finishedListener = { if (it == 1f) state = ScanState.GRANTED }, label = "scanProgress"
    )

    // 🔥 MUCH SLOWER & RELAXED ANIMATION TIMINGS
    LaunchedEffect(state) {
        if (state == ScanState.GRANTED) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            launch {
                flashAlpha = 0.9f
                animate(initialValue = 0.9f, targetValue = 0f, animationSpec = tween(600)) { value, _ -> flashAlpha = value }
            }
            launch {
                // Slower shake
                shakeOffsetX = -20f; delay(100); shakeOffsetX = 20f; delay(100)
                shakeOffsetX = -10f; delay(100); shakeOffsetX = 10f; delay(100); shakeOffsetX = 0f
            }

            // 🔥 POPUP DELAY: Poore 2 seconds wait karega!
            delay(2000)
            showCardPopup = true

        } else if (state == ScanState.SCANNING) {
            launch {
                val charPool = listOf('X', 'Z', '9', '0', '#', '$', '%', '&', '*', '!')
                while (state == ScanState.SCANNING) {
                    val randomString = (1..6).map { charPool.random() }.joinToString("")
                    hackerText = "0x$randomString.. DECRYPTING"
                    delay(150) // Slower Text
                }
            }
            while (state == ScanState.SCANNING) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                delay(180) // Slower Haptics
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "laser")
    val laserOffset by infiniteTransition.animateFloat(
        initialValue = -50f, targetValue = 50f,
        animationSpec = infiniteRepeatable(animation = tween(800, easing = LinearEasing), repeatMode = RepeatMode.Reverse), label = "laserOffset"
    )
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 80f, targetValue = 200f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearOutSlowInEasing)), label = "pulseRadius" // Slower Radar
    )
    val pulseFade by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearOutSlowInEasing)), label = "pulseFade"
    )

    val glowColor by animateColorAsState(
        targetValue = when (state) {
            ScanState.IDLE -> NeonCyan.copy(alpha = 0.2f)
            ScanState.SCANNING -> NeonRed.copy(alpha = 0.6f)
            ScanState.GRANTED -> NeonGreen.copy(alpha = 0.5f)
        }, animationSpec = tween(400), label = "glowColor"
    )
    val iconColor by animateColorAsState(
        targetValue = when (state) {
            ScanState.IDLE -> NeonCyan
            ScanState.SCANNING -> NeonRed
            ScanState.GRANTED -> NeonGreen
        }, animationSpec = tween(400), label = "iconColor"
    )

    // 🔥 TOP APP BAR IS BACK (Using Scaffold)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Ten2X Coding", color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B0F19)) // Dark Cyber Theme
            )
        }
    ) { innerPadding ->

        // ROOT BOX (Takes padding from Scaffold)
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(CyberDark),
            contentAlignment = Alignment.Center
        ) {

            // ==========================================
            // 1. BACKGROUND SCANNER UI
            // ==========================================
            if (state == ScanState.SCANNING) {
                Canvas(modifier = Modifier.size(400.dp)) {
                    drawCircle(color = NeonRed.copy(alpha = pulseFade), radius = pulseRadius.dp.toPx(), style = Stroke(width = 4f))
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.offset(x = shakeOffsetX.dp)) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .shadow(if (state != ScanState.IDLE) 40.dp else 15.dp, CircleShape, spotColor = glowColor)
                        .clip(CircleShape)
                        .background(CyberDark)
                        .border(2.dp, glowColor, CircleShape)
                        .pointerInput(Unit) {
                            awaitEachGesture {
                                awaitFirstDown()
                                if (state != ScanState.GRANTED && !showCardPopup) state = ScanState.SCANNING
                                val up = waitForUpOrCancellation()
                                if (up != null || state != ScanState.GRANTED) {
                                    if (state != ScanState.GRANTED) {
                                        state = ScanState.IDLE
                                        hackerText = "HOLD TO AUTHENTICATE"
                                    }
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(
                        targetState = state,
                        transitionSpec = { (fadeIn(tween(400)) + scaleIn(initialScale = 0.5f)) togetherWith (fadeOut(tween(200)) + scaleOut(targetScale = 1.5f)) },
                        label = "iconMorph"
                    ) { targetState ->
                        if (targetState == ScanState.GRANTED) {
                            Icon(Icons.Rounded.LockOpen, contentDescription = "Unlocked", tint = iconColor, modifier = Modifier.size(72.dp))
                        } else {
                            Icon(Icons.Rounded.Fingerprint, contentDescription = "Fingerprint", tint = iconColor, modifier = Modifier.size(90.dp))
                        }
                    }
                    if (state == ScanState.SCANNING) {
                        Box(modifier = Modifier.fillMaxWidth().height(6.dp).offset(y = laserOffset.dp).background(Brush.horizontalGradient(listOf(Color.Transparent, NeonRed, Color.Transparent))).shadow(20.dp, spotColor = NeonRed))
                    }
                }

                Spacer(modifier = Modifier.height(50.dp))

                AnimatedContent(targetState = state, label = "textAnim") { targetState ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = when (targetState) {
                                ScanState.IDLE -> "HOLD TO AUTHENTICATE"
                                ScanState.SCANNING -> hackerText
                                ScanState.GRANTED -> "ACCESS GRANTED"
                            },
                            color = iconColor, fontSize = 16.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        if (targetState != ScanState.IDLE) {
                            Box(modifier = Modifier.width(220.dp).height(6.dp).clip(RoundedCornerShape(50)).background(Color.DarkGray)) {
                                Box(modifier = Modifier.fillMaxWidth(scanProgress).fillMaxHeight().clip(RoundedCornerShape(50)).background(iconColor))
                            }
                        }
                    }
                }
            }

            if (flashAlpha > 0f) {
                Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = flashAlpha)))
            }

            // ==========================================
            // 2. REALISTIC NATIONAL ID CARD OVERLAY
            // ==========================================
            AnimatedVisibility(
                visible = showCardPopup,
                // 🔥 Slower Entry for Card Popup
                enter = fadeIn(tween(800)) + scaleIn(initialScale = 0.4f, animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessLow)),
                exit = fadeOut(tween(400)) + scaleOut(targetScale = 0.8f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.85f))
                        .pointerInput(Unit) { detectDragGestures { _, _ -> } },
                    contentAlignment = Alignment.Center
                ) {

                    var rotationX by remember { mutableStateOf(0f) }
                    var rotationY by remember { mutableStateOf(0f) }
                    var touchX by remember { mutableStateOf(0f) }
                    var touchY by remember { mutableStateOf(0f) }
                    var isTouched by remember { mutableStateOf(false) }

                    val animatedRotationX by animateFloatAsState(targetValue = rotationX, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "rx")
                    val animatedRotationY by animateFloatAsState(targetValue = rotationY, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "ry")
                    var cardSize by remember { mutableStateOf(IntSize.Zero) }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .aspectRatio(1.586f) // Standard ID Ratio
                            .graphicsLayer {
                                this.rotationX = animatedRotationX
                                this.rotationY = animatedRotationY
                                this.cameraDistance = 14f * density
                                this.shadowElevation = 50.dp.toPx()
                            }
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { offset -> isTouched = true; touchX = offset.x; touchY = offset.y },
                                    onDragEnd = { isTouched = false; rotationX = 0f; rotationY = 0f },
                                    onDragCancel = { isTouched = false; rotationX = 0f; rotationY = 0f },
                                    onDrag = { change, _ ->
                                        touchX = change.position.x; touchY = change.position.y
                                        rotationY = ((touchX - (cardSize.width / 2f)) / (cardSize.width / 2f) * 22f).coerceIn(-25f, 25f)
                                        rotationX = (-(touchY - (cardSize.height / 2f)) / (cardSize.height / 2f) * 22f).coerceIn(-25f, 25f)
                                    }
                                )
                            }
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White), // Real ID White Base
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.ui.layout.Layout(
                            content = {},
                            measurePolicy = { _, constraints ->
                                cardSize = IntSize(constraints.maxWidth, constraints.maxHeight)
                                layout(constraints.maxWidth, constraints.maxHeight) {}
                            }
                        )

                        // --- THE AADHAAR STYLE REALISTIC UI ---
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Header
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 8.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text("GOVERNMENT OF INDIA", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 14.sp)
                            }

                            // Red Border Line
                            Box(modifier = Modifier.fillMaxWidth().height(3.dp).background(Color(0xFFE53935)))

                            // Body
                            Row(
                                modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 1. Photo Area
                                Box(
                                    modifier = Modifier
                                        .width(75.dp)
                                        .fillMaxHeight()
                                        .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFFF0F0F0)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // YAHAN APNI PIC REPLACE KAR SAKTE HAIN
                                    Image(
                                        painter = painterResource(id = android.R.drawable.ic_menu_camera),
                                        contentDescription = "Photo",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                // 2. Details Area
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                                    Text("VIKRAM SINGH", color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("DOB: 14/08/1995", color = Color.DarkGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("MALE", color = Color.DarkGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }

                                // 3. 🔥 QR CODE SCANNER (Right Side)
                                Icon(
                                    imageVector = Icons.Rounded.QrCode2,
                                    contentDescription = "Scanner",
                                    tint = Color.Black,
                                    modifier = Modifier.size(60.dp)
                                )
                            }

                            // Footer (Masked Aadhaar Number)
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "XXXX XXXX 1028", // Fake Masked Number
                                    color = Color.Black,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 4.sp
                                )
                            }
                        }

                        // Holographic 3D Glare
                        if (isTouched) {
                            Box(
                                modifier = Modifier.fillMaxSize().background(
                                    Brush.radialGradient(
                                        colors = listOf(Color.White.copy(alpha = 0.8f), HologramGlare.copy(alpha = 0.4f), Color.Transparent),
                                        center = Offset(touchX, touchY), radius = 600f
                                    )
                                )
                            )
                        }
                    }

                    // Close Overlay Button
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(24.dp)
                            .size(32.dp)
                            .clickable {
                                showCardPopup = false
                                state = ScanState.IDLE
                                hackerText = "HOLD TO AUTHENTICATE"
                            }
                    )
                }
            }
        }
    }
}
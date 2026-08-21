package com.ten2xcoding.holographic3dcard
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Contactless
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// --- Colors ---
val ScreenBackground = Color(0xFF060911)
val CardSurface = Color(0xFF0F1420)
val FoilCyan = Color(0xFF00F0FF)
val FoilPurple = Color(0xFFB026FF)
val FoilPink = Color(0xFFFF007A)
val FoilGold = Color(0xFFFFD700)
@Preview(showBackground = true)
@Composable
fun Holographic3DCardPreview() {
    Holographic3DCardScreen()
}
@Composable
fun Holographic3DCardScreen() {
    // 3D Rotation States
    var rotationX by remember { mutableFloatStateOf(0f) }
    var rotationY by remember { mutableFloatStateOf(0f) }
    // Touch Position for Glare Effect
    var touchX by remember { mutableFloatStateOf(0f) }
    var touchY by remember { mutableFloatStateOf(0f) }
    var isTouched by remember { mutableStateOf(false) }
    // Smooth Spring Physics
    val animatedRotationX by animateFloatAsState(
        targetValue = rotationX,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "rotationX"
    )
    val animatedRotationY by animateFloatAsState(
        targetValue = rotationY,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "rotationY"
    )
    var cardSize by remember { mutableStateOf(IntSize.Zero) }
    // --- FULL SCREEN (Center Aligned + Cyberpunk Ambient Background) ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground),
        contentAlignment = Alignment.Center
    ) {
        // 1. Ambient Background Light Orbs (Glow effect behind the card)
        Box(
            modifier = Modifier
                .size(320.dp)
                .offset(x = (-60).dp, y = (-40).dp)
                .blur(90.dp)
                .background(FoilCyan.copy(alpha = 0.18f), shape = CircleShape)
        )
        Box(
            modifier = Modifier
                .size(320.dp)
                .offset(x = 60.dp, y = 60.dp)
                .blur(90.dp)
                .background(FoilPurple.copy(alpha = 0.18f), shape = CircleShape)
        )
        // 2. Subtle Cyberpunk Grid Background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val step = 40.dp.toPx()
            for (x in 0..size.width.toInt() step step.toInt()) {
                drawLine(
                    color = Color.White.copy(alpha = 0.03f),
                    start = Offset(x.toFloat(), 0f),
                    end = Offset(x.toFloat(), size.height),
                    strokeWidth = 1f
                )
            }
            for (y in 0..size.height.toInt() step step.toInt()) {
                drawLine(
                    color = Color.White.copy(alpha = 0.03f),
                    start = Offset(0f, y.toFloat()),
                    end = Offset(size.width, y.toFloat()),
                    strokeWidth = 1f
                )
            }
        }
        // --- 3. THE 3D HOLOGRAPHIC CARD (Centered) ---
        Box(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .aspectRatio(1.586f) // Standard Card Ratio
                // 3D Matrix & Depth Layer
                .graphicsLayer {
                    this.rotationX = animatedRotationX
                    this.rotationY = animatedRotationY
                    this.cameraDistance = 14f * density
                    this.shadowElevation = 30.dp.toPx()
                }
                // Interactive Touch Tracking
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            isTouched = true
                            touchX = offset.x
                            touchY = offset.y
                        },
                        onDragEnd = {
                            isTouched = false
                            rotationX = 0f
                            rotationY = 0f
                        },
                        onDragCancel = {
                            isTouched = false
                            rotationX = 0f
                            rotationY = 0f
                        },
                        onDrag = { change, _ ->
                            touchX = change.position.x
                            touchY = change.position.y
                            val xOffset = (touchX - (cardSize.width / 2f)) / (cardSize.width / 2f)
                            val yOffset = (touchY - (cardSize.height / 2f)) / (cardSize.height / 2f)
                            rotationY = (xOffset * 22f).coerceIn(-25f, 25f)
                            rotationX = (-yOffset * 22f).coerceIn(-25f, 25f)
                        }
                    )
                }
                .clip(RoundedCornerShape(22.dp))
                .background(CardSurface)
                .border(
                    width = 1.2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(FoilCyan.copy(alpha = 0.8f), FoilPink.copy(alpha = 0.8f), FoilGold.copy(alpha = 0.5f))
                    ),
                    shape = RoundedCornerShape(22.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Measure Size for Math
            Layout(
                content = {},
                measurePolicy = { _, constraints ->
                    cardSize = IntSize(constraints.maxWidth, constraints.maxHeight)
                    layout(constraints.maxWidth, constraints.maxHeight) {}
                }
            )
            // --- Card Front Content ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(22.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Row: Brand + Chip + NFC
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TEN2X CODING",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        letterSpacing = 2.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Contactless,
                            contentDescription = "Contactless",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        // Gold EMV Chip
                        Box(
                            modifier = Modifier
                                .size(38.dp, 28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Brush.linearGradient(listOf(FoilGold, Color(0xFFB8860B))))
                                .border(0.5.dp, Color.Black.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        )
                    }
                }
                // Middle: Card Number
                Text(
                    text = "5392  ••••  ••••  1028",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 3.sp
                )
                // Bottom Row: Holder Name & Expiry
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(text = "CARD HOLDER", color = FoilCyan, fontSize = 9.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "CREATOR MATRIX", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "EXPIRES", color = FoilCyan, fontSize = 9.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "08/29", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
            // --- 4. THE MULTI-COLOR HOLOGRAPHIC FOIL SHIMMER ---
            if (isTouched) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.4f),     // Bright flare center
                                    FoilCyan.copy(alpha = 0.25f),       // Cyan sheen
                                    FoilPink.copy(alpha = 0.2f),        // Pink reflection
                                    FoilPurple.copy(alpha = 0.15f),     // Purple aura
                                    Color.Transparent
                                ),
                                center = Offset(touchX, touchY),
                                radius = 550f
                            )
                        )
                )
            }
        }
        // Bottom Hint Text
        Text(
            text = "✨ DRAG TO TILT IN 3D",
            color = Color.White.copy(alpha = 0.35f),
            fontSize = 11.sp,
            letterSpacing = 2.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
        )
    }
}
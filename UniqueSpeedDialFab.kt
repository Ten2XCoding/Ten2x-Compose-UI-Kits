package com.ten2xcoding.ui.theme.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PremiumSpeedDialFab() {
    var isExpanded by remember { mutableStateOf(false) }

    // Bouncy Rotation: 0 se 135 degrees (+ becomes X)
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 135f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "rotationAnimation"
    )

    // Gradient for main FAB
    val fabGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF6A11CB), Color(0xFF2575FC))
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Blur/Dark Backdrop
        if (isExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { isExpanded = false }
            )
        }

        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            // Action Buttons with Spring Animation
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + scaleIn(animationSpec = spring(dampingRatio = 0.6f, stiffness = 200f)),
                exit = fadeOut() + scaleOut()
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    PremiumActionItem(icon = Icons.Rounded.CameraAlt, label = "Camera", color = Color(0xFFFF4B4B))
                    Spacer(modifier = Modifier.height(16.dp))
                    PremiumActionItem(icon = Icons.Rounded.Share, label = "Share Post", color = Color(0xFF00C6FF))
                    Spacer(modifier = Modifier.height(16.dp))
                    PremiumActionItem(icon = Icons.Rounded.Edit, label = "Write Story", color = Color(0xFFFFA700))
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Main Gradient FAB
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(fabGradient)
                    .clickable { isExpanded = !isExpanded }
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Menu",
                    tint = Color.White,
                    modifier = Modifier
                        .rotate(rotationAngle) // Rotates to X
                )
            }
        }
    }
}

@Composable
private fun PremiumActionItem(icon: ImageVector, label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Glassmorphic Label
        Box(
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Text(
                text = label,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.width(16.dp))

        // Mini FAB with specific color
        SmallFloatingActionButton(
            onClick = { },
            containerColor = color,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(imageVector = icon, contentDescription = label)
        }
    }
}

// Video ke liye Awesome Background Preview
@Preview(showBackground = true)
@Composable
fun PreviewPremiumFab() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF3F4F6))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Ten2XCoding",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1F2937)
                )
                Spacer(modifier = Modifier.height(20.dp))
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(Color.White, RoundedCornerShape(16.dp))
                    )
                }
            }
            PremiumSpeedDialFab()
        }
    }
}
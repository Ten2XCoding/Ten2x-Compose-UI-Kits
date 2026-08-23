package com.ten2xcoding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

// --- Colors ---
val ScreenBg = Color(0xFF090C10)
val NavBarBg = Color(0xFF161B22)
val NeonAccent = Color(0xFF00E5FF)
val UnselectedColor = Color(0xFF7A8490)

// --- Nav Data ---
data class NavItem(val title: String, val icon: ImageVector)
val navItems = listOf(
    NavItem("Home", Icons.Rounded.Home),
    NavItem("Chat", Icons.Rounded.Email),
    NavItem("Profile", Icons.Rounded.Person),
    NavItem("Config", Icons.Rounded.Settings)
)

@Preview(showBackground = true)
@Composable
fun AnimatedBottomNavScreen() {
    var selectedIndex by remember { mutableStateOf(0) }
    var previousIndex by remember { mutableStateOf(0) }

    // Box to hold everything, using systemBarsPadding for edge-to-edge support
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {
        // --- 🔥 1. FIXED TOP BRANDING (Ten2X Tech) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding() // Pushes text safely below the battery/wifi icons
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "TEN2X TECH",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp
            )
        }

        // --- Dummy Screen Content ---
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = navItems[selectedIndex].title.uppercase(),
                color = NeonAccent,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp
            )
        }

        // --- THE FLOATING JELLY BOTTOM NAV ---
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                // navigationBarsPadding pushes it above the home swipe line on modern Androids
                .navigationBarsPadding()
                .padding(bottom = 24.dp, start = 24.dp, end = 24.dp)
                .fillMaxWidth()
                .height(76.dp)
                .shadow(24.dp, RoundedCornerShape(38.dp), spotColor = NeonAccent.copy(alpha = 0.2f))
                .clip(RoundedCornerShape(38.dp))
                .background(NavBarBg)
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val itemWidth = maxWidth / navItems.size
                val baseIndicatorWidth = 56.dp

                // Jelly Math
                val targetOffsetX = (itemWidth * selectedIndex) + (itemWidth / 2) - (baseIndicatorWidth / 2)
                val distance = abs(selectedIndex - previousIndex)
                val isMoving = selectedIndex != previousIndex
                val targetWidth = if (isMoving) baseIndicatorWidth + (itemWidth * distance * 0.6f) else baseIndicatorWidth

                val animatedOffsetX by animateDpAsState(
                    targetValue = targetOffsetX,
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow),
                    label = "indicatorOffset",
                    finishedListener = { previousIndex = selectedIndex }
                )

                val animatedWidth by animateDpAsState(
                    targetValue = targetWidth,
                    animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMediumLow),
                    label = "indicatorWidth"
                )

                // THE JELLY INDICATOR
                Box(
                    modifier = Modifier
                        .offset(x = animatedOffsetX)
                        .align(Alignment.CenterStart)
                        .width(animatedWidth)
                        .height(56.dp)
                        .shadow(16.dp, CircleShape, spotColor = NeonAccent)
                        .clip(RoundedCornerShape(28.dp))
                        .background(NeonAccent.copy(alpha = 0.18f))
                )

                // THE ICONS ROW
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    navItems.forEachIndexed { index, item ->
                        val isSelected = selectedIndex == index
                        LiquidNavItem(
                            item = item,
                            isSelected = isSelected,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                if (!isSelected) {
                                    previousIndex = selectedIndex
                                    selectedIndex = index
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LiquidNavItem(
    item: NavItem,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    // Animations
    val yOffset by animateDpAsState(
        targetValue = if (isSelected) (-20).dp else 0.dp,
        animationSpec = spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessLow),
        label = "yOffset"
    )

    // 🔥 IMPROVEMENT 1: Spin Physics
    val rotation by animateFloatAsState(
        targetValue = if (isSelected) 0f else -25f, // Tilts when going down, straightens when jumping up
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow),
        label = "rotation"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) NeonAccent else UnselectedColor,
        animationSpec = tween(300),
        label = "color"
    )
    val iconScale by animateFloatAsState(
        targetValue = if (isSelected) 1.3f else 1f,
        animationSpec = spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    val contentAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0f,
        animationSpec = tween(300, delayMillis = if (isSelected) 100 else 0),
        label = "alpha"
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (!isSelected) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                }
            },
        contentAlignment = Alignment.Center
    ) {

        if (isSelected) {
            Box(
                modifier = Modifier
                    .offset(y = yOffset)
                    .size(40.dp)
                    .shadow(20.dp, CircleShape, spotColor = NeonAccent)
                    .clip(CircleShape)
                    .background(ScreenBg)
            )
        }

        // The Icon (Now with rotation)
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = iconColor,
            modifier = Modifier
                .offset(y = yOffset)
                .size(28.dp)
                .scale(iconScale)
                .rotate(rotation) // Applies the tilt
        )

        // Title and Glowing Dot
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 6.dp)
                .alpha(contentAlpha)
        ) {
            Text(
                text = item.title,
                color = NeonAccent,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            // 🔥 IMPROVEMENT 2: The Neon Core Dot
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .shadow(8.dp, CircleShape, spotColor = NeonAccent)
                    .clip(CircleShape)
                    .background(NeonAccent)
            )
        }
    }
}
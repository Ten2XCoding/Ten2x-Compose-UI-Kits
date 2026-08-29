package com.ten2xcoding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- Colors ---
val ScreenBackground = Color(0xFF0F172A)
val CardHeaderBg = Color(0xFF1E293B)
val FoldedPaperBg = Color(0xFFF8FAFC)
val AccentGreen = Color(0xFF10B981)

// --- Data Model ---
data class OrderData(
    val title: String,
    val store: String,
    val price: String,
    val txId: String,
    val payment: String,
    val tax: String,
    val total: String
)

val orderList = listOf(
    OrderData("MacBook Pro M3", "Apple Store • Today", "$1,999", "#APL-882910", "Apple Pay (••• 4092)", "$140.00", "$2,139.00"),
    OrderData("Sony WH-1000XM5", "Amazon • Yesterday", "$348", "#AMZ-992102", "Visa (••• 1234)", "$24.00", "$372.00"),
    OrderData("Nike Air Max", "Nike Store • 3 days ago", "$150", "#NKE-772199", "Mastercard (••• 9876)", "$12.00", "$162.00")
)

@Preview(showBackground = true)
@Composable
fun OrigamiCardScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .systemBarsPadding()
    ) {
        // --- APP BAR ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TEN2X TECH",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp
            )
        }

        Text(
            text = "ORDER HISTORY",
            color = Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )

        // --- SCROLLABLE LIST OF CARDS ---
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(orderList) { order ->
                OrigamiTransactionCard(order)
            }
        }
    }
}

@Composable
fun OrigamiTransactionCard(order: OrderData) {
    var isExpanded by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow),
        label = "arrow"
    )

    val foldRotation by animateFloatAsState(
        targetValue = if (isExpanded) 0f else -90f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow),
        label = "foldRotation"
    )

    val lightingAlpha by animateFloatAsState(
        targetValue = if (isExpanded) 0f else 0.6f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow),
        label = "lighting"
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(CardHeaderBg)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    isExpanded = !isExpanded
                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                }
                .padding(20.dp)
                .graphicsLayer { shadowElevation = 10f }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF334155)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Receipt, contentDescription = "Receipt", tint = Color.White)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(order.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(order.store, color = Color.Gray, fontSize = 12.sp)
                }

                Text(order.price, color = AccentGreen, fontWeight = FontWeight.Black, fontSize = 18.sp)

                Spacer(modifier = Modifier.width(12.dp))

                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = "Expand",
                    tint = Color.Gray,
                    modifier = Modifier.rotate(arrowRotation)
                )
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow)),
            exit = shrinkVertically(animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .graphicsLayer {
                        transformOrigin = TransformOrigin(0.5f, 0f)
                        rotationX = foldRotation
                        cameraDistance = 16f * density
                    }
                    .shadow(8.dp, RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                    .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                    .background(FoldedPaperBg)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text("TRANSACTION DETAILS", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    ReceiptRow("Transaction ID", order.txId)
                    Spacer(modifier = Modifier.height(8.dp))
                    ReceiptRow("Payment Method", order.payment)
                    Spacer(modifier = Modifier.height(8.dp))
                    ReceiptRow("Tax & Fees", order.tax)

                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE2E8F0)))
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("TOTAL", color = Color(0xFF1E293B), fontWeight = FontWeight.Black, fontSize = 14.sp)
                        Text(order.total, color = AccentGreen, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                }

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = lightingAlpha))
                )
            }
        }
    }
}

@Composable
fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 13.sp)
        Text(text = value, color = Color(0xFF334155), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}
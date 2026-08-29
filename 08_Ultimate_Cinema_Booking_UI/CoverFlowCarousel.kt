package com.ten2xcoding
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ConfirmationNumber
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
// --- Movie Data ---
data class Movie(val title: String, val genre: String, val posterUrl: String, val price: Int = 15)
val movieList = listOf(
    Movie("DUNE: PART 2", "Sci-Fi / Action", "https://i.pinimg.com/originals/f0/a8/56/f0a85672d9e818baae8d257b31f4c6fc.jpg"),
    Movie("OPPENHEIMER", "Drama / History", "https://image.tmdb.org/t/p/w500/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg"),
    Movie("SPIDER-MAN", "Action / Animation", "https://image.tmdb.org/t/p/w500/8Vt6mWEReuy4Of61Lnj5Xj704m8.jpg"),
    Movie("JOHN WICK 4", "Action / Thriller", "https://image.tmdb.org/t/p/w500/vZloFAK7NmvMGKE7VkF5UHaz0I.jpg"),
    Movie("AVATAR 2", "Sci-Fi / Adventure", "https://i.pinimg.com/736x/91/1a/2d/911a2db55ff3a1faa44a7e766b9a1b3e.jpg")
)
val DarkBackground = Color(0xFF090C10)
val NeonCyan = Color(0xFF00E5FF)
val SuccessGreen = Color(0xFF00E676)
enum class PaymentState { IDLE, PROCESSING, SUCCESS, TICKET }
// 🔥 FEATURE 3: Custom Ticket Shape with Side Cutouts
class TicketShape(private val cornerRadius: Float, private val cutoutRadius: Float) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path().apply {
            val cutoutY = size.height * 0.65f // Position of the cutouts
            moveTo(cornerRadius, 0f)
            lineTo(size.width - cornerRadius, 0f)
            arcTo(Rect(size.width - 2 * cornerRadius, 0f, size.width, 2 * cornerRadius), -90f, 90f, false)
            // Right Cutout
            lineTo(size.width, cutoutY - cutoutRadius)
            arcTo(Rect(size.width - cutoutRadius, cutoutY - cutoutRadius, size.width + cutoutRadius, cutoutY + cutoutRadius), -90f, -180f, false)
            lineTo(size.width, size.height - cornerRadius)
            arcTo(Rect(size.width - 2 * cornerRadius, size.height - 2 * cornerRadius, size.width, size.height), 0f, 90f, false)
            lineTo(cornerRadius, size.height)
            arcTo(Rect(0f, size.height - 2 * cornerRadius, 2 * cornerRadius, size.height), 90f, 90f, false)
            // Left Cutout
            lineTo(0f, cutoutY + cutoutRadius)
            arcTo(Rect(-cutoutRadius, cutoutY - cutoutRadius, cutoutRadius, cutoutY + cutoutRadius), 90f, -180f, false)
            lineTo(0f, cornerRadius)
            arcTo(Rect(0f, 0f, 2 * cornerRadius, 2 * cornerRadius), 180f, 90f, false)
            close()
        }
        return Outline.Generic(path)
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = true)
@Composable
fun CoverFlowCarouselScreen() {
    var selectedMovie by remember { mutableStateOf<Movie?>(null) }
    val haptic = LocalHapticFeedback.current
    val pagerState = rememberPagerState(pageCount = { movieList.size })
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // 🔥 FEATURE 1: Dynamic Blurred Background
        AsyncImage(model = movieList[pagerState.currentPage].posterUrl, contentDescription = "Blurred Background", contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().blur(80.dp) // Apple Music style extreme blur
                .graphicsLayer { alpha = 0.5f } // Dim the background
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().systemBarsPadding()
        ) {
            Text(text = "NOW SHOWING",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
            Spacer(modifier = Modifier.height(40.dp))
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(horizontal = 70.dp)
            ) { page -> val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                val absoluteOffset = pageOffset.absoluteValue
                val scale = 1f - (absoluteOffset * 0.15f).coerceIn(0f, 0.3f)
                val alpha = 1f - (absoluteOffset * 0.4f).coerceIn(0f, 0.7f)
                val rotationY = pageOffset * 25f
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .graphicsLayer {
                            scaleX = scale; scaleY = scale
                            this.alpha = alpha; this.rotationY = rotationY
                            cameraDistance = 12f * density
                        }
                        .shadow(if (absoluteOffset < 0.5f) 32.dp else 8.dp, RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.BottomStart
                ) {
                    AsyncImage(
                        model = movieList[page].posterUrl,
                        contentDescription = "Movie Poster",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f)),
                                    startY = 300f
                                )
                            )
                    )
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(movieList[page].title, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(movieList[page].genre, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color.White)
                                .clickable {
                                    haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                    selectedMovie = movieList[page]
                                }
                                .padding(horizontal = 24.dp, vertical = 10.dp)
                        ) {
                            Text("BOOK TICKETS", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = selectedMovie != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            selectedMovie?.let { movie ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f))
                        .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {},
                    contentAlignment = Alignment.BottomCenter
                ) {
                    BookingSheetContent(movie) { selectedMovie = null }
                }
            }
        }
    }
}
@Composable
fun BookingSheetContent(movie: Movie, onClose: () -> Unit) {
    var selectedTime by remember { mutableStateOf("07:30 PM") }
    val times = listOf("10:00 AM", "02:15 PM", "07:30 PM", "10:45 PM")
    // 🔥 FEATURE 2: Real Seat Selection Engine
    var selectedSeats by remember { mutableStateOf(setOf<String>()) }
    val ticketCount = selectedSeats.size
    var paymentState by remember { mutableStateOf(PaymentState.IDLE) }
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(Color(0xFF161B22))
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {}
            .padding(24.dp)
            .animateContentSize(animationSpec = tween(400))
    ) {
        AnimatedContent(targetState = paymentState, label = "paymentFlow") { state ->
            when (state) {
                PaymentState.IDLE -> {
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(movie.title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Black)
                            Icon(Icons.Rounded.ConfirmationNumber, contentDescription = "Close", tint = Color.Gray, modifier = Modifier.clickable { onClose() })
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        // Time Selection
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            times.forEach { time ->
                                val isSelected = selectedTime == time
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) NeonCyan else Color(0xFF21262D))
                                        .clickable { selectedTime = time; haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(time, color = if (isSelected) Color.Black else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        // 🔥 Seat Map Grid
                        Text("Select Seats", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Cinema Screen Curve
                            Box(modifier = Modifier.width(150.dp).height(4.dp).background(NeonCyan.copy(alpha = 0.5f), CircleShape))
                            Spacer(modifier = Modifier.height(16.dp))
                            // Generate 4 Rows, 6 Columns of seats
                            for (row in 0..3) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 8.dp)) {
                                    for (col in 1..6) {
                                        val seatId = "${'A' + row}$col"
                                        val isSelected = selectedSeats.contains(seatId)
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (isSelected) NeonCyan else Color(0xFF21262D))
                                                .clickable {
                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                    selectedSeats = if (isSelected) selectedSeats - seatId else selectedSeats + seatId
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(seatId, color = if (isSelected) Color.Black else Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                        // Checkout Button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (ticketCount > 0) NeonCyan else Color.DarkGray)
                                .clickable(enabled = ticketCount > 0) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    paymentState = PaymentState.PROCESSING
                                    scope.launch {
                                        delay(1500)
                                        paymentState = PaymentState.SUCCESS
                                        haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                        delay(1000)
                                        paymentState = PaymentState.TICKET
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    }
                                }
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (ticketCount > 0) "PAY $${movie.price * ticketCount}.00" else "SELECT A SEAT",
                                color = if (ticketCount > 0) Color.Black else Color.Gray,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
                PaymentState.PROCESSING -> {
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = NeonCyan)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Processing Payment...", color = Color.Gray, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
                PaymentState.SUCCESS -> {
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Rounded.CheckCircle, contentDescription = "Success", tint = SuccessGreen, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Payment Successful!", color = SuccessGreen, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    }
                }
                PaymentState.TICKET -> {
                    // 🔥 FEATURE 3: Real Cutout Ticket Rendering
                    val ticketShape = TicketShape(cornerRadius = 40f, cutoutRadius = 30f)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(ticketShape)
                            .background(Color.White)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text("CINEMA E-TICKET", color = Color.Gray, fontSize = 12.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(movie.title, color = Color.Black, fontSize = 24.sp, fontWeight = FontWeight.Black)
                            Text("Standard 2D • INOX Cinemas", color = Color.DarkGray, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text("DATE & TIME", color = Color.Gray, fontSize = 10.sp)
                                    Text("Today, $selectedTime", color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("SEATS ($ticketCount)", color = Color.Gray, fontSize = 10.sp)
                                    Text(selectedSeats.joinToString(", "), color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        // Dashed Cutout Line aligned perfectly with the physical cutouts
                        Canvas(modifier = Modifier.fillMaxWidth().height(1.dp).padding(horizontal = 20.dp)) {
                            drawLine(
                                color = Color.LightGray,
                                start = androidx.compose.ui.geometry.Offset(0f, 0f),
                                end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF8FAFC))
                                .padding(24.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("TOTAL PAID", color = Color.Gray, fontSize = 10.sp)
                                Text("$${movie.price * ticketCount}.00", color = SuccessGreen, fontSize = 20.sp, fontWeight = FontWeight.Black)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                repeat(10) { index ->
                                    val width = if (index % 3 == 0) 4.dp else 2.dp
                                    Box(modifier = Modifier.width(width).height(30.dp).background(Color.Black))
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black)
                                .clickable { onClose() }
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("DONE", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
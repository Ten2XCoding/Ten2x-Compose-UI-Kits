package com.ten2xcoding

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ten2xcoding.ui.theme.Ten2XCodingTheme
import com.ten2xcoding.ui.theme.screen.PremiumSpeedDialFab

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Asli mobile screen par jo dikhega wo yahan aata hai 👇
        setContent {
            Ten2XCodingTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Fake Dashboard aur FAB ko screen par render kar rahe hain
                    MainAppScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// Yeh function aapke mobile par ek mast background aur FAB show karega
@Composable
fun MainAppScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF3F4F6)) // Light Grey Background
    ) {
        // Fake Dashboard UI (Video record karne ke liye)
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Ten2XCoding",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1F2937)
            )
            Spacer(modifier = Modifier.height(20.dp))

            // 4 Fake Cards loop se bana diye
            repeat(4) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Color.White, RoundedCornerShape(16.dp))
                )
            }
        }

        // Aapka FAB (Agar aapne Premium wala paste kiya hai toh 'PremiumSpeedDialFab()' likhein)
        PremiumSpeedDialFab()
    }
}
package com.ten2xcoding // Apna package name match kar lena

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.ten2xcoding.holographic3dcard.Holographic3DCardScreen
import com.ten2xcoding.ui.theme.Ten2XCodingTheme


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Ten2XCodingTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "Ten2X Coding",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color(0xFF0B0F19) // Dark Cyberpunk Background
                            )
                        )
                    }
                ) { innerPadding ->

                    Box(modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                    ) {

                        Holographic3DCardScreen()

                    }
                }
            }
        }
    }
}
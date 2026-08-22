package com.ten2xcoding // Yahan apna exact package name daal lena

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.ten2xcoding.holographic3dcard.Holographic3DCardScreen
import com.ten2xcoding.ui.theme.Ten2XCodingTheme // Yahan apni theme ka import check kar lena

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Ten2XCodingTheme {
                // Surface se poori screen cover ho jayegi
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 🔥 CALLING OUR GOD-TIER UI HERE
                    CyberpunkOverlayApp()
                }
            }
        }
    }
}
package com.example.bookproject1.app

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.bookproject1.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val splashAlpha = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        // بقاء شاشة البداية ظاهرة بوضوح
        delay(800)

        // تلاشي الشاشة بالكامل (الخلفية والصورة معاً) لتكشف الشاشة الرئيسية من تحتها
        splashAlpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )

        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = splashAlpha.value } // 👈 الشاشة تذوب بنعومة
            .background(Color(0xFF44281A)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash_logo),
            contentDescription = "شعار كتاب المصابيح",
            modifier = Modifier.fillMaxSize()
        )
    }
}
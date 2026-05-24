package com.example.bikerent.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun BikeTransitionOverlay(visible: Boolean) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(80)),
        exit  = fadeOut(tween(180))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.96f)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(80.dp)) {
                    Icon(
                        imageVector = Icons.Filled.DirectionsBike,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.fillMaxSize()
                    )
                    SpinningWheelsOverlay(modifier = Modifier.fillMaxSize())
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "BikeRent",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }
        }
    }
}

@Composable
private fun SpinningWheelsOverlay(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "wheels")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wheelAngle"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        // Exact wheel centers from Icons.Filled.DirectionsBike (24x24 viewport)
        // Rear wheel center: (5, 17), Front wheel center: (19, 17)
        val rearCenter  = Offset(w * 5f  / 24f, h * 17f / 24f)
        val frontCenter = Offset(w * 19f / 24f, h * 17f / 24f)
        val outerRadius = w * 5f / 24f
        val spokeWidth  = outerRadius * 0.13f
        val green = Color(0xFF2E7D32)

        for (center in listOf(rearCenter, frontCenter)) {
            repeat(6) { i ->
                val angle = Math.toRadians((rotation + i * 60.0))
                val start = Offset(
                    center.x + outerRadius * 0.13f * cos(angle).toFloat(),
                    center.y + outerRadius * 0.13f * sin(angle).toFloat()
                )
                val end = Offset(
                    center.x + outerRadius * 0.76f * cos(angle).toFloat(),
                    center.y + outerRadius * 0.76f * sin(angle).toFloat()
                )
                drawLine(green.copy(alpha = 0.75f), start, end, spokeWidth, StrokeCap.Round)
            }
        }
    }
}

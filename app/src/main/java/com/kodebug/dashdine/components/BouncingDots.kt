package com.kodebug.dashdine.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BouncingDots(
    color: Color = Color.White,
    dotSize: Dp = 8.dp,
    spaceBetween: Dp = 6.dp,
    travelDistance: Dp = 10.dp
) {
    val infiniteTransition = rememberInfiniteTransition()

    val animations = listOf(
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = travelDistance.value,
            animationSpec = infiniteRepeatable(
                animation = tween(400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
                initialStartOffset = StartOffset(0)
            )
        ),
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = travelDistance.value,
            animationSpec = infiniteRepeatable(
                animation = tween(400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
                initialStartOffset = StartOffset(100)
            )
        ),
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = travelDistance.value,
            animationSpec = infiniteRepeatable(
                animation = tween(400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
                initialStartOffset = StartOffset(200)
            )
        ),
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = travelDistance.value,
            animationSpec = infiniteRepeatable(
                animation = tween(400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
                initialStartOffset = StartOffset(300)
            )
        ),
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = travelDistance.value,
            animationSpec = infiniteRepeatable(
                animation = tween(400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
                initialStartOffset = StartOffset(400)
            )
        ),

    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(spaceBetween),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.wrapContentWidth()
    ) {
        animations.forEach { anim ->
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .offset(y = (-anim.value).dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}
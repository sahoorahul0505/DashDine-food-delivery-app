package com.kodebug.dashdine.components

import androidx.compose.foundation.Indication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kodebug.dashdine.ui.theme.Orange

@Composable
fun <T> ItemCardMini(
    item: T,
    onClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(32.dp),
    containerColor: Color = Color.White,
    shadowCornerRadius: Dp = 32.dp,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    shadowColor: Color = Color.Gray.copy(alpha = .4f),
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .drawBehind {
                val shadowColor = shadowColor
                val cornerRadius = shadowCornerRadius.toPx()
                val blurRadius = 90f
                val offsetX = 0.dp.toPx()
                val offsetY = 28.dp.toPx()

                val paint = Paint().asFrameworkPaint().apply {
                    isAntiAlias = true
                    color = android.graphics.Color.TRANSPARENT
                    setShadowLayer(
                        blurRadius,
                        offsetX,
                        offsetY,
                        shadowColor.toArgb()
                    )
                }

                drawIntoCanvas {
                    it.nativeCanvas.drawRoundRect(
                        40f,
                        0f,
                        size.width -40,
                        size.height,
                        cornerRadius,
                        cornerRadius,
                        paint
                    )
                }
            }
    ) {
        Card(
            modifier = Modifier
                .clip(shape = shape)
                .clickable( onClick = { onClick(item) }),
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = containerColor),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(
                modifier = Modifier.padding(contentPadding),
                content = content
            )
        }
    }
}

@Composable
fun <T> ItemCardLarge(
    item: T,
    onClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(12.dp),
    containerColor: Color = Color.White,
    shadowCornerRadius: Dp = 14.dp,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    shadowColor: Color = Color.Gray.copy(alpha = .4f),
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .drawBehind {
                val shadowColor = shadowColor
                val cornerRadius = shadowCornerRadius.toPx()
                val blurRadius = 50f
                val offsetX = 8.dp.toPx()
                val offsetY = 10.dp.toPx()

                val paint = Paint().asFrameworkPaint().apply {
                    isAntiAlias = true
                    color = android.graphics.Color.TRANSPARENT
                    setShadowLayer(
                        blurRadius,
                        offsetX,
                        offsetY,
                        shadowColor.toArgb()
                    )
                }

                drawIntoCanvas {
                    it.nativeCanvas.drawRoundRect(
                        0f,
                        0f,
                        size.width,
                        size.height,
                        cornerRadius,
                        cornerRadius,
                        paint
                    )
                }
            }
    ) {
        Card(
            modifier = Modifier
                .clip(shape = shape)
                .clickable( onClick = { onClick(item) }),
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = containerColor),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(
                modifier = Modifier.padding(contentPadding),
                content = content
            )
        }
    }
}

//@Preview(showBackground = true)
@Composable
private fun ItemButtonPreview() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ItemCardMini(
            item = null,
            onClick = {},
            shadowColor = Orange.copy(alpha = .5f),
            containerColor = Color.White.copy(alpha = .9f),
            shape = RoundedCornerShape(45.dp),
            modifier = Modifier
        ) {
            Column(
                modifier = Modifier
                    .height(98.dp)
                    .width(60.dp)
                    .padding(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(shape = CircleShape)
                        .background(color = Color.Red)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text("Pizza")
            }

        }

    }
}
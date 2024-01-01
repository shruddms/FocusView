package com.github.soof.focusview

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun FocusCompose(
    modifier: Modifier = Modifier,
    viewType: FocusViewType = FocusViewType.CIRCLE,
    cornerRadius: Float = 16f,
    backgroundColor: Color? = null,
    backgroundGradient: Brush = Brush.verticalGradient(
        listOf(Color.Black.copy(alpha = 0.7f), Color.Black)
    ),
    insideColor: Color = Color.Transparent,
    borderColor: Color = Color.Transparent,
    borderWidth: Float = 2f,
    focusWidthMultiplier: Float = 0.8f,
    focusHeightMultiplier: Float = 0.8f,
    focusContent: @Composable () -> Unit
) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            FocusComposeView(
                context = ctx,
                viewType = viewType,
                cornerRadius = cornerRadius,
                backgroundColor = backgroundColor,
                backgroundGradient = backgroundGradient,
                insideColor = insideColor,
                borderColor = borderColor,
                borderWidth = borderWidth,
                focusWidthMultiplier = focusWidthMultiplier,
                focusHeightMultiplier = focusHeightMultiplier,
                focusContent = focusContent
            )
        },
        update = { _ ->
            // Update the view here
        }
    )
}

enum class FocusViewType {
    CIRCLE, SQUARE, ROUNDED_SQUARE, TRIANGLE, RHOMBUS
}


package com.github.soof.focusview

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AbstractComposeView
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.unit.dp
import kotlin.math.min
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath

@SuppressLint("ViewConstructor")
internal class FocusComposeView(
    context: Context,
    private val viewType: FocusViewType = FocusViewType.CIRCLE,
    private val cornerRadius: Float = 16f,
    private val backgroundColor: Color? = null,
    private val backgroundGradient: Brush? = null,
    private val insideColor: Color = Color.Transparent,
    private val borderColor: Color = Color.White,
    private val borderWidth: Float = 2f,
    private val focusWidthMultiplier: Float = 0.8f,
    private val focusHeightMultiplier: Float = 0.8f,
    private val focusContent: @Composable () -> Unit
) : AbstractComposeView(context) {

    init {
        setViewTreeLifecycleOwner(findViewTreeLifecycleOwner())
    }

    @Composable
    override fun Content() {
        DrawFocusView()
        focusContent()
    }

    @Composable
    private fun DrawFocusView() {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val focusPath = createFocusPath()
            drawBackground(focusPath)
            drawFocusShape()
            drawBorder(focusPath)
        }
    }

    private fun DrawScope.createFocusPath(): Path {
        val path = Path()
        val focusWidth = size.width * focusWidthMultiplier
        val focusHeight = size.height * focusHeightMultiplier
        val topLeft = Offset(
            x = (size.width - focusWidth) / 2,
            y = (size.height - focusHeight) / 2
        )


        when (viewType) {
            FocusViewType.CIRCLE -> {
                path.addOval(Rect(topLeft, Size(focusWidth, focusHeight)))
            }
            FocusViewType.SQUARE, FocusViewType.ROUNDED_SQUARE -> {
                path.addRect(Rect(topLeft, Size(focusWidth, focusHeight)))
            }
            FocusViewType.TRIANGLE -> {
                path.moveTo(topLeft.x + focusWidth / 2, topLeft.y)
                path.lineTo(topLeft.x, topLeft.y + focusHeight)
                path.lineTo(topLeft.x + focusWidth, topLeft.y + focusHeight)
                path.close()
            }
            FocusViewType.RHOMBUS -> {
                path.moveTo(topLeft.x + focusWidth / 2, topLeft.y)
                path.lineTo(topLeft.x, topLeft.y + focusHeight / 2)
                path.lineTo(topLeft.x + focusWidth / 2, topLeft.y + focusHeight)
                path.lineTo(topLeft.x + focusWidth, topLeft.y + focusHeight / 2)
                path.close()
            }
        }
        return path
    }


    private fun DrawScope.drawBackground(focusPath: Path) {
        if (backgroundColor != null) {
            clipPath(path = focusPath, clipOp = ClipOp.Difference) {
                drawRect(color = backgroundColor, size = size)
            }
        } else if (backgroundGradient != null) {
            clipPath(path = focusPath, clipOp = ClipOp.Difference) {
                drawRect(brush = backgroundGradient, size = size)
            }
        }
    }

    private fun DrawScope.drawBorder(focusPath: Path) {
        drawPath(path = focusPath, color = borderColor, style = Stroke(width = borderWidth.dp.toPx()))
    }

    private fun DrawScope.drawFocusShape() {
        val focusWidth = size.width * focusWidthMultiplier
        val focusHeight = size.height * focusHeightMultiplier
        val topLeft = Offset(
            x = (size.width - focusWidth) / 2,
            y = (size.height - focusHeight) / 2
        )
        val cornerRadiusPx = cornerRadius.dp.toPx()

        when (viewType) {
            FocusViewType.CIRCLE -> {
                val diameter = min(focusWidth, focusHeight)
                drawCircle(
                    color = insideColor,
                    radius = diameter / 2,
                    center = Offset(topLeft.x + diameter / 2, topLeft.y + diameter / 2)
                )
            }
            FocusViewType.SQUARE, FocusViewType.ROUNDED_SQUARE -> {
                if (viewType == FocusViewType.ROUNDED_SQUARE) {
                    drawRoundRect(
                        color = insideColor,
                        size = Size(focusWidth, focusHeight),
                        cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx),
                        topLeft = topLeft
                    )
                } else {
                    drawRect(
                        color = insideColor,
                        size = Size(focusWidth, focusHeight),
                        topLeft = topLeft
                    )
                }
            }
            FocusViewType.TRIANGLE -> {
                drawTriangle(
                    color = insideColor,
                    size = Size(focusWidth, focusHeight),
                    topLeft = topLeft
                )
            }
            FocusViewType.RHOMBUS -> {
                drawRhombus(
                    color = insideColor,
                    size = Size(focusWidth, focusHeight),
                    topLeft = topLeft
                )
            }
        }
    }

    private fun DrawScope.drawTriangle(color: Color, size: Size, topLeft: Offset) {
        val path = Path().apply {
            moveTo(topLeft.x + size.width / 2, topLeft.y)
            lineTo(topLeft.x, topLeft.y + size.height)
            lineTo(topLeft.x + size.width, topLeft.y + size.height)
            close()
        }
        drawPath(path, color)
    }

    private fun DrawScope.drawRhombus(color: Color, size: Size, topLeft: Offset) {
        val path = Path().apply {
            moveTo(topLeft.x + size.width / 2, topLeft.y)
            lineTo(topLeft.x, topLeft.y + size.height / 2)
            lineTo(topLeft.x + size.width / 2, topLeft.y + size.height)
            lineTo(topLeft.x + size.width, topLeft.y + size.height / 2)
            close()
        }
        drawPath(path, color)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (shouldCreateCompositionOnAttachedToWindow) {
            createComposition()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        disposeComposition()
    }

    companion object {
        private var shouldCreateCompositionOnAttachedToWindow = true
    }
}

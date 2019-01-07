package com.intelliviz.quakereport

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.intelliviz.quakereport.HorizontalAxis.companion.HORIZONTAL_MARGIN_SP
import com.intelliviz.quakereport.HorizontalAxis.companion.PADDING_SP
import kotlin.math.roundToInt

class HorizontalAxis(context: Context, var horizontalProjection: Float, values: FloatArray, width: Float, var height: Float) {
    private var margin: Float = 0F
    private var axisInc: Int = 0
    private var minValue: Float
    private var maxValue: Float
    private var ticPaint: Paint
    private var padding: Float = 0F

    object companion {
        var HORIZONTAL_MARGIN_SP: Float = 30F
        var PADDING_SP: Float = 8F
    }

    init{
        ticPaint = Paint()
        ticPaint.color = Color.BLACK

        margin = spToPixel(context, HORIZONTAL_MARGIN_SP)
        padding = spToPixel(context, PADDING_SP)

        var min = values[0]
        var max = values[0]
        for(value in values) {
            if(value < min) {
                min = value
            }
            if(value > max) {
                max = value
            }
        }

        minValue = min
        maxValue = max

        val textWidth = getTextWidth(ticPaint, minValue.toString()) * 3
        val spTextWidth = spToPixel(context, textWidth)
        val slots = (width - 2 * margin) / spTextWidth
        val inc = (maxValue - minValue) / slots
        axisInc = inc.roundToInt()
        if(axisInc < inc) {
            axisInc++
        }
    }

    fun draw(canvas: Canvas?, context: Context) {

        val textSize = spToPixel(context, 16F)
        ticPaint.textSize = textSize
        val minYInt = minValue.toInt()
        val maxYInt = maxValue.toInt()
        for(mag in minYInt..maxYInt step axisInc) {
            drawAxisTic(canvas, mag.toFloat())
        }
    }

    private fun drawAxisTic(canvas: Canvas?, yValue: Float) {
        val pixelX = worldToPixelX(yValue)
        val textHeight = getTextHeight(ticPaint, yValue.toString())
        val textWidth = getTextWidth(ticPaint, yValue.toString())
        val ystart = height - padding
        canvas?.drawText(yValue.toString(), pixelX-textWidth/2, ystart, ticPaint)
        drawVerticalLine(canvas, pixelX, height - (padding + textHeight))
    }

    private fun spToPixel(context: Context, sp: Float): Float {
        val density = context.resources.displayMetrics.scaledDensity
        return sp * density
    }

    private fun getTextHeight(paint: Paint, text: String): Float {
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        return bounds.height().toFloat()
    }

    private fun getTextWidth(paint: Paint, text: String): Float {
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        return bounds.width().toFloat()
    }

    private fun worldToPixelX(x: Float): Float {
        return horizontalProjection * (x-minValue) + margin
    }

    private fun drawVerticalLine(canvas: Canvas?, x: Float, startY: Float) {
        canvas?.drawLine(x, startY, x, startY-margin, ticPaint)
    }
}
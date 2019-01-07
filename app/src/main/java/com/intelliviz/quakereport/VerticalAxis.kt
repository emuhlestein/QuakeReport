package com.intelliviz.quakereport

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.intelliviz.quakereport.VerticalAxis.companion.PADDING_SP
import com.intelliviz.quakereport.VerticalAxis.companion.VERTICAL_MARGIN_SP
import kotlin.math.roundToInt

class VerticalAxis(context: Context, var verticalProjection: Float, values: FloatArray, var height: Float) {
    private var margin: Float = 0F
    private var axisInc: Int = 0
    private var minValue: Float
    private var maxValue: Float
    private var ticPaint: Paint = Paint()
    private var padding: Float = 0F

    object companion {
        var VERTICAL_MARGIN_SP: Float = 50F
        var PADDING_SP: Float = 8F
    }

    init{
        ticPaint.color = Color.BLACK

        margin = spToPixel(context, VERTICAL_MARGIN_SP)
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

        val textHeight = getTextHeight(ticPaint, minValue.toString()) * 3
        val spTextHeight = spToPixel(context, textHeight)
        var slots = (height - 2 * margin) / spTextHeight
        var inc = (maxValue - minValue) / slots
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
        val pixelY = worldToPixelY(yValue)
        val textHeight = getTextHeight(ticPaint, yValue.toString())
        val textWidth = getTextWidth(ticPaint, yValue.toString())
        val xstart = padding
        canvas?.drawText(yValue.toString(), xstart, pixelY + textHeight / 2, ticPaint)
        drawHorizontalLine(canvas, textWidth+padding, pixelY)
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

    private fun worldToPixelY(y: Float): Float {
        return height - (verticalProjection * (y-minValue) + margin)
    }

    private fun drawHorizontalLine(canvas: Canvas?, startX: Float, y: Float) {
        canvas?.drawLine(startX, y, margin, y, ticPaint)
    }
}
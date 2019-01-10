package com.intelliviz.quakereport

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.TypedValue
import com.intelliviz.quakereport.HorizontalAxis.companion.HORIZONTAL_MARGIN_SP
import com.intelliviz.quakereport.HorizontalAxis.companion.PADDING_SP
import kotlin.math.ceil
import kotlin.math.roundToInt



class HorizontalAxis(context: Context, var horizontalProjection: Float, values: FloatArray, var width: Float, var height: Float) {
    private var margin: Int = 0
    private var axisInc: Int = 0
    private var minValue: Float
    private var maxValue: Float
    private var ticPaint: Paint = Paint()
    private var padding: Int = 0

    object companion {
        var HORIZONTAL_MARGIN_SP: Float = 30F
        var PADDING_SP: Float = 8F
    }

    init{
        val textSize = spToPixel(context, 16F)
        ticPaint.textSize = textSize.toFloat()
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

        val scaledSizeInPixels = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                16F,
                context.resources.displayMetrics)
        ticPaint.textSize = scaledSizeInPixels

        val spTextWidth = getTextWidth(ticPaint, minValue.toInt().toString()+1)
        val slots = ((width - 2 * margin) / spTextWidth)
        val islots = ceil(slots).toInt()
        val inc = (maxValue - minValue) / slots
        axisInc = inc.roundToInt()
        if(axisInc < inc) {
            axisInc++
        }
    }

    fun draw(canvas: Canvas?, context: Context) {

        val textSize = spToPixel(context, 16F)
        ticPaint.textSize = textSize.toFloat()
        val scaledSizeInPixels = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                16F,
                context.resources.displayMetrics)
        ticPaint.textSize = scaledSizeInPixels
        val minInt = minValue.toInt()
        val maxInt = maxValue.toInt()
        for(value in minInt..maxInt step axisInc) {
            drawAxisTic(canvas, value.toFloat())
        }

        val Width = width.toInt()
        for(value in 0..Width step 100) {
            drawVerticalLine(canvas, value.toFloat(), height - (padding + 50))
        }
    }

    private fun drawAxisTic(canvas: Canvas?, value: Float) {
        val pixelX = worldToPixelX(value)
        val textHeight = getTextHeight(ticPaint, value.toString())
        val textWidth = getTextWidth(ticPaint, value.toString())
        val ystart = height - padding
        canvas?.drawText(value.toInt().toString(), pixelX-textWidth/2, ystart, ticPaint)
        //drawVerticalLine(canvas, pixelX, height - (padding + textHeight))
    }

    private fun spToPixel(context: Context, sp: Float): Int {
        return (sp * context.resources.displayMetrics.scaledDensity).toInt()
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
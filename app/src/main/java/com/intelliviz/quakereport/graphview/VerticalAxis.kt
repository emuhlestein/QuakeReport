package com.intelliviz.quakereport.graphview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import kotlin.math.roundToInt


/**
 * Class to display a vertical axis. The min and max data values are determined and the increment to
 * evenly increment from min to max is calculated. There is a margin that extends from the edge
 * of the screen to the plotting area. In the margin is found the tic labels, the tic marks, and
 * the axis label.
 */
class VerticalAxis(context: Context, private var projection: VerticalProjection, var label: String, values: FloatArray, var height: Float) {
    private var margin: Int = 0
    private var axisInc: Int = 0
    private var minValue: Float
    private var maxValue: Float
    private var ticPaint: Paint = Paint()
    private var padding: Int = 0
    private var format = ""

    companion object {
        var MARGIN_SP: Float = 40F // the width of the margin from edge of screen to plotting area
        var PADDING_SP: Float = 20F
    }

    init{
        val textSize = spToPixel(context, 16F)
        ticPaint.textSize = textSize.toFloat()
        ticPaint.color = Color.BLACK

        margin = spToPixel(context, MARGIN_SP)
        padding = spToPixel(context, PADDING_SP)

        minValue = values.min() ?: 0F
        maxValue = values.max() ?: 0F

        val textHeight = getTextHeight(ticPaint, minValue.toString())
        val spTextHeight = spToPixel(context, textHeight)
        val slots = (height - 2 * margin) / spTextHeight
        val inc = (maxValue - minValue) / slots
        axisInc = inc.roundToInt()
        if(axisInc < inc) {
            axisInc++
        }

        var maxLen = 0
        values.forEach { value ->
            val str:String = "%1.0f".format(value)
            if(str.length > maxLen) {
                maxLen = str.length
            }
        }
        format = "%$maxLen.0f"
    }

    fun draw(canvas: Canvas?, context: Context) {

        val textSize = spToPixel(context, 16F)
        ticPaint.textSize = textSize.toFloat()
        val minInt = minValue.toInt()
        val maxInt = maxValue.toInt()
        for(value in minInt..maxInt step axisInc) {
            drawAxisTic(canvas, value.toFloat())
        }

        canvas?.save()
        canvas?.rotate(-90F, textSize.toFloat(), height / 2)
        canvas?.drawText(label, 0F, height / 2, ticPaint)
        canvas?.restore()
    }

    private fun drawAxisTic(canvas: Canvas?, value: Float) {
        val pixelY = worldToPixelY(value)
        val textHeight = getTextHeight(ticPaint, value.toString())
        val textWidth = getTextWidth(ticPaint, value.toString())
        val str:String = format.format(value)
        canvas?.drawText(str, padding.toFloat(), pixelY + textHeight / 2, ticPaint)
        drawTic(canvas, textWidth+padding, margin.toFloat(), pixelY)
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

    private fun worldToPixelY(y: Float): Float {
        return projection.worldToPixel(y)
    }

    private fun drawTic(canvas: Canvas?, startX: Float, endX: Float, y: Float) {
        canvas?.drawLine(startX, y, endX, y, ticPaint)
    }
}
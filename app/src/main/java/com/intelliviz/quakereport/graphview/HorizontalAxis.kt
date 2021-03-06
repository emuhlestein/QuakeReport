package com.intelliviz.quakereport.graphview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import android.util.TypedValue
import kotlin.math.roundToInt



class HorizontalAxis(context: Context, private var projection: HorizontalProjection, var label: String,
                     private var minValue: Float, private var maxValue: Float, values: FloatArray, var width: Float, var height: Float) {
    private var margin: Int = 0
    private var axisInc: Int = 0
    private var ticPaint: Paint = Paint()
    private var padding: Int = 0
    private var format = ""

    companion object {
        var MARGIN_SP: Float = 30F
        var PADDING_SP: Float = 20F
    }

    init{
        val textSize = spToPixel(context, 16F)
        ticPaint.textSize = textSize.toFloat()
        ticPaint.color = Color.BLACK

        margin = spToPixel(context, MARGIN_SP)
        padding = spToPixel(context, PADDING_SP)

        val scaledSizeInPixels = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                16F,
                context.resources.displayMetrics)
        ticPaint.textSize = scaledSizeInPixels

        val spTextWidth = getTextWidth(ticPaint, minValue.toInt().toString()+1)
        val slots = ((width - 2 * margin) / spTextWidth)
        val inc = (maxValue - minValue) / slots
        axisInc = inc.roundToInt()
        if(axisInc < 1) {
            Log.e("EDM", "Bad step value")
            axisInc = 1
        }
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

        val textWidth = getTextWidth(ticPaint, label)
        canvas?.drawText(label, width / 2 - textWidth/2, height - 12, ticPaint)
    }

    private fun drawAxisTic(canvas: Canvas?, value: Float) {
        val pixelX = worldToPixel(value)
        val textHeight = getTextHeight(ticPaint, value.toString())
        val textWidth = getTextWidth(ticPaint, value.toString())
        val ystart = height - padding
        val str:String = format.format(value)
        canvas?.drawText(str, pixelX-textWidth/2, ystart, ticPaint)
        drawTic(canvas, pixelX, height - (padding + textHeight), height - (padding + margin))
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

    private fun worldToPixel(x: Float): Float {
        return projection.worldToPixel(x)
    }

    private fun drawTic(canvas: Canvas?, x: Float, startY: Float, endY: Float) {
        canvas?.drawLine(x, startY, x, endY, ticPaint)
    }
}
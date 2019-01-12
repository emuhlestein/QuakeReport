package com.intelliviz.quakereport

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.intelliviz.quakereport.VerticalAxis.companion.HORIZONTAL_MARGIN_SP
import com.intelliviz.quakereport.VerticalAxis.companion.PADDING_SP
import com.intelliviz.quakereport.VerticalAxis.companion.VERTICAL_MARGIN_SP
import kotlin.math.roundToInt



class VerticalAxis(context: Context, var verticalProjection: Float, values: FloatArray, var height: Float) {
    private var margin: Int = 0
    private var axisInc: Int = 0
    private var minValue: Float
    private var maxValue: Float
    private var ticPaint: Paint = Paint()
    private var padding: Int = 0
    private var pxWidth: Int = 0
    private var labels: MutableList<String>
    private var format = ""

    object companion {
        var VERTICAL_MARGIN_SP: Float = 50F
        var HORIZONTAL_MARGIN_SP: Float = 50F
        var PADDING_SP: Float = 12F
    }

    init{
        val textSize = spToPixel(context, 16F)
        ticPaint.textSize = textSize.toFloat()
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

        val textHeight = getTextHeight(ticPaint, minValue.toString())
        val spTextHeight = spToPixel(context, textHeight)
        var slots = (height - 2 * margin) / spTextHeight
        var inc = (maxValue - minValue) / slots
        axisInc = inc.roundToInt()
        if(axisInc < inc) {
            axisInc++
        }

        labels = mutableListOf<String>()
        var maxLen = 0
        values.forEach { value ->
            val str:String = "%1.0f".format(value)
            if(str.length > maxLen) {
                maxLen = str.length
            }
        }
        format = "%${maxLen}.0f"
    }

    fun draw(canvas: Canvas?, context: Context) {

        val textSize = spToPixel(context, 16F)
        ticPaint.textSize = textSize.toFloat()
        val minInt = minValue.toInt()
        val maxInt = maxValue.toInt()
        for(value in minInt..maxInt step axisInc) {
            drawAxisTic(canvas, value.toFloat())
        }
        var label = "Veritcal Label"
        val length =  getTextHeight(ticPaint, label)
        canvas?.save()
        canvas?.rotate(-90F, 48F, height / 2)
        canvas?.drawText(label, -2*HORIZONTAL_MARGIN_SP, height / 2, ticPaint)
        canvas?.restore()
    }

    private fun drawAxisTic(canvas: Canvas?, value: Float) {
        val pixelY = worldToPixelY(value)
        val textHeight = getTextHeight(ticPaint, value.toString())
        val textWidth = getTextWidth(ticPaint, value.toString())
        val xstart = padding
        val str:String = "%3.0f".format(value)
        canvas?.drawText(str, xstart.toFloat(), pixelY + textHeight / 2, ticPaint)
        drawHorizontalLine(canvas, textWidth+padding, pixelY)
    }

    private fun spToPixel(context: Context, sp: Float): Int {
        //return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.resources.displayMetrics).toInt()
        return (sp * context.resources.displayMetrics.scaledDensity).toInt()
        //val density = context.resources.displayMetrics.scaledDensity
        //return sp * density
    }

    private fun pixelToSp(context: Context, px: Float): Int {
        //return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.resources.displayMetrics).toInt()
        return (px/context.resources.displayMetrics.scaledDensity).toInt()
        //val density = context.resources.displayMetrics.scaledDensity
        //return sp * density
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
        canvas?.drawLine(startX, y, margin.toFloat(), y, ticPaint)
    }
}
package com.intelliviz.quakereport

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.intelliviz.quakereport.GraphView.companion.HORIZONTAL_MARGIN_SP
import com.intelliviz.quakereport.GraphView.companion.PADDING_SP
import com.intelliviz.quakereport.GraphView.companion.VERTICAL_MARGIN_SP


class GraphView(context: Context, attributes: AttributeSet): SurfaceView(context, attributes), SurfaceHolder.Callback {

    object companion {
        var HORIZONTAL_MARGIN_SP: Float = 50F
        var VERTICAL_MARGIN_SP: Float = 50F
        var PADDING_SP: Float = 8F
    }
    private lateinit var surfaceHolder: SurfaceHolder
    private var backgroundPaint: Paint
    private var bluePaint: Paint
    private var greenPaint: Paint
    private var ticPaint: Paint
    private var deltaX: Float = 0F
    private var deltaY: Float = 0F
    private var minX: Float = 0F
    private var maxX: Float = 0F
    private var minY: Float = 0F
    private var maxY: Float = 0F
    private var horizontalProjection: Float = 0F
    private var verticalProjection: Float = 0F
    private var horizontalMargin: Float = 0F
    private var verticalMargin: Float = 0F
    private var padding: Float = 0F
    private lateinit var xValues: FloatArray
    private lateinit var yValues: FloatArray

    init{
        backgroundPaint = Paint()
        backgroundPaint.color = Color.WHITE
        bluePaint = Paint()
        bluePaint.color = Color.BLUE
        greenPaint = Paint()
        greenPaint.color = Color.GREEN
        ticPaint = Paint()
        ticPaint.color = Color.BLACK
        greenPaint.textSize = 8F
        holder.addCallback(this)

        horizontalMargin = spToPixel(context, HORIZONTAL_MARGIN_SP)
        verticalMargin = spToPixel(context, VERTICAL_MARGIN_SP)
        padding = spToPixel(context, PADDING_SP)
    }

    fun setData(xValues: FloatArray, yValues: FloatArray) {
        var minValue = xValues[0]
        var maxValue = xValues[0]
        for(value in xValues) {
            if(value < minValue) {
                minValue = value
            }
            if(value > maxValue) {
                maxValue = value
            }
        }

        minX = minValue
        maxX = maxValue

        minValue = yValues[0]
        maxValue = yValues[0]
        for(value in yValues) {
            if(value < minValue) {
                minValue = value
            }
            if(value > maxValue) {
                maxValue = value
            }
        }

        minY = minValue
        maxY = maxValue

        this.xValues = xValues
        this.yValues = yValues
    }

    fun setMinMaxX(minX: Float, maxX: Float) {
        this.minX = minX
        this.maxX = maxX
    }

    fun setMinMaxY(minY: Float, maxY: Float) {
        this.minY = minY
        this.maxY = maxY
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

        Log.d("EDM", "width = $width, height = $height")
        var canvas = holder?.lockCanvas()

        canvas?.drawColor(Color.WHITE)
        deltaX = maxX - minX
        deltaY = width.toFloat() - 1.5F * horizontalMargin
        horizontalProjection = deltaY/deltaX

        deltaY = maxY - minY
        deltaX = height.toFloat() - 1.5F * verticalMargin
        verticalProjection = deltaX/deltaY

        val verticalAxis = VerticalAxis(context, verticalProjection, yValues, height.toFloat())
        val horizontalAxis = HorizontalAxis(context, horizontalProjection, xValues, width.toFloat(), height.toFloat())

        canvas?.drawRect(0F, 0F, canvas.width.toFloat(), canvas.height.toFloat(), backgroundPaint)

        var textSize = spToPixel(context, 16F)

        ticPaint.textSize = textSize


        for(i in 0..(yValues.size-1)) {
            drawDot(canvas, xValues[i], yValues[i])
        }

        verticalAxis.draw(canvas, context)
        horizontalAxis.draw(canvas, context)

        holder?.unlockCanvasAndPost(canvas)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        Log.d("EDM", "surfaceCreated")
    }

    private fun worldToPixelX(x: Float): Float {
        return horizontalProjection * (x-minX) + horizontalMargin
    }

    private fun worldToPixelY(y: Float): Float {
        return height - (verticalProjection * (y-minY) + verticalMargin)
    }

    private fun spToPixel(context: Context, sp: Float): Float {
        var density = context.resources.displayMetrics.scaledDensity
        return sp * density
    }

    private fun getTextWidth(paint: Paint, text: String): Float {
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        return bounds.width().toFloat()
    }

    private fun getTextHeight(paint: Paint, text: String): Float {
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        return bounds.height().toFloat()
    }

    private fun drawDot(canvas: Canvas?, x: Float, y: Float) {
        val pixelX = worldToPixelX(x)
        val pixelY = worldToPixelY(y)
        canvas?.drawCircle(pixelX, pixelY, 10F, greenPaint)
    }

    private fun drawVerticalAxisTic(canvas: Canvas?, yValue: Float) {
        val pixelY = worldToPixelY(yValue)
        val height = getTextHeight(ticPaint, yValue.toString())
        val width = getTextWidth(ticPaint, yValue.toString())
        val xstart = padding
        canvas?.drawText(yValue.toString(), xstart, pixelY + height / 2, ticPaint)
        drawHorizontalLine(canvas, width+padding, pixelY)
    }

    private fun drawHorizontalAxisTic(canvas: Canvas?, xValue: Float) {
        val pixelX = worldToPixelX(xValue)
        val textHeight = getTextHeight(ticPaint, xValue.toString())
        val textWidth = getTextWidth(ticPaint, xValue.toString())
        val ystart = height - padding
        canvas?.drawText(xValue.toString(), pixelX, ystart, ticPaint)
        drawVerticalLine(canvas, pixelX, height - (padding + textHeight))
    }

    private fun drawHorizontalLine(canvas: Canvas?, startX: Float, y: Float) {
        canvas?.drawLine(startX, y, horizontalMargin, y, ticPaint)
    }

    private fun drawVerticalLine(canvas: Canvas?, x: Float, startY: Float) {
        canvas?.drawLine(x, startY, x, verticalMargin, ticPaint)
    }
}
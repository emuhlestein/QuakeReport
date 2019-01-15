package com.intelliviz.quakereport

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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
    private var backgroundPaint: Paint = Paint()
    private var bluePaint: Paint
    private var greenPaint: Paint
    private var ticPaint: Paint
    private var deltaX: Float = 0F
    private var deltaY: Float = 0F
    private var minX: Float = 0F
    private var maxX: Float = 0F
    private var minY: Float = 0F
    private var maxY: Float = 0F
    private lateinit var horizontalProjection: HorizontalProjection
    private lateinit var verticalProjection: VerticalProjection
    private var horizontalMargin: Float = 0F
    private var verticalMargin: Float = 0F
    private var padding: Float = 0F
    private lateinit var xValues: FloatArray
    private lateinit var yValues: FloatArray
    private var verticalLabel: String = ""

    init{
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

    fun setVerticalLabel(label: String) {
        verticalLabel = label
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
        var horProjection = deltaY/deltaX

        horizontalProjection = HorizontalProjection(horProjection, minX, horizontalMargin)

        deltaY = maxY - minY
        deltaX = height.toFloat() - 2.0F * verticalMargin
        var vertProjection = deltaX/deltaY

        verticalProjection = VerticalProjection(vertProjection, minY, height.toFloat(), verticalMargin)

        val verticalAxis = VerticalAxis(context, verticalProjection, verticalLabel, yValues, height.toFloat())
        val horizontalAxis = HorizontalAxis(context, horizontalProjection, "this is a test label", xValues, width.toFloat(), height.toFloat())

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
        return horizontalProjection.worldToPixel(x)
    }

    private fun worldToPixelY(y: Float): Float {
        return verticalProjection.worldToPixel(y)
    }

    private fun spToPixel(context: Context, sp: Float): Float {
        var density = context.resources.displayMetrics.scaledDensity
        return sp * density
    }

    private fun drawDot(canvas: Canvas?, x: Float, y: Float) {
        val pixelX = worldToPixelX(x)
        val pixelY = worldToPixelY(y)
        canvas?.drawCircle(pixelX, pixelY, 10F, greenPaint)
    }

    private fun drawHorizontalLine(canvas: Canvas?, startX: Float, y: Float) {
        canvas?.drawLine(startX, y, horizontalMargin, y, ticPaint)
    }

    private fun drawVerticalLine(canvas: Canvas?, x: Float, startY: Float) {
        canvas?.drawLine(x, startY, x, verticalMargin, ticPaint)
    }
}
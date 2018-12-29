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


class GraphView(context: Context, attributes: AttributeSet): SurfaceView(context, attributes), SurfaceHolder.Callback {
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

//    override fun draw(canvas: Canvas?) {
//        super.draw(canvas)
//        canvas?.drawRect(0F, 0F, canvas.width.toFloat(), canvas.height.toFloat(), backgroundPaint)
//    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        Log.d("EDM", "width = $width, height = $height")
        var canvas = holder?.lockCanvas()
        canvas?.drawColor(Color.WHITE)
        deltaX = maxX - minX
        deltaY = width.toFloat()
        horizontalProjection = deltaY/deltaX

        deltaY = maxY - minY
        deltaX = height.toFloat()
        verticalProjection = deltaX/deltaY

        var theWidth = width
        var theHeight = height
        var pixelX = getPixelX(1950F)
        canvas?.drawRect(0F, 0F, canvas.width.toFloat(), canvas.height.toFloat(), backgroundPaint)
        //canvas?.drawRect(0F, 0F, 100F, 100F, bluePaint)
        //canvas?.drawCircle(pixelX, 400F, 10F, bluePaint)

        var textSize = spToPixel(16F, context)

        greenPaint.textSize = textSize


        var textWidth = getTextWidth(greenPaint, "1900")
        var half = textWidth / 2


        pixelX = getPixelX(1920F)
        var textX = pixelX - half
        //canvas?.drawCircle(pixelX, 400F, 10F, greenPaint)

        drawDot(canvas, 8F, 1910)
        drawDot(canvas, 6F, 1935)
        drawDot(canvas, 7F, 1970)
        drawDot(canvas, 4F, 1985)
        drawDot(canvas, 3F, 2000)

        //canvas?.drawText("1900", textX, 400F, greenPaint)
        holder?.unlockCanvasAndPost(canvas)

        // resize UI
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        Log.d("EDM", "surfaceCreated")
    }

    private fun getPixelX(x: Float): Float {
        return horizontalProjection * (x-minX)
    }

    private fun getPixelY(y: Float): Float {
        return verticalProjection * (y-minY)
    }

    private fun spToPixel(sp: Float, context: Context): Float {
        var density = context.resources.displayMetrics.scaledDensity
        return sp * density
    }

    private fun getTextWidth(paint: Paint, text: String): Float {
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        return bounds.width().toFloat()
    }

    private fun drawDot(canvas: Canvas?, mag: Float, year: Int) {
        var pixelX = getPixelX(year.toFloat())
        var pixelY = getPixelY(mag)
        canvas?.drawCircle(pixelX, pixelY, 10F, greenPaint)
    }
}
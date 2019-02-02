package com.intelliviz.quakereport.graphview

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

    companion object {
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
    private lateinit var zValues: IntArray
    private var verticalLabel: String = ""
    private var horizontalLabel: String = ""
    private lateinit var spotPaint: Paint
    private var legendValues = HashMap<Int, Paint>()

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

        xValues = floatArrayOf()
        yValues = floatArrayOf()
        zValues = intArrayOf()
    }

    fun setData(values: ArrayList<PointValue>) {
        val xValues = ArrayList<Float>()
        val yValues = ArrayList<Float>()
        val zValues = ArrayList<Int>()
        values.forEach {
            xValues.add(it.x)
            yValues.add(it.y)
            zValues.add(it.z)
        }

        setData(xValues.toFloatArray(), yValues.toFloatArray())
        this.zValues = zValues.toIntArray()
    }

    fun setData(xValues: FloatArray, yValues: FloatArray) {
        minX = xValues.min()!!
        maxX = xValues.max()!!
        minY = yValues.min()!!
        maxY = yValues.max()!!
        this.xValues = xValues
        this.yValues = yValues
    }

    fun setLegendValues(map: HashMap<Int, Int>) {
        map.forEach{(key, value) ->
            var paint = Paint()
            paint.color = value
            legendValues[key] = paint
        }

//        for(i in 0 until legendValues.size) {
//            this.legendValues[legendValues[i]] = valueColors[i]
//        }
    }

    fun setVerticalLabel(label: String) {
        verticalLabel = label
    }

    fun setHorizontalLabel(label: String) {
        horizontalLabel = label
    }

    fun setSpotColor(paint: Paint) {
        spotPaint = paint
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
        val canvas = holder?.lockCanvas()
        clear(canvas)

        if(xValues.isEmpty()) {
            holder?.unlockCanvasAndPost(canvas)
            return
        }

        canvas?.drawColor(Color.WHITE)
        deltaX = maxX - minX
        deltaY = width.toFloat() - 1.5F * horizontalMargin
        val horProjection = deltaY/deltaX

        horizontalProjection = HorizontalProjection(horProjection, minX, horizontalMargin)

        deltaY = maxY - minY
        deltaX = height.toFloat() - 2.0F * verticalMargin
        val vertProjection = deltaX/deltaY

        verticalProjection = VerticalProjection(vertProjection, minY, height.toFloat(), verticalMargin)

        val verticalAxis = VerticalAxis(context, verticalProjection, verticalLabel, yValues, height.toFloat())
        val horizontalAxis = HorizontalAxis(context, horizontalProjection, horizontalLabel, xValues, width.toFloat(), height.toFloat())



        val textSize = spToPixel(context, 16F)

        ticPaint.textSize = textSize


        for(i in 0..(yValues.size-1)) {
            drawDot(canvas, xValues[i], yValues[i], zValues[i])
        }

        verticalAxis.draw(canvas, context)
        horizontalAxis.draw(canvas, context)

        drawLegend(canvas)

        holder?.unlockCanvasAndPost(canvas)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {
        Log.d("EDM", "surfaceCreated")
        setWillNotDraw(false)
    }

    private fun worldToPixelX(x: Float): Float {
        return horizontalProjection.worldToPixel(x)
    }

    private fun worldToPixelY(y: Float): Float {
        return verticalProjection.worldToPixel(y)
    }

    private fun spToPixel(context: Context, sp: Float): Float {
        val density = context.resources.displayMetrics.scaledDensity
        return sp * density
    }

    private fun drawDot(canvas: Canvas?, x: Float, y: Float, z: Int) {
        val pixelX = worldToPixelX(x)
        val pixelY = worldToPixelY(y)
        var paint = this.legendValues[z]
        canvas?.drawCircle(pixelX, pixelY, 10F, paint)
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

    private fun drawLegend(canvas: Canvas?) {
        val textSize = spToPixel(context, 16F)
        var x = textSize
        val y = verticalMargin/2 * .75F
        var label = "Magnitude:"
        val textHeight = getTextHeight(ticPaint, label)
        canvas?.drawText(label, x, y+textHeight/2, ticPaint)
        var textWidth = getTextWidth(ticPaint, label)

        x += (textWidth + textSize)

        legendValues.forEach{(key, value) ->
            val str = "-" + key.toString() + ","
            val paint = value
            drawLegendItem(canvas, paint, str, x, y)
            x += 140F
        }
//        for(i in 0 until legendValues.size) {
//            var str = "-" + legendValues[i].second.toString() + ","
//            var paint = legendValues[i].first
//            drawLegendItem(canvas, paint, str, x, y)
//            x += 140F
//        }


//        var str = "-" + "7" + ","
//        drawLegendItem(canvas, greenPaint, str, x, y)
//
//        x += 100F
//        str = "-" + "8"
//        drawLegendItem(canvas, bluePaint, str, x, y)
    }

    private fun drawLegendItem(canvas: Canvas?, dotPaint: Paint, str: String, x: Float, y: Float) {
        val textHeight = getTextHeight(ticPaint, str)
        canvas?.drawCircle(x, y, 10F, dotPaint)
        canvas?.drawText(str, x+20F, y+textHeight/2, ticPaint)
    }

    private fun clear(canvas: Canvas?) {
        canvas?.drawRect(0F, 0F, canvas.width.toFloat(), canvas.height.toFloat(), backgroundPaint)
    }

}
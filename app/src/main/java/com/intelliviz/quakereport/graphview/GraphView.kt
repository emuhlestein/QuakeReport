package com.intelliviz.quakereport.graphview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View

class GraphView : View {
    companion object {
        var HORIZONTAL_MARGIN_SP: Float = 50F
        var VERTICAL_MARGIN_SP: Float = 50F
        var PADDING_SP: Float = 8F
    }

    private var mWidth: Float = 0.toFloat()
    private var mHeight: Float = 0.toFloat()
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
    private var xValues: FloatArray
    private var yValues: FloatArray
    private var zValues: IntArray
    private var verticalLabel: String = ""
    private var horizontalLabel: String = ""
    private var legendValues = HashMap<Int, Paint>()
    private lateinit var verticalAxis: VerticalAxis
    private lateinit var horizontalAxis: HorizontalAxis


    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mWidth = w.toFloat()
        mHeight = h.toFloat()

        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)


        Log.d("EDM", "width = $width, height = $height")

        if(xValues.isEmpty()) {
            return
        }

        canvas.drawColor(Color.WHITE)


        val textSize = spToPixel(context, 16F)

        ticPaint.textSize = textSize


        for(i in 0..(yValues.size-1)) {
            drawDot(canvas, xValues[i], yValues[i], zValues[i])
        }

        verticalAxis.draw(canvas, context)
        horizontalAxis.draw(canvas, context)

        drawLegend(canvas)
    }

    init {
        backgroundPaint.color = Color.WHITE
        bluePaint = Paint()
        bluePaint.color = Color.BLUE
        greenPaint = Paint()
        greenPaint.color = Color.GREEN
        ticPaint = Paint()
        ticPaint.color = Color.BLACK
        greenPaint.textSize = 8F

        horizontalMargin = spToPixel(context, GraphView.HORIZONTAL_MARGIN_SP)
        verticalMargin = spToPixel(context, GraphView.VERTICAL_MARGIN_SP)
        padding = spToPixel(context, GraphView.PADDING_SP)

        xValues = floatArrayOf()
        yValues = floatArrayOf()
        zValues = intArrayOf()
    }

    fun setData(values: ArrayList<PointValue>, map: HashMap<Int, Int>) {
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

        map.forEach{(key, value) ->
            val paint = Paint()
            paint.color = value
            legendValues[key] = paint
        }

        invalidate()
        requestLayout()
    }

    private fun setData(xValues: FloatArray, yValues: FloatArray) {
        minX = xValues.min()!!
        maxX = xValues.max()!!
        minY = yValues.min()!!
        maxY = yValues.max()!!
        this.xValues = xValues
        this.yValues = yValues
        init()
    }

    fun setVerticalLabel(label: String) {
        verticalLabel = label
    }

    fun setHorizontalLabel(label: String) {
        horizontalLabel = label
    }

    private fun init() {
        deltaX = maxX - minX
        deltaY = mWidth - 1.5F * horizontalMargin
        val horProjection = deltaY/deltaX
        horizontalProjection = HorizontalProjection(horProjection, minX, horizontalMargin)

        deltaY = maxY - minY
        deltaX = mHeight - 2.0F * verticalMargin
        val vertProjection = deltaX/deltaY

        verticalProjection = VerticalProjection(vertProjection, minY, mHeight, verticalMargin)

        verticalAxis = VerticalAxis(context, verticalProjection, verticalLabel, yValues, mHeight)
        horizontalAxis = HorizontalAxis(context, horizontalProjection, horizontalLabel, xValues, mWidth, mHeight)
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
        val paint = legendValues[z]
        canvas?.drawCircle(pixelX, pixelY, 10F, paint!!)
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
        val label = "Magnitude:"
        val textHeight = getTextHeight(ticPaint, label)
        canvas?.drawText(label, x, y+textHeight/2, ticPaint)
        val textWidth = getTextWidth(ticPaint, label)

        x += (textWidth + textSize)

        legendValues.forEach{(key, value) ->
            val str = "-" + key.toString() + ","
            drawLegendItem(canvas, value, str, x, y)
            x += 140F
        }
    }

    private fun drawLegendItem(canvas: Canvas?, dotPaint: Paint, str: String, x: Float, y: Float) {
        val textHeight = getTextHeight(ticPaint, str)
        canvas?.drawCircle(x, y, 10F, dotPaint)
        canvas?.drawText(str, x+20F, y+textHeight/2, ticPaint)
    }
}

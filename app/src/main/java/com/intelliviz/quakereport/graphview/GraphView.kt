package com.intelliviz.quakereport.graphview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.support.v4.view.GestureDetectorCompat
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import kotlinx.android.synthetic.main.activity_earthquake_trends.view.*

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
    private var currentMinX: Float = 0F
    private var currentMaxX: Float = 0F
    private var currentMinY: Float = 0F
    private var currentMaxY: Float = 0F
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
    private var legendValues: LinkedHashMap<Int, Paint>
    private lateinit var verticalAxis: VerticalAxis
    private lateinit var horizontalAxis: HorizontalAxis
    private var scaleFactor: Float = 1.0F
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private lateinit var detector: GestureDetectorCompat


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
        legendValues = linkedMapOf()

        scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
        detector = GestureDetectorCompat(context, ScrollListener())
        //setOnTouchListener(OnSlidingTouchListener())
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val action: Int? = event?.actionMasked

//        if(action == MotionEvent.ACTION_DOWN) {
//            Log.e("EDM", "Action Down")
//            return true
//        }

        detector.onTouchEvent(event)

        if(!scaleGestureDetector.onTouchEvent(event)) {

        }
        //if(!detector.onTouchEvent(event)) {
            //scaleGestureDetector.onTouchEvent(event)
        //}
        return true
    }


    fun setData(values: ArrayList<PointValue>, colors: LinkedHashMap<Int, Int>) {
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

        for(i in colors.keys) {
            val paint = Paint()
            paint.color = colors[i]!!
            legendValues[i] = paint
        }

        invalidate()
        requestLayout()
    }

    private fun setData(xValues: FloatArray, yValues: FloatArray) {
        minX = xValues.min()!!
        maxX = xValues.max()!!
        minY = yValues.min()!!
        maxY = yValues.max()!!
        currentMinX = minX
        currentMaxX = maxX
        currentMinY = minY
        currentMaxY = maxY
        this.xValues = xValues
        this.yValues = yValues
        init()
    }

    fun setScale(scaleFactor: Float) {
        var dx = (maxX - minX)
        var midX = minX + dx/2
        var fact = dx * (1 - Math.abs(1-scaleFactor))
        var diff = Math.abs((fact - dx)/2)
        currentMinX = minX + diff
        currentMaxX = maxX - diff

        if(currentMinX > midX) {
            currentMinX = midX - 1
        }
        if(currentMaxX < midX) {
            currentMaxX = midX + 1
        }

        var dy = (maxY - minY)
        if(dy == 0F) {
            dy = 2F
        }
        var midY = minY + dy/2
        fact = dy * (1 - Math.abs(1-scaleFactor))
        diff = Math.abs((fact - dy)/2)
        currentMinY = minY + diff
        currentMaxY = maxY - diff

        if(currentMinY > midY) {
            currentMinY = midY - 1
        }
        if(currentMaxY < midY) {
            currentMaxY = midY + 1
        }

        updateVerticalProjection(currentMinY, currentMaxY)
        updateHorizontalProjection(currentMinX, currentMaxX)
        invalidate()
        requestLayout()
    }

    fun setScrollX(dpixel: Float) {
        var x0 = pixelToWorldX(0F)
        var x1 = pixelToWorldX(dpixel)

        Log.e("EDM", "delta world X: $x: min: $currentMinX  max: $currentMaxX")
        var change = x0 - x1
        var diffX = currentMaxX - currentMinX
        currentMinX = currentMinX+change
        if(currentMinX <= minX) {
            currentMinX = minX
        }

        currentMaxX = currentMinX + diffX
        if(currentMaxX >= maxX) {
            currentMaxX = maxX
            currentMinX = currentMaxX - diffX
        }

        updateHorizontalProjection(currentMinX, currentMaxX)
        invalidate()
        requestLayout()
    }

    fun setScrollY(dpixel: Float) {
        var y0 = pixelToWorldY(0F)
        var y1 = pixelToWorldY(dpixel)

        Log.e("EDM", "delta world Y: $y: min: $currentMinY  max: $currentMaxY")
        var change = y0 - y1
        var diffY = currentMaxY - currentMinY
        currentMinY = currentMinY+change
        if(currentMinY <= minY) {
            currentMinY = minY
        }

        currentMaxY = currentMinY + diffY
        if(currentMaxY >= maxY) {
            currentMaxY = maxY
            currentMinY = currentMaxY - diffY
        }


        updateVerticalProjection(currentMinY, currentMaxY)
        invalidate()
        requestLayout()
    }

    fun setVerticalLabel(label: String) {
        verticalLabel = label
    }

    fun setHorizontalLabel(label: String) {
        horizontalLabel = label
    }

    private fun init() {
        updateVerticalProjection(currentMinY, currentMaxY)
        updateHorizontalProjection(currentMinX, currentMaxX)
    }

    private fun updateVerticalProjection(minY: Float, maxY: Float) {
        val deltaY = maxY - minY
        val deltaX = mHeight - 2.0F * verticalMargin
        val vertProjection = deltaX/deltaY
        verticalProjection = VerticalProjection(vertProjection, minY, mHeight, verticalMargin)
        verticalAxis = VerticalAxis(context, verticalProjection, verticalLabel, minY!!, maxY!!, yValues, mHeight)
    }

    /**
     * minX is minx of the view in world space
     * maxX is maxx of the view in world space
     */
    private fun updateHorizontalProjection(minX: Float, maxX: Float) {
        val deltaX = maxX - minX
        val deltaY = mWidth - 1.5F * horizontalMargin
        val horProjection = deltaY/deltaX
        horizontalProjection = HorizontalProjection(horProjection, minX, horizontalMargin)
        horizontalAxis = HorizontalAxis(context, horizontalProjection, horizontalLabel, minX!!, maxX!!, xValues, mWidth, mHeight)
    }

    private fun worldToPixelX(x: Float): Float {
        return horizontalProjection.worldToPixel(x)
    }

    private fun pixelToWorldX(x: Float): Float {
        return horizontalProjection.pixelToWorld(x)
    }

    private fun worldToPixelY(y: Float): Float {
        return verticalProjection.worldToPixel(y)
    }

    private fun pixelToWorldY(y: Float): Float {
        return verticalProjection.pixelToWorld(y)
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

        for(i in legendValues.keys) {
            val str = "-" + i.toString() + ","
            drawLegendItem(canvas, legendValues[i]!!, str, x, y)
            x += 140F
        }
    }

    private fun drawLegendItem(canvas: Canvas?, dotPaint: Paint, str: String, x: Float, y: Float) {
        val textHeight = getTextHeight(ticPaint, str)
        canvas?.drawCircle(x, y, 10F, dotPaint)
        canvas?.drawText(str, x+20F, y+textHeight/2, ticPaint)
    }

    private inner class ScaleListener: ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector?): Boolean {
            scaleFactor *= scaleGestureDetector!!.scaleFactor

            scaleFactor = Math.max(1.0F, Math.min(scaleFactor, 10F))
            Log.d("EDM", "Raw scale factor: ${scaleFactor}")

            earthquakeGraphView.setScale(scaleFactor)
            return true
        }
    }

    private inner class ScrollListener: GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent, e2: MotionEvent,
                              distanceX: Float, distanceY: Float): Boolean {
            var dx = e2.x - e1.x
            var dy = e2.y - e1.y
            if(Math.abs(dx) > Math.abs(dy)) {
                // sliding in x direction
                Log.e("EDM", "dx: $dx")
                setScrollX(dx)
                return true
            } else if(Math.abs(dy) > Math.abs(dx)){
                // sliding in y direction
                Log.e("EDM", "dy: $dy")
                setScrollY(dy)
                return true
            }
            return false
        }
    }

    private inner class OnSlidingTouchListener(val gestureDetector: GestureDetector): OnTouchListener {

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            return gestureDetector.onTouchEvent(event)
        }
    }
}

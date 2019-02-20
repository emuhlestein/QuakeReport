package com.intelliviz.quakereport.graphview

class VerticalProjection(var projection: Float, var min: Float, var height: Float, var offset: Float) {
    fun worldToPixel(value: Float): Float {
        return height - (projection * (value-min) + offset)
    }

    fun pixelToWorld(value: Float): Float {
        return (min - (value - height + offset)/projection)
    }
}
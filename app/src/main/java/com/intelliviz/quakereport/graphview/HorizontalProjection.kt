package com.intelliviz.quakereport.graphview

class HorizontalProjection(var projection: Float, var min: Float, var offset: Float) {
    fun worldToPixel(value: Float): Float {
        return projection * (value-min) + offset
    }

    fun pixelToWorld(value: Float): Float {
        return ((value - offset)/projection) + min
    }
}
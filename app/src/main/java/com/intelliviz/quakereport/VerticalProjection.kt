package com.intelliviz.quakereport

class VerticalProjection(var projection: Float, var min: Float, var height: Float, var offset: Float) {
    fun worldToPixel(value: Float): Float {
        return height - (projection * (value-min) + offset)
    }
}
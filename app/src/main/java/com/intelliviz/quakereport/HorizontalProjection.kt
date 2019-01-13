package com.intelliviz.quakereport

class HorizontalProjection(var projection: Float, var min: Float, var offset: Float) {
    fun worldToPixel(value: Float): Float {
        return  projection * (value-min) + offset
    }
}
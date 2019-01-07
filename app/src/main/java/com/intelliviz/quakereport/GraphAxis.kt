package com.intelliviz.quakereport

class GraphAxis(values: FloatArray) {
    private var minValue: Float
    private var maxValue: Float

    init {
        var min = values[0]
        var max = values[0]
        for(value in values) {
            if(value < min) {
                min = value
            }
            if(value > max) {
                max = value
            }
        }

        minValue = min
        maxValue = max
    }
}
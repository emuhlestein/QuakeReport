package com.intelliviz.quakereport.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.intelliviz.quakereport.GraphView
import com.intelliviz.quakereport.R

class EarthquakeTrendFragment: Fragment() {

    companion object {

        fun newInstance(): EarthquakeTrendFragment {
            val fragment = EarthquakeTrendFragment()
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.earthquake_trend_fragment, container, false)

        val earthquakeGraphView = view.findViewById<GraphView>(R.id.earthquakeGraphView)
//        earthquakeGraphView.setMinMaxX(1900F, 2018F)
//        earthquakeGraphView.setXInc(20)
//        earthquakeGraphView.setMinMaxY(0F, 9F)
//        earthquakeGraphView.setYInc(1)

        var y: FloatArray = floatArrayOf(0F, 1F, 2F, 3F, 4F, 5F, 6F, 7F, 8F, 9F, 10F)
        var x: FloatArray = floatArrayOf(1900F, 1910F, 1935F, 1950F, 1960F, 1970F, 1975F, 1985F, 1995F, 2000F, 2010F)
        earthquakeGraphView.setData(x, y)
        return view
    }
}
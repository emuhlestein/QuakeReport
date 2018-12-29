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
        earthquakeGraphView.setMinMaxX(1900F, 2018F)
        earthquakeGraphView.setMinMaxY(0F, 9F)
        return view
    }
}
package com.intelliviz.quakereport.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class EarthquakeRecentFragment: BaseFragment() {
    companion object {

        fun newInstance(): EarthquakeRecentFragment {
            val fragment = EarthquakeRecentFragment()
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val textView = TextView(getContext())
        textView.text = "Recent Earhtquakes"
        return textView
    }

    override fun loadEarthquakes(startDate: String, endDate: String, minMag: Int, maxMag: Int) {
    }
}
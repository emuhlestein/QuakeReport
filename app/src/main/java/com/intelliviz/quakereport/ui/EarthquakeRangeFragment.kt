package com.intelliviz.quakereport.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.intelliviz.quakereport.R

class EarthquakeRangeFragment: Fragment() {
    companion object {

        fun newInstance(): EarthquakeRangeFragment {
            val fragment = EarthquakeRangeFragment()
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.earthquake_range_fragment, container, false)
        return view
    }
}
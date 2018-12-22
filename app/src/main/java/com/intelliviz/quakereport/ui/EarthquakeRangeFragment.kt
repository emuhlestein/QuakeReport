package com.intelliviz.quakereport.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class EarthquakeRangeFragment: Fragment() {
    companion object {

        fun newInstance(): EarthquakeRangeFragment {
            val fragment = EarthquakeRangeFragment()
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val textView = TextView(getContext())
        textView.text = "Range Earhtquakes"
        return textView
    }
}
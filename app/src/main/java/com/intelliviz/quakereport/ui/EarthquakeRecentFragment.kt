package com.intelliviz.quakereport.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class EarthquakeRecentFragment: Fragment() {
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
}
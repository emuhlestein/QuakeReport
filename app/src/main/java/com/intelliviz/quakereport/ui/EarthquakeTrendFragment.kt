package com.intelliviz.quakereport.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class EarthquakeTrendFragment: Fragment() {
    companion object {

        fun newInstance(): EarthquakeTrendFragment {
            val fragment = EarthquakeTrendFragment()
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val textView = TextView(getContext())
        textView.text = "Trend Earhtquakes"
        return textView
    }
}
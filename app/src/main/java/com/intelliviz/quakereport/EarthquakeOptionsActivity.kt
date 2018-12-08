package com.intelliviz.quakereport

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_earhtquake_options.*
import java.text.SimpleDateFormat
import java.util.*

class EarthquakeOptionsActivity : AppCompatActivity(), DatePickerFragment.OnDateSelectedListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_earhtquake_options)

        start_date_button.setOnClickListener {
            val newFragment = DatePickerFragment.newInstance(START_DATE)
            newFragment.show(supportFragmentManager, "date picker")
        }

        end_date_button.setOnClickListener {
            val newFragment = DatePickerFragment.newInstance(END_DATE)
            newFragment.show(supportFragmentManager, "date picker")
        }

        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        val currentDate = Date()
        val s = dateFormat.format(currentDate)

        start_date_text_view.text = s

        end_date_text_view.text = s
    }

    override fun onDateSelected(day: String, month: String, year: String, id: Int) {
        if(id == START_DATE) {

        } else if(id == END_DATE) {

        }
    }

    companion object {
        private var START_DATE: Int = 1
        private var END_DATE: Int = 2
    }
}

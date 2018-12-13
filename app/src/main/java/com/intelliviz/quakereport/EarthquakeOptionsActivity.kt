package com.intelliviz.quakereport

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_earhtquake_options.*
import java.text.SimpleDateFormat
import java.util.*

class EarthquakeOptionsActivity : AppCompatActivity(), DatePickerFragment.OnDateSelectedListener {


    var endDate = ""
    var startDate = ""
    var minMag = 0
    var maxMag = 0
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

        ok_button.setOnClickListener {
            val minmag = min_mag_spinner.selectedItem.toString().toInt()
            val maxmag = max_mag_spinner.selectedItem.toString().toInt()
            val startdate = start_date_text_view.text
            val enddate = end_date_text_view.text
            val intent = Intent()
            intent.putExtra(EXTRA_START_DATE, startdate)
            intent.putExtra(EXTRA_END_DATE, enddate)
            intent.putExtra(EXTRA_MIN_MAG, minmag)
            intent.putExtra(EXTRA_MAX_MAG, maxmag)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        cancel_button.setOnClickListener {
            finish()
        }
    }

    override fun onDateSelected(day: String, month: String, year: String, id: Int) {
        if(id == START_DATE) {
            start_date_text_view.text = year + "-" + month + "-" + day
        } else if(id == END_DATE) {
            end_date_text_view.text = year + "-" + month + "-" + day
        }
    }

    companion object {
        private val START_DATE: Int = 1
        private val END_DATE: Int = 2
        val EXTRA_END_DATE = "end_date"
        val EXTRA_START_DATE = "start_date"
        val EXTRA_MIN_MAG = "min_mag"
        val EXTRA_MAX_MAG = "max_mag"
    }
}

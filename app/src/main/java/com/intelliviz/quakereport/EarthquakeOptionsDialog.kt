package com.intelliviz.quakereport

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_earhtquake_options.*
import java.text.SimpleDateFormat
import java.util.*

class EarthquakeOptionsDialog : DialogFragment(), DatePickerFragment.OnDateSelectedListener {

    companion object {
        private val ARG_ID = "id"
        private val START_DATE: Int = 1
        private val END_DATE: Int = 2
        private val DATE_REQUEST: Int = 3

        val EXTRA_END_DATE = "end_date"
        val EXTRA_START_DATE = "start_date"
        val EXTRA_MIN_MAG = "min_mag"
        val EXTRA_MAX_MAG = "max_mag"

        fun newInstance(id: Int): EarthquakeOptionsDialog {
            val args: Bundle = Bundle()
            args.putInt(ARG_ID, id)
            val fragment = EarthquakeOptionsDialog()
            fragment.arguments = args
            return fragment
        }
    }

    interface OnOptionsSelectedListener {
        fun onOptionsSelected(startDate: String, endDate: String, minMag: Int, maxMag: Int)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_earhtquake_options, container, false)

        val startDateButton = view.findViewById<View>(R.id.start_date_button) as Button

        startDateButton.setOnClickListener {
            val newFragment = DatePickerFragment.newInstance(START_DATE)
            newFragment.setTargetFragment(this, DATE_REQUEST)
            newFragment.show(activity!!.supportFragmentManager, "date picker")
        }

        val endDateButton = view.findViewById<View>(R.id.end_date_button) as Button
        endDateButton.setOnClickListener {
            val newFragment = DatePickerFragment.newInstance(END_DATE)
            newFragment.setTargetFragment(this, DATE_REQUEST)
            newFragment.show(activity!!.supportFragmentManager, "date picker")
        }

        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        val currentDate = Date()
        val s = dateFormat.format(currentDate)

        val startDateTextView = view.findViewById<View>(R.id.start_date_text_view) as TextView
        startDateTextView.setText(s)

        val endDateTextView = view.findViewById<View>(R.id.end_date_text_view) as TextView
        endDateTextView.setText(s)

        val okButton = view.findViewById<View>(R.id.ok_button) as Button
        okButton.setOnClickListener {
            val minmag = min_mag_spinner.selectedItem.toString().toInt()
            val maxmag = max_mag_spinner.selectedItem.toString().toInt()
            val startdate = start_date_text_view.text.toString()
            val enddate = end_date_text_view.text.toString()
            val intent = Intent()
            intent.putExtra(EarthquakeOptionsActivity.EXTRA_START_DATE, startdate)
            intent.putExtra(EarthquakeOptionsActivity.EXTRA_END_DATE, enddate)
            intent.putExtra(EarthquakeOptionsActivity.EXTRA_MIN_MAG, minmag)
            intent.putExtra(EarthquakeOptionsActivity.EXTRA_MAX_MAG, maxmag)
            sendResult(startdate, enddate, minmag, maxmag)
            dismiss()
        }

        val cancelButton = view.findViewById<View>(R.id.cancel_button) as Button
        cancelButton.setOnClickListener {
            dismiss()
        }
        return view
    }

    override fun onDateSelected(day: String, month: String, year: String, id: Int) {
        if(id == START_DATE) {
            start_date_text_view.text = year + "-" + month + "-" + day
        } else if(id == END_DATE) {
            end_date_text_view.text = year + "-" + month + "-" + day
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val id = data?.getIntExtra(DatePickerFragment.EXTRA_ID, -1)
        val year = data?.getStringExtra(DatePickerFragment.EXTRA_YEAR)
        val month = data?.getStringExtra(DatePickerFragment.EXTRA_MONTH)
        val day = data?.getStringExtra(DatePickerFragment.EXTRA_DAY)
        if(id == START_DATE) {
            start_date_text_view.text = year + "-" + month + "-" + day
        } else if(id == END_DATE) {
            end_date_text_view.text = year + "-" + month + "-" + day
        }
    }


    fun sendResult(startDate: String, endDate: String, minMag: Int, maxMag: Int) {
        if(targetFragment != null) {
            var intent = Intent()
            intent.putExtra(EarthquakeOptionsActivity.EXTRA_START_DATE, startDate)
            intent.putExtra(EarthquakeOptionsActivity.EXTRA_END_DATE, endDate)
            intent.putExtra(EarthquakeOptionsActivity.EXTRA_MIN_MAG, minMag)
            intent.putExtra(EarthquakeOptionsActivity.EXTRA_MAX_MAG, maxMag)
            targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
        } else {
            if(activity is EarthquakeOptionsDialog.OnOptionsSelectedListener) {
                var listener: EarthquakeOptionsDialog.OnOptionsSelectedListener? = null
                listener = activity as EarthquakeOptionsDialog.OnOptionsSelectedListener
                listener?.onOptionsSelected(startDate, endDate, minMag, maxMag)
            }
        }
    }
}

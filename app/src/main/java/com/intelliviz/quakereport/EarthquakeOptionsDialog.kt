package com.intelliviz.quakereport

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_earhtquake_options.*

class EarthquakeOptionsDialog : DialogFragment(), DatePickerFragment.OnDateSelectedListener {

    companion object {
        private val ARG_ID = "id"
        private val ARG_START_DATE = "start_date"
        private val ARG_END_DATE = "end_date"
        private val ARG_MIN_MAG = "min_mag"
        private val ARG_MAX_MAG = "max_mag"
        private val START_DATE: Int = 1
        private val END_DATE: Int = 2
        private val DATE_REQUEST: Int = 3

        val EXTRA_END_DATE = "end_date"
        val EXTRA_START_DATE = "start_date"
        val EXTRA_MIN_MAG = "min_mag"
        val EXTRA_MAX_MAG = "max_mag"

        fun newInstance(id: Int, startDate: String, endDate: String, minMag: Int, maxMag: Int): EarthquakeOptionsDialog {
            val args: Bundle = Bundle()
            args.putInt(ARG_ID, id)
            args.putString(ARG_START_DATE, startDate)
            args.putString(ARG_END_DATE, endDate)
            args.putInt(ARG_MIN_MAG, minMag)
            args.putInt(ARG_MAX_MAG, maxMag)
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

        val startDate = arguments!!.getString(ARG_START_DATE)
        val endDate = arguments!!.getString(ARG_END_DATE)
        val minMag = arguments!!.getInt(ARG_MIN_MAG)
        val maxMag = arguments!!.getInt(ARG_MAX_MAG)

        val startDateButton = view.findViewById<View>(R.id.start_date_button) as Button

        startDateButton.setOnClickListener {
            val sDate = start_date_text_view.text.toString()
            val newFragment = DatePickerFragment.newInstance(START_DATE, sDate)
            newFragment.setTargetFragment(this, DATE_REQUEST)
            newFragment.show(activity!!.supportFragmentManager, "date picker")
        }

        val endDateButton = view.findViewById<View>(R.id.end_date_button) as Button
        endDateButton.setOnClickListener {
            val eDate = end_date_text_view.text.toString()
            val newFragment = DatePickerFragment.newInstance(END_DATE, eDate)
            newFragment.setTargetFragment(this, DATE_REQUEST)
            newFragment.show(activity!!.supportFragmentManager, "date picker")
        }

        val startDateTextView = view.findViewById<View>(R.id.start_date_text_view) as TextView
        startDateTextView.setText(startDate)

        val endDateTextView = view.findViewById<View>(R.id.end_date_text_view) as TextView
        endDateTextView.setText(endDate)

        val okButton = view.findViewById<View>(R.id.ok_button) as Button
        okButton.setOnClickListener {
            val minmag = min_mag_spinner.selectedItem.toString().toInt()
            val maxmag = max_mag_spinner.selectedItem.toString().toInt()
            val startdate = start_date_text_view.text.toString()
            val enddate = end_date_text_view.text.toString()
            val intent = Intent()
            intent.putExtra(EXTRA_START_DATE, startdate)
            intent.putExtra(EXTRA_END_DATE, enddate)
            intent.putExtra(EXTRA_MIN_MAG, minmag)
            intent.putExtra(EXTRA_MAX_MAG, maxmag)
            sendResult(startdate, enddate, minmag, maxmag)
            dismiss()
        }

        val cancelButton = view.findViewById<View>(R.id.cancel_button) as Button
        cancelButton.setOnClickListener {
            dismiss()
        }

        val minMagSpinner = view.findViewById<View>(R.id.min_mag_spinner) as Spinner
        minMagSpinner.setSelection(minMag-1)

        val maxMagSpinner = view.findViewById<View>(R.id.max_mag_spinner) as Spinner
        maxMagSpinner.setSelection(maxMag-1)
        return view
    }

    override fun onDateSelected(day: String, month: String, year: String, id: Int) {
        if(id == START_DATE) {
            start_date_text_view.text = getFormatDate(day, month, year)
        } else if(id == END_DATE) {
            end_date_text_view.text = getFormatDate(day, month, year)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val id = data?.getIntExtra(DatePickerFragment.EXTRA_ID, -1)
        val year = data?.getStringExtra(DatePickerFragment.EXTRA_YEAR)
        val month = data?.getStringExtra(DatePickerFragment.EXTRA_MONTH)
        val day = data?.getStringExtra(DatePickerFragment.EXTRA_DAY)
        if(id == START_DATE) {
            start_date_text_view.text = getFormatDate(day!!, month!!, year!!)
        } else if(id == END_DATE) {
            end_date_text_view.text = getFormatDate(day!!, month!!, year!!)
        }
    }

    fun sendResult(startDate: String, endDate: String, minMag: Int, maxMag: Int) {
        if(targetFragment != null) {
            var intent = Intent()
            intent.putExtra(EarthquakeOptionsDialog.EXTRA_START_DATE, startDate)
            intent.putExtra(EarthquakeOptionsDialog.EXTRA_END_DATE, endDate)
            intent.putExtra(EarthquakeOptionsDialog.EXTRA_MIN_MAG, minMag)
            intent.putExtra(EarthquakeOptionsDialog.EXTRA_MAX_MAG, maxMag)
            targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
        } else {
            if(activity is EarthquakeOptionsDialog.OnOptionsSelectedListener) {
                var listener: EarthquakeOptionsDialog.OnOptionsSelectedListener? = null
                listener = activity as EarthquakeOptionsDialog.OnOptionsSelectedListener
                listener?.onOptionsSelected(startDate, endDate, minMag, maxMag)
            }
        }
    }

    private fun getFormatDate(day: String, month: String, year: String): String {
        return year + "-" + month + "-" + day
    }
}

package com.intelliviz.quakereport.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.intelliviz.quakereport.QueryPreferences
import com.intelliviz.quakereport.QueryPreferences.MODE_RANGE
import com.intelliviz.quakereport.QueryPreferences.MODE_RECENT
import com.intelliviz.quakereport.R
import kotlinx.android.synthetic.main.earthquake_options.*

class EarthquakeOptionsDialog : DialogFragment(), DatePickerFragment.OnDateSelectedListener {

    var minMagSpinner: Spinner? = null
    var maxMagSpinner: Spinner? = null
    lateinit var magnitudes: Array<String>

    companion object {
        private const val ARG_ID = "id"
        private const val ARG_START_DATE = "start_date"
        private const val ARG_END_DATE = "end_date"
        private const val ARG_MIN_MAG = "min_mag"
        private const val ARG_MAX_MAG = "max_mag"
        private const val ARG_NUM_DAYS = "num_days"
        private const val ARG_MODE = "mode"
        private const val START_DATE: Int = 1
        private const val END_DATE: Int = 2
        private const val DATE_REQUEST: Int = 3

        const val EXTRA_MODE = ARG_MODE
        const val EXTRA_END_DATE = ARG_END_DATE
        const val EXTRA_START_DATE = ARG_START_DATE
        const val EXTRA_MIN_MAG = ARG_MIN_MAG
        const val EXTRA_MAX_MAG = ARG_MAX_MAG
        const val EXTRA_NUM_DAYS = ARG_NUM_DAYS

        fun newInstance(mode: Int, startDate: String, endDate: String, minMag: Int, maxMag: Int, numDays: Int): EarthquakeOptionsDialog {
            val args = Bundle()
            args.putInt(ARG_MODE, mode)
            args.putString(ARG_START_DATE, startDate)
            args.putString(ARG_END_DATE, endDate)
            args.putInt(ARG_MIN_MAG, minMag)
            args.putInt(ARG_MAX_MAG, maxMag)
            args.putInt(ARG_NUM_DAYS, numDays)
            val fragment = EarthquakeOptionsDialog()
            fragment.arguments = args
            return fragment
        }
    }

    interface OnOptionsSelectedListener {
        fun onOptionsSelected(mode: Int, startDate: String, endDate: String, minMag: Int, maxMag: Int, numDays: Int)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.earthquake_options, container, false)

        val mode = arguments!!.getInt(ARG_MODE)
        val startDate = arguments!!.getString(ARG_START_DATE)
        val endDate = arguments!!.getString(ARG_END_DATE)
        val minMag = arguments!!.getInt(ARG_MIN_MAG)
        val maxMag = arguments!!.getInt(ARG_MAX_MAG)
        val numDays = arguments!!.getInt(ARG_NUM_DAYS)

        val modeGroup =  view.findViewById<View>(R.id.modeRadioGroup) as RadioGroup
        val rangeButton = view.findViewById<View>(R.id.range_button) as RadioButton
        val recentButton = view.findViewById<View>(R.id.recent_button) as RadioButton
        val startDateButton = view.findViewById<View>(R.id.start_date_button) as Button
        val endDateButton = view.findViewById<View>(R.id.end_date_button) as Button
        val startDateLayout = view.findViewById<View>(R.id.start_date_layout) as LinearLayout
        val endDateLayout = view.findViewById<View>(R.id.end_date_layout) as LinearLayout
        val lastNumDaysLayout = view.findViewById<View>(R.id.last_num_days_layout) as LinearLayout
        val lastNumDays = view.findViewById<View>(R.id.last_num_days) as EditText

        if(mode == QueryPreferences.MODE_RANGE) {
            modeGroup.check(R.id.range_button)
            startDateLayout.visibility = View.VISIBLE
            endDateLayout.visibility = View.VISIBLE
            lastNumDaysLayout.visibility = View.GONE
        } else {
            modeGroup.check(R.id.recent_button)
            startDateLayout.visibility = View.GONE
            endDateLayout.visibility = View.GONE
            lastNumDaysLayout.visibility = View.VISIBLE
        }

        lastNumDays.setText(numDays.toString())

        recentButton.setOnClickListener {
            startDateLayout.visibility = View.GONE
            endDateLayout.visibility = View.GONE
            lastNumDaysLayout.visibility = View.VISIBLE
        }

        rangeButton.setOnClickListener {
            startDateLayout.visibility = View.VISIBLE
            endDateLayout.visibility = View.VISIBLE
            lastNumDaysLayout.visibility = View.GONE
        }

        if(mode == MODE_RANGE) {
            modeGroup.check(R.id.range_button)
        } else {
            modeGroup.check(R.id.recent_button)
        }

        magnitudes = resources.getStringArray(R.array.magnitudes)

        startDateButton.setOnClickListener {
            val sDate = start_date_text_view.text.toString()
            val newFragment = DatePickerFragment.newInstance(START_DATE, sDate)
            newFragment.setTargetFragment(this, DATE_REQUEST)
            newFragment.show(activity!!.supportFragmentManager, "date picker")
        }

        endDateButton.setOnClickListener {
            val eDate = end_date_text_view.text.toString()
            val newFragment = DatePickerFragment.newInstance(END_DATE, eDate)
            newFragment.setTargetFragment(this, DATE_REQUEST)
            newFragment.show(activity!!.supportFragmentManager, "date picker")
        }

        val startDateTextView = view.findViewById<View>(R.id.start_date_text_view) as TextView
        startDateTextView.text = startDate

        val endDateTextView = view.findViewById<View>(R.id.end_date_text_view) as TextView
        endDateTextView.text = endDate

        val okButton = view.findViewById<View>(R.id.ok_button) as Button
        okButton.setOnClickListener {
            var newMode = MODE_RANGE
            if(recentButton.isChecked) {
                newMode = MODE_RECENT
            }
            val minmag = min_mag_spinner.selectedItem.toString().toInt()
            val maxmag = max_mag_spinner.selectedItem.toString().toInt()
            val startdate = start_date_text_view.text.toString()
            val enddate = end_date_text_view.text.toString()
            val newNumDays = lastNumDays.text.toString().toInt()
            val intent = Intent()
            intent.putExtra(EXTRA_START_DATE, startdate)
            intent.putExtra(EXTRA_END_DATE, enddate)
            intent.putExtra(EXTRA_MIN_MAG, minmag)
            intent.putExtra(EXTRA_MAX_MAG, maxmag)
            sendResult(newMode, startdate, enddate, minmag, maxmag, newNumDays)
            dismiss()
        }

        val cancelButton = view.findViewById<View>(R.id.cancel_button) as Button
        cancelButton.setOnClickListener {
            dismiss()
        }

        minMagSpinner = view.findViewById<View>(R.id.min_mag_spinner) as Spinner
        minMagSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val minMagnitude = magnitudes[position]
                val maxMagnitude = magnitudes[maxMagSpinner!!.selectedItemPosition]
                if(minMagnitude > maxMagnitude) {
                    maxMagSpinner?.setSelection(position)
                }
            }
        }

        minMagSpinner?.setSelection(minMag-1)

        maxMagSpinner = view.findViewById<View>(R.id.max_mag_spinner) as Spinner
        maxMagSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val maxMagnitude = magnitudes[position]
                val minMagnitude = magnitudes[minMagSpinner!!.selectedItemPosition]
                if(maxMagnitude < minMagnitude) {
                    minMagSpinner?.setSelection(position)
                }
            }
        }

        maxMagSpinner?.setSelection(maxMag-1)
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

    fun sendResult(mode: Int, startDate: String, endDate: String, minMag: Int, maxMag: Int, numDays: Int) {
        if(targetFragment != null) {
            val intent = Intent()
            intent.putExtra(EXTRA_MODE, mode)
            intent.putExtra(EXTRA_START_DATE, startDate)
            intent.putExtra(EXTRA_END_DATE, endDate)
            intent.putExtra(EXTRA_MIN_MAG, minMag)
            intent.putExtra(EXTRA_MAX_MAG, maxMag)
            intent.putExtra(EXTRA_NUM_DAYS, numDays)
            targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
        } else {
            if(activity is OnOptionsSelectedListener) {
                //var listener: EarthquakeOptionsDialog.OnOptionsSelectedListener? = null
                val listener = activity as OnOptionsSelectedListener
                listener.onOptionsSelected(mode, startDate, endDate, minMag, maxMag, numDays)
            }
        }
    }

    private fun getFormatDate(day: String, month: String, year: String): String {
        return year + "-" + month + "-" + day
    }
}

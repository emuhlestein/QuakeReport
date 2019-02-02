package com.intelliviz.quakereport.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.intelliviz.quakereport.R
import kotlinx.android.synthetic.main.earthquake_trends_options.*

class EarthquakeTrendsOptionsDialog : DialogFragment() {
    var minMagSpinner: Spinner? = null
    var maxMagSpinner: Spinner? = null
    var yearEditText: EditText? = null
    lateinit var magnitudes: Array<String>
    companion object {
        private const val ARG_YEAR = "year"
        private const val ARG_MIN_MAG = "min_mag"
        private const val ARG_MAX_MAG = "max_mag"
        const val EXTRA_MIN_MAG = ARG_MIN_MAG
        const val EXTRA_MAX_MAG = ARG_MAX_MAG
        const val EXTRA_YEAR = ARG_YEAR

        fun newInstance(year: Int, minMag: Int, maxMag: Int): EarthquakeTrendsOptionsDialog {
            val args = Bundle()
            args.putInt(EarthquakeTrendsOptionsDialog.ARG_YEAR, year)
            args.putInt(EarthquakeTrendsOptionsDialog.ARG_MIN_MAG, minMag)
            args.putInt(EarthquakeTrendsOptionsDialog.ARG_MAX_MAG, maxMag)
            val fragment = EarthquakeTrendsOptionsDialog()
            fragment.arguments = args
            return fragment
        }
    }

    interface OnTrendOptionsSelectedListener {
        fun onOptionsSelected(year: Int, minMag: Int, maxMag: Int)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.earthquake_trends_options, container, false)

        val year = arguments!!.getInt(EarthquakeTrendsOptionsDialog.ARG_YEAR)
        val minMag = arguments!!.getInt(EarthquakeTrendsOptionsDialog.ARG_MIN_MAG)
        val maxMag = arguments!!.getInt(EarthquakeTrendsOptionsDialog.ARG_MAX_MAG)


        yearEditText = view.findViewById<View>(R.id.year_edit_text) as EditText
        minMagSpinner = view.findViewById<View>(R.id.min_mag_spinner) as Spinner
        maxMagSpinner = view.findViewById<View>(R.id.max_mag_spinner) as Spinner

        yearEditText!!.setText(year.toString())

        magnitudes = resources.getStringArray(R.array.magnitudes)

        val okButton = view.findViewById<View>(R.id.ok_button) as Button
        okButton.setOnClickListener {
            val minMagSpinner = view.findViewById<View>(R.id.min_mag_spinner) as Spinner
            val maxMagSpinner = view.findViewById<View>(R.id.max_mag_spinner) as Spinner
            val minmag = minMagSpinner.selectedItem.toString().toInt()
            val maxmag = maxMagSpinner.selectedItem.toString().toInt()
            val yearInt = year_edit_text.text.toString().toInt()
            val intent = Intent()
            intent.putExtra(EarthquakeTrendsOptionsDialog.EXTRA_YEAR, yearInt)
            intent.putExtra(EarthquakeTrendsOptionsDialog.EXTRA_MIN_MAG, minmag)
            intent.putExtra(EarthquakeTrendsOptionsDialog.EXTRA_MAX_MAG, maxmag)
            sendResult(year, minmag, maxmag)
            dismiss()
        }

        val cancelButton = view.findViewById<View>(R.id.cancel_button) as Button
        cancelButton.setOnClickListener {
            dismiss()
        }


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

    fun sendResult(year: Int, minMag: Int, maxMag: Int) {
        if(targetFragment != null) {
            val intent = Intent()
            intent.putExtra(EarthquakeTrendsOptionsDialog.EXTRA_YEAR, year)
            intent.putExtra(EarthquakeTrendsOptionsDialog.EXTRA_MIN_MAG, minMag)
            intent.putExtra(EarthquakeTrendsOptionsDialog.EXTRA_MAX_MAG, maxMag)
            targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
        } else {
            if(activity is EarthquakeTrendsOptionsDialog.OnTrendOptionsSelectedListener) {
                //var listener: EarthquakeOptionsDialog.OnOptionsSelectedListener? = null
                val listener = activity as EarthquakeTrendsOptionsDialog.OnTrendOptionsSelectedListener
                listener.onOptionsSelected(year, minMag, maxMag)
            }
        }
    }
}

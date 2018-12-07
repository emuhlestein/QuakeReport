package com.intelliviz.quakereport

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import java.util.*

class DatePickerFragment : DialogFragment() {

    var listener: DatePickerFragment.OnDateSelectedListener? = null

    interface OnDateSelectedListener {
        fun onDateSelected(day: String, month: String, year: String)
    }

    private val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, day ->
        if(activity is OnDateSelectedListener ) {
            listener = activity as OnDateSelectedListener
            listener?.onDateSelected("" + view.dayOfMonth, "" + (view.month + 1), "" + view.year)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(activity!!, dateSetListener, year, month, day)
    }
}

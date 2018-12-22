package com.intelliviz.quakereport.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.DialogFragment

class DatePickerFragment : DialogFragment() {

    var mId: Int = 0
    var listener: OnDateSelectedListener? = null

    interface OnDateSelectedListener {
        fun onDateSelected(day: String, month: String, year: String, id: Int)
    }

    companion object {
        val EXTRA_YEAR = "year"
        val EXTRA_MONTH = "month"
        val EXTRA_DAY = "day"
        val EXTRA_ID = "id"
        private val ARG_ID = "id"
        private val ARG_DATE = "date"
        fun newInstance(id: Int, date: String): DatePickerFragment {
            val args: Bundle = Bundle()
            args.putInt(ARG_ID, id)
            args.putString(ARG_DATE, date)
            val fragment = DatePickerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, day ->
        if(activity is OnDateSelectedListener) {
            listener = activity as OnDateSelectedListener
            listener?.onDateSelected("" + view.dayOfMonth, "" + (view.month + 1), "" + view.year, mId)
        } else if(targetFragment != null) {
            val intent = Intent()
            intent.putExtra(EXTRA_ID, mId)
            intent.putExtra(EXTRA_YEAR, "" + view.year)
            intent.putExtra(EXTRA_MONTH, "" + (view.month + 1))
            intent.putExtra(EXTRA_DAY, "" + view.dayOfMonth)
            targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        mId = arguments!!.getInt(ARG_ID)
        val date = arguments!!.getString(ARG_DATE)


        return DatePickerDialog(activity!!, dateSetListener, getYear(date), getMonth(date), getDay(date))
    }

    private fun getDay(date: String): Int {
        val tokens: List<String> = date.split("-")
        return tokens[2].toInt()
    }

    private fun getMonth(date: String): Int {
        val tokens: List<String> = date.split("-")
        return tokens[1].toInt() - 1
    }

    private fun getYear(date: String): Int {
        val tokens: List<String> = date.split("-")
        return tokens[0].toInt()
    }
}

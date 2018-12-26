
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
import kotlinx.android.synthetic.main.earthquake_recent_options.*

class EarthquakeRecentOptionsDialog : DialogFragment(){

    var minMagSpinner: Spinner? = null
    var maxMagSpinner: Spinner? = null
    lateinit var magnitudes: Array<String>

    companion object {
        private const val ARG_ID = "id"
        private const val ARG_NUM_DAYS = "num_days"
        private const val ARG_MIN_MAG = "min_mag"
        private const val ARG_MAX_MAG = "max_mag"
        private const val DATE_REQUEST: Int = 3

        const val EXTRA_MIN_MAG = "min_mag"
        const val EXTRA_MAX_MAG = "max_mag"
        const val EXTRA_NUM_DAYS = "num_days"

        fun newInstance(id: Int, numDays: Int, minMag: Int, maxMag: Int): EarthquakeRecentOptionsDialog {
            val args = Bundle()
            args.putInt(ARG_ID, id)
            args.putInt(ARG_NUM_DAYS, numDays)
            args.putInt(ARG_MIN_MAG, minMag)
            args.putInt(ARG_MAX_MAG, maxMag)
            val fragment = EarthquakeRecentOptionsDialog()
            fragment.arguments = args
            return fragment
        }
    }

    interface OnOptionsSelectedListener {
        fun onOptionsSelected(numDays: Int, minMag: Int, maxMag: Int)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.earthquake_recent_options, container, false)

        val numDays = arguments!!.getInt(ARG_NUM_DAYS)
        val minMag = arguments!!.getInt(ARG_MIN_MAG)
        val maxMag = arguments!!.getInt(ARG_MAX_MAG)

        magnitudes = resources.getStringArray(R.array.magnitudes)

        val lastNumDays = view.findViewById<EditText>(R.id.last_num_days)
        lastNumDays.setText(numDays.toString())

        val okButton = view.findViewById<View>(R.id.ok_button) as Button
        okButton.setOnClickListener {
            val minmag = min_mag2_spinner.selectedItem.toString().toInt()
            val maxmag = max_mag2_spinner.selectedItem.toString().toInt()
            val numDays = last_num_days.text.toString().toInt()
            val intent = Intent()
            intent.putExtra(EXTRA_MIN_MAG, minmag)
            intent.putExtra(EXTRA_MAX_MAG, maxmag)
            intent.putExtra(EXTRA_NUM_DAYS, numDays)
            sendResult(numDays, minmag, maxmag)
            dismiss()
        }

        val cancelButton = view.findViewById<View>(R.id.cancel_button) as Button
        cancelButton.setOnClickListener {
            dismiss()
        }

        minMagSpinner = view.findViewById<View>(R.id.min_mag2_spinner) as Spinner
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

        maxMagSpinner = view.findViewById<View>(R.id.max_mag2_spinner) as Spinner
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

    fun sendResult(numDays: Int, minMag: Int, maxMag: Int) {
        if(targetFragment != null) {
            val intent = Intent()
            intent.putExtra(EXTRA_NUM_DAYS, numDays)
            intent.putExtra(EXTRA_MIN_MAG, minMag)
            intent.putExtra(EXTRA_MAX_MAG, maxMag)
            targetFragment!!.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
        } else {
            if(activity is OnOptionsSelectedListener) {
                //var listener: EarthquakeRangeOptionsDialog.OnOptionsSelectedListener? = null
                val listener = activity as OnOptionsSelectedListener
                listener.onOptionsSelected(numDays, minMag, maxMag)
            }
        }
    }

    private fun getFormatDate(day: String, month: String, year: String): String {
        return year + "-" + month + "-" + day
    }
}

package cz.muni.fi.pv239.teamup

import android.app.Activity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.location.places.ui.PlacePicker
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.activity_add_event.*
import java.text.SimpleDateFormat
import java.util.*
import android.widget.Toast
import com.google.android.gms.location.places.Place
import android.content.Intent




class AddEventActivity :
        AppCompatActivity(),
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {

    private val PLACE_PICKER_REQUEST = 1

    private lateinit var datePicker: DatePickerDialog
    private lateinit var timePicker: TimePickerDialog

    private var dateTime: Calendar = Calendar.getInstance()
    private val dateFormatter = SimpleDateFormat("dd.MM.yyyy")
    private val timeFormatter = SimpleDateFormat("HH:mm")

    private lateinit var location: Place

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)

        val now = Calendar.getInstance()
        datePicker = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        )
        timePicker = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        )

        date_view.text = formatDate()
        time_view.text = timeFormatter.format(now.time)
    }

    fun onDatePickerClick(v: View) {
        datePicker.show(fragmentManager, "Datepickerdialog")
    }

    fun onTimePickerClick(v: View) {
        timePicker.show(fragmentManager, "Timepickerdialog")
    }

    fun onLocationPickerClick(v: View) {
        val builder = PlacePicker.IntentBuilder()
        startActivityForResult(builder.build(this), 1)
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
        dateTime.set(Calendar.MINUTE, minute)
        time_view.text = timeFormatter.format(dateTime.time)
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        dateTime.set(year, monthOfYear, dayOfMonth)
        date_view.text = formatDate()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                val place = PlacePicker.getPlace(this, data)
                location = place
                location_view.text = place.name
            }
        }
    }

    private fun formatDate(): String {
        val dayOfWeek = when (dateTime.get(Calendar.DAY_OF_WEEK)) {
            2 -> getString(R.string.monday)
            3 -> getString(R.string.tuesday)
            4 -> getString(R.string.wednesday)
            5 -> getString(R.string.thursday)
            6 -> getString(R.string.friday)
            7 -> getString(R.string.saturday)
            1 -> getString(R.string.sunday)
            else -> ""
        }
        return dateFormatter.format(dateTime.time) + "    " + dayOfWeek
    }
}

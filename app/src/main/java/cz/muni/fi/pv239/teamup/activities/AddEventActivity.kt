package cz.muni.fi.pv239.teamup.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.Places
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import cz.muni.fi.pv239.teamup.R
import cz.muni.fi.pv239.teamup.data.SportEvent
import kotlinx.android.synthetic.main.activity_add_event.*
import java.util.*


class AddEventActivity :
        AppCompatActivity(),
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {

    private val PLACE_PICKER_REQUEST = 1

    private val DEFAULT_PLACE_ID = "ChIJEVE_wDqUEkcRsLEUZg-vAAQ" // Brno

    private lateinit var datePicker: DatePickerDialog
    private lateinit var timePicker: TimePickerDialog

    private var dateTime: Calendar = Calendar.getInstance()

    private lateinit var location: Place

    private val database: DatabaseReference by lazy { FirebaseDatabase.getInstance().reference }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)


        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        // set current date
        val now = Calendar.getInstance()
        datePicker = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        )
        // set current time
        timePicker = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        )
        // set default place as location
        Places.getGeoDataClient(this, null).getPlaceById(DEFAULT_PLACE_ID).addOnCompleteListener({ task ->
            if (task.isSuccessful) {
                val places = task.result
                val place = places.get(0)
                location = place.freeze()
                location_view.text = location.name
                places.release()
            } else {
                Log.e(this::class.java.name, "Default place not found.")
            }
        })

        date_view.text = formatDate()
        time_view.text = SportEvent.timeFormatter.format(now.time)
    }

    fun onAddClick(v: View) {
        Log.d(this::class.java.name, "CHECK")
        if (!valid()) return
        val key = database.child("events").push().key
        val shpr = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
        val event = SportEvent(
                key,
                eventNameView.text.toString(),
                shpr.getString("user.uid", null),
                SportEvent.dateFormatter.format(dateTime.time),
                SportEvent.timeFormatter.format(dateTime.time),
                location.id,
                location.name.toString(),
                maxPeopleView.text.toString().toInt(),
                actualPeopleView.text.toString().toInt())
        database.child("events").child(key).setValue(event, { databaseError: DatabaseError?, _ ->
            if (databaseError == null) {
                Snackbar.make(v, getString(R.string.successfulyCreated), Snackbar.LENGTH_LONG).show()
                Handler().postDelayed({ finish() }, 2000)

            } else {
                Snackbar.make(v, databaseError.message, Snackbar.LENGTH_LONG).show()
            }
        })
        Snackbar.make(v, getString(R.string.adding), Snackbar.LENGTH_INDEFINITE).show()
        fabAddView.hide()
    }

    fun onDatePickerClick(v: View) {
        datePicker.show(fragmentManager, "Datepickerdialog")
    }

    fun onTimePickerClick(v: View) {
        timePicker.show(fragmentManager, "Timepickerdialog")
    }

    fun onLocationPickerClick(v: View) {
        val builder = PlacePicker.IntentBuilder()
        builder.setLatLngBounds(location.viewport)
        startActivityForResult(builder.build(this), 1)
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        dateTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
        dateTime.set(Calendar.MINUTE, minute)
        time_view.text = SportEvent.timeFormatter.format(dateTime.time)
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        dateTime.set(year, monthOfYear, dayOfMonth)
        date_view.text = formatDate()
    }

    /**
     * Callback from place picker.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
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
        return SportEvent.dateFormatter.format(dateTime.time) + "    " + dayOfWeek
    }

    private fun valid(): Boolean {
        val maxPeople = maxPeopleView.text.toString().toInt()
        val actualPeople = actualPeopleView.text.toString().toInt()
        if (maxPeople < 2) {
            maxPeopleView.error = getString(R.string.lowMaxPeople)
            return false
        }
        if (actualPeople >= maxPeople) {
            actualPeopleView.error = getString(R.string.lowerActualThanMaxPeople)
            return false
        }
        if (eventNameView.text.toString().isEmpty()) {
            eventNameView.error = getString(R.string.emptyEventName)
            return false
        }
        return true
    }
}

package cz.muni.fi.pv239.teamup.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import cz.muni.fi.pv239.teamup.R
import cz.muni.fi.pv239.teamup.data.SportEvent
import kotlinx.android.synthetic.main.activity_event_detail.*
import kotlinx.android.synthetic.main.activity_main.*

class EventDetailActivity : AppCompatActivity() {

    // map view fragment
    private lateinit var mapFragment: MapFragment

    // database
    private lateinit var database: DatabaseReference

    // event
    private lateinit var event: SportEvent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        // init database
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        database = FirebaseDatabase.getInstance().reference

        // get map fragment
        mapFragment = supportFragmentManager.findFragmentById(R.id.fragment_frame_for_map) as MapFragment

        // fetch sport event from Firebase
        val oneTimeListener = object : ValueEventListener {
            override fun onCancelled(dataSnapshot: DatabaseError?) {
                Snackbar.make(coordinatorLayout, getString(R.string.notCorrectLoad), Snackbar.LENGTH_INDEFINITE).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // the event was received
                event = dataSnapshot.getValue(SportEvent::class.java) ?: throw IllegalStateException("Added Event is null")
                // show on map
                mapFragment.addMarker(event.key, event)
                // set text views
                detailPeopleView.text = event.actualPeople.toString() + " / " + event.maxPeople.toString()
                detailDateView.text = event.date
                detailTimeView.text = event.time
                detailPlaceView.text = event.locationName
            }
        }
        database.child("events").child(intent.getStringExtra("eventKey")).addListenerForSingleValueEvent(oneTimeListener)
    }
}

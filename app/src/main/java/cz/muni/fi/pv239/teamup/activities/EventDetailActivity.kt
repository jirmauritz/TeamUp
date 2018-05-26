package cz.muni.fi.pv239.teamup.activities

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.view.View
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import cz.muni.fi.pv239.teamup.R
import cz.muni.fi.pv239.teamup.data.SportEvent
import kotlinx.android.synthetic.main.activity_event_detail.*
import kotlinx.android.synthetic.main.activity_signin.*
import android.widget.ArrayAdapter
import cz.muni.fi.pv239.teamup.data.User
import kotlinx.android.synthetic.main.participant.*


class EventDetailActivity : AppCompatActivity() {

    // map view fragment
    private lateinit var mapFragment: MapFragment

    // database
    private lateinit var database: DatabaseReference

    // event
    private lateinit var event: SportEvent

    // participants
    private var participants: MutableList<String> = mutableListOf()
    private lateinit var participantsAdapter: ArrayAdapter<String>

    // listeners
    private lateinit var userListener: ValueEventListener
    private lateinit var eventListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)

        // set back buttion on toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        database = FirebaseDatabase.getInstance().reference

        // get map fragment
        mapFragment = supportFragmentManager.findFragmentById(R.id.fragment_frame_for_map) as MapFragment

        // participants adapter
        participantsAdapter = ArrayAdapter(this@EventDetailActivity, R.layout.participant, participants)
        detailParticipantsView.adapter = participantsAdapter

        // fetch user from Firebase
        userListener = object : ValueEventListener {
            override fun onCancelled(dataSnapshot: DatabaseError?) {
                Snackbar.make(coordinatorLayout, getString(R.string.notCorrectLoad), Snackbar.LENGTH_INDEFINITE).show()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // the user was received
                val user = dataSnapshot.getValue(User::class.java) ?: throw IllegalStateException("User is null")
                participants.add(user.displayName)
                // show in view
                participantsAdapter.notifyDataSetChanged()
                // if actual user equals this one, change text to remove user
                val shpr = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
                if (user.uid == shpr.getString("user.uid", null)) {
                    joinButton.text = getString(R.string.removeFromEvent)
                    joinButton.visibility = View.VISIBLE
                }
            }
        }

        // fetch event from Firebase
        eventListener = object : ValueEventListener {
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
                // set title
                supportActionBar?.title = event.name

                // hide button if full
                if (event.actualPeople == event.maxPeople) {
                    joinButton.visibility = View.GONE
                }

                for (uid in event.signedUsers) {
                    database.child("users").child(uid).addListenerForSingleValueEvent(userListener)
                }
            }
        }

        val globalListener = object : ChildEventListener {
            override fun onCancelled(dataSnapshot: DatabaseError?) {
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot?, previousChildName: String?) {
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot?, previousChildName: String?) {
                reload()
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
            }

        }

        database.child("events").addChildEventListener(globalListener)

        reload()
    }

    fun joinButtonAction(v: View) {
        val shpr = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
        if (event.signedUsers.contains(shpr.getString("user.uid", null))) {
            // removal
            event.signedUsers.remove(shpr.getString("user.uid", null))
            event.actualPeople -= 1
            database.child("events").child(event.key).setValue(event, { databaseError: DatabaseError?, _ ->
                if (databaseError == null) {
                    Snackbar.make(v, getString(R.string.removedFromEvent), Snackbar.LENGTH_SHORT).show()
                    joinButton.text = getString(R.string.join)
                } else {
                    Snackbar.make(v, databaseError.message, Snackbar.LENGTH_LONG).show()
                }
            })
            Snackbar.make(v, getString(R.string.removing), Snackbar.LENGTH_INDEFINITE).show()
        } else {
            // join
            event.signedUsers.add(shpr.getString("user.uid", null))
            event.actualPeople += 1
            database.child("events").child(event.key).setValue(event, { databaseError: DatabaseError?, _ ->
                if (databaseError == null) {
                    Snackbar.make(v, getString(R.string.youAreIn), Snackbar.LENGTH_SHORT).show()
                    joinButton.text = getString(R.string.removeFromEvent)
                } else {
                    Snackbar.make(v, databaseError.message, Snackbar.LENGTH_LONG).show()
                }
            })
            Snackbar.make(v, getString(R.string.creating), Snackbar.LENGTH_INDEFINITE).show()
        }
    }

    fun reload() {
        participants.clear()
        database.child("events").child(intent.getStringExtra("eventKey")).addListenerForSingleValueEvent(eventListener)
    }
}

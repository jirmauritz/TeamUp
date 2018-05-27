package cz.muni.fi.pv239.teamup.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import cz.muni.fi.pv239.teamup.R
import cz.muni.fi.pv239.teamup.data.SportEvent
import cz.muni.fi.pv239.teamup.recycler.RecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_my_events.*


class MyEventsActivity : AppCompatActivity() {

    // database
    private lateinit var database: DatabaseReference

    // all events
    private val events = mutableMapOf<String, SportEvent>()

    // sorted events
    private val sortedEvents = sortedSetOf<SportEvent>(
            compareBy({ SportEvent.dateFormatter.parse(it.date) },
                    { SportEvent.timeFormatter.parse(it.time) }))

    // selected
    private var selectedView: View? = null

    // list adapter
    private lateinit var listAdapter: RecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_events)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        database = FirebaseDatabase.getInstance().reference

        // shared preferencies
        val shpr = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)

        // register recycler view
        listAdapter = RecyclerViewAdapter(sortedEvents, { event, view ->
            selectedView?.isSelected = false
            view.isSelected = true
            selectedView = view
            val intent = Intent(this, EventDetailActivity::class.java)
            intent.putExtra("eventKey", event.key)
            startActivity(intent)
        }, shpr.getString("user.uid", null))

        val layoutManager = LinearLayoutManager(applicationContext)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = listAdapter

        val childEventListener = object : ChildEventListener {
            override fun onCancelled(dataSnapshot: DatabaseError?) {
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot?, previousChildName: String?) {
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot?, previousChildName: String?) {
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                val event = events.remove(dataSnapshot.key)
                sortedEvents.remove(event)
                listAdapter.notifyDataSetChanged()
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val event: SportEvent = dataSnapshot.getValue(SportEvent::class.java)
                        ?: throw IllegalStateException("Added Event is null")



                val shpr = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
                shpr.getString("user.uid", null)
                if (event.userUid == shpr.getString("user.uid", null)) {
                    events[dataSnapshot.key] = event
                    sortedEvents.add(event)
                    listAdapter.notifyDataSetChanged()
                }
            }
        }
        database.child("events").addChildEventListener(childEventListener)

    }
}

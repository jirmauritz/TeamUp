package cz.muni.fi.pv239.teamup.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.firebase.ui.auth.AuthUI
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import cz.muni.fi.pv239.teamup.R
import cz.muni.fi.pv239.teamup.data.SportEvent
import cz.muni.fi.pv239.teamup.recycler.RecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    // database
    private lateinit var database: DatabaseReference

    // all events
    private val events = mutableMapOf<String, SportEvent>()

    // selected
    private var selectedView: View? = null

    // list adapter
    private lateinit var listAdapter: RecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        database = FirebaseDatabase.getInstance().reference

        fabMainView.setOnClickListener { view ->
            run { startActivity(Intent(this, AddEventActivity::class.java)) }
        }

        // register recycler view
        listAdapter = RecyclerViewAdapter(events, { event, view ->
            selectedView?.isSelected = false
            view.isSelected = true
            selectedView = view
            val intent = Intent(this, EventDetailActivity::class.java)
            intent.putExtra("eventKey", event.key)
            startActivity(intent)
        })

        val layoutManager = LinearLayoutManager(applicationContext)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = listAdapter
        // division line between items
//        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, layoutManager.orientation)
//        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(applicationContext, R.drawable.line)!!)
//        recyclerView.addItemDecoration(dividerItemDecoration)

        val childEventListener = object : ChildEventListener {
            override fun onCancelled(dataSnapshot: DatabaseError?) {
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot?, previousChildName: String?) {
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot?, previousChildName: String?) {
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                events.remove(dataSnapshot.key)
                listAdapter.notifyDataSetChanged()
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val event: SportEvent = dataSnapshot.getValue(SportEvent::class.java)
                        ?: throw IllegalStateException("Added Event is null")

                val shpr = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE)
                val uid = shpr.getString("user.uid", null)

                val cal = Calendar.getInstance()
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)

                if (SportEvent.getDateWithTime(event.date, event.time).time.after(cal.time)) {
                    events[dataSnapshot.key] = event
                    listAdapter.notifyDataSetChanged()
                }
            }
        }
        database.child("events").addChildEventListener(childEventListener)

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.action_upcoming_events -> {
                startActivity(Intent(this, UpcomingEventsActivity::class.java))
                return true
            }
            R.id.action_my_events -> {
                startActivity(Intent(this, MyEventsActivity::class.java))
                return true
            }
            R.id.action_history -> {
                startActivity(Intent(this, HistoryActivity::class.java))
                return true
            }
            R.id.action_signout -> {
                signOut()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun signOut() {
        AuthUI.getInstance().signOut(this)
        startActivity(Intent(this, SignInActivity::class.java))
    }

    override fun onBackPressed() {
        // do nothing - we don't want to return to sign in page
    }

}

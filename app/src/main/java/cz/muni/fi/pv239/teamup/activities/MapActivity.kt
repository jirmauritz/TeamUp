package cz.muni.fi.pv239.teamup.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import cz.muni.fi.pv239.teamup.R

import kotlinx.android.synthetic.main.activity_map.*

class MapActivity : AppCompatActivity() {

    // map view fragment
    private lateinit var mapFragment: MapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        setSupportActionBar(toolbar)

        fabMapView.setOnClickListener { view ->
            run { startActivity(Intent(this, AddEventActivity::class.java)) }
        }
    }

    override fun onBackPressed() {
        // do nothing - we don't want to return to sign in page
    }

}

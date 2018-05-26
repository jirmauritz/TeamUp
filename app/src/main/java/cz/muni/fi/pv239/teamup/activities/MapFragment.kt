package cz.muni.fi.pv239.teamup.activities


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import cz.muni.fi.pv239.teamup.R
import cz.muni.fi.pv239.teamup.data.SportEvent
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.content.Intent
import android.content.Context.LOCATION_SERVICE
import android.location.LocationManager




/**
 * A simple [Fragment] subclass.
 */
@RuntimePermissions
class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener {

    // tag that is used in the logs
    private val TAG = MapFragment::class.java.name

    // google map fragment reference
    private lateinit var gMap: GoogleMap
    // center of the map
    private lateinit var mapCenter: LatLng
    // markers
    val markers = mutableMapOf<String, Marker>()

    private val defaultZoom: Float by lazy { resources.getString(R.string.defaultZoom).toFloat() }
    private val southBorder: Double by lazy { resources.getString(R.string.southBorder).toDouble() }
    private val westBorder: Double by lazy { resources.getString(R.string.westBorder).toDouble() }
    private val northBorder: Double by lazy { resources.getString(R.string.northBorder).toDouble() }
    private val eastBorder: Double by lazy { resources.getString(R.string.eastBorder).toDouble() }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val rootView: View = inflater.inflate(R.layout.fragment_map, container, false)
                ?: throw IllegalArgumentException("Inflator in MapFragment is null.")

        val mapView = rootView.findViewById<MapView>(R.id.mapView)
        mapView.onCreate(savedInstanceState)

        mapView.onResume() // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(activity?.applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mapView.getMapAsync(this)

        return rootView
    }

    override fun onMapReady(mMap: GoogleMap) {
        gMap = mMap

        enableLocationWithPermissionCheck()

        // set map style from map_style.json, possible to edit on https://mapstyle.withgoogle.com/
        try {
            val success = gMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(activity, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Map style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }

        // disable rotation of the map by two fingers
        gMap.uiSettings.isRotateGesturesEnabled = false

        // set minimal zoom level, so that users dont zoom out to whole europe
        gMap.setMinZoomPreference(defaultZoom)

        // restrict the view on the country only
        val viewBorder = LatLngBounds(
                LatLng(southBorder, westBorder),
                LatLng(northBorder, eastBorder))
        // constrain the camera target to the country bounds
        gMap.setLatLngBoundsForCameraTarget(viewBorder)

        // move the camera to the center of the target country and set minimal zoom
        mapCenter = viewBorder.center
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(viewBorder.center, defaultZoom))

        // set what to do when my location button is clicked
        gMap.setOnMyLocationButtonClickListener(this)
    }

    fun addMarker(key: String, event: SportEvent) {
        // get place
        Places.getGeoDataClient(activity as EventDetailActivity, null).getPlaceById(event.locationId).addOnCompleteListener({ task ->
            if (task.isSuccessful) {
                val places = task.result
                val placeInList = places.get(0)
                val place = placeInList.freeze()
                places.release()
                val marker = gMap.addMarker(MarkerOptions()
                        .position(place.latLng)
                        .alpha(0.9f)
                        .icon(BitmapDescriptorFactory.defaultMarker(22f))
                        .title(event.name)
                        .snippet(event.locationName))
                marker.tag = key
                markers[key] = marker
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.latLng, defaultZoom))
            } else {
                Log.e(this::class.java.name, "Default place not found.")
            }
        })
    }

    override fun onMyLocationButtonClick(): Boolean {
        // check whether GPS enabled
        val lm = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!gps_enabled) {
            // if not enabled, ask user to enable
            val intent = Intent(ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
            return true
        } else {
            return false
        }
    }

    @SuppressLint("MissingPermission")
    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun enableLocation() {
        // For showing a move to my location button
        gMap.isMyLocationEnabled = true
        gMap.uiSettings.isMyLocationButtonEnabled = true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated function
        onRequestPermissionsResult(requestCode, grantResults)
    }


}// Required empty public constructor

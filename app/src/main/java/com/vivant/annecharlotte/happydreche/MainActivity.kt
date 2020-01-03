package com.vivant.annecharlotte.happydreche

import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
//import android.support.design.widget.Snackbar
//import android.support.design.widget.NavigationView
//import android.support.v4.view.GravityCompat
//import android.support.v7.app.ActionBarDrawerToggle
//import android.support.v7.app.AppCompatActivity


import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.vivant.annecharlotte.happydreche.firestore.MarkersUrl
import com.vivant.annecharlotte.happydreche.firestore.Project
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.io.IOException

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    private val TAG = "MainActivity";

    private lateinit var mMap: GoogleMap
    private var latitude:Double=0.toDouble()
    private var longitude:Double=0.toDouble()
    private lateinit var mLastLocation:Location
    private var progressBar: ProgressBar? = null

    //Location
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var  locationCallback: LocationCallback

    //Markers
    private var mMarker:Marker?=null
    private var currentLat: Double = 0.toDouble()
    private var currentLng:Double = 0.toDouble()
    private var currentType: Int = 0
    private lateinit var currentName: String
    private lateinit var currentId: String

    private lateinit var tab_id: Array<String?>
    private lateinit var tab_type: IntArray
    private lateinit var tab_name: Array<String?>
    private lateinit var tab_latitude: DoubleArray
    private lateinit var tab_longitude: DoubleArray

    //private var projectViewModel: ProjectViewModel? = null

    companion object {
        private const val MY_PERMISSION_CODE: Int = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        configureProgressBar()
        showProgressBar()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //Request runtime permission
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkLocationPermission()) {
                buildLocationRequest();
                buidLocationCallback();

                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()
                );
            }
        }
        else {
            buildLocationRequest();
            buidLocationCallback();

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )
        }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun buidLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                mLastLocation = p0!!.locations.get(p0!!.locations.size-1) //Get last location

                if (mMarker != null) {
                    mMarker!!.remove()
                }

                latitude = mLastLocation.latitude
                longitude = mLastLocation.longitude

                val latLng = LatLng(latitude, longitude)

                //Move camera
                mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                mMap!!.animateCamera(CameraUpdateFactory.zoomTo(10f))
                mMap!!.setPadding(0,0,16,304)
            }
        }
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f
    }

    private fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))
                ActivityCompat.requestPermissions(this, arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ), MY_PERMISSION_CODE)
            else
                ActivityCompat.requestPermissions(this, arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ), MY_PERMISSION_CODE)
            return false
        }
        else return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSION_CODE -> {
                if (grantResults.size>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                        if (checkLocationPermission()) {
                            buildLocationRequest();
                            buidLocationCallback();

                            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                            fusedLocationProviderClient.requestLocationUpdates(
                                locationRequest,
                                locationCallback,
                                Looper.myLooper())

                            mMap!!.isMyLocationEnabled = true
                        }
                }
                else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_map  -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_search -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.onStop()
    }

    override fun onMapReady(googleMap: GoogleMap) {
       mMap = googleMap

        // Init Goole Play Services
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mMap!!.isMyLocationEnabled = true
            }
        }
        else
            mMap!!.isMyLocationEnabled = true

        // Enable zoom control
        mMap.uiSettings.isZoomControlsEnabled = true

        /*val Lille = LatLng(50.63, 3.07)
        mMap.addMarker(MarkerOptions().position(Lille).title("Happy Lille"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Lille))*/

        getData()
    }


    //------------------------------------------------
    // Data
    //------------------------------------------------

    @Throws(IOException::class)
    fun getData() {
        Log.d(TAG, "getData")
        val projet1 = Project("001", "projet_1", "contact@projet1", "www.projet1", "012456789", "descripton projet 1", 1,
          "www.photo.projet1", "4", "rue de Soissons", "", "60800", "Crépy-en-Valois", "France")
        val projet2 = Project("002", "projet_2", "contact@projet2", "www.projet2", "012456789", "descripton projet 2", 1,
            "www.photo.projet2", "6", "rue Alexandre Dumas", "", "60800", "Crépy-en-Valois", "France")

        val projets1et2 = listOf(projet1, projet2)
        updateProjectList(projets1et2)
        //configureViewModel()
        //this.getAllProjects()
    }

/*    private fun configureViewModel() {
        Log.d(TAG, "configureViewModel")
        val mViewModelFactory = Injection.provideViewModelFactory(this)
        this.propertyViewModel = ViewModelProviders.of(this, mViewModelFactory).get(PropertyViewModel::class.java!!)
    }

    private fun getAllProjects() {
        this.propertyViewModel.getAllProperty().observe(this, ???({ this.updatePropertyList(it) }))
    }*/

    private fun updateProjectList(projects: List<Project>) {
        //tab_id = emptyArray()
        //tab_id = arrayOf("id1", "id2")
        tab_id = arrayOfNulls(projects.size)
        tab_type = IntArray(projects.size)
        //tab_name = arrayOf("nom1", "nom2")
        tab_name = arrayOfNulls(projects.size)
        tab_latitude = DoubleArray(projects.size)
        tab_longitude = DoubleArray(projects.size)

        for (i in projects) {

        }

        for (i in projects.indices) {
            loadMarkerInfos(projects[i], i)
        }

        hideProgressBar()
    }

    fun loadMarkerInfos(currentProject: Project, i: Int) {
        Log.d(TAG, "i= "+i)
        currentType = currentProject.projectType
        tab_type[i] = currentType
        Log.d(TAG, "type= " + currentType)


        currentId = currentProject.projectId
        tab_id[i] = currentId
        Log.d(TAG, "id: " +currentId)

        currentName = currentProject.projectName
        tab_name[i] = currentName
        Log.d(TAG, "name: " +currentName)

        tab_latitude[i] = setPropertyLatLng(
            Address(
                currentProject.projectId,
                currentProject.projectAddressNumber,
                currentProject.projectAddressStreet,
                currentProject.projectAddressStreet2,
                currentProject.projectAddressZipcode,
                currentProject.projectAddressTown,
                currentProject.projectAddressCountry
            )
        ).latitude
        Log.d(TAG,"latitude: "+ tab_latitude[i].toString())
        tab_longitude[i] = setPropertyLatLng(
            Address(
                currentProject.projectId,
                currentProject.projectAddressNumber,
                currentProject.projectAddressStreet,
                currentProject.projectAddressStreet2,
                currentProject.projectAddressZipcode,
                currentProject.projectAddressTown,
                currentProject.projectAddressCountry
            )
        ).longitude
        Log.d(TAG,"longitude: "+ tab_longitude[i].toString())

        addMarker(LatLng(tab_latitude[i], tab_longitude[i]), tab_type[i], tab_name[i])
    }

    private fun addMarker(latLng: LatLng, type: Int,name: String?) {
// Ajuster la couleur du marqueur avec type
        var markerColor : Float = BitmapDescriptorFactory.HUE_AZURE
        when (type) {
            1 -> markerColor = BitmapDescriptorFactory.HUE_AZURE
            2 -> markerColor = BitmapDescriptorFactory.HUE_BLUE
            3 -> markerColor = BitmapDescriptorFactory.HUE_ORANGE
            4 -> markerColor = BitmapDescriptorFactory.HUE_RED
            5 -> markerColor = BitmapDescriptorFactory.HUE_GREEN
        }

        val options = MarkerOptions()
            .position(latLng)
            .title(name)
            .icon(BitmapDescriptorFactory.defaultMarker(markerColor))

        mMap.addMarker(options)
    }


    //------------------------------------------------
    // Geocoder
    //------------------------------------------------

    fun setPropertyLatLng(address: Address): LatLng {
        try {
            currentLat = geocoder(address).latitude
        } catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            currentLng = geocoder(address).longitude
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return LatLng(currentLat, currentLng)
    }

    @Throws(IOException::class)
    fun geocoder(currentAddress: Address): LatLng {
        val markersUrl = MarkersUrl()
        val location = markersUrl.createGeocoderUrl(
            currentAddress.numberInStreet,
            currentAddress.street,
            currentAddress.zipcode,
            currentAddress.town,
            currentAddress.country
        )
        val gc = Geocoder(this)
        val list = gc.getFromLocationName(location, 1)
        val add = list[0]
        val locality = add.locality
        val lat = add.latitude
        val lng = add.longitude
        return LatLng(lat, lng)
    }

    //--------------------------------------------------
    // Design
    //--------------------------------------------------
    private fun configureProgressBar() {
        progressBar = findViewById<ProgressBar>(R.id.map_progress_bar) as ProgressBar
    }

    private fun showProgressBar() {
        progressBar?.visibility=View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar?.visibility=View.GONE
    }

}

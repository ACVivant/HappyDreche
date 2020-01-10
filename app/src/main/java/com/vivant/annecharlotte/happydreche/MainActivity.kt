package com.vivant.annecharlotte.happydreche

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.transition.FragmentTransitionSupport
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
import com.google.firebase.database.*
import com.vivant.annecharlotte.happydreche.ListFragment.OnFragmentInteractionListener
import com.vivant.annecharlotte.happydreche.firestore.MarkersUrl
import com.vivant.annecharlotte.happydreche.firestore.Project
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_list.*
import java.io.IOException

class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener,
MapFragment.OnFragmentInteractionListener, ListFragment.OnFragmentInteractionListener, PresentationFragment.OnFragmentInteractionListener{


    private val TAG = "MainActivity";

    //Fragments
    lateinit var mapFragment: MapFragment
    lateinit var listFragment: ListFragment
    lateinit var presentationFragment: PresentationFragment

    companion object {
        private const val MY_PERMISSION_CODE: Int = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        mapFragment = MapFragment.newInstance()
        listFragment = ListFragment.newInstance()
        presentationFragment = PresentationFragment.newInstance("presentation fragment", "OK")

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.logout_menu, menu)
        return true
    }

   override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

   override fun onOptionsItemSelected(item: MenuItem): Boolean {
       when (item.itemId) {
           R.id.top_logout -> {
               Toast.makeText(this, "Bonne nouvelle onOption", Toast.LENGTH_LONG).show()
               return true
           }
           else -> return false
       }
   }

     override fun onNavigationItemSelected(item: MenuItem): Boolean {
       // Handle navigation view item clicks here.

     //  displayScreen(item.itemId)
     when (item.itemId) {
         R.id.nav_pres  -> {
             supportFragmentManager
                 .beginTransaction()
                 .replace(R.id.fragment_container, presentationFragment)
                 .addToBackStack(presentationFragment.toString())
                 .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                 .commit()
         }
           R.id.nav_map  -> {
               supportFragmentManager
                   .beginTransaction()
                   .replace(R.id.fragment_container, mapFragment)
                   .addToBackStack(mapFragment.toString())
                   .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                   .commit()
           }

           R.id.nav_list -> {
               supportFragmentManager
                   .beginTransaction()
                   .replace(R.id.fragment_container, listFragment)
                   .addToBackStack(listFragment.toString())
                   .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                   .commit()
           }
           R.id.nav_search -> {

           }

         R.id.nav_add -> {

         }
           R.id.nav_share -> {

           }
           R.id.nav_send -> {

           }
       }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onFragmentInteraction(uri: Uri) {
        Log.d(TAG, "On Fragment Interaction")
    }
}

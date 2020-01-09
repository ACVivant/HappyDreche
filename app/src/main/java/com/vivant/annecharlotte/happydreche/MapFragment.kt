package com.vivant.annecharlotte.happydreche


import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import com.vivant.annecharlotte.happydreche.firestore.MarkersUrl
import com.vivant.annecharlotte.happydreche.firestore.Project
import kotlinx.android.synthetic.main.fragment_map.*
import java.io.IOException

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_ID = "param_id"
private const val ARG_NAME = "param_name"
private const val ARG_TYPE = "param_type"
private const val ARG_STREET_NUMBER = "param_number_in_street"
private const val ARG_STREET = "param_street"
private const val ARG_ZIPCODE = "param_zipcode"
private const val ARG_TOWN = "param_town"
private const val ARG_COUNTRY = "param_country"
private const val TAG = "MapFragment"
private const val MY_PERMISSION_CODE: Int = 1000

/**
 * A simple [Fragment] subclass.
 *
 */
class MapFragment : Fragment(), OnMapReadyCallback {

    // TODO: Rename parameter arguments, choose names that match

    var progressBar: ProgressBar? =null
    private lateinit var mMap: GoogleMap

    private var latitude:Double=0.toDouble()
    private var longitude:Double=0.toDouble()
    private lateinit var mLastLocation: Location

    //Location
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var  locationCallback: LocationCallback
    var myLatLng: LatLng?=null

    //Markers
    private var mMarker: Marker?=null
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

    // Data
    lateinit var ref: DatabaseReference
    lateinit var projectsList: MutableList<Project>


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var paramId: Array<String>? = null
    private var paramName: Array<String>? = null
    private var paramType: IntArray? = null
    private var paramStreetNumber : Array<String>? = null
    private var paramStreet: Array<String>? = null
    private var paramZipcode: Array<String>? = null
    private var paramTown: Array<String>? = null
    private var paramCountry: Array<String>? = null

    private var listener: OnFragmentInteractionListener? = null


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.d(TAG, "onActivityCreated start")
        map_view.onCreate(savedInstanceState)
        map_view.onResume()
        map_view.getMapAsync(this)
        Log.d(TAG, "onActivityCreated end")
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(TAG, "onCreateView")
        // Inflate the layout for this fragment
        val view:View = inflater.inflate(R.layout.fragment_map, container, false)

        progressBar=view.findViewById<ProgressBar>(R.id.map_progress_bar) as ProgressBar

        showProgressBar()

        Log.d(TAG, "onCreateView")
        runPermissions()

        return view

    }

    private fun runPermissions() {
        Log.d(TAG, "runPermissions")
        //Request runtime permission
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkLocationPermission()) {
                    buildLocationRequest()
                    buildLocationCallback()

                    fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                    fusedLocationProviderClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.myLooper()
                    )
                }
            }
            else {
                buildLocationRequest()
                buildLocationCallback()

                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.myLooper()
                )
            }
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach")
        if (context is OnFragmentInteractionListener) {
            listener = context
            arguments?.let {
                /*paramId = it.getStringArray(ARG_ID)
                paramName = it.getStringArray(ARG_NAME)
                paramType = it.getIntArray(ARG_TYPE)
                paramStreetNumber = it.getStringArray(ARG_STREET_NUMBER)
                paramStreet = it.getStringArray(ARG_STREET)
                paramZipcode = it.getStringArray(ARG_ZIPCODE)
                paramTown = it.getStringArray(ARG_TOWN)
                paramCountry = it.getStringArray(ARG_COUNTRY)

                Log.d(TAG,"print: " + paramName.toString())*/
            }
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach")
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        /*fun newInstance(paramId: Array<String>, paramName: Array<String>, paramType: IntArray,
                        paramStreetNumber : Array<String>, paramStreet: Array<String>, paramZipcode: Array<String>, paramTown: Array<String>, paramCountry: Array<String> ) =
            MapFragment().apply {
                arguments = Bundle().apply {
                    putStringArray(ARG_ID, paramId)
                    putStringArray(ARG_NAME, paramName)
                    putIntArray(ARG_TYPE, paramType)
                    putStringArray(ARG_STREET_NUMBER, paramStreetNumber)
                    putStringArray(ARG_STREET, paramStreet)
                    putStringArray(ARG_ZIPCODE, paramZipcode)
                    putStringArray(ARG_TOWN, paramTown)
                    putStringArray(ARG_COUNTRY, paramCountry)
                }
            }*/
        fun newInstance() =
            MapFragment().apply {
                arguments = Bundle().apply {}
            }
    }

    //--------------------------------------------------
    // Map
    //--------------------------------------------------
    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(TAG, "onMapReady")

        googleMap?.let { mMap = it }

        // Init Goole Play Services
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    context!!,
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

        //Move camera
       if(myLatLng!=null) mMap!!.moveCamera(CameraUpdateFactory.newLatLng(myLatLng))
        mMap!!.animateCamera(CameraUpdateFactory.zoomTo(9f))
        mMap!!.setPadding(0,0,16,304)

        getData()
    }

    override fun onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        super.onStop()
    }

    private fun checkLocationPermission(): Boolean {
        Log.d(TAG, "checkLocationPermission")
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION))
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ), MY_PERMISSION_CODE)
            else
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ), MY_PERMISSION_CODE)
            return false
        }
        else return true
    }

    private fun buildLocationRequest() {
        Log.d(TAG, "buildLocationRequest")
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f
    }

    private fun buildLocationCallback() {
        Log.d(TAG, "buildLocationCallback")
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                Log.d(TAG, "buildLocationCallback inside")
                mLastLocation = p0!!.locations.get(p0!!.locations.size-1) //Get last location

                if (mMarker != null) {
                    mMarker!!.remove()
                }

                latitude = mLastLocation.latitude
                longitude = mLastLocation.longitude

                val latLng = LatLng(latitude, longitude)
                myLatLng = latLng

                //Move camera
                mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                mMap!!.animateCamera(CameraUpdateFactory.zoomTo(9f))
                mMap!!.setPadding(0,0,16,304)
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Log.d(TAG, "onRequestPermissionsResult")
        when (requestCode) {
            MY_PERMISSION_CODE -> {
                if (grantResults.size>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                        if (checkLocationPermission()) {
                            buildLocationRequest()
                            buildLocationCallback()

                            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.requireActivity())
                            fusedLocationProviderClient.requestLocationUpdates(
                                locationRequest,
                                locationCallback,
                                Looper.myLooper())

                            mMap!!.isMyLocationEnabled = true
                        }
                }
                else {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    //--------------------------------------------------
    // Markers
    //--------------------------------------------------
    private fun updateProjectList(projects: Array<Project>) {
        Log.d(TAG, "updateProjectList")
        Log.d(TAG, "taille " + projects.size)

        tab_id = arrayOfNulls(projects.size)
        tab_type = IntArray(projects.size)
        //tab_name = arrayOf("nom1", "nom2")
        tab_name = arrayOfNulls(projects.size)
        tab_latitude = DoubleArray(projects.size)
        tab_longitude = DoubleArray(projects.size)

        for (i in projects.indices) {
            Log.d(TAG, "indice: " +i)
            loadMarkerInfos(projects[i], i)
        }

        hideProgressBar()
    }

    fun loadMarkerInfos(currentProject: Project, i: Int) {
        Log.d(TAG, "i= "+i)
        currentType = currentProject.projectType
        tab_type[i] = currentType
        Log.d(TAG, "type= " + currentType)


        currentName = currentProject.projectName
        tab_name[i] = currentName
        Log.d(TAG, "name: " +currentName)

        tab_latitude[i] = setPropertyLatLng(
            Address(
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
        var markerColor : Float = HUE_AZURE
        when (type) {
            1 -> markerColor = HUE_AZURE
            2 -> markerColor = HUE_BLUE
            3 -> markerColor = HUE_ORANGE
            4 -> markerColor = HUE_RED
            5 -> markerColor = HUE_GREEN
        }

        val options = MarkerOptions()
            .position(latLng)
            .title(name)
            .icon(defaultMarker(markerColor))

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
         val gc = Geocoder(context)
         val list = gc.getFromLocationName(location, 1)
         val add = list[0]
         val locality = add.locality
         val lat = add.latitude
         val lng = add.longitude
         return LatLng(lat, lng)
     }

    //--------------------------------------------------
    // Data
    //--------------------------------------------------
  @Throws(IOException::class)
    fun getData() {
        Log.d(TAG, "getData")

        projectsList = mutableListOf()

        ref = FirebaseDatabase.getInstance().getReference("projects")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0!!.exists()) {
                    projectsList.clear()

                    for(h in p0.children) {
                        val project = h.getValue(Project::class.java)
                        projectsList.add(project!!)

                        Log.d(TAG, "name: "+project!!.projectName)

                    }
                }
                updateProjectList(projectsList.toTypedArray())
            }
        })
    }


    //--------------------------------------------------
    // Design
    //--------------------------------------------------

      private fun showProgressBar() {
          progressBar?.visibility=View.VISIBLE
      }

      private fun hideProgressBar() {
          progressBar?.visibility=View.GONE
      }



}

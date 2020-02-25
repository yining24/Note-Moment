package com.angela.notemoment.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.angela.notemoment.data.Note
import com.angela.notemoment.databinding.FragmentMymapBinding
import com.angela.notemoment.ext.getVmFactory
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.GoogleMap
import android.app.Activity
import android.content.Context
import com.angela.notemoment.*
import com.angela.notemoment.R
import com.google.android.gms.maps.model.Marker
import android.widget.TextView








class MyMapFragment : Fragment(), OnMapReadyCallback {

    private val viewModel by viewModels<MyMapViewModel> { getVmFactory() }

    lateinit var mapView: MapView
    lateinit var myGoogleMap: GoogleMap
    lateinit var binding: FragmentMymapBinding


    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    val MAPVIEW_BUNDLE_KEY: String? = "MapViewBundleKey"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mymap, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        // Gets the MapView from the XML layout and creates it
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(this.activity)
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        }
//
//        // Updates the location and zoom of the MapView
//        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
//            LatLng(43.1, -87.9), 10F
//        )
//        myGoogleMap.animateCamera(cameraUpdate)



        return binding.root
    }


    override fun onResume() {
        mapView.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }
        mapView.onSaveInstanceState(mapViewBundle)
    }


    class MyMapAdapter(val context: Context) : GoogleMap.InfoWindowAdapter {

        override fun getInfoWindow(marker: Marker): View? {

            val window =
                (context as Activity).layoutInflater.inflate(R.layout.item_map_info_window, null)

            return window
        }

        override fun getInfoContents(marker: Marker): View? {
            return null
        }


    }


    override fun onMapReady(googleMap: GoogleMap) {
        myGoogleMap = googleMap
//        val lan1 = LatLng(25.039321, 121.567173)  //50嵐 微風松高店

        myGoogleMap.uiSettings.isZoomControlsEnabled = true
//        myGoogleMap.uiSettings.isMyLocationButtonEnabled=true
        myGoogleMap.uiSettings.isMapToolbarEnabled = true
//        myGoogleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN


        //map marker
        viewModel.notes.observe(this, Observer { list ->
            list?.let { notesList ->
                notesList.forEach {
                    val latlng = LatLng(it.lat, it.lng)
                    myGoogleMap.addMarker(MarkerOptions().position(latlng)).showInfoWindow()
                    Logger.i("marker latlng = $latlng")
                }
            }
        })

        myGoogleMap.setInfoWindowAdapter(CustomInfoWindowAdapter(context as Activity))

//        googleMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
//            override fun getInfoWindow(marker: Marker): View? {
//                return null
//            }
//
//            override fun getInfoContents(marker: Marker): View {
//                val itemOmanPostAddressMapInfoWindowBinding = DataBindingUtil.inflate(
//                    LayoutInflater.from(context),
//                    R.layout.item_oman_post_address_map_info_window,
//                    null,
//                    true
//                )
//                itemOmanPostAddressMapInfoWindowBinding.setData(getRelevantData(marker.position))
//                itemOmanPostAddressMapInfoWindowBinding.executePendingBindings()
//                return itemOmanPostAddressMapInfoWindowBinding.getRoot()
//            }
//        })


//        myGoogleMap.addMarker(markerOpt).showInfoWindow()

//        val location: LatLng? = LatLng(myLocation!!.latitude,myLocation.longitude)

//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient((activity as MainActivity) )
//        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
//            Logger.d("fusedLocationProviderClient = ${it.latitude}")
//        }

    }


//            myGoogleMap.setOnMarkerClickListener { marker ->
//                val locAddress = marker.title
//
//                if (previousMarker != null) {
//                    previousMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
//
//                    marker.isInfoWindowShown()
//                }
//                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
//                previousMarker = marker
//
//                marker.isInfoWindowShown()
//
//                true
//            }
//
//            google.maps.event.addListener(infowindow, 'closeclick', function(){
//                a = a * -1;
//            });


//    private fun queryMapNear(myLocation: LatLng?, queryRadius: Int, googleMap: GoogleMap) {
//        googleMap.clear()
//        val lan1 = LatLng(25.039321, 121.567173)  //50嵐 微風松高店
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient((activity as MainActivity) )
//        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
//            val newLocation = LatLng(it.latitude,it.longitude)
//            Logger.d("fusedLocationProviderClient = ${it.latitude}")
//            val lat = 0.009043717
//            val lon = 0.008983112/cos(newLocation.latitude)
//            val lowerLat = it.latitude - (lat * queryRadius)
//            val lowerLon = it.longitude - (lon * queryRadius)
//            val greaterLat = it.latitude + (lat * queryRadius)
//            val greaterLon = it.longitude + (lon * queryRadius)
//            val lesserGeopoint = GeoPoint(lowerLat, lowerLon)
//            val greaterGeopoint = GeoPoint(greaterLat, greaterLon)
//            val db = FirebaseFirestore.getInstance()
//            db.collection("stores")
//                .document("50lan")
//                .collection("branch")
//                .whereGreaterThan("geo",lesserGeopoint)
//                .whereLessThan("geo",greaterGeopoint)
//                .get()
//                .addOnSuccessListener { result ->
//                    for (document in result) {
//                        Log.d("queryMapNear", "${document.id} => ${document.data}")
//                        Log.d("queryMapNear", "${document.id} => ${document.getGeoPoint("geo")!!.latitude}")
//                        document.getGeoPoint("geo")?.let {
//                            //icon size
//                            val height = 100
//                            val width = 100
//                            // if (isAdded) to avoid fragment not attach context
//                            @Suppress("DEPRECATION")
//                            val iconDraw = if (isAdded){
//                                val bitmapdraw = resources.getDrawable(R.drawable.drink_map_icon_1)
//                                val b = bitmapdraw.toBitmap()
//                                val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
//                                BitmapDescriptorFactory.fromBitmap(smallMarker)
//                            }else{
//                                BitmapDescriptorFactory.defaultMarker()
//                            }
//                            val queryResult = LatLng(it.latitude,it.longitude)
//                            googleMap.addMarker(MarkerOptions().position(queryResult)
//                                .title("${document.data["branchName"]}")
//                                .snippet("${document.data["storeName"]}")
////                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bottom_navigation_home_1))
//                                .icon(iconDraw)
//                            )
//                        }
//                    }
//                    val cameraRatio = 16F - queryRadius
//                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLocation,cameraRatio.toFloat()))
//                    val circleOptions = CircleOptions()
//                    circleOptions.center(newLocation)
//                        .radius(queryRadius.toDouble()*1000)
//                        .fillColor(Color.argb(70,150,50,50))
//                        .strokeWidth(3F)
//                        .strokeColor(Color.RED)
//                    googleMap.addCircle(circleOptions)
////                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds())
////                val aa = LatLngBounds(lan1,lan1)
////                googleMap.setLatLngBoundsForCameraTarget(aa)
//                }
//                .addOnFailureListener { exception ->
//                    Log.w("queryMapNear", "Error getting documents.", exception)
//                }
//        }
//    }


    class InfoWindowAdapter(context: Context) : GoogleMap.InfoWindowAdapter {
        private val context: Context

        init {
            this.context = context.applicationContext
        }

        override fun getInfoWindow(marker: Marker): View? {
            return null
        }

        override fun getInfoContents(marker: Marker): View {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.item_map_info_window, null)
            val infoTitle = view.findViewById(R.id.info_title) as TextView
            return view
        }


    }





}

//class CustomMarkerInfoWindowView : GoogleMap.InfoWindowAdapter {
//
//    private val markerItemView: View
//
//    init {
//        markerItemView = layoutInflater.inflate(R.layout.marker_info_window, null)  // 1
//    }
//
//    override fun getInfoWindow(marker: Marker): View { // 2
//        val user = marker.tag as User? ?: return clusterItemView  // 3
//        val itemNameTextView = markerItemView.findViewById(R.id.itemNameTextView)
//        val itemAddressTextView = markerItemView.findViewById(R.id.itemAddressTextView)
//        itemNameTextView.setText(marker.title)
//        itemAddressTextView.setText(user.getAddress())
//        return markerItemView  // 4
//    }
//
//    override fun getInfoContents(marker: Marker): View? {
//        return null
//    }
//
//}
//
//class CustomMarkerInfoWindowView2 : GoogleMap.InfoWindowAdapter {
//    override fun getInfoWindow(marker: Marker): View? {
//        return null
//    }
//
//    override fun getInfoContents(marker: Marker): View {
//        val itemOmanPostAddressMapInfoWindowBinding = DataBindingUtil.inflate(
//            LayoutInflater.from(context),
//            R.layout.item_oman_post_address_map_info_window,
//            null,
//            true
//        )
//        itemOmanPostAddressMapInfoWindowBinding.setData(getRelevantData(marker.position))
//        itemOmanPostAddressMapInfoWindowBinding.executePendingBindings()
//        return itemOmanPostAddressMapInfoWindowBinding.getRoot()
//    }
//})





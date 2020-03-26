package com.angela.notemoment.map

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.angela.notemoment.NavigationDirections
import com.angela.notemoment.databinding.FragmentMymapBinding
import com.angela.notemoment.ext.getVmFactory
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.GoogleMap
import com.angela.notemoment.R
import com.angela.notemoment.util.Logger
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker


class MyMapFragment : Fragment(), GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    override fun onMarkerClick(p0: Marker?): Boolean {
        Logger.i("map onMarkerClick :: $p0")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private val viewModel by viewModels<MyMapViewModel> { getVmFactory() }

    private lateinit var mapView: MapView
    private lateinit var myGoogleMap: GoogleMap
    private val mapBundleKey: String = "MapViewBundleKey"
    lateinit var binding: FragmentMymapBinding

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(mapBundleKey)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(mapBundleKey, mapViewBundle)
        }
        mapView.onSaveInstanceState(mapViewBundle)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        myGoogleMap = googleMap
        myGoogleMap.uiSettings.isMapToolbarEnabled = false

        viewModel.notes.observe(viewLifecycleOwner, Observer {
            it?.let {
                val focusMarker = viewModel.getFocusMarker(it)
                val initCameraRatio = 1F
                myGoogleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        focusMarker,
                        initCameraRatio
                    )
                )
            }
        })


        //map marker
        viewModel.notes.observe(this, Observer { list ->
            list?.let { notesList ->
                notesList.forEach {
                    val latlng = LatLng(it.lat, it.lng)
                    myGoogleMap.addMarker(MarkerOptions().position(latlng).title(it.locateName))
                        .setIcon(
                            BitmapDescriptorFactory.fromBitmap(
                                BitmapFactory.decodeResource(
                                    resources,
                                    R.drawable.icon_red_pin
                                )
                            )
                        )
                    Logger.i("marker latlng = $latlng")
                }
            }
        })

        myGoogleMap.setOnMarkerClickListener { marker ->
            viewModel.markerTitle.value = marker.title
            val cameraRatio = 15F
            myGoogleMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    marker.position,
                    cameraRatio
                )
            )
            viewModel.getMarkerNote(marker.position)
            viewModel.showNotesWindow()

            true
        }

        myGoogleMap.setOnMapClickListener {
            viewModel.closeNotesWindow()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mymap, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val adapter = MyMapNoteAdapter(viewModel)
        binding.recyclerMapNote.adapter = adapter

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        try {
            MapsInitializer.initialize(this.activity)
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        }


        viewModel.navigateToDetailNote.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalDetailNoteFragment(it))
                viewModel.onSelectNoteToDetail()
                viewModel.closeNotesWindow()
            }
        })

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

}



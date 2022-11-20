package com.ahmadabuhasan.storyapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.ahmadabuhasan.storyapp.R
import com.ahmadabuhasan.storyapp.data.Result
import com.ahmadabuhasan.storyapp.databinding.ActivityMapsBinding
import com.ahmadabuhasan.storyapp.model.ListStory
import com.ahmadabuhasan.storyapp.utils.Constant
import com.ahmadabuhasan.storyapp.utils.SessionManager
import com.ahmadabuhasan.storyapp.viewmodel.StoryViewModel
import com.ahmadabuhasan.storyapp.viewmodel.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(),
    OnMapReadyCallback {

    private lateinit var sharedPref: SessionManager
    private lateinit var viewModel: StoryViewModel
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mMap: GoogleMap

    private val boundBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        sharedPref = SessionManager(this)
        setupViewModel()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupViewModel() {
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[StoryViewModel::class.java]
    }

    @Override
    override fun onMapReady(gMap: GoogleMap) {
        mMap = gMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID

        getDataStory()
    }


    private fun getDataStory() {
        val token = "Bearer ${sharedPref.getToken}"
        viewModel.vmStoryLocation(token).observe(this) {

            when (it) {
                is Result.Loading -> {
                    showLoading(true)
                }

                is Result.Success -> {
                    showLoading(false)
                    showMarker(it.data.listStory)
                }

                is Result.Error -> {
                    showLoading(false)
                }
            }
        }
    }

    private fun showMarker(listStory: List<ListStory>) {
        listStory.forEach { story ->
            val latLng = LatLng(story.lat, story.lon)
            val marker = mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(story.name)
                    .snippet(story.description)
                    .alpha(0.7f)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            )

            boundBuilder.include(latLng)
            marker?.tag = story
            mMap.moveCamera(CameraUpdateFactory.zoomTo(7F))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            mMap.setOnInfoWindowClickListener {
                val i = Intent(this, DetailStoryActivity::class.java).apply {
                    putExtra(Constant.BUNDLE_STORY, it.tag as ListStory)
                }
                startActivity(i)
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}
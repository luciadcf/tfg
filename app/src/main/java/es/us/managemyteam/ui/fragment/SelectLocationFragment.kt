package es.us.managemyteam.ui.fragment

import es.managemyteam.app.R
import es.managemyteam.app.databinding.FragmentSelectLocationBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import es.us.managemyteam.data.model.LocationBo
import es.us.managemyteam.extension.*
import es.us.managemyteam.repository.util.Error
import es.us.managemyteam.repository.util.ResourceObserver
import es.us.managemyteam.ui.view.common_map.MapListener
import es.us.managemyteam.ui.view.common_map.MarkerItemVo
import es.us.managemyteam.ui.viewmodel.CreateEventViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SelectLocationFragment : BaseFragment<FragmentSelectLocationBinding>(), MapListener {

    private var latLngSelected: LatLng? = null
    private val createEventViewModel: CreateEventViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMap(savedInstanceState)
        setupGetLocationSelectedObserver()

        viewBinding.selectLocationBtnSave.setOnClickListener {
            saveLocation()
        }
    }

    private fun saveLocation() {
        if (latLngSelected != null) {
            createEventViewModel.setLocationSelected(
                LocationBo(
                    latLngSelected?.toMyLatLng(),
                    viewBinding.selectLocationMap.getAddressFromMarkerPosition(latLngSelected!!)
                )
            )
            popBack()
        } else {
            Toast.makeText(
                context,
                getString(R.string.create_event_select_location_error),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun setupMap(savedInstanceState: Bundle?) {
        viewBinding.selectLocationMap.apply {
            onCreate(savedInstanceState)
            setEnableInteraction(true)
            setShowUserPosition(true)
            showLocationButton(true)
            setMapListener(this@SelectLocationFragment)
        }
    }

    private fun setupGetLocationSelectedObserver() {
        createEventViewModel.getLocationSelectedData()
            .observe(viewLifecycleOwner, object : ResourceObserver<LocationBo?>() {
                override fun onSuccess(response: LocationBo?) {
                    response?.let {
                        val mapView = viewBinding.selectLocationMap
                        val marker = it.location?.getMarkerItemVo(mapView)
                        marker?.let { markerVo ->
                            mapView.addMarker(markerVo)
                            mapView.showMarkerInfo(markerVo)
                        }
                    }
                }

                override fun onError(error: Error) {
                    super.onError(error)
                    showErrorDialog(getString(error.errorMessageId))
                }
            })
        createEventViewModel.getLocationSelected()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        viewBinding.selectLocationMap.onRequestPermissionResult(this, grantResults)
    }

    override fun onResume() {
        super.onResume()
        viewBinding.selectLocationMap.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewBinding.selectLocationMap.onPause()
    }

    override fun onStart() {
        super.onStart()
        viewBinding.selectLocationMap.onStart()
    }

    override fun onStop() {
        super.onStop()
        viewBinding.selectLocationMap.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        viewBinding.selectLocationMap.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewBinding.selectLocationMap.onDestroy()
    }


    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSelectLocationBinding {
        return FragmentSelectLocationBinding.inflate(inflater, container, false)
    }

    override fun setupToolbar(toolbar: Toolbar) {
        toolbar.apply {
            setToolbarTitle(getString(R.string.select_location))
            setNavIcon(ContextCompat.getDrawable(context, R.drawable.ic_back))
            setNavAction { popBack() }
            show()
        }
    }

    override fun setupBottomBar(bottomBottomNavigationView: BottomNavigationView) {
        bottomBottomNavigationView.hide()
    }

    override fun onMapClicked(latLng: LatLng) {
        latLngSelected = latLng
        viewBinding.selectLocationMap.apply {
            val markerItemVo = latLng.toMyLatLng().getMarkerItemVo(this)
            clearMarkers()
            addMarker(markerItemVo)
            showMarkerInfo(markerItemVo)
        }
    }

    override fun onMarkerClicked(markerClicked: MarkerItemVo) {
        // no-op
    }

    override fun onMapReady() {
        viewBinding.selectLocationMap.initialize(this@SelectLocationFragment)
    }

    override fun onLocationChanged(latLng: LatLng) {
        // no-op
    }
}
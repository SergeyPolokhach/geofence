package com.polohach.geofence.example.ui.screens.main.map

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.cleveroad.bootstrap.kotlin_core.ui.BackPressable
import com.cleveroad.bootstrap.kotlin_ext.isNetworkConnected
import com.cleveroad.bootstrap.kotlin_ext.safeLet
import com.cleveroad.bootstrap.kotlin_ext.setClickListeners
import com.cleveroad.bootstrap.kotlin_permissionrequest.PermissionRequest
import com.cleveroad.bootstrap.kotlin_permissionrequest.PermissionResult
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.polohach.geofence.example.GFApp
import com.polohach.geofence.example.R
import com.polohach.geofence.example.extensions.checkNoGrantedPermission
import com.polohach.geofence.example.extensions.notNull
import com.polohach.geofence.example.geofence.GeoFenceUtil
import com.polohach.geofence.example.models.*
import com.polohach.geofence.example.preferences.PreferencesProvider
import com.polohach.geofence.example.ui.ACCURACY_RADIUS_IN_METERS
import com.polohach.geofence.example.ui.Location
import com.polohach.geofence.example.ui.U_ANCHOR
import com.polohach.geofence.example.ui.base.BaseFragment
import com.polohach.geofence.example.utils.bindInterfaceOrThrow
import kotlinx.android.synthetic.main.fragment_map.*


class MapFragment : BaseFragment<MapViewModel>(),
        View.OnClickListener,
        OnMapReadyCallback,
        BackPressable {

    override val viewModelClass = MapViewModel::class.java
    override val layoutId = R.layout.fragment_map
    override fun getScreenTitle() = NO_TITLE
    override fun getToolbarId() = NO_TOOLBAR
    override fun hasToolbar() = false
    override fun hasVersions() = false

    companion object {
        private const val MAP_FRAGMENT_TAG = "google_map"

        fun newInstance() = MapFragment().apply { arguments = Bundle() }
    }

    private val permissionRequest = PermissionRequest()

    private lateinit var geoFencingClient: GeofencingClient
    private var callback: MapFragmentCallback? = null
    private var googleMap: GoogleMap? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        callback = bindInterfaceOrThrow<MapFragmentCallback>(parentFragment, context)
        context?.let { geoFencingClient = LocationServices.getGeofencingClient(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initGoogleApiClient()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMap()
        setClickListeners(fabLocationButton)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionRequest.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onPause() {
        viewModel.getUsers(ActionType.UNSUBSCRIBE)
        super.onPause()
    }

    override fun onDetach() {
        callback = null
        super.onDetach()
    }

    override fun observeLiveData(viewModel: MapViewModel) {
        viewModel.run {
            usersLiveData.observe(this@MapFragment, Observer { users ->
                drawUsers(users)
            })
            geoFenceLiveData.observe(this@MapFragment, Observer { geoFences ->
                drawGeoFences(geoFences)
                context?.let { GeoFenceUtil().setGeoFences(it, geoFences) }
            })
            getUsers(ActionType.SUBSCRIBE).observe(this@MapFragment, Observer { list ->
                setChangeUserData(list)
            })
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        this.googleMap = googleMap
        this.googleMap?.apply {
            with(uiSettings) {
                isMyLocationButtonEnabled = false
                isMapToolbarEnabled = false
            }
            setOnMapClickListener { latLng -> animateCamera(latLng) }
        }
        PreferencesProvider.takeIf { it.isFirstOpen }
                ?.apply {
                    onGPSPermissionRequest()
                    isFirstOpen = false
                }
                ?: checkPermissionAndSetLocation()
        viewModel.run {
            getGeoFence()
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.fabLocationButton -> onGPSPermissionRequest()
        }
    }

    private fun initMap() {
        (childFragmentManager.findFragmentByTag(MAP_FRAGMENT_TAG) as SupportMapFragment?)?.let {
            if (googleMap == null) it.getMapAsync(this)
        } ?: SupportMapFragment.newInstance().let {
            childFragmentManager
                    .beginTransaction()
                    .replace(R.id.mapContainer, it, MAP_FRAGMENT_TAG)
                    .commit()
            it.getMapAsync(this)
        }
    }

    private fun initGoogleApiClient() {
        takeIf { isNetworkConnected() }?.apply {
            activity?.takeIf { fusedLocationProviderClient == null }
                    ?.let {
                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(it)
                    }
        } ?: showSnackBar(R.string.no_internet_connection)
    }

    private fun checkPermissionAndSetLocation() {
        activity?.takeIf { checkNoGrantedPermission(it, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION) }
                ?.apply { animateCamera(getMyUser().getPosition()) }
                ?: onGPSPermissionRequest()
    }

    //request for a geoposition permission
    // if gps is not turned on to show the dialog for turn on (check geoposition permission before)
    @SuppressLint("MissingPermission")
    private fun onGPSPermissionRequest() {
        permissionRequest.request(this,
                RequestCode.REQUEST_PERMISSION_GEOPOSITION_MAP.invoke(),
                arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION),
                object : PermissionResult {
                    override fun onPermissionGranted() {
                        fusedLocationProviderClient?.lastLocation?.addOnSuccessListener {
                            setLocation(it)
                        }
                    }

                    override fun onPermissionDenied() {
                        animateCamera(getMyUser().getPosition())
                        super.onPermissionDenied()
                    }
                })
    }

    private fun setChangeUserData(users: List<UserModel>) {
        val currentUserList = viewModel.usersLiveData.value?.toMutableList() ?: mutableListOf()
        users.forEach { user ->
            currentUserList.find { it.id == user.id }
                    ?.apply {
                        this.latitude = user.latitude
                        this.longitude = user.longitude
                        this.accuracy = user.accuracy
                    }
                    ?: let { currentUserList.add(user) }
        }
        viewModel.usersLiveData.value = currentUserList
    }

    private fun setLocation(location: android.location.Location?) {
        animateCamera(location
                ?.run {
                    getMyUser().accuracy =
                            if (accuracy < ACCURACY_RADIUS_IN_METERS) accuracy else ACCURACY_RADIUS_IN_METERS
                    LatLng(latitude, longitude)
                }
                ?: getMyUser().getPosition())
    }

    private fun animateCamera(position: LatLng) {
        getMyUser().apply {
            latitude = position.latitude
            longitude = position.longitude
            drawUser(this, id)
        }
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(position, Location.STANDARD_ZOOM))
    }

    private fun drawUsers(users: List<User>, currentUserId: String? = GFApp.firebaseUser?.uid) {
        users.forEach { user ->
            user.takeIf { it.id != currentUserId }
                    ?.let { drawUser(it, currentUserId) }
        }
    }

    private fun drawUser(user: User, currentUserId: String?) {
        user.apply {
            circle?.remove()
            circle = drawCircle(getPosition(), accuracy.notNull(), R.color.blumine_10)
            drawMarker(this, currentUserId)
        }
    }

    private fun drawMarker(user: User, currentUserId: String?) {
        val latLng = user.getPosition()
        user.apply {
            marker = marker?.apply { this.position = latLng }
                    ?: initMarker(latLng,
                            if (currentUserId == id) R.drawable.ic_pin_my_location else R.drawable.ic_location_24dp)
        }
    }

    private fun initMarker(latLng: LatLng, iconRes: Int) =
            googleMap?.run {
                addMarker(MarkerOptions()
                        .position(latLng)
                        .flat(true)
                        .anchor(U_ANCHOR, U_ANCHOR)
                        .icon(BitmapDescriptorFactory.fromResource(iconRes)))
            }

    private fun drawGeoFences(geoFences: List<GeoFence>) {
        geoFences.forEach {
            drawCircle(it.getPosition(), it.radius.notNull(), R.color.red_20)
        }
    }

    private fun drawCircle(position: LatLng, radius: Float, colorId: Int) =
            safeLet(context, googleMap) { ctx, map ->
                map.addCircle(CircleOptions()
                        .center(position)
                        .fillColor(ContextCompat.getColor(ctx, colorId))
                        .strokeColor(android.R.color.transparent)
                        .radius(radius.toDouble()))
            }

    private fun isNetworkConnected() = context?.isNetworkConnected() == true

    private fun getMyUser() = GFApp.instance.myUser
}

package com.polohach.geofence.example.ui.screens.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.cleveroad.bootstrap.kotlin_ext.hide
import com.cleveroad.bootstrap.kotlin_ext.setClickListeners
import com.cleveroad.bootstrap.kotlin_ext.show
import com.polohach.geofence.example.GFApp
import com.polohach.geofence.example.R
import com.polohach.geofence.example.gps.GpsUiDelegate
import com.polohach.geofence.example.gps.GpsUiDelegateImpl
import com.polohach.geofence.example.preferences.PreferencesProvider
import com.polohach.geofence.example.ui.base.BaseActivity
import com.polohach.geofence.example.ui.screens.auth.AuthActivity
import com.polohach.geofence.example.ui.screens.main.chat.ChatFragment
import com.polohach.geofence.example.ui.screens.main.map.MapFragment
import com.polohach.geofence.example.ui.screens.main.map.MapFragmentCallback
import com.polohach.geofence.example.utils.authorization_helper.AuthorizationHelper
import com.polohach.geofence.example.utils.authorization_helper.GoogleAuthorizationHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity<MainViewModel>(),
        GoogleAuthorizationHelper.GoogleAuthCallback,
        AuthorizationHelper.AuthorizationCallback,
        GpsUiDelegate.GpsUiCallback,
        View.OnClickListener,
        MapFragmentCallback {

    companion object {

        fun start(context: Context?) {
            context?.apply ctx@{ startActivity(getIntent(this@ctx)) }
        }

        private fun getIntent(context: Context?) = Intent(context, MainActivity::class.java)
    }

    override val viewModelClass = MainViewModel::class.java

    override val containerId = R.id.container

    override val layoutId = R.layout.activity_main

    override lateinit var contextForHelper: Context
    private lateinit var googleAuthHelper: GoogleAuthorizationHelper
    private val gpsUiDelegate by lazy(LazyThreadSafetyMode.NONE) { GpsUiDelegateImpl(this) }

    override fun hasProgressBar() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contextForHelper = this
        googleAuthHelper = GoogleAuthorizationHelper(this)

        setClickListeners(bShowChat, bSignOut)
        showMapScreen()
        initDrawer()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        bShowChat.show()
    }

    @SuppressLint("RtlHardcoded")
    override fun onClick(view: View) {
        when (view.id) {
            R.id.bSignOut -> signOut()
            R.id.bShowChat -> showChatScreen().also {
                dlSettings.closeDrawer(Gravity.LEFT)
                bShowChat.hide()
            }
        }
    }

    override fun observeLiveData(viewModel: MainViewModel) {
        // Put your code here....
    }

    override fun onSuccess(authType: Int, token: String) {
        // do nothing
    }

    override fun onFail(authType: Int, throwable: Throwable?) {
        viewModel.errorLiveData.value = throwable?.message
    }

    override fun checkPermissions() {
        gpsUiDelegate.checkPermission()
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        gpsUiDelegate.onResumeFragments()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        gpsUiDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun getActivity() = this

    override fun onPause() {
        gpsUiDelegate.onPause()
        super.onPause()
    }

    private fun initDrawer() {
        GFApp.firebaseUser?.apply {
            tvUserUid.text = uid
            tvUserEmail.text = email
        }
        swTrackMyLocation.apply {
            isChecked = PreferencesProvider.trackLocation
            setOnCheckedChangeListener { _, isChecked -> GFApp.instance.switchGpsTracker(isChecked) }
        }
    }

    private fun signOut() {
        GFApp.instance.switchGpsTracker(false)
        googleAuthHelper.signOut()
        AuthActivity.start(this)
        finish()
    }

    private fun showMapScreen() {
        replaceFragment(MapFragment.newInstance(), false)
    }

    private fun showChatScreen() {
        replaceFragment(ChatFragment.newInstance(), true)
    }
}

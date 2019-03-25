package com.polohach.geofence.example.ui.screens.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.polohach.geofence.example.R
import com.polohach.geofence.example.ui.base.BaseActivity
import com.polohach.geofence.example.ui.screens.auth.sign_in.SignInCallback
import com.polohach.geofence.example.ui.screens.auth.sign_in.SignInFragment
import com.polohach.geofence.example.ui.screens.main.MainActivity

class AuthActivity : BaseActivity<AuthViewModel>(),
        SignInCallback {

    companion object {

        fun start(context: Context?) {
            context?.apply ctx@{ startActivity(getIntent(this@ctx)) }
        }

        fun getIntent(context: Context?) = Intent(context, AuthActivity::class.java)
    }

    override val viewModelClass = AuthViewModel::class.java

    override val containerId = R.id.container

    override val layoutId = R.layout.activity_auth

    override fun hasProgressBar() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        showSignInScreen()
    }

    override fun observeLiveData(viewModel: AuthViewModel) {
        // Put your code here....
    }

    override fun successfulSignIn() {
        showMain()
    }

    private fun showSignInScreen() {
        replaceFragment(SignInFragment.newInstance(), false)
    }

    private fun showMain() {
        MainActivity.start(this)
        finish()
    }
}

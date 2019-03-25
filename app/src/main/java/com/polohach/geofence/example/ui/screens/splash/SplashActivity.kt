package com.polohach.geofence.example.ui.screens.splash

import android.content.Context
import android.content.Intent
import androidx.lifecycle.Observer
import com.polohach.geofence.example.R
import com.polohach.geofence.example.ui.base.BaseActivity
import com.polohach.geofence.example.ui.screens.auth.AuthActivity
import com.polohach.geofence.example.ui.screens.main.MainActivity

class SplashActivity : BaseActivity<SplashViewModel>() {

    override val viewModelClass = SplashViewModel::class.java
    override val containerId = R.id.container
    override val layoutId = R.layout.activity_splash

    companion object {

        fun start(context: Context?) {
            context?.apply ctx@{ startActivity(getIntent(this@ctx)) }
        }

        fun getIntent(context: Context?) = Intent(context, SplashActivity::class.java)
    }

    override fun hasProgressBar() = true

    override fun observeLiveData(viewModel: SplashViewModel) {
        viewModel.run {
            checkCurrentUser()
            hasCurrentUser.observe(this@SplashActivity, Observer<Boolean> { isLogin ->
                if (isLogin) showMain() else showAuth()
            })
        }
    }

    private fun showMain() {
        MainActivity.start(this)
        finish()
    }

    private fun showAuth() {
        AuthActivity.start(this)
        finish()
    }
}

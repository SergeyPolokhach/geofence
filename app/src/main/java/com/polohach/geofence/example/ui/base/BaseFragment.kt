package com.polohach.geofence.example.ui.base

import com.cleveroad.bootstrap.kotlin_core.ui.BaseLifecycleFragment
import com.cleveroad.bootstrap.kotlin_core.ui.BaseLifecycleViewModel
import com.polohach.geofence.example.BuildConfig
import com.polohach.geofence.example.EMPTY_STRING
import com.polohach.geofence.example.R

abstract class BaseFragment<T : BaseLifecycleViewModel> : BaseLifecycleFragment<T>() {

    override var endpoint = EMPTY_STRING

    override var versionName = BuildConfig.VERSION_NAME

    override fun getVersionsLayoutId() = R.id.versionsContainer

    override fun getEndPointTextViewId() = R.id.tvEndpoint

    override fun getVersionsTextViewId() = R.id.tvVersion

    override fun isDebug() = BuildConfig.DEBUG

    override fun hasVersions() = isDebug()

    override fun showBlockBackAlert() {
        // override this method if you need to show a warning when going to action back
    }
}
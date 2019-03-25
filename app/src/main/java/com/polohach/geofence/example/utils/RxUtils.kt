package com.polohach.geofence.example.utils

import io.reactivex.FlowableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Helper class for RxJava
 */
object RxUtils {
    fun <T> ioToMainTransformer() = FlowableTransformer<T, T> {
        it
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}
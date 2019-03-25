package com.polohach.geofence.example.utils.authorization_helper

import android.app.Activity
import android.content.Intent
import com.cleveroad.bootstrap.kotlin_ext.safeLet
import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.polohach.geofence.example.R
import com.polohach.geofence.example.utils.RxUtils
import com.polohach.geofence.example.utils.authorization_helper.AuthorizationHelper.Companion.EMAIL_AUTH
import com.polohach.geofence.example.utils.authorization_helper.AuthorizationHelper.Companion.GOOGLE_PLUS_AUTH
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference


class GoogleAuthorizationHelper(callback: GoogleAuthCallback) : AuthorizationHelper, GoogleApiClient.OnConnectionFailedListener {

    private var googleApiClient: GoogleApiClient? = null
    private var callbackWR: WeakReference<GoogleAuthCallback>? = WeakReference(callback)
    private var disposable: Disposable? = null

    init {
        initHelper(callback)
    }

    private fun initHelper(callback: GoogleAuthCallback) {
        callback.contextForHelper?.let {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestIdToken(it.getString(R.string.google_plus_server_client_id))
                    .build()

            googleApiClient = GoogleApiClient.Builder(it)
                    .addOnConnectionFailedListener(this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build()
            googleApiClient?.connect()
        }
    }

    override fun auth() {
        safeLet(googleApiClient, callbackWR?.get()) { googleApiClient, callback ->
            if (googleApiClient.isConnected) {
                val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
                callback.startActivityForResult(signInIntent, GOOGLE_PLUS_AUTH)
            }
        }
    }

    override fun auth(email: String, password: String) {
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { checkAuthResult(it) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackWR?.get()?.let { googleAuthCallback ->
            safeLet(googleAuthCallback, googleAuthCallback.contextForHelper) { callback, _ ->
                if (requestCode == GOOGLE_PLUS_AUTH) {
                    if (disposable?.isDisposed?.not() == true) disposable?.dispose()
                    disposable = Flowable.just(Auth.GoogleSignInApi.getSignInResultFromIntent(data))
                            .filter { it.isSuccess /* GoogleSignInResult::isSuccess*/ }
                            .map { it.signInAccount /*GoogleSignInResult::getSignInAccount*/ }
                            .map { GoogleAuthProvider.getCredential(it.idToken, null) }
                            .map { FirebaseAuth.getInstance().signInWithCredential(it) }
                            .compose(RxUtils.ioToMainTransformer())
                            .switchIfEmpty(Flowable.error(Exception("Authentication error!")))
                            .subscribe({ login(GOOGLE_PLUS_AUTH, it.toString()) },
                                    { throwable ->
                                        throwable.takeIf { it is GetTokenException }
                                                ?.apply {
                                                    (throwable.cause as? UserRecoverableAuthException)
                                                            ?.apply { callback.startActivityForResult(intent, GOOGLE_PLUS_AUTH) }
                                                }
                                                ?: apply { callback.onFail(GOOGLE_PLUS_AUTH, throwable) }
                                    })
                }
            }
        }
    }

    override fun connect() {
        googleApiClient?.let { if (!it.isConnected && !it.isConnecting) it.connect() }
    }

    override fun disconnect() {
        googleApiClient?.disconnect()
        if (disposable?.isDisposed?.not() == true) disposable?.dispose()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        // do nothing
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    private fun login(authType: Int, token: String) {
        callbackWR?.get()?.onSuccess(authType, token)
    }

    private fun checkAuthResult(task: Task<AuthResult>) {
        disposable = Flowable.just(task)
                .filter { it.isSuccessful }
                .switchIfEmpty(Flowable.error(Exception("Authentication error!")))
                .compose(RxUtils.ioToMainTransformer())
                .subscribe({ login(EMAIL_AUTH, it.result.toString()) },
                        { callbackWR?.get()?.onFail(EMAIL_AUTH, it) })
    }

    interface GoogleAuthCallback : AuthorizationHelper.AuthorizationCallback {
        /**
         * Implementation must call [Activity.startActivityForResult]
         * or [Fragment.startActivityForResult]
         * this method has usage in [GoogleAuthorizationHelper.auth]. So if there are
         * no intents for using Google authorization implementation of this method can be empty
         *
         * @param intent      The intent to start.
         * @param requestCode If >= 0, this code will be returned in
         * onActivityResult() when the activity exits.
         */
        fun startActivityForResult(intent: Intent, requestCode: Int)
    }

    private class GetTokenException : RuntimeException()
}
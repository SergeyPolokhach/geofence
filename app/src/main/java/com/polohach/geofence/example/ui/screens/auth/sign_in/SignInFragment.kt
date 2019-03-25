package com.polohach.geofence.example.ui.screens.auth.sign_in

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.cleveroad.bootstrap.kotlin_ext.setClickListeners
import com.polohach.geofence.example.R
import com.polohach.geofence.example.extensions.showTextInputError
import com.polohach.geofence.example.extensions.string
import com.polohach.geofence.example.ui.base.BaseFragment
import com.polohach.geofence.example.utils.HideErrorTextWatcher
import com.polohach.geofence.example.utils.ValidationField
import com.polohach.geofence.example.utils.ValidationResponseWrapper
import com.polohach.geofence.example.utils.authorization_helper.AuthorizationHelper
import com.polohach.geofence.example.utils.authorization_helper.GoogleAuthorizationHelper
import com.polohach.geofence.example.utils.bindInterfaceOrThrow
import kotlinx.android.synthetic.main.fragment_sign_in.*

class SignInFragment : BaseFragment<SignInViewModel>(),
        View.OnClickListener,
        AuthorizationHelper.AuthorizationCallback,
        GoogleAuthorizationHelper.GoogleAuthCallback {

    companion object {
        fun newInstance() = SignInFragment().apply {
            arguments = Bundle()
        }
    }

    override val layoutId = R.layout.fragment_sign_in
    override val viewModelClass = SignInViewModel::class.java

    private val validationObserver = Observer<ValidationResponseWrapper> {
        when (it.field) {
            ValidationField.EMAIL -> tilSignInEmail.showTextInputError(it.response)
            ValidationField.PASSWORD -> tilSignInPassword.showTextInputError(it.response)
        }
    }

    override lateinit var contextForHelper: Context
    private lateinit var googleAuthHelper: GoogleAuthorizationHelper

    private var callback: SignInCallback? = null

    override fun getScreenTitle() = NO_TITLE

    override fun getToolbarId() = NO_TOOLBAR

    override fun hasToolbar() = false

    override fun observeLiveData(viewModel: SignInViewModel) {
        with(viewModel) {
            validationLD.observe(this@SignInFragment, validationObserver)
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        callback = bindInterfaceOrThrow<SignInCallback>(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contextForHelper = view.context
        googleAuthHelper = GoogleAuthorizationHelper(this)
        setupUi()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AuthorizationHelper.GOOGLE_PLUS_AUTH -> googleAuthHelper.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onResume() {
        super.onResume()
        googleAuthHelper.connect()
    }

    override fun onPause() {
        googleAuthHelper.disconnect()
        super.onPause()
    }

    override fun onDetach() {
        callback = null
        super.onDetach()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.bSignIn -> signInWitsEmailAndPassword()
            R.id.bGoogleSignIn -> signInWithGoogle()
        }
    }

    override fun onSuccess(authType: Int, token: String) {
        hideProgress()
        callback?.successfulSignIn()
    }

    override fun onFail(authType: Int, throwable: Throwable?) {
        viewModel.errorLiveData.value = throwable?.message
    }

    private fun setupUi() {
        setClickListeners(bSignIn, bGoogleSignIn)
        etSignInEmail.addTextWatcher(HideErrorTextWatcher(tilSignInEmail))
        etSignInPassword.addTextWatcher(HideErrorTextWatcher(tilSignInPassword))
    }

    private fun signInWitsEmailAndPassword() {
        val email = etSignInEmail.string().takeIf { it.isNotBlank() }
                ?: tilSignInEmail.hint.toString()
        val password = etSignInPassword.string().takeIf { it.isNotBlank() }
                ?: tilSignInPassword.hint.toString()
        viewModel.signIn(email, password).apply {
            takeIf { this }?.let {
                showProgress()
                googleAuthHelper.auth(email, password)
            }
        }
    }

    private fun signInWithGoogle() {
        googleAuthHelper.auth()
    }
}

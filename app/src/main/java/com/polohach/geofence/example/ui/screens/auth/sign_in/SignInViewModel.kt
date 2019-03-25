package com.polohach.geofence.example.ui.screens.auth.sign_in

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.cleveroad.bootstrap.kotlin_validators.ValidatorsFactory
import com.polohach.geofence.example.ui.base.BaseViewModel
import com.polohach.geofence.example.utils.ValidationField
import com.polohach.geofence.example.utils.ValidationResponseWrapper

class SignInViewModel(application: Application) : BaseViewModel(application) {

    private val emailValidator = ValidatorsFactory.getEmailValidator(application)
    private val passwordValidator = ValidatorsFactory.getPasswordValidator(application)

    val validationLD = MutableLiveData<ValidationResponseWrapper>()

    fun signIn(email: String, password: String) = validate(email, password)

    private fun validate(email: String, password: String): Boolean =
            validateEmail(email) and validatePassword(password)

    private fun validateEmail(email: String): Boolean =
            emailValidator.validate(email).run {
                validationLD.value = ValidationResponseWrapper(this, ValidationField.EMAIL)
                isValid
            }

    private fun validatePassword(password: String): Boolean =
            passwordValidator.validate(password).run {
                validationLD.value = ValidationResponseWrapper(this, ValidationField.PASSWORD)
                isValid
            }
}

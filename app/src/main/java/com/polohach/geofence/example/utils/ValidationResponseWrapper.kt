package com.polohach.geofence.example.utils

import com.cleveroad.bootstrap.kotlin_validators.ValidationResponse

enum class ValidationField {
    EMAIL,
    PASSWORD
}

data class ValidationResponseWrapper(val response: ValidationResponse,
                                     val field: ValidationField)

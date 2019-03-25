package com.polohach.geofence.example.network.error

import com.fasterxml.jackson.annotation.JsonProperty


data class Error(@JsonProperty("code")
                 val code: Int? = null,
                 @JsonProperty("key")
                 var key: String? = null,
                 @JsonProperty("message")
                 var message: String? = null)



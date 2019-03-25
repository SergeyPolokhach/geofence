package com.polohach.geofence.example.network.error

import com.fasterxml.jackson.annotation.JsonProperty


data class ServerError(@JsonProperty("__v")
                       var v: String? = null,
                       @JsonProperty("code")
                       val code: Int? = null,
                       @JsonProperty("message")
                       val message: String? = null,
                       @JsonProperty("errors")
                       var errors: List<Error>? = null)

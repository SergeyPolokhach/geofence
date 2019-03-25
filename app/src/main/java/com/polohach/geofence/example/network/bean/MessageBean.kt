package com.polohach.geofence.example.network.bean

import com.fasterxml.jackson.annotation.JsonProperty


data class MessageBean(@JsonProperty("email")
                       val email: String?,
                       @JsonProperty("latitude")
                       val latitude: Double?,
                       @JsonProperty("longitude")
                       val longitude: Double?,
                       @JsonProperty("time")
                       val time: Long?,
                       @JsonProperty("transition")
                       val transition: Int?,
                       @JsonProperty("id")
                       val id: String?)

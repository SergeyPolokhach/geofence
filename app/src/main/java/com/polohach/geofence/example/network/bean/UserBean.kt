package com.polohach.geofence.example.network.bean

import com.fasterxml.jackson.annotation.JsonProperty


data class UserBean(@JsonProperty("id")
                    val id: String?,
                    @JsonProperty("latitude")
                    val latitude: Double?,
                    @JsonProperty("longitude")
                    val longitude: Double?,
                    @JsonProperty("radius")
                    val accuracy: Float?)

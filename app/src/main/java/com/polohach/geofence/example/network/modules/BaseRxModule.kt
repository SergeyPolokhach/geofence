package com.polohach.geofence.example.network.modules

import com.polohach.geofence.example.models.converters.Converter


abstract class BaseRxModule<T, NetworkModel, M>(val api: T, val converter: Converter<NetworkModel, M>)

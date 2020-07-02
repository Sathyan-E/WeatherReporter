package com.example.weatherdetailer.network

import com.google.gson.annotations.SerializedName

class Wind {
    @SerializedName("speed")
    var spped:Float=0.toFloat()
    @SerializedName("deg")
    var deg:Float = 0.toFloat()
}
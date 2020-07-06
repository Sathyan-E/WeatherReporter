package com.example.weatherdetailer.network

import com.google.gson.annotations.SerializedName

class Current {
    @SerializedName("dt")
    var dt:Long?=null
    @SerializedName("sunrise")
    var sunrise:Long=0
    @SerializedName("sunset")
    var sunset:Long=0
    @SerializedName("temp")
    var  temp:Float= 0F
    @SerializedName("feels_like")
    var feels_like:Float=0F
    @SerializedName("pressure")
    var pressure:Float=0F
    @SerializedName("humidity")
    var humiidity:Float=0F
    @SerializedName("dew_point")
    var dew:Float=0F
    @SerializedName("uvi")
    var uvi:Float=0F
    @SerializedName("clouds")
    var clouds:Float=0F
    @SerializedName("visibility")
    var visibility:Long=0
    @SerializedName("wind_speed")
    var w_speed:Float=0F
    @SerializedName("wind_deg")
    var w_deg:Float=0F
    @SerializedName("weather")
    var weather=ArrayList<Weather>()

}
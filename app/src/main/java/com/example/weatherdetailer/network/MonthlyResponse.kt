package com.example.weatherdetailer.network

import com.google.gson.annotations.SerializedName

class MonthlyResponse {
    @SerializedName("cod")
    var cod:String?=null
    @SerializedName("message")
    var message:Int=0
    @SerializedName("cnt")
    var cnt:Int=0
    @SerializedName("list")
    var list=ArrayList<WeatherResponse>()
    @SerializedName("city")
    var city:City?=null


}
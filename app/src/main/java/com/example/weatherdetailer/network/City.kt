package com.example.weatherdetailer.network

import com.google.gson.annotations.SerializedName

class City {
    @SerializedName("id")
    var id:String?=null
    @SerializedName("name")
    var name:String?=null
    @SerializedName("coord")
    var coord: Coord? =null
    @SerializedName("country")
    var country: String?=null
    @SerializedName("population")
    var population:String?=null
    @SerializedName("timeZone")
    var time:String?=null
    @SerializedName("sunrise")
    var sunrise:String?=null
    @SerializedName("sunset")
    var sunset:String?=null

}
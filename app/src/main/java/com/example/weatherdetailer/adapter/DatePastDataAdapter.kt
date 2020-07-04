package com.example.weatherdetailer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherdetailer.network.Current
import com.example.weatherdetailer.network.WeatherResponse
import kotlinx.android.synthetic.main.datefragment_pastdata_item.view.*

class DatePastDataAdapter(private val list: List<Current>, private val layout:Int):RecyclerView.Adapter<DatePastDataAdapter.PastDataViewHolder>() {





class PastDataViewHolder(val view:View):RecyclerView.ViewHolder(view){
    val mainClimate=view.main
    val description=view.description
    val temp=view.temp
}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastDataViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(layout,parent,false)
        return PastDataViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: PastDataViewHolder, position: Int) {
        holder.mainClimate.text=list[position].weather[0].main
        holder.description.text=list[position].weather[0].description
        holder.temp.text=list[position].temp.toString()
    }
}
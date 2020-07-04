package com.example.weatherdetailer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherdetailer.network.WeatherResponse
import kotlinx.android.synthetic.main.reportlayoutitem.view.*

class DateForecastAdapter (private  val list:List<WeatherResponse>, private val layout:Int):RecyclerView.Adapter<DateForecastAdapter.ForecastDataViewHolder>(){

    class ForecastDataViewHolder(val view:View):RecyclerView.ViewHolder(view){
        val dateTextView=view.date
        val descriptionTextView=view.description
        val tempTextView=view.temp

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastDataViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(layout,parent,false)
        return ForecastDataViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder:ForecastDataViewHolder, position: Int) {
        holder.dateTextView.text=list[position].date
        holder.descriptionTextView.text=list[position].weather[0].description
        holder.tempTextView?.text= list[position].main?.temp.toString()

    }
}
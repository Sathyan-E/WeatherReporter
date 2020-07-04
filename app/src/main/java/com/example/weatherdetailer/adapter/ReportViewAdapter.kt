package com.example.weatherdetailer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherdetailer.network.WeatherResponse
import kotlinx.android.synthetic.main.reportlayoutitem.view.*

class ReportViewAdapter(private val list:List<WeatherResponse>,private  val layout:Int):RecyclerView.Adapter<ReportViewAdapter.ReportViewHolder>() {





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(layout,parent,false)
        return ReportViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.dateTextView.text=list[position].date
        holder.descriptionTextView.text=list[position].weather[0].description
        holder.tempTextView?.text= list[position].main?.temp.toString()

    }

    class ReportViewHolder(val containerView:View):RecyclerView.ViewHolder(containerView){
        val dateTextView=containerView.date
        val descriptionTextView=containerView.description
        val tempTextView=containerView.temp

    }

}
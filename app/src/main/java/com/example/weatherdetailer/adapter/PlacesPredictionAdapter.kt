package com.example.weatherdetailer.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherdetailer.R
import com.google.android.libraries.places.api.model.AutocompletePrediction

class PlacesPredictionAdapter(val list:ArrayList<AutocompletePrediction>,var clickListener:OnPlaceClickListener):RecyclerView.Adapter<PlacesPredictionAdapter.PlaceViewHolder>() {

    class PlaceViewHolder(itemview: View): RecyclerView.ViewHolder(itemview){
        val placeTextView: TextView =itemview.findViewById(R.id.place_name)

        fun initialize(item: AutocompletePrediction, listener:OnPlaceClickListener){
            placeTextView.text=item.toString()

            itemView.setOnClickListener{
                listener.onItemClick(item,adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
       val view=LayoutInflater.from(parent.context).inflate(R.layout.place_item_layout,parent,false)
        return PlaceViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.initialize(list.get(position),clickListener)
    }


}
interface OnPlaceClickListener{
    fun onItemClick(place:AutocompletePrediction,pos:Int)

}
package com.varnittyagi.ecart.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.varnittyagi.ecart.R
import com.varnittyagi.ecart.models.categorymodel.Category

class CategorisedAdapter (private var context:Context, private var listener:OnItemClickListener) : RecyclerView.Adapter<CategorisedAdapter.mviewHolder>() {
    private val items = ArrayList<Category>()
val API_BASE_URL = "https://tutorials.mianasad.com/ecommerce"
    val CATEGORIES_IMAGE_URL = "/uploads/category/"

  class mviewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
     val profileImage:ImageView = itemView.findViewById(R.id.profile_image)
     val categorisedtext:TextView = itemView.findViewById(R.id.categoryTextName)
      val categoryCL:ConstraintLayout = itemView.findViewById(R.id.categoryItemCL)
  }



    fun updateList(newList: ArrayList<Category>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mviewHolder {
val view =  mviewHolder(LayoutInflater.from(context).inflate(R.layout.category_item,parent,false))
        view.categoryCL.setOnClickListener {
            listener.onItemClick(items,view.adapterPosition)
        }
return view
    }

    override fun onBindViewHolder(holder: mviewHolder, position: Int) {
val currentitem = items[position]
        Glide.with(context).load(API_BASE_URL+CATEGORIES_IMAGE_URL+currentitem.icon).into(holder.profileImage)
        holder.categorisedtext.text = currentitem.name
       // holder.profileImage.setBackgroundColor(android.graphics.Color.parseColor(currentitem.color))

    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface OnItemClickListener {
        fun onItemClick(modelClass:ArrayList<Category>,position: Int)
    }
}

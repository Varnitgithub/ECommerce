package com.varnittyagi.ecart.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.varnittyagi.ecart.R
import com.varnittyagi.ecart.activities.ProductDetails
import com.varnittyagi.ecart.models.productmodel.Product
import kotlin.concurrent.thread

class MainAdapter (private var context:Context, private var listener:OnItemClickListener) : RecyclerView.Adapter<MainAdapter.mviewHolder>() {
    private val items = ArrayList<Product>()

    val API_BASE_URL = "https://tutorials.mianasad.com/ecommerce"
    val PRODUCT_IMAGE_URL = "/uploads/product/"
  class mviewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
     val mainprofileImage:ImageView = itemView.findViewById(R.id.main_profile_image)
     val description:TextView = itemView.findViewById(R.id.description)
     val price:TextView = itemView.findViewById(R.id.price)
      val mainitemLL:LinearLayout = itemView.findViewById(R.id.mainItemLL)
  }


    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun updateList(newList: ArrayList<Product>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mviewHolder {
val view = mviewHolder(LayoutInflater.from(context).inflate(R.layout.main_items,parent,false))
view.mainitemLL.setOnClickListener {
    listener.onProductItemClicks(items,view.adapterPosition)
}
return view
    }

    override fun onBindViewHolder(holder: mviewHolder, position: Int) {
val currentitem = items[position]
        Glide.with(context).load(API_BASE_URL+PRODUCT_IMAGE_URL+currentitem.image).into(holder.mainprofileImage)
        holder.description.text = currentitem.name
        holder.price.text = "$ ${currentitem.price}"

    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface OnItemClickListener {
        fun onProductItemClicks(modelClass:ArrayList<Product>, position: Int)
    }
}

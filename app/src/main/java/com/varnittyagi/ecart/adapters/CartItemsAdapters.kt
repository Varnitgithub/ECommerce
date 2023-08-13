package com.varnittyagi.ecart.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.api.Distribution.BucketOptions.Linear
import com.varnittyagi.ecart.R
import com.varnittyagi.ecart.models.categorymodel.Category
import com.varnittyagi.ecart.models.firebasemodel.FirebaseModel

class CartItemsAdapters(private var context: Context, private var inclistener: OnincreaseItemClickListener
,private var declistener: OndecreaseItemClickListener) :
    RecyclerView.Adapter<CartItemsAdapters.mviewHolder>() {
    private val items = ArrayList<FirebaseModel>()
    private val API_BASE_URL = "https://tutorials.mianasad.com/ecommerce"
    private val NEW_IMAGE_URL = "/uploads/product/"

    class mviewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cartItemImage: ImageView = itemView.findViewById(R.id.cartItemImage)
        val cartItemName: TextView = itemView.findViewById(R.id.cartItemName)
        val cartItemPrice: TextView = itemView.findViewById(R.id.cartItemPrice)
        val cartItemQuantity: TextView = itemView.findViewById(R.id.cartItemQuantity)
        val cartll:LinearLayout = itemView.findViewById(R.id.cartLL)
        var quantityincrease:ImageButton = itemView.findViewById(R.id.quantityIncrease)
        var quantitydecrease:ImageButton = itemView.findViewById(R.id.quantityDecrease)
    }




    fun updateList(newList: ArrayList<FirebaseModel>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mviewHolder {
        val view = mviewHolder(LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false))
        view.quantityincrease.setOnClickListener {
            inclistener.onIncCartItemClick(items,view.absoluteAdapterPosition)
        }
        view.quantitydecrease.setOnClickListener {
            declistener.onDecCartItemClick(items,view.absoluteAdapterPosition)
        }
        return view
    }

    override fun onBindViewHolder(holder: mviewHolder, position: Int) {
        val currentitem = items[position]
        Glide.with(context).load(API_BASE_URL + NEW_IMAGE_URL + currentitem.image)
            .into(holder.cartItemImage)
        holder.cartItemName.text = currentitem.name
        holder.cartItemPrice.text = currentitem.price
        holder.cartItemQuantity.text = "${currentitem.totalNoOfItemSelected} item(s)"


        //holder.profileImage.setBackgroundColor(android.graphics.Color.parseColor(currentitem.color))

    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface OndecreaseItemClickListener {
        fun onDecCartItemClick(modelClass: ArrayList<FirebaseModel> , position: Int)
    }
    interface OnincreaseItemClickListener {
        fun onIncCartItemClick(modelClass: ArrayList<FirebaseModel> , position: Int)
    }
}

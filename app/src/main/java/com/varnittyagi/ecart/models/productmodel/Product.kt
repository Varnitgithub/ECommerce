package com.varnittyagi.ecart.models.productmodel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(
    val created_at: Long,
    val draft: Int,
    val id: Int,
    val image: String,
    val last_update: Long,
    val name: String,
    val price: Int,
    val price_discount: Int,
    val status: String,
    val stock: Int
):Parcelable
package com.varnittyagi.ecart.models.allcategorydetails

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
)
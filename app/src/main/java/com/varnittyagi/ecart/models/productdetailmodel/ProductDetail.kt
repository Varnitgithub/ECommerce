package com.varnittyagi.ecart.models.productdetailmodel



data class ProductDetail(
    val categories: List<Category>,
    val created_at: Long,
    val description: String,
    val draft: Int,
    val id: Int,
    val image: String,
    val last_update: Long,
    val name: String,
    val price: Int,
    val price_discount: Int,
    val product_images: List<Any>,
    val status: String,
    val stock: Int
)
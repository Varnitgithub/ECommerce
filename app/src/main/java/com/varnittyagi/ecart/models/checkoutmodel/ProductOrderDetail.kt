package com.varnittyagi.ecart.models.checkoutmodel

data class ProductOrderDetail(
    val amount: Int,
    val created_at: Long,
    val last_update: Long,
    val price_item: Double,
    val product_id: String,
    val product_name: String
)
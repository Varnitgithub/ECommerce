package com.varnittyagi.ecart.models.checkoutmodel

data class CheckoutModel(
    val product_order: ProductOrder,
    val product_order_detail: List<ProductOrderDetail>
)
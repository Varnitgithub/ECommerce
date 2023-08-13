package com.varnittyagi.ecart.models.productdetailmodel

data class Category(
    val brief: String,
    val color: String,
    val created_at: Long,
    val draft: Int,
    val icon: String,
    val id: Int,
    val last_update: Long,
    val name: String,
    val priority: Int
)
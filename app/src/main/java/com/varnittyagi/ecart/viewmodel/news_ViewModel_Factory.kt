package com.varnittyagi.ecart.viewmodel

import Repository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class news_ViewModel_Factory(private val repository: Repository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ECart_View_Model(repository) as T
    }
}
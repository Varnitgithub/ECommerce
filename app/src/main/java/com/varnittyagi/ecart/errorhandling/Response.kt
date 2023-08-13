package com.varnittyagi.ecart.errorhandling

sealed class Response<T>(val data:T? = null,val errorMessage:String? = null){
class Loading<T>:Response<T>()
    class Success<T>(responseData:T?=null):Response<T>(data = responseData)
class Error<T>(error:String?=null):Response<T>(errorMessage = error)

}

package com.example.bedashingapp.data.model.remote

data class Customer(
    var code: String = "",
    var name: String = ""

) {
    override fun toString(): String {
        return name
    }
}


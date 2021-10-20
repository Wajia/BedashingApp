package com.example.bedashingapp.data.model.remote

data class LoginResponse(
    var d: D5
)

data class D5(
    var results: List<Employee>
)

data class Employee(
    val ObjectID: String,
    val EmployeeID: String,
    val EmployeeCommon: List<EmployeeCommon>
)

data class EmployeeCommon(
    val UserName_KUT: String,
    val Password_KUT: String
)
package com.example.bedashingapp.data.model.remote

data class GetUserDetailsResponse(
    val value: List<UserDetailsModel>
)

data class UserDetailsModel(
    val InternalKey: Int,
    val UserPassword: String?,
    val UserCode: String,
    val UserName: String,
    val Superuser: String?,
    val eMail: String?,
    val MobilePhoneNumber: String?,
    val Branch: String?,
    val Department: String?,
    val Locked: String?,
    val Group: String?,
    val CashLimit: String?,
    val U_DfltSers: String?,
    val U_DfltRegn: String?,
    val U_DfltStor: String?

)



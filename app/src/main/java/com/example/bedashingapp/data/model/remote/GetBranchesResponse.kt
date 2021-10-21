package com.example.bedashingapp.data.model.remote

data class GetBranchesResponse(
    val value: List<Branch>
)

data class Branch(
    val BPLID: Int?,
    val BPLName: String,
    val BPLNameForeign: String,
    val DefaultCustomerID: String,
    val DefaultVendorID: String,
    val DefaultWarehouseID: String


) {
    override fun toString(): String {
        return BPLNameForeign
    }
}

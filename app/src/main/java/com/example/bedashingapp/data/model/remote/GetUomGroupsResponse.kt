package com.example.bedashingapp.data.model.remote

data class GetUomGroupsResponse(
    val value: List<UOMGroup>
)

data class UOMGroup(
    val AbsEntry: Int,
    val UoMGroupDefinitionCollection: List<UoMGroupDefinitionCollection>
)

data class UoMGroupDefinitionCollection(
    val AlternateUoM: Int,
    val AlternateQuantity: Double,
    val BaseQuantity: Double,
    val WeightFactor: Int,
    val UdfFactor: Int
)
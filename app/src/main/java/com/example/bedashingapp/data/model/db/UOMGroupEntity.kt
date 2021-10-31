package com.example.bedashingapp.data.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.bedashingapp.data.model.remote.UoMGroupDefinitionCollection

@Entity(tableName = "table_uom_group")
data class UOMGroupEntity(
    @PrimaryKey
    val AbsEntry: String,
    val UoMGroupDefinitionCollection: List<UoMGroupDefinitionCollection>
)

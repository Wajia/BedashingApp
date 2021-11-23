package com.example.bedashingapp.data.model.local

import com.example.bedashingapp.data.model.remote.Branch
import com.example.bedashingapp.data.model.remote.Warehouse

data class PreviousUserBranch(
    var userName: String,
    var userBranch: Branch,
    var userWarehouse: Warehouse,
)

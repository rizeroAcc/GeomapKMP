package com.rizero.shared_core_data.model

data class Project(
    val name : String,
    val id : String,
    val membersCount : Int,
    val syncStatus : Int,
    val role : Int,
)
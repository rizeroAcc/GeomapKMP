package com.rizero.shared_core_data.model

data class Project(
    val name : String,
    val id : String,
    val serverID : String?,
    val membersCount : Int,
    val role : Int,
)
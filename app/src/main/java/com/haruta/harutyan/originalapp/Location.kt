package com.haruta.harutyan.originalapp


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class Location(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,

    val name: String,
    val latitude: Double,
    val longitude: Double,
)
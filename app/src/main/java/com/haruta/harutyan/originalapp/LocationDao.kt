package com.haruta.harutyan.originalapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface LocationDao {
    // データを追加
    @Insert
    fun insert(location: Location)

    // データを更新
    @Update
    fun update(location: Location)

    // データを削除
    @Delete
    fun delete(location: Location)

    // 全てのデータを取得
    @Query("select * from locations")
    fun getAll(): List<Location>

    // 全てのデータを削除
    @Query("delete from locations")
    fun deleteAll()

    // locationのuidがidのUserを取得
    @Query("select * from locations where uid = :id")
    fun getLocation(id: Int): Location
}
package com.haruta.harutyan.originalapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    // データを追加
    @Insert
    fun insert(location: locationRoom.User)

    // データを更新
    @Update
    fun update(location: locationRoom.User)

    // データを削除
    @Delete
    fun delete(location: locationRoom.User)

    // 全てのデータを取得
    @Query("select * from location")
    fun getAll(): List<locationRoom.User>

    // 全てのデータを削除
    @Query("delete from location")
    fun deleteAll()

    // UserのuidがidのUserを取得
    @Query("select * from location where uid = :id")
    fun getUser(id: Int): locationRoom.User
}
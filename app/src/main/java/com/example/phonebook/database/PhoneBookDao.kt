package com.example.phonebook.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PhoneBookDao {
    @Query("SELECT * FROM PhoneBookDbModel ORDER BY name")
    fun getAllSync(): List<PhoneBookDbModel>

    @Query("SELECT * FROM PhoneBookDbModel WHERE id IN (:phoneIds) ORDER BY name ")
    fun getPhoneByIdsSync(phoneIds: List<Long>): List<PhoneBookDbModel>

    @Query("SELECT * FROM PhoneBookDbModel WHERE id LIKE :id ORDER BY name")
    fun findByIdSync(id: Long): PhoneBookDbModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(phoneBookDbModel: PhoneBookDbModel)

    @Insert
    fun insertAll(vararg phoneBookDbModel: PhoneBookDbModel)

    @Query("DELETE FROM PhoneBookDbModel WHERE id LIKE :id")
    fun delete(id: Long)

    @Query("DELETE FROM PhoneBookDbModel WHERE id IN (:phoneIds)")
    fun delete(phoneIds: List<Long>)
}
package com.example.todoapp.holidays.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HolidayDao {
    @Query("SELECT * FROM holidays")
    suspend fun getHolidays(): List<HolidayEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addHolidays(holidays: List<HolidayEntity>)

    @Query("DELETE FROM holidays")
    suspend fun deleteAllHolidays()
}
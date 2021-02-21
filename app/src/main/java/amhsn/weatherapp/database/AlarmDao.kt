package amhsn.weatherapp.database

import amhsn.weatherapp.pojo.CustomAlarm
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AlarmDao {
    @Query("SELECT * FROM alarm_table")
    fun getAllAlarms(): LiveData<List<CustomAlarm>>

    @Insert
    fun insertAlarm(alarm: CustomAlarm)

    @Delete
    fun deleteAlarm(alarm: CustomAlarm)

    @Update
    fun updateAlarm(alarm: CustomAlarm)

    @Query("delete from alarm_table where id = :id")
    fun deleteAlarm(id: Long)
}
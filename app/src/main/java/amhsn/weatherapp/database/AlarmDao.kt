package amhsn.weatherapp.database

import amhsn.weatherapp.pojo.CustomAlarm
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AlarmDao {
    @Query("SELECT * FROM alarm_table")
    fun getAllAlarms(): LiveData<List<CustomAlarm>>

    @Insert
    fun insertAlarm(alarm: CustomAlarm)

    @Delete
    fun deleteAlarm(alarm: CustomAlarm)
}
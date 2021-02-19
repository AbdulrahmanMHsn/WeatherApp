package amhsn.weatherapp.database

import amhsn.weatherapp.pojo.CustomAlarm
import amhsn.weatherapp.pojo.Favourite
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Favourite::class, CustomAlarm::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getFavouriteDaoInstance(): FavouriteDao
    abstract fun getAlarmDaoInstance(): AlarmDao

    companion object {
        var instance: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            instance ?: synchronized(this)
            {
                val roomInstance =
                    Room.databaseBuilder(context, AppDatabase::class.java, "WeatherDB1").build()
                instance = roomInstance
            }
            return instance!!
        }
    }

}
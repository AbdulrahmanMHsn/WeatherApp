package amhsn.weatherapp.database

import amhsn.weatherapp.pojo.Favourite
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavouriteDao {

    @Query("SELECT * FROM favourite_table")
    fun getAllFavourite() : LiveData<List<Favourite>>

    @Insert
    fun insertFavourite(movie: Favourite)

    @Delete
    fun deleteFavourite(movie: Favourite)
}
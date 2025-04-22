import androidx.room.Database
import androidx.room.RoomDatabase

//TODO MAKE SURE THIS WORKS WITH DOCUMENT OBJECT AND DAO

@Database(entities = [Document::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun documentDao(): DocumentDao
}
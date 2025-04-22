import android.content.Context
import androidx.room.Room

//TODO DOUBLE CHECK THIS WORKS WITH DAO, OBJECT AND DATABASE

object DatabaseProvider {
    @Volatile private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "fossdocs-db"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}

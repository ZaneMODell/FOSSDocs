import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

//TODO CHANGE THIS TO MATCH DOCUMENT OBJECT

@Dao
interface DocumentDao {

    @Query("SELECT * FROM documents ORDER BY lastOpened DESC")
    suspend fun getAll(): List<Document>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(document: Document)

    @Delete
    suspend fun delete(document: Document)
}

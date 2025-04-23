import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

//TODO CHANGE THIS TO MATCH DOCUMENT OBJECT

@Dao
interface DocumentDao {

    @Query("SELECT * FROM documents ORDER BY lastOpened DESC")
    fun getAllByLastOpened(): Flow<List<Document>>

    @Query("SELECT * FROM documents ORDER BY name ASC")
    fun getAllByName(): Flow<List<Document>>

    @Upsert
    suspend fun insert(document: Document)

    @Delete
    suspend fun delete(document: Document)
}

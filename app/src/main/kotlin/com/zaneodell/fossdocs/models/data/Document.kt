import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documents")
data class Document(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "path")
    val path: String,
    @ColumnInfo(name = "lastOpened")
    val lastOpened: Long
)
{
//    fun imagePreview() : Bitmap{
//
//    }
}

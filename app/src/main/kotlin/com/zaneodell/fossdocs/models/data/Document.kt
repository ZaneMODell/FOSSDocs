import androidx.room.Entity
import androidx.room.PrimaryKey

//TODO CHANGE THIS TO ACTUALLY MAKE DB OBJECT MAKE SENSE AND HAVE GOOD SCHEMA

@Entity(tableName = "documents")
data class Document(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val path: String,
    val lastOpened: Long
)

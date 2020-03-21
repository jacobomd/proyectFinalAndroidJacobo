package io.keepcoding.eh_ho.database
import androidx.room.*
import java.util.*


@Entity(tableName = "topic_table", indices = [Index(value = ["topic_id"], unique = true)])
data class TopicEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "topic_id") val topicId: String,
    @ColumnInfo(name = "topic_title") val title: String,
    @ColumnInfo(name = "topic_date") val date: Date,
    @ColumnInfo(name = "topic_posts") val posts: Int,
    @ColumnInfo(name = "topic_views") val views: Int,
    @ColumnInfo(name = "last_posted_at") val last_posted_at: Date
)

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}


@Entity(tableName = "user_table", indices = [Index(value = ["user_id"], unique = true)])
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "user_id") val id: String,
    @ColumnInfo(name = "user_username") val username: String,
    @ColumnInfo(name = "user_name") val name: String,
    @ColumnInfo(name = "user_avatar_template") val avatar_template: String
)

@Entity(tableName = "posters_table", indices = [Index(value = ["posters_id"], unique = true)])
data class PostersEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "posters_id") val id: String,
    @ColumnInfo(name = "posters_username") val username: String,
    @ColumnInfo(name = "posters_cooked") val cooked: String,
    @ColumnInfo(name = "posters_createdAt") val createdAt: String,
    @ColumnInfo(name = "posters_topic_id") val posters_topic_id: String
)

@Dao
interface TopicDao {
    @Query("SELECT * FROM topic_table ORDER BY last_posted_at DESC")
    fun getTopics(): List<TopicEntity>

    @Query("SELECT * FROM user_table")
    fun getUsers(): List<UserEntity>

    @Query("SELECT * FROM posters_table")
    fun getAllPosters(): List<PostersEntity>

    @Query("SELECT * FROM topic_table WHERE topic_id LIKE :id")
    fun getTopicById(id: String): TopicEntity

    @Query("SELECT * FROM posters_table WHERE posters_topic_id LIKE :id ORDER BY posters_createdAt")
    fun getPostsByTopic(id: String): List<PostersEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTopics(topicList: List<TopicEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllUsers(userList: List<UserEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllPosters(postersList: List<PostersEntity>): List<Long>

    @Delete
    fun delete(topic: TopicEntity)

}

@Database(entities = [UserEntity::class, TopicEntity::class, PostersEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class TopicDatabase : RoomDatabase() {
    abstract fun topicDao(): TopicDao
}
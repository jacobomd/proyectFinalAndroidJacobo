package io.keepcoding.eh_ho.database

import androidx.room.*
import java.sql.Date


@Entity(tableName = "topic_table")
data class TopicEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "topic_id") val topicId: String,
    @ColumnInfo(name = "topic_title") val title: String,
    @ColumnInfo(name = "topic_date") val date: String,
    @ColumnInfo(name = "topic_posts") val posts: Int,
    @ColumnInfo(name = "topic_views") val views: Int
)

@Entity(tableName = "user_table")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "user_id") val id: String,
    @ColumnInfo(name = "user_username") val username: String,
    @ColumnInfo(name = "user_name") val name: String,
    @ColumnInfo(name = "user_avatar_template") val avatar_template: String
)

@Dao
interface TopicDao {
    @Query("SELECT * FROM topic_table")
    fun getTopics(): List<TopicEntity>

    @Query("SELECT * FROM user_table")
    fun getUsers(): List<UserEntity>

    @Query("SELECT * FROM topic_table WHERE topic_id LIKE :id")
    fun getTopicById(id: String): TopicEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllTopics(topicList: List<TopicEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllUsers(userList: List<UserEntity>): List<Long>

    @Delete
    fun delete(topic: TopicEntity)

}

@Database(entities = [UserEntity::class, TopicEntity::class], version = 1)
abstract class TopicDatabase : RoomDatabase() {
    abstract fun topicDao(): TopicDao
}
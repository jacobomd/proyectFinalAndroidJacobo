package io.keepcoding.eh_ho.domain


import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

data class Topic(
    @SerializedName("id") val id: String = UUID.randomUUID().toString(),
    @SerializedName("title") val title: String,
    @SerializedName("created_at") val date: Date = Date(),
    @SerializedName("posts_count") val posts: Int = 0,
    @SerializedName("views") val views: Int = 0,
    @SerializedName("posters") val posters: List<Poster>
) {

    companion object {

        fun parseTopics(response: JSONObject): List<Topic> {
            val jsonTopics = response.getJSONObject("topic_list")
                .getJSONArray("topics")

            val topics = mutableListOf<Topic>()

            for (i in 0 until jsonTopics.length()) {

                val posters = mutableListOf<Poster>()
                val jsonPosters = jsonTopics.getJSONObject(i).getJSONArray("posters")
                for(j in 0 until jsonPosters.length()) {
                    val parsedPost = Poster(
                        jsonPosters.getJSONObject(j).getString("description"),
                        jsonPosters.getJSONObject(j).getInt("user_id")
                    )
                    posters.add(parsedPost)
                }

                val parsedTopic = Topic(
                    jsonTopics.getJSONObject(i).getInt("id").toString(),
                    jsonTopics.getJSONObject(i).getString("title"),
                    dateFormatted(jsonTopics.getJSONObject(i)),
                    jsonTopics.getJSONObject(i).getInt("posts_count"),
                    jsonTopics.getJSONObject(i).getInt("views"),
                    posters
                )

                topics.add(parsedTopic)
            }

            return topics
        }

        private fun dateFormatted(jsonObject: JSONObject) : Date {

             val date = jsonObject.getString("created_at")
                .replace("Z", "+0000")

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
            val dateFormatted = dateFormat.parse(date) ?: Date()
            return dateFormatted
        }

        const val MINUTES_MILLIS = 1000L * 60
        const val HOUR_MILLIS = MINUTES_MILLIS * 60
        const val DAY_MILLIS = HOUR_MILLIS * 24
        const val MONTH_MILLIS = DAY_MILLIS * 30
        const val YEAR_MILLIS = MONTH_MILLIS * 12

    }


    data class Poster( val description: String, val user_id: Int)


    data class TimeOffset(val amount: Int, val unit: Int)

    fun getTimeOffset(dateToCompare: Date = Date()): TimeOffset {
        val current = dateToCompare.time
        val diff = current - date.time

        val years = diff / YEAR_MILLIS
        if (years > 0) return TimeOffset(
            years.toInt(),
            Calendar.YEAR
        )

        val months = diff / MONTH_MILLIS
        if (months > 0) return TimeOffset(
            months.toInt(),
            Calendar.MONTH
        )

        val days = diff / DAY_MILLIS
        if (days > 0) return TimeOffset(
            days.toInt(),
            Calendar.DAY_OF_MONTH
        )

        val hours = diff / HOUR_MILLIS
        if (hours > 0) return TimeOffset(
            hours.toInt(),
            Calendar.HOUR
        )

        val minutes = diff / MINUTES_MILLIS
        if (minutes > 0) return TimeOffset(
            minutes.toInt(),
            Calendar.MINUTE
        )

        return TimeOffset(0, Calendar.MINUTE)
    }

}

data class User(
    @SerializedName("id") val id: String = UUID.randomUUID().toString(),
    @SerializedName("username") val username: String,
    @SerializedName("name") val name: String,
    @SerializedName("avatar_template") val avatar_template: String
)

{
    companion object {

        fun parseUsers(response: JSONObject): List<User> {
            val jsonUsers = response.getJSONArray("users")

            val users = mutableListOf<User>()


            for (i in 0 until jsonUsers.length()) {
                val parsedUser = parseUser(jsonUsers.getJSONObject(i))
                users.add(parsedUser)
            }

            return users
        }

        private fun parseUser(jsonObject: JSONObject): User {

            return User(
                jsonObject.getInt("id").toString(),
                jsonObject.getString("username"),
                jsonObject.getString("name"),
                jsonObject.getString("avatar_template")

            )
        }

    }
}

data class DetailUser(
    @SerializedName("id") val id: String = UUID.randomUUID().toString(),
    @SerializedName("username") val username: String,
    @SerializedName("avatar_template") val avatar_template: String,
    @SerializedName("name") val name: String,
    @SerializedName("last_posted_at") val last_posted_at: String,
    @SerializedName("last_seen_at") val last_seen_at: String,
    @SerializedName("created_at") val date: String,
    @SerializedName("moderator") val moderator: String
)

{
    companion object {

        fun parseUsers(response: JSONObject): DetailUser {
            val jsonUsers = response.getJSONObject("user")

            val date = jsonUsers.getString("last_seen_at")
                .replace("Z", "+0000")

            val dateFormatted = convertDate(date)

            return DetailUser(
                jsonUsers.getInt("id").toString(),
                jsonUsers.getString("username"),
                jsonUsers.getString("avatar_template"),
                jsonUsers.getString("name"),
                jsonUsers.getString("last_posted_at"),
                dateFormatted,
                jsonUsers.getString("created_at"),
                jsonUsers.getString("moderator")

            )
        }

        @SuppressLint("SimpleDateFormat")
        private fun convertDate(date: String): String {
            val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
            val targetFormat = SimpleDateFormat("MMM dd, yyyy")
            val dateResult = originalFormat.parse(date)
            val formattedDate = targetFormat.format(dateResult)
            return formattedDate
        }

    }
}

data class Post(
    val id: String = UUID.randomUUID().toString(),
    val username: String,
    val cooked: String,
    val createdAt: String

) {
    companion object {


        fun parsePosts(response: JSONObject): List<Post> {
            val jsonPosts = response.getJSONObject("post_stream")
                .getJSONArray("posts")

            val posts = mutableListOf<Post>()


            for (i in 0 until jsonPosts.length()) {
                val parsedPost =
                    parsePost(jsonPosts.getJSONObject(i))
                posts.add(parsedPost)
            }

            return posts
        }

        private fun parsePost(jsonObject: JSONObject): Post {
            val date = jsonObject.getString("created_at")
                .replace("Z", "+0000")

            val dateFormatted = convertDate(date)

            val content = jsonObject.getString("cooked")
                .replace("<p>", "")
                .replace("</p>", "")

            return Post(
                jsonObject.getInt("id").toString(),
                jsonObject.getString("username"),
                content,
                dateFormatted

            )
        }


        private fun convertDate(date: String): String {
            val originalFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
            val targetFormat = SimpleDateFormat("MMM dd, yyyy")
            val dateResult = originalFormat.parse(date)
            val formattedDate = targetFormat.format(dateResult)
            return formattedDate
        }


    }
}

package io.keepcoding.eh_ho.data.repository

import android.content.Context
import android.os.Handler
import androidx.room.Room
import com.android.volley.NetworkError
import com.android.volley.Request
import com.android.volley.ServerError
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.service.ApiRequestQueue
import io.keepcoding.eh_ho.data.service.ApiRoutes
import io.keepcoding.eh_ho.data.service.RequestError
import io.keepcoding.eh_ho.data.service.UserRequest
import io.keepcoding.eh_ho.database.PostersEntity
import io.keepcoding.eh_ho.database.TopicDatabase
import io.keepcoding.eh_ho.domain.CreatePostModel
import io.keepcoding.eh_ho.domain.Post
import org.json.JSONObject
import kotlin.concurrent.thread

object PostsRepo {

    fun getPosts(
        context: Context,
        idTopic: Int,
        onSuccess: (List<Post>) -> Unit,
        onError: (RequestError) -> Unit
    ) {
        val db: TopicDatabase = Room.databaseBuilder(context, TopicDatabase::class.java, "topic-database")
            .build()
        val request = UserRequest(
            Request.Method.GET,
            ApiRoutes.getPosts(idTopic),
            null,
            {
                it?.let {
                    onSuccess.invoke(Post.parsePosts(it))
                }

                if (it == null)
                    onError.invoke(RequestError(messageId = R.string.error_invalid_response))
            },
            {
                it.printStackTrace()
                if (it is NetworkError) {
                val handler = Handler(context.mainLooper)
                thread {
                    val postersList = db.topicDao().getPostsByTopic(idTopic.toString())
                    val runnable = Runnable {
                        if (postersList.isNotEmpty()) {
                            onSuccess(postersList.toModelPosters())
                        } else {
                            onError.invoke(RequestError(messageId = R.string.error_network))
                        }
                    }
                    handler.post(runnable)
                }
                }
                else
                    onError.invoke(RequestError(it))
            })

        ApiRequestQueue.getRequesteQueue(context)
            .add(request)
    }

    fun getAllPosts(
        context: Context,
        onSuccess: (List<Post>) -> Unit,
        onError: (RequestError) -> Unit
    ) {
        val db: TopicDatabase = Room.databaseBuilder(context, TopicDatabase::class.java, "topic-database")
            .build()
        val request = UserRequest(
            Request.Method.GET,
            ApiRoutes.getAllPosts(),
            null,
            {
                it?.let {
                    onSuccess.invoke(Post.parseAllPosts(it))
                    thread {
                        db.topicDao()
                            .insertAllPosters(postersList = Post.parseAllPosts(it).toEntityPoster())
                    }
                }
                if (it == null) {
                    onError.invoke(RequestError(messageId = R.string.error_network))
                }
            },
            {
                it.printStackTrace()
                if (it is NetworkError)
                    onError.invoke(RequestError(messageId = R.string.error_network))
                else
                    onError.invoke(RequestError(it))
            })
        ApiRequestQueue.getRequesteQueue(context)
            .add(request)
    }



    fun createPost(
        context: Context,
        model: CreatePostModel,
        onSuccess: (CreatePostModel) -> Unit,
        onError: (RequestError) -> Unit
    ) {
        val username = UserRepo.getUsername(context)
        val request = UserRequest(
            Request.Method.POST,
            ApiRoutes.createPost(),
            model.toJson(),
            {
                it?.let {
                    onSuccess.invoke(model)
                }

                if (it == null)
                    onError.invoke(RequestError(messageId = R.string.error_invalid_response))
            },
            {
                it.printStackTrace()

                if (it is ServerError && it.networkResponse.statusCode == 422) {
                    val body = String(it.networkResponse.data, Charsets.UTF_8)
                    val jsonError = JSONObject(body)
                    val errors = jsonError.getJSONArray("errors")
                    var errorMessage = ""

                    for (i in 0 until errors.length()) {
                        errorMessage += "${errors[i]} "
                    }

                    onError.invoke(
                        RequestError(
                            it,
                            message = errorMessage
                        )
                    )


                } else if (it is NetworkError)
                    onError.invoke(
                        RequestError(
                            it,
                            messageId = R.string.error_network
                        )
                    )
                else
                    onError.invoke(RequestError(it))
            }
        )

        ApiRequestQueue.getRequesteQueue(context)
            .add(request)
    }
}

private fun List<PostersEntity>.toModelPosters(): List<Post> = map { it.toModel() }

private fun PostersEntity.toModel(): Post = Post(
    id = id,
    username = username,
    cooked = cooked,
    createdAt = createdAt,
    topic_id = posters_topic_id
)


private fun List<Post>.toEntityPoster(): List<PostersEntity> = map { it.toEntity() }

private fun Post.toEntity(): PostersEntity = PostersEntity(
    id = id,
    username = username,
    cooked = cooked,
    createdAt = createdAt,
    posters_topic_id = topic_id.toString()
)

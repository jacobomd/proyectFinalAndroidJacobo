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
import io.keepcoding.eh_ho.database.TopicDatabase
import io.keepcoding.eh_ho.database.TopicEntity
import io.keepcoding.eh_ho.database.UserEntity
import io.keepcoding.eh_ho.domain.CreateTopicModel
import io.keepcoding.eh_ho.domain.DetailUser
import io.keepcoding.eh_ho.domain.Topic
import io.keepcoding.eh_ho.domain.User
import org.json.JSONObject
import kotlin.concurrent.thread


object TopicsRepo {

    fun getTopics(
        context: Context,
        onSuccess: (List<Topic>, List<User>) -> Unit,
        onError: (RequestError) -> Unit
    ) {
        val db: TopicDatabase = Room.databaseBuilder(context, TopicDatabase::class.java, "topic-database")
            .build()
        val request = UserRequest(
            Request.Method.GET,
            ApiRoutes.getTopics(),
            null,
            {
                it?.let {
                onSuccess.invoke(Topic.parseTopics(it), User.parseUsers(it))
                   thread {
                        db.topicDao().insertAllTopics(topicList = Topic.parseTopics(it).toEntityTopic())
                        db.topicDao().insertAllUsers(userList = User.parseUsers(it).toEntityUser())
                    }
                }

                if (it == null)
                    onError.invoke(RequestError(messageId = R.string.error_invalid_response))
            },
            {
                it.printStackTrace()
                if (it is NetworkError) {
                    val handler = Handler(context.mainLooper)
                    thread {
                        val topicList = db.topicDao().getTopics()
                        val userList = db.topicDao().getUsers()
                        val runnable = Runnable {
                            if (topicList.isNotEmpty()) {
                                onSuccess(topicList.toModelTopic(), userList.toModelUser())
                            } else {
                                onError.invoke(RequestError(messageId = R.string.error_network))
                            }
                        }
                        handler.post(runnable)
                    }
                } else {
                    onError.invoke(RequestError(it))
                }
            })

        ApiRequestQueue.getRequesteQueue(context)
            .add(request)
    }

    fun getDetailUser(
        context: Context,
        username: String,
        onSuccess: (DetailUser) -> Unit,
        onError: (RequestError) -> Unit
    ) {
        val request = UserRequest(
            Request.Method.GET,
            ApiRoutes.getDetailUser(username),
            null,
            {
                it?.let {
                    onSuccess.invoke(DetailUser.parseUsers(it))
                    println("El contenido del topic es : ${it}")
                }

                if (it == null)
                    onError.invoke(RequestError(messageId = R.string.error_invalid_response))
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

    fun createTopic(
        context: Context,
        model: CreateTopicModel,
        onSuccess: (CreateTopicModel) -> Unit,
        onError: (RequestError) -> Unit
    ) {
        val request = UserRequest(
            Request.Method.POST,
            ApiRoutes.createTopic(),
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

                    onError.invoke(RequestError(it, message = errorMessage))

                } else if (it is NetworkError)
                    onError.invoke(RequestError(it, messageId = R.string.error_network))
                else
                    onError.invoke(RequestError(it))
            }
        )

        ApiRequestQueue.getRequesteQueue(context)
            .add(request)
    }

}

// TO MODEL
private fun List<TopicEntity>.toModelTopic(): List<Topic> = map { it.toModel() }

private fun TopicEntity.toModel(): Topic = Topic(
    id = topicId,
    title = title,
    posts = posts,
    views = views,
    posters = mutableListOf()
)

private fun List<UserEntity>.toModelUser(): List<User> = map { it.toModel() }

private fun UserEntity.toModel(): User = User(
    id = id,
    username = username,
    name = name,
    avatar_template = avatar_template
)

// TO ENTITY
private fun List<Topic>.toEntityTopic(): List<TopicEntity> = map { it.toEntity() }

private fun Topic.toEntity(): TopicEntity = TopicEntity(
    topicId = id,
    title = title,
    date = date.toString(),
    posts = posts,
    views = views
)

private fun List<User>.toEntityUser(): List<UserEntity> = map { it.toEntity() }

private fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    username = username,
    name = name,
    avatar_template = avatar_template
)



package io.keepcoding.eh_ho.data.repository

import android.content.Context
import com.android.volley.NetworkError
import com.android.volley.Request
import com.android.volley.ServerError
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.service.ApiRequestQueue
import io.keepcoding.eh_ho.data.service.ApiRoutes
import io.keepcoding.eh_ho.data.service.RequestError
import io.keepcoding.eh_ho.data.service.UserRequest
import io.keepcoding.eh_ho.domain.DetailUser
import io.keepcoding.eh_ho.domain.Topic
import io.keepcoding.eh_ho.domain.User
import io.keepcoding.eh_ho.feature.topics.view.state.TopicManagementState
import org.json.JSONObject


object TopicsRepo {

    fun getTopics(
        context: Context,
        onSuccess: (List<Topic>, List<User>) -> Unit,
        onError: (RequestError) -> Unit
    ) {
        val request = UserRequest(
            Request.Method.GET,
            ApiRoutes.getTopics(),
            null,
            {
                it?.let {
                onSuccess.invoke(Topic.parseTopics(it), User.parseUsers(it))
                println("El contenido del topic es : ${it.getJSONObject("topic_list").getJSONArray("topics")}")
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

}
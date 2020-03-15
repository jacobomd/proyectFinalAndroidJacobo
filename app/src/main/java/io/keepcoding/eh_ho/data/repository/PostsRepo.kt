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
import io.keepcoding.eh_ho.domain.CreatePostModel
import io.keepcoding.eh_ho.domain.Post
import org.json.JSONObject

object PostsRepo {

    fun getPosts(
        context: Context,
        idTopic: Int,
        onSuccess: (List<Post>) -> Unit,
        onError: (RequestError) -> Unit
    ) {
        val username = UserRepo.getUsername(context)
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
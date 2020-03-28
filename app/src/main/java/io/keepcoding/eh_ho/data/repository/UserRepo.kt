package io.keepcoding.eh_ho.data.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import com.android.volley.NetworkError
import com.android.volley.Request
import com.android.volley.ServerError
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.service.*
import io.keepcoding.eh_ho.domain.ResetPasswordModel
import io.keepcoding.eh_ho.domain.SignModel
import io.keepcoding.eh_ho.domain.SignUpModel
import org.json.JSONObject

const val PREFERENCES_SESSION = "session"
const val PREFERENCES_SESSION_USERNAME = "username"

object UserRepo {

    fun signIn(context: Context,
               signModel: SignModel,
               onSuccess: (SignModel) -> Unit,
               onError: (RequestError) -> Unit
    ) {
        val request = AdminRequest(
            Request.Method.GET,
            ApiRoutes.signIn(signModel.username),
            null,
            {response ->
                saveSession(context, signModel.username)
                onSuccess.invoke(signModel)
            },
            {error ->
                error.printStackTrace()

                if (error is ServerError && error.networkResponse.statusCode == 404)
                    onError.invoke(
                        RequestError(
                            error,
                            messageId = R.string.error_not_registered
                        )
                    )
                else if (error is NetworkError)
                    onError.invoke(
                        RequestError(
                            error,
                            messageId = R.string.error_network
                        )
                    )
                else
                    onError.invoke(RequestError(error))
            }
        )

        ApiRequestQueue
            .getRequesteQueue(context)
            .add(request)
    }

    fun signUp(
        context: Context,
        signUpModel: SignUpModel,
        onSuccess: (SignUpModel) -> Unit,
        onError: (RequestError) -> Unit
    ) {
        val request = AdminRequest(
            Request.Method.POST,
            ApiRoutes.signUp(),
            signUpModel.toJson(),
            { response ->

                response?.let {
                    if (it?.optBoolean("success")) {
                        saveSession(context, signUpModel.username)
                        onSuccess.invoke(signUpModel)
                    }
                    else
                        onError.invoke(RequestError(message = it?.getString("message")))
                }

                if (response == null)
                    onError.invoke(RequestError(messageId = R.string.error_invalid_response))

            },
            {
                it.printStackTrace()

                if (it is NetworkError)
                    onError.invoke(RequestError(it, messageId = R.string.error_network))
                else
                    onError.invoke(RequestError(it))
            }
        )

        ApiRequestQueue.getRequesteQueue(context)
            .add(request)

    }

    fun resetPassword(
        context: Context,
        model: ResetPasswordModel,
        onSuccess: (ResetPasswordModel, userFound: Boolean) -> Unit,
        onError: (RequestError) -> Unit
    ) {
        val request = UserRequest(

            Request.Method.POST,
            ApiRoutes.resetPassword(),
            model.toJson(),
            {
                it?.let {
                    val userFound = it.getBoolean("user_found")
                    onSuccess.invoke(model, userFound)
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


                }

                else if (it is NetworkError)
                    onError.invoke(RequestError(it, messageId = R.string.error_network))
                else
                    onError.invoke(RequestError(it))
            }
        )
        ApiRequestQueue.getRequesteQueue(context)
            .add(request)
    }


    fun isLogged(context: Context): Boolean {
        val pref = context.getSharedPreferences(PREFERENCES_SESSION, MODE_PRIVATE)
        val user = pref.getString(PREFERENCES_SESSION_USERNAME, null)
        return user != null
    }

    fun getUsername(context: Context): String {
        val pref = context.getSharedPreferences(PREFERENCES_SESSION, MODE_PRIVATE)
        val user = pref.getString(PREFERENCES_SESSION_USERNAME, "") ?: ""

        return user
    }


    fun logOut(context: Context) {
        val pref = context.getSharedPreferences(PREFERENCES_SESSION, MODE_PRIVATE)
        pref.edit()
            .remove(PREFERENCES_SESSION_USERNAME)
            .apply()
    }

    fun checkInternet(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
        return isConnected
    }

    private fun saveSession(context: Context, userName: String) {
        val pref = context.applicationContext.getSharedPreferences(PREFERENCES_SESSION, MODE_PRIVATE)
        pref.edit()
            .putString(PREFERENCES_SESSION_USERNAME, userName)
            .apply()
    }
}
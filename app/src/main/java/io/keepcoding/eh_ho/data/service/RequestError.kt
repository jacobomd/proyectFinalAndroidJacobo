package io.keepcoding.eh_ho.data.service

import com.android.volley.VolleyError

data class RequestError (
    val volleyError: VolleyError? = null,
    val message: String? = null,
    val messageId: Int? = null
)

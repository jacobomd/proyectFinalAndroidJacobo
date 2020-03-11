package io.keepcoding.eh_ho.domain

import org.json.JSONObject

data class SignModel(
    val username: String,
    val password: String
)

data class SignUpModel(
    val username: String,
    val email: String,
    val password: String
) {
    fun toJson(): JSONObject {
        return JSONObject()
            .put("name", username)
            .put("username", username)
            .put("email", email)
            .put("password", password)
            .put("active", true)
            .put("approved", true)
    }
}

data class CreateTopicModel(
    val title: String,
    val content: String
) {
    fun toJson(): JSONObject {
        return JSONObject()
            .put("title", title)
            .put("raw", content)
    }
}

data class CreatePostModel(
    val content: String,
    val topic_id: Int
) {
    fun toJson(): JSONObject {
        return JSONObject()
            .put("raw", content)
            .put("topic_id", topic_id)
    }
}

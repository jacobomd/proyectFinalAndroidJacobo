package io.keepcoding.eh_ho.feature.topics.view.state


import io.keepcoding.eh_ho.data.service.RequestError
import io.keepcoding.eh_ho.domain.Topic

sealed class TopicManagementState {
    object Loading : TopicManagementState()
    class LoadTopicList(val topicList: List<Topic>) : TopicManagementState()
    class RequestErrorReported(val requestError: RequestError) : TopicManagementState()
}
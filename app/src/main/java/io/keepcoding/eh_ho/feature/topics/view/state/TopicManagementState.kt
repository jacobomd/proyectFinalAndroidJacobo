package io.keepcoding.eh_ho.feature.topics.view.state


import io.keepcoding.eh_ho.data.service.RequestError
import io.keepcoding.eh_ho.domain.DetailUser
import io.keepcoding.eh_ho.domain.Topic
import io.keepcoding.eh_ho.domain.User

sealed class TopicManagementState {
    object Loading : TopicManagementState()
    class LoadTopicList(val topicList: List<Topic>, val userByTopic: List<User>) : TopicManagementState()
    class DetailUserList(val detail: DetailUser) : TopicManagementState ()
    class RequestErrorReported(val requestError: RequestError) : TopicManagementState()
    object NavigateToLoginIn: TopicManagementState()
    object NavigateToCreateTopic: TopicManagementState()
    class NavigateToPostsOfTopic(val topic: Topic) : TopicManagementState()
}
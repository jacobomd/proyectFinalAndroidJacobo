package io.keepcoding.eh_ho.feature.topics.view.state


import io.keepcoding.eh_ho.data.service.RequestError
import io.keepcoding.eh_ho.domain.DetailUser
import io.keepcoding.eh_ho.domain.Topic

sealed class TopicManagementState {
    object Loading : TopicManagementState()
    class LoadTopicList(val topicList: List<Topic>) : TopicManagementState()
    class DetailUserList(val detail: DetailUser) : TopicManagementState ()
    class RequestErrorReported(val requestError: RequestError) : TopicManagementState()
    object NavigateToLoginIn: TopicManagementState()
    object NavigateToCreateTopic: TopicManagementState()
    class NavigateToPostsOfTopic(val topic: Topic) : TopicManagementState()
    object CreateTopicLoading : TopicManagementState()
    object CreateTopicCompleted : TopicManagementState()
    object ErrorConnection : TopicManagementState()
    object ErrorConnectionModeOffline : TopicManagementState()
    class TopicCreatedSuccessfully(val msg: String) : TopicManagementState()
    class TopicNotCreated(val createError: String) : TopicManagementState()
    class CreateTopicFormErrorReported(val errorMsg: String) : TopicManagementState()
}
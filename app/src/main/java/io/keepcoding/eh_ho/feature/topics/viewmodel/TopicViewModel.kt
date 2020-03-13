package io.keepcoding.eh_ho.feature.topics.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.keepcoding.eh_ho.data.repository.TopicsRepo
import io.keepcoding.eh_ho.domain.Topic
import io.keepcoding.eh_ho.feature.topics.view.state.TopicManagementState

class TopicViewModel : ViewModel() {

    private lateinit var _topicManagementState: MutableLiveData<TopicManagementState>
    val topicManagementState: LiveData<TopicManagementState>
        get() {
            if (!::_topicManagementState.isInitialized) {
                _topicManagementState = MutableLiveData()
            }
            return _topicManagementState
        }

    fun onViewCreatedWithNoSavedData(context: Context) {
        _topicManagementState.value = TopicManagementState.Loading
        TopicsRepo.getTopics(
            context,
            { topics, users->
                _topicManagementState.value =
                    TopicManagementState.LoadTopicList(topicList = topics, userByTopic = users)
            },
            { error ->
                _topicManagementState.value =
                    TopicManagementState.RequestErrorReported(requestError = error)
            })
    }

    fun onTopicsFragmentResumed(context: Context?) {
        _topicManagementState.value = TopicManagementState.Loading
        fetchTopicsAndHandleResponse(context = context)
    }

    fun onRetryButtonClicked(context: Context?) {
        _topicManagementState.value = TopicManagementState.Loading
        fetchTopicsAndHandleResponse(context = context)
    }

    fun onSwipeRefreshLayoutClicked(context: Context?) {
        _topicManagementState.value = TopicManagementState.Loading
        fetchTopicsAndHandleResponse(context = context)
    }

    // Navigate to topic detail view and display associated data
    fun onTopicSelected(topic: Topic) {
        println("topic seleccionadooo")
    }

    fun onAvatarSelected(username: String, context: Context) {
        fetchDetailUser(username = username, context = context)
    }


    fun onCreateTopicButtonClicked() {
        _topicManagementState.value = TopicManagementState.NavigateToCreateTopic
        println("create topic seleccionadooo")
    }

    fun onLogIn_OutOptionClicked () {
        _topicManagementState.value = TopicManagementState.NavigateToLoginIn
    }

    fun onSearchOptionClicked () {
        println("search button menu seleccionadooo")
    }

    private fun fetchDetailUser(username: String, context: Context) {
        context.let {
            TopicsRepo.getDetailUser(it, username,
                { detail ->
                    _topicManagementState.value = TopicManagementState.DetailUserList(detail = detail)
                },
                { error ->
                    _topicManagementState.value =
                        TopicManagementState.RequestErrorReported(requestError = error)
                }
                )
        }
    }

    private fun fetchTopicsAndHandleResponse(context: Context?) {
        context?.let {
            TopicsRepo.getTopics(it,
                { topics, users ->
                    _topicManagementState.value =
                        TopicManagementState.LoadTopicList(topicList = topics, userByTopic = users)
                },
                { error ->
                    _topicManagementState.value =
                        TopicManagementState.RequestErrorReported(requestError = error)
                })
        }
    }



}
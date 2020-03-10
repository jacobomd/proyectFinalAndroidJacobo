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
            { topics ->
                _topicManagementState.value =
                    TopicManagementState.LoadTopicList(topicList = topics)
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

    // Navigate to topic detail view and display associated data
    fun onTopicSelected(topic: Topic) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun onCreateTopicButtonClicked() {
        //_topicManagementState.value = TopicManagementState.NavigateToCreateTopic
    }

    fun onLogOutOptionClicked(context: Context) {

    }


    private fun fetchTopicsAndHandleResponse(context: Context?) {
        context?.let {
            TopicsRepo.getTopics(it,
                { topics ->
                    _topicManagementState.value =
                        TopicManagementState.LoadTopicList(topicList = topics)
                },
                { error ->
                    _topicManagementState.value =
                        TopicManagementState.RequestErrorReported(requestError = error)
                })
        }
    }

}
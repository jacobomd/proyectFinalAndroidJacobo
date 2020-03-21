package io.keepcoding.eh_ho.feature.topics.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.Room
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.repository.PostsRepo
import io.keepcoding.eh_ho.data.repository.TopicsRepo
import io.keepcoding.eh_ho.data.repository.UserRepo
import io.keepcoding.eh_ho.database.TopicDatabase
import io.keepcoding.eh_ho.domain.CreateTopicModel
import io.keepcoding.eh_ho.domain.Topic
import io.keepcoding.eh_ho.feature.topics.view.state.TopicManagementState
import io.keepcoding.eh_ho.feature.topics.view.ui.TopicsActivity

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

        PostsRepo.getAllPosts(
            context,
            {

            },
            {

            }
        )
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
        _topicManagementState.value = TopicManagementState.NavigateToPostsOfTopic(topic)
    }

    fun onAvatarSelected(username: String, context: Context) {
        fetchDetailUser(username = username, context = context)
    }


    fun onCreateTopicButtonClicked(context: Context) {
        if (UserRepo.checkInternet(context)) {
            _topicManagementState.value = TopicManagementState.NavigateToCreateTopic
        }
        else {
            _topicManagementState.value = TopicManagementState.ErrorConnection
        }
    }

    fun onLogIn_OutOptionClicked () {
        _topicManagementState.value = TopicManagementState.NavigateToLoginIn
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

    fun onCreateTopicOptionClicked(context: Context, createTopicModel: CreateTopicModel) {

        if (isValidCreateTopicForm(model = createTopicModel)) {
            _topicManagementState.value = TopicManagementState.CreateTopicLoading
            TopicsRepo.createTopic(
                context = context,
                model = createTopicModel,
                onSuccess = { topicModel ->
                    _topicManagementState.value = TopicManagementState.CreateTopicCompleted
                    if (topicModel == createTopicModel) {
                        _topicManagementState.value =
                            TopicManagementState.TopicCreatedSuccessfully(msg = context.getString(R.string.message_topic_created))
                    } else {
                        _topicManagementState.value =
                            TopicManagementState.TopicNotCreated(createError = context.getString(R.string.error_topic_not_created))
                    }
                },
                onError = { error ->
                    _topicManagementState.value = TopicManagementState.CreateTopicCompleted
                    _topicManagementState.value =
                        TopicManagementState.RequestErrorReported(requestError = error)
                }
            )
        } else {
            _topicManagementState.value = TopicManagementState.CreateTopicFormErrorReported(
                errorMsg = getCreateTopicFormError(context, createTopicModel)
            )
        }

    }

    private fun getCreateTopicFormError(context: Context, model: CreateTopicModel): String =
        with(model) {
            when {
                title.isEmpty() -> context.getString(R.string.error_title_empty)
                content.isEmpty() -> context.getString(R.string.error_content_empty)
                else -> context.getString(R.string.error_unknown)
            }
        }


    private fun isValidCreateTopicForm(model: CreateTopicModel): Boolean =
        with(model) { title.isNotEmpty() && content.isNotEmpty()
    }

    fun onSearchViewQueryText(context: Context, key: String?) {
        TopicsRepo.getTopics(
            context,
            { topicList, userList ->
                _topicManagementState.value =
                    TopicManagementState.LoadTopicList(topicList = topicList.filterByKey(key), userByTopic = userList)
            },
            { error ->
                _topicManagementState.value =
                    TopicManagementState.RequestErrorReported(requestError = error)
            })
    }

}

private fun List<Topic>.filterByKey(key: String?): List<Topic> =
    key?.let { k ->
        filter { it.title.contains(other = k, ignoreCase = true) }
    } ?: run {
        this
    }
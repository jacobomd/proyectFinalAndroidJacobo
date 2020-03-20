package io.keepcoding.eh_ho.feature.topics.view.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.repository.UserRepo
import io.keepcoding.eh_ho.data.service.RequestError
import io.keepcoding.eh_ho.domain.CreateTopicModel
import io.keepcoding.eh_ho.domain.DetailUser
import io.keepcoding.eh_ho.domain.Topic
import io.keepcoding.eh_ho.domain.User
import io.keepcoding.eh_ho.feature.login.LoginActivity
import io.keepcoding.eh_ho.feature.posts.view.EXTRA_TOPIC_ID
import io.keepcoding.eh_ho.feature.posts.view.EXTRA_TOPIC_TITLE
import io.keepcoding.eh_ho.feature.posts.view.PostsActivity
import io.keepcoding.eh_ho.feature.topics.view.state.TopicManagementState
import io.keepcoding.eh_ho.feature.topics.viewmodel.TopicViewModel
import kotlinx.android.synthetic.main.content_topic.*
import kotlinx.android.synthetic.main.content_topic.fragmentContainer


const val TRANSACTION_CREATE_TOPIC = "create_topic"

class TopicsActivity : AppCompatActivity(),
TopicsFragment.TopicsInteractionListener, CreateTopicFragment.CreateTopicInteractionListener {

    private val topicViewModel: TopicViewModel by lazy { TopicViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topics)
        setSupportActionBar(toolbar)
        setTitle(R.string.title_list_topics)
        initView()
        initModel()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(
                R.id.fragmentContainer,
                TopicsFragment(),
                TOPICS_FRAGMENT_TAG
            ).commit()

            topicViewModel.onViewCreatedWithNoSavedData(context = this)
        }

    }


    override fun onTopicSelected(topic: Topic) {
        topicViewModel.onTopicSelected(topic = topic)
    }


    override fun onAvatarSelected(username: String) {
        topicViewModel.onAvatarSelected(username = username, context = this)    }

    override fun onRetryButtonClicked() {
        topicViewModel.onRetryButtonClicked(context = this)
    }

    override fun onTopicsFragmentResumed() {
        topicViewModel.onTopicsFragmentResumed(context = this)
    }

    override fun onCreateTopicButtonClicked() {
        topicViewModel.onCreateTopicButtonClicked()
    }

    override fun onCreateTopicOptionClicked(model: CreateTopicModel) {
        topicViewModel.onCreateTopicOptionClicked(context = this, createTopicModel = model)    }

    override fun onSwipeRefreshLayoutClicked() {
        topicViewModel.onSwipeRefreshLayoutClicked(context = this)
    }

    override fun onLogIn_OutOptionClicked() {
        topicViewModel.onLogIn_OutOptionClicked()
    }

    override fun onLogOutClicked() {
        UserRepo.logOut(this)
        supportFragmentManager.beginTransaction().replace(
            R.id.fragmentContainer,
            TopicsFragment(),
            TOPICS_FRAGMENT_TAG
        ).commit()
    }

    override fun onQueryTextSubmit(query: String) {
        topicViewModel.onSearchViewQueryText(key = query, context = this )
    }

    override fun onQueryTextChange(newText: String) {
        topicViewModel.onSearchViewQueryText(key = newText, context = this)
    }

    private fun initView() {
        setSupportActionBar(toolbar)
    }

    private fun initModel() {
        topicViewModel.topicManagementState.observe(this, Observer { state ->
            when (state) {
                TopicManagementState.Loading -> enableLoadingView()
                is TopicManagementState.LoadTopicList -> loadTopicList(list = state.topicList, user = state.userByTopic)
                is TopicManagementState.DetailUserList -> loadDetailUser(detail = state.detail)
                is TopicManagementState.RequestErrorReported -> showRequestError(error = state.requestError)
                is TopicManagementState.NavigateToLoginIn -> navigateToLoginIn()
                is TopicManagementState.NavigateToCreateTopic -> navigateToCreateTopic()
                is TopicManagementState.NavigateToPostsOfTopic -> navigateToPostsOfTopic(topic = state.topic)
                is TopicManagementState.TopicCreatedSuccessfully -> showMessage(msg = state.msg)
                is TopicManagementState.TopicNotCreated -> showError(msg = state.createError)
                is TopicManagementState.CreateTopicFormErrorReported -> showError(msg = state.errorMsg)
                TopicManagementState.CreateTopicLoading -> toggleCreateTopicLoadingView(enable = true)
                TopicManagementState.CreateTopicCompleted -> {
                    toggleCreateTopicLoadingView(enable = false)
                    dismissCreateDialogFragment()
                }
            }
        })
    }


    private fun navigateToCreateTopic() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, CreateTopicFragment(), CREATE_TOPIC_FRAGMENT_TAG)
            .addToBackStack(TRANSACTION_CREATE_TOPIC)
            .commit()
    }

    private fun navigateToLoginIn() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }


    private fun enableLoadingView() {
        getTopicsFragmentIfAvailableOrNull()?.enableLoading(enabled = true)
    }

    private fun showRequestError(error: RequestError) {
        getTopicsFragmentIfAvailableOrNull()?.run {
            enableLoading(enabled = false)
            handleRequestError(requestError = error)
        }
    }

    private fun loadTopicList(list: List<Topic>, user: List<User>) {
        getTopicsFragmentIfAvailableOrNull()?.run {
            enableLoading(enabled = false)
            loadTopicList(topicList = list, userByTopic = user)
        }
    }

    private fun loadDetailUser(detail: DetailUser) {
        getTopicsFragmentIfAvailableOrNull()?.run {
            showDialogAlert(detail)
        }
    }

    private fun getTopicsFragmentIfAvailableOrNull(): TopicsFragment? {
        val fragment: Fragment? =
            supportFragmentManager.findFragmentByTag(TOPICS_FRAGMENT_TAG)

        return if (fragment != null && fragment.isVisible) {
            fragment as TopicsFragment
        } else {
            null
        }
    }

    private fun navigateToPostsOfTopic(topic: Topic) {
        val intent = Intent(this, PostsActivity::class.java)

        intent.putExtra(EXTRA_TOPIC_ID, topic.id)
        intent.putExtra(EXTRA_TOPIC_TITLE, topic.title)

        startActivity(intent)
        finish()
    }

    private fun showError(msg: String) {
        Snackbar.make(fragmentContainer, msg, Snackbar.LENGTH_LONG).show()
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private fun toggleCreateTopicLoadingView(enable: Boolean) {
        getCreateTopicFragmentIfAvailableOrNull()?.enableLoadingDialog(enable = enable)
    }

    private fun dismissCreateDialogFragment() {
        if (getCreateTopicFragmentIfAvailableOrNull() != null) {
            supportFragmentManager.popBackStack()
        }
    }

    private fun getCreateTopicFragmentIfAvailableOrNull(): CreateTopicFragment? {
        val fragment: Fragment? =
            supportFragmentManager.findFragmentByTag(CREATE_TOPIC_FRAGMENT_TAG)

        return if (fragment != null && fragment.isVisible) {
            fragment as CreateTopicFragment
        } else {
            null
        }
    }

}

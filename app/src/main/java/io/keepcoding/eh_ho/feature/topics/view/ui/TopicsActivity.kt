package io.keepcoding.eh_ho.feature.topics.view.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.repository.UserRepo
import io.keepcoding.eh_ho.data.service.RequestError
import io.keepcoding.eh_ho.domain.DetailUser
import io.keepcoding.eh_ho.domain.Topic
import io.keepcoding.eh_ho.domain.User
import io.keepcoding.eh_ho.feature.login.LoginActivity
import io.keepcoding.eh_ho.feature.topics.view.state.TopicManagementState
import io.keepcoding.eh_ho.feature.topics.viewmodel.TopicViewModel

import kotlinx.android.synthetic.main.activity_topics.*
import kotlinx.android.synthetic.main.content_topic.*

class TopicsActivity : AppCompatActivity(),
TopicsFragment.TopicsInteractionListener {

    private val topicViewModel: TopicViewModel by lazy { TopicViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topics)
        setSupportActionBar(toolbar)
        setTitle(R.string.title_list_topics)
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

    override fun onSearchOptionClicked() {
        topicViewModel.onSearchOptionClicked()    }

    private fun initModel() {
        topicViewModel.topicManagementState.observe(this, Observer { state ->
            when (state) {
                TopicManagementState.Loading -> enableLoadingView()
                is TopicManagementState.LoadTopicList -> loadTopicList(list = state.topicList, user = state.userByTopic)
                is TopicManagementState.DetailUserList -> loadDetailUser(detail = state.detail)
                is TopicManagementState.RequestErrorReported -> showRequestError(error = state.requestError)
                is TopicManagementState.NavigateToLoginIn -> navigateToLoginIn()
                is TopicManagementState.NavigateToCreateTopic -> navigateToCreateTopic()
                is TopicManagementState.NavigateToDetailUser -> navigateToDetailUser(username = state.username)
            }
        })
    }

    private fun navigateToDetailUser(username: String) {

    }

    private fun navigateToCreateTopic() {
            print("Navegar hacia la vista del create topic")
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

}

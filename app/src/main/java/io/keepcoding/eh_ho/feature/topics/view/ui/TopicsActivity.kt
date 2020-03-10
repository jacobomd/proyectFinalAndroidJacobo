package io.keepcoding.eh_ho.feature.topics.view.ui

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.service.RequestError
import io.keepcoding.eh_ho.domain.Topic
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

    override fun onLogOutOptionClicked() {
        topicViewModel.onLogOutOptionClicked(context = this)
    }

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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun initModel() {
        topicViewModel.topicManagementState.observe(this, Observer { state ->
            when (state) {
                TopicManagementState.Loading -> enableLoadingView()
                is TopicManagementState.LoadTopicList -> loadTopicList(list = state.topicList)
                is TopicManagementState.RequestErrorReported -> showRequestError(error = state.requestError)
            }
        })
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

    private fun loadTopicList(list: List<Topic>) {
        getTopicsFragmentIfAvailableOrNull()?.run {
            enableLoading(enabled = false)
            loadTopicList(topicList = list)
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

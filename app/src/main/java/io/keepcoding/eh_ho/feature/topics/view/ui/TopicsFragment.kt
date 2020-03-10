package io.keepcoding.eh_ho.feature.topics.view.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.service.RequestError
import io.keepcoding.eh_ho.domain.Topic
import io.keepcoding.eh_ho.feature.topics.view.adapter.TopicsAdapter
import kotlinx.android.synthetic.main.fragment_topics.*
import kotlinx.android.synthetic.main.view_retry.*
import java.lang.RuntimeException


const val TOPICS_FRAGMENT_TAG = "TOPICS_FRAGMENT"

class TopicsFragment : Fragment() {

    var listener: TopicsInteractionListener? = null
    lateinit var topicsAdapter: TopicsAdapter


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is TopicsInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement ${TopicsInteractionListener::class.java.simpleName}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        topicsAdapter = TopicsAdapter { topicItemClicked(it) }
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
       // inflater?.inflate(R.menu.menu_topics, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_topics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listTopics.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        listTopics.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        listTopics.adapter = topicsAdapter

        buttonCreate.setOnClickListener { createTopicButtonClicked() }

        buttonRetry.setOnClickListener { retryButtonClicked() }

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        swipeRefreshLayout.setOnRefreshListener { swipeRefreshLayoutClicked() }


        // OPCION MAS SIMPLE PARA OCULTAR EL FLOATINBUTTONACTION AL HACER SCROLL

        /*listTopics.addOnScrollListener( object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == 1)
                    buttonCreate.hide()
                else
                    buttonCreate.show()
            }
        })*/

    }


    override fun onResume() {
        super.onResume()
        listener?.onTopicsFragmentResumed()

    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
     //       R.id.action_log_out -> listener?.onLogOutOptionClicked()
        }
        return super.onOptionsItemSelected(item)
    }

    fun loadTopicList(topicList: List<Topic>) {
        enableLoading(false)
        topicsAdapter.setTopics(topics = topicList)
        swipeRefreshLayout.isRefreshing = false
    }


    private fun swipeRefreshLayoutClicked() {
        listener?.onSwipeRefreshLayoutClicked()
    }

    private fun createTopicButtonClicked() {
        listener?.onCreateTopicButtonClicked()
    }

    private fun retryButtonClicked() {
        listener?.onRetryButtonClicked()

    }


    fun enableLoading(enabled: Boolean) {
        viewRetry.visibility = View.INVISIBLE

        if (enabled) {
            listTopics.visibility = View.INVISIBLE
            buttonCreate.hide()
            viewLoading.visibility = View.VISIBLE
        } else {
            listTopics.visibility = View.VISIBLE
            buttonCreate.show()
            viewLoading.visibility = View.INVISIBLE
        }
    }


    fun handleRequestError(requestError: RequestError) {

        listTopics.visibility = View.INVISIBLE
        viewRetry.visibility = View.VISIBLE

        val message = if (requestError.messageId != null)
            getString(requestError.messageId)
        else if (requestError.message != null)
            requestError.message
        else
            getString(R.string.error_request_default)

        Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG).show()
    }


    private fun topicItemClicked(topic: Topic) {
        listener?.onTopicSelected(topic)

    }

    interface TopicsInteractionListener {
        fun onTopicSelected(topic: Topic)
        fun onLogOutOptionClicked()
        fun onRetryButtonClicked()
        fun onTopicsFragmentResumed()
        fun onCreateTopicButtonClicked()
        fun onSwipeRefreshLayoutClicked()
    }

}





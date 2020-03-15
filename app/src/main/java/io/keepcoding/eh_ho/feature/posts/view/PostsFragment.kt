package io.keepcoding.eh_ho.feature.posts.view


import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.repository.PostsRepo
import io.keepcoding.eh_ho.data.service.RequestError
import kotlinx.android.synthetic.main.fragment_posts.*
import kotlinx.android.synthetic.main.fragment_posts.parentLayout
import kotlinx.android.synthetic.main.fragment_posts.viewRetry
import kotlinx.android.synthetic.main.view_retry.*


class PostsFragment : Fragment() {
    var listener: PostsInteractionListener? = null
    lateinit var adapter : PostsAdapter

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is PostsInteractionListener) {
            listener = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        adapter = PostsAdapter()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_create_post, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val texto = arguments?.getString(EXTRA_TOPIC_ID)
        val topicId = texto?.toInt()

        listPosts.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        listPosts.adapter = adapter

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)

        topicId?.let {
            loadPost(it)
            buttonRetry.setOnClickListener {
                loadPost(topicId)
            }
            swipeRefreshLayout.setOnRefreshListener {
                loadPost(topicId)
            }
        }


    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.button_create_post -> listener?.onGoToCreatePost()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadPost(idTopic: Int) {

        enableLoading(true)

        context?.let {
            PostsRepo.getPosts(
                it,
                idTopic,
                {
                    enableLoading(false)
                    adapter.setPosts(it)
                    swipeRefreshLayout.isRefreshing = false
                },
                {
                    enableLoading(false)
                    handleRequestError(it)
                }
            )
        }
    }

    private fun enableLoading(enabled: Boolean) {
        viewRetry.visibility = View.INVISIBLE

        if (enabled) {
            listPosts.visibility = View.INVISIBLE
            viewLoading.visibility = View.VISIBLE
        } else {
            listPosts.visibility = View.VISIBLE
            viewLoading.visibility = View.INVISIBLE
        }
    }

    private fun handleRequestError(requestError: RequestError) {

        listPosts.visibility = View.INVISIBLE
        viewRetry.visibility = View.VISIBLE

        val message = if (requestError.messageId != null)
            getString(requestError.messageId)
        else if (requestError.message != null)
            requestError.message
        else
            getString(R.string.error_request_default)

        Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG).show()
    }

    interface PostsInteractionListener {
        fun onGoToCreatePost()
    }

}

package io.keepcoding.eh_ho.feature.posts.view.ui


import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.repository.PostsRepo
import io.keepcoding.eh_ho.data.repository.UserRepo
import io.keepcoding.eh_ho.data.service.RequestError
import io.keepcoding.eh_ho.feature.posts.view.adapter.PostsAdapter
import kotlinx.android.synthetic.main.fragment_posts.*
import kotlinx.android.synthetic.main.fragment_posts.parentLayout
import kotlinx.android.synthetic.main.fragment_posts.viewRetry
import kotlinx.android.synthetic.main.view_retry.*

const val POSTS_FRAGMENT_TAG = "POSTS_FRAGMENT"

class PostsFragment : Fragment() {
    private var listener: PostsInteractionListener? = null
    private lateinit var adapter : PostsAdapter

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
        (activity as AppCompatActivity).supportActionBar?.title = "Posts"

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

        if (item?.itemId == R.id.button_create_post && UserRepo.isLogged(requireContext())) {
            listener?.onGoToCreatePost()
        }
        else {
            if (item?.itemId == R.id.button_create_post) {
                showAlertPermission()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun showConnectionError() {
        Snackbar.make(parentLayout, R.string.error_network, Snackbar.LENGTH_LONG).show()
    }

    private fun showAlertPermission() {
        // Initialize a new instance of
        val builder = AlertDialog.Builder(requireContext())

        // Set the alert dialog title
        builder.setTitle("Session information")

        // Display a message on alert dialog
        builder.setMessage("Please log in to perform this action ...")

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("Ok") { dialog, _ ->
            dialog.dismiss()
        }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
    }

    private fun loadPost(idTopic: Int) {

        enableLoading(true)

        context?.let { context ->
            PostsRepo.getPosts(
                context,
                idTopic,
                {
                    enableLoading(false)
                    adapter.setPosts(it)
                    swipeRefreshLayout.isRefreshing = false
                },
                { error ->
                    enableLoading(false)
                    handleRequestError(error)
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

        val message = when {
            requestError.messageId != null -> getString(requestError.messageId)
            requestError.message != null -> requestError.message
            else -> getString(R.string.error_request_default)
        }

        Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG).show()
    }

    interface PostsInteractionListener {
        fun onGoToCreatePost()
    }

}

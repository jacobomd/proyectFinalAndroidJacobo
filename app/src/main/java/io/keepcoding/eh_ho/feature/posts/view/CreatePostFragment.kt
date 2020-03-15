package io.keepcoding.eh_ho.feature.posts.view


import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import io.keepcoding.eh_ho.common.LoadingDialogFragment

import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.domain.CreatePostModel
import io.keepcoding.eh_ho.data.repository.PostsRepo
import io.keepcoding.eh_ho.data.service.RequestError
import io.keepcoding.eh_ho.feature.topics.view.ui.TAG_LOADING_DIALOG
import kotlinx.android.synthetic.main.fragment_create_post.*
import kotlinx.android.synthetic.main.fragment_create_post.parentLayout



class CreatePostFragment : Fragment() {

    var topicId: String? = null
    var listener: CreatePostInteractionListener? = null
    lateinit var loadingDialogFragment: LoadingDialogFragment

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is CreatePostInteractionListener)
            listener = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val topicTitle = arguments?.getString(EXTRA_TOPIC_TITLE)
        (activity as AppCompatActivity).supportActionBar?.title = "Create post"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        topicId= arguments?.getString(EXTRA_TOPIC_ID)
        setHasOptionsMenu(true)
        loadingDialogFragment = LoadingDialogFragment.newInstance(getString(R.string.label_create_post))
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_send_post, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            R.id.menu_button_send_post -> createPost()
        }
        return super.onOptionsItemSelected(item)

    }

    private fun enableLoadingDialog(enable: Boolean) {
        if (enable)
            loadingDialogFragment.show(childFragmentManager,
                TAG_LOADING_DIALOG
            )
        else
            loadingDialogFragment.dismiss()
    }

    private fun createPost() {
        if (isFormValid()) {
            postPost()
        }
        else
            showErrors()
    }

    private fun postPost() {
        val model = CreatePostModel(
            editPost.text.toString(),
            topicId.toString().toInt()
        )
        context?.let {
            enableLoadingDialog(true)
            PostsRepo.createPost(
                it,
                model,
                {
                    enableLoadingDialog(false)
                    listener?.onPostCreated()
                },
                {
                    enableLoadingDialog(false)
                    handleError(it)
                }
            )
        }
    }

    private fun handleError(requestError: RequestError) {
        val message = if (requestError.messageId != null)
            getString(requestError.messageId)
        else if (requestError.message != null)
            requestError.message
        else
            getString(R.string.error_request_default)

        Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG).show()
    }

    private fun showErrors() {
        if (editPost.text?.isEmpty() == true)
            editPost.error = getString(R.string.error_empty)
    }

    private fun isFormValid() =
        editPost.text?.isNotEmpty() ?: false

    interface CreatePostInteractionListener {
        fun onPostCreated()
    }

}

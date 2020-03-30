package io.keepcoding.eh_ho.feature.topics.view.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.repository.UserRepo
import io.keepcoding.eh_ho.data.service.RequestError
import io.keepcoding.eh_ho.domain.DetailUser
import io.keepcoding.eh_ho.domain.Topic
import io.keepcoding.eh_ho.feature.topics.view.adapter.TopicsAdapter
import kotlinx.android.synthetic.main.detail_user_dialog.*
import kotlinx.android.synthetic.main.fragment_topics.*
import kotlinx.android.synthetic.main.view_retry.*
import java.lang.RuntimeException


const val TOPICS_FRAGMENT_TAG = "TOPICS_FRAGMENT"

class TopicsFragment : Fragment() {

    private  var listener: TopicsInteractionListener? = null
     private lateinit var topicsAdapter: TopicsAdapter


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
        topicsAdapter = TopicsAdapter(topicClickListener = {
            topicItemClicked(it)
        }, avatarClickListener = {
            avatarItemClicked(it)
        })


    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {

        inflater?.inflate(R.menu.menu_topics, menu)
        if (UserRepo.isLogged(requireContext())) {
            menu?.getItem(0)?.setIcon(R.drawable.ic_session_out)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
                R.id.action_login -> actionLoginClicked()
        }
        return super.onOptionsItemSelected(item)
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

        listTopics.addOnScrollListener( object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == 1 ) {
                    buttonCreate.hide()
                }
                else {
                    buttonCreate.show()
                }
            }
        })


        buttonCreate.setOnClickListener {
            if (UserRepo.isLogged(requireContext())) {
                createTopicButtonClicked()
            } else {
                showAlertNotPossibleCreateTopic()
            }
        }

        buttonRetry.setOnClickListener { retryButtonClicked() }

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary)
        swipeRefreshLayout.setOnRefreshListener { swipeRefreshLayoutClicked() }

        search_view.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                listener?.onQueryTextSubmit(query = query.toString())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                listener?.onQueryTextChange(newText = newText.toString())
                return true
            }
        })
        search_view.setOnCloseListener {
            false
        }

    }


    override fun onResume() {
        super.onResume()
        listener?.onTopicsFragmentResumed()

    }

    fun showDialogAlert(detailUser: DetailUser) {
        this.alertDialogDetailUser(username = detailUser.username, name = detailUser.name, moderator = detailUser.moderator,
            lastSeen = detailUser.last_seen_at, avatar= detailUser.avatar_template)
    }


    fun loadTopicList(topicList: List<Topic>) {
        enableLoading(false)
        topicsAdapter.setTopics(topics = topicList)
        swipeRefreshLayout.isRefreshing = false
    }

    private fun actionLoginClicked () {
        if (UserRepo.isLogged(requireContext())) {
            showAlertLogOut()
        } else {
            listener?.onLogInOutOptionClicked()
        }
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

    private fun showAlertNotPossibleCreateTopic() {
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
        dialog.show()    }

    private fun showAlertLogOut () {
        // Initialize a new instance of
        val builder = AlertDialog.Builder(requireContext())

        // Set the alert dialog title
        builder.setTitle("Session information")

        // Display a message on alert dialog
        builder.setMessage("Do you want to leave the session?")

        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("Ok") { dialog, _ ->
            listener?.onLogOutClicked()
            dialog.dismiss()
        }

        // Finally, make the alert dialog using builder
        val dialog: AlertDialog = builder.create()

        // Display the alert dialog on app interface
        dialog.show()
    }

    @SuppressLint("InflateParams")
    private fun alertDialogDetailUser (username: String, name: String, moderator: String, lastSeen: String, avatar: String)
    {

        //Inflate the dialog with custom view
        val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.detail_user_dialog, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(requireContext())
            .setView(mDialogView)
        //show dialog
        val mAlertDialog = mBuilder.show()

        val avatarFinal = avatar.replace("{size}", "150")
        val image = "https://mdiscourse.keepcoding.io/${avatarFinal}"
        loadImage(image, mAlertDialog.imgAvatar)

        mAlertDialog.txrUserName.text = username
        mAlertDialog.txtName.text = name
        mAlertDialog.txtModerator.text = moderator
        mAlertDialog.txtLastseen.text = lastSeen
        mAlertDialog.txtPrivateMessag.text = "?"

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

    fun handleConnectionError() {
        Snackbar.make(parentLayout, R.string.error_network, Snackbar.LENGTH_LONG).show()
    }

    fun handleErrorConnectionModeOffline() {
        Snackbar.make(parentLayout, R.string.error_network_mode_offline, 5000).show()
    }


    fun handleRequestError(requestError: RequestError) {

        listTopics.visibility = View.INVISIBLE
        viewRetry.visibility = View.VISIBLE

        val message = when {
            requestError.messageId != null -> getString(requestError.messageId)
            requestError.message != null -> requestError.message
            else -> getString(R.string.error_request_default)
        }

        Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG).show()
    }

    private fun loadImage (data : String, image: ImageView) {

        val transformation = RoundedTransformationBuilder()
            .borderColor(Color.BLACK)
            .borderWidthDp(0F)
            .cornerRadiusDp(60F)
            .oval(false)
            .build()

        Picasso.with(requireContext())
            .load(data)
            .fit()
            .transform(transformation)
            .into(image)
    }


    private fun topicItemClicked(topic: Topic) {
        listener?.onTopicSelected(topic)

    }

    private fun avatarItemClicked(username: String) {
        listener?.onAvatarSelected(username)
    }

    interface TopicsInteractionListener {
        fun onTopicSelected(topic: Topic)
        fun onRetryButtonClicked()
        fun onTopicsFragmentResumed()
        fun onCreateTopicButtonClicked()
        fun onSwipeRefreshLayoutClicked()
        fun onLogInOutOptionClicked()
        fun onLogOutClicked()
        fun onAvatarSelected(username: String)
        fun onQueryTextSubmit(query: String)
        fun onQueryTextChange(newText: String)
    }

}





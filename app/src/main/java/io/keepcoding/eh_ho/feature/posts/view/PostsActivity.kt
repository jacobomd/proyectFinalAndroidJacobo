package io.keepcoding.eh_ho.feature.posts.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.feature.topics.view.ui.TopicsActivity
import java.lang.IllegalArgumentException

const val EXTRA_TOPIC_ID = "topic_id"
const val EXTRA_TOPIC_TITLE = "topic_title"
const val TRANSACTION_CREATE_POST = "create_post"

class PostsActivity : AppCompatActivity(),
    PostsFragment.PostsInteractionListener,
    CreatePostFragment.CreatePostInteractionListener {


    var topicTitle : String? = null
    var topicId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)

        topicId = intent.getStringExtra(EXTRA_TOPIC_ID)
        topicTitle = intent.getStringExtra(EXTRA_TOPIC_TITLE)

        val args = Bundle()
        args.putString(EXTRA_TOPIC_ID, topicId)

        val postsFragment = PostsFragment()
        postsFragment.arguments = args

        if (topicId !=null ) {
           if (savedInstanceState == null){
               supportFragmentManager.beginTransaction()
                   .add(R.id.fragmentContainer, postsFragment)
                   .commit()
           }

        } else {
            throw IllegalArgumentException("You should provide an id for the post")
        }

    }

    override fun onGoToCreatePost() {

        val args = Bundle()
        args.putString(EXTRA_TOPIC_TITLE, topicTitle)
        args.putString(EXTRA_TOPIC_ID, topicId)


        val createPostFragment = CreatePostFragment()
        createPostFragment.arguments = args

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, createPostFragment)
            .addToBackStack(TRANSACTION_CREATE_POST)
            .commit()
    }

    override fun onPostCreated() {
        supportFragmentManager.popBackStack()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, TopicsActivity::class.java)
        startActivity(intent)
        finish()
    }
}

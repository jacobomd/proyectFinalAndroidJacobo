package io.keepcoding.eh_ho.feature.topics.view.ui

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import io.keepcoding.eh_ho.R

import kotlinx.android.synthetic.main.activity_topics.*

class TopicsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topics)
        setSupportActionBar(toolbar)

    }

}

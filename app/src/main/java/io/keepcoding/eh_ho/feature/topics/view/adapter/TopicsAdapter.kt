package io.keepcoding.eh_ho.feature.topics.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.domain.Topic
import io.keepcoding.eh_ho.feature.topics.view.viewholder.TopicHolder


class TopicsAdapter (
    val topicClickListener: ((Topic) -> Unit)? = null
): RecyclerView.Adapter<TopicHolder>() {

    private val topics = mutableListOf<Topic>()
    private val listener : ((View) -> Unit) = {
        val topic = it.tag as Topic
        //Log.d(TopicsAdapter::class.java.simpleName, latestNew.toString())
        topicClickListener?.invoke(topic)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_topic, parent, false)

        return TopicHolder(view)
    }

    override fun getItemCount(): Int {
        return topics.size
    }

    override fun onBindViewHolder(holder: TopicHolder, position: Int) {
        val topic = topics[position]
        holder.topic= topic
        holder.itemView.setOnClickListener (listener)
    }

    fun setTopics (topics: List<Topic>) {
        this.topics.clear()
        this.topics.addAll(topics)
        notifyDataSetChanged()
    }


}
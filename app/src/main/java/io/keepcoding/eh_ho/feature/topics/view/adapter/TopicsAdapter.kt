package io.keepcoding.eh_ho.feature.topics.view.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.repository.UserRepo
import io.keepcoding.eh_ho.domain.Topic
import io.keepcoding.eh_ho.domain.User
import kotlinx.android.synthetic.main.item_topic.view.*
import java.util.*


class TopicsAdapter (
    val topicClickListener: ((Topic) -> Unit),
    val avatarClickListenter: ((String) -> Unit)
): RecyclerView.Adapter<TopicsAdapter.TopicHolder>() {

    private val topics = mutableListOf<Topic>()
    private val users = mutableListOf<User>()

    private val listener : ((View) -> Unit) = {
        val topic = it.tag as Topic
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
        holder.topic = topic
        holder.itemView.setOnClickListener(listener)

    }

    fun setTopics (topics: List<Topic>) {
        this.topics.clear()
        this.topics.addAll(topics)
        notifyDataSetChanged()
    }

    inner class TopicHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var topic : Topic? = null
            set(value) {
                field = value

                with(itemView) {
                    tag = field
                    field?.let {
                        labelTitle.text = field?.title
                        labelPosts.text = field?.posts.toString()
                        labelViews.text = field?.views.toString()
                        setTimeOffset(it.getTimeOffset())
                        loadImage(field!!.avatar_template, imagButtAvatar)

                        if (UserRepo.checkInternet(itemView.context)) {
                            imagButtAvatar.setOnClickListener {
                                avatarClickListenter(field!!.username)
                            }
                        }

                    }

                }

            }

        private fun loadImage (data : String, image: ImageView) {

            if (UserRepo.checkInternet(itemView.context)) {
                val transformation = RoundedTransformationBuilder()
                    .borderColor(Color.BLACK)
                    .borderWidthDp(0F)
                    .cornerRadiusDp(40F)
                    .oval(false)
                    .build()

                Picasso.with(itemView.context)
                    .load(data)
                    .fit()
                    .transform(transformation)
                    .into(image)
            } else {
                image.setImageResource(R.drawable.imagen__2x)
            }
        }


        private fun setTimeOffset(timeOffset: Topic.TimeOffset) {
            val quantityString = when (timeOffset.unit) {
                Calendar.YEAR -> R.plurals.years
                Calendar.MONTH -> R.plurals.months
                Calendar.DAY_OF_MONTH -> R.plurals.days
                Calendar.HOUR -> R.plurals.hours
                else -> R.plurals.minutes
            }

            itemView.label_date.text =
                if (timeOffset.amount != 0)
                    itemView.context.resources.getQuantityString(quantityString, timeOffset.amount, timeOffset.amount)
                else
                    itemView.context.resources.getString(R.string.minutes_zero)

        }


    }

}
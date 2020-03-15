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
        holder.getUsers(this.users)
        holder.itemView.setOnClickListener(listener)

    }

    fun setTopics (topics: List<Topic>, users: List<User>) {
        this.topics.clear()
        this.topics.addAll(topics)
        this.users.clear()
        this.users.addAll(users)
        notifyDataSetChanged()
    }

    inner class TopicHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var users = mutableListOf<User>()

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

                    }

                    for (poster in topic?.posters!!.listIterator()) {
                        if (poster.description.startsWith("Original Poster")) {
                            val userId = poster.user_id

                            for (user in users.listIterator()) {
                                if (userId.toString() == user.id) {
                                    val avatar = user.avatar_template
                                    val avatarFinal = avatar.replace("{size}", "150")
                                    val image = "https://mdiscourse.keepcoding.io/${avatarFinal}"
                                    loadImage(image, imagButtAvatar)
                                    imagButtAvatar.setOnClickListener {
                                        println("avarar pulsadoooooooo de username: ${user.username}")
                                        avatarClickListenter(user.username)
                                    }
                                }
                            }

                        }
                    }
                }


            }


        private fun loadImage (data : String, image: ImageView) {

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

        fun getUsers(users: MutableList<User>) {
            this.users = users
        }

    }

}
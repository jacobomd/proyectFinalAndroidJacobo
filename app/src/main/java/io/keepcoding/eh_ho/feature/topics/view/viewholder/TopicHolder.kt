package io.keepcoding.eh_ho.feature.topics.view.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.domain.Topic
import kotlinx.android.synthetic.main.item_topic.view.*
import java.util.*

 class TopicHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

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
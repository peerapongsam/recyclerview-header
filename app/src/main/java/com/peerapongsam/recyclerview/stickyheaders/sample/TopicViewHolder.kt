package com.peerapongsam.recyclerview.stickyheaders.sample

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_topic.view.*

class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bindValue(item: Any?) {
        itemView.tv_title.text = item.toString()
    }
}

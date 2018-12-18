package com.peerapongsam.recyclerview.stickyheaders.sample

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.peerapongsam.recyclerview.stickyheader.StickyHeaderRecyclerViewAdapter

class TopicsAdapter : ListAdapter<Any, TopicViewHolder>(object : DiffUtil.ItemCallback<Any?>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        return true
    }

    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        return true
    }
}), StickyHeaderRecyclerViewAdapter<HeaderViewHolder> {


    @Suppress("PropertyName")
    private val TAG = "TopicsAdapter"

    private var onClickListener: ((Any) -> Unit)? = null

    fun setOnClickListener(listener: ((Any) -> Unit)?) {
        this.onClickListener = listener
    }

    override fun getHeaderId(position: Int): Long {
        Log.d(TAG, "getHeaderId() called with: position = [$position]")
        return position.toLong()
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): HeaderViewHolder {
        return HeaderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_header, parent, false))
    }

    override fun onBindHeaderViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bindValue(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        return TopicViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_topic, parent, false))
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        holder.bindValue(getItem(position))
        holder.itemView.setOnClickListener {
            onClickListener?.invoke(getItem(position))
        }
    }
}


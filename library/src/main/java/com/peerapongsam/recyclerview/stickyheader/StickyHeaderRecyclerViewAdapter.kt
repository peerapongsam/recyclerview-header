package com.peerapongsam.recyclerview.stickyheader

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

interface StickyHeaderRecyclerViewAdapter<VH : RecyclerView.ViewHolder> {

    fun getHeaderId(position: Int): Long

    fun onCreateHeaderViewHolder(parent: ViewGroup): VH

    fun onBindHeaderViewHolder(holder: VH, position: Int)

    fun getItemCount(): Int
}

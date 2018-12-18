package com.peerapongsam.recyclerview.stickyheader.orientation

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LinearLayoutOrientationProvider : OrientationProvider {

    override fun getOrientation(recyclerView: RecyclerView): Int {
        return (recyclerView.layoutManager as? LinearLayoutManager)?.orientation
            ?: throw IllegalStateException("StickyHeaderRecyclerViewAdapter can only be used with LinearLayoutManager")
    }

    override fun isReverseLayout(recyclerView: RecyclerView): Boolean {
        return (recyclerView.layoutManager as? LinearLayoutManager)?.reverseLayout
            ?: throw IllegalStateException("StickyHeaderRecyclerViewAdapter can only be used with LinearLayoutManager")
    }
}

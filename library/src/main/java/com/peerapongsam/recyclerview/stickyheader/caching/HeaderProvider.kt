package com.peerapongsam.recyclerview.stickyheader.caching

import android.view.View
import androidx.recyclerview.widget.RecyclerView

interface HeaderProvider {

    fun getHeader(recyclerView: RecyclerView, position: Int): View

    fun invalidate()
}

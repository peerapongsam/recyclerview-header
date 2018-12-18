package com.peerapongsam.recyclerview.stickyheader

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.SoundEffectConstants
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class StickyHeaderRecyclerViewTouchListener(
    private val recyclerView: RecyclerView,
    private val decoration: StickyHeaderRecyclerViewItemDecoration
) : RecyclerView.OnItemTouchListener {

    private val tabDetector: GestureDetector

    init {
        tabDetector = GestureDetector(recyclerView.context, SingleTapDetector())
    }

    private var onHeaderClickListener: ((header: View, position: Int, headerId: Long) -> Unit)? = null

    fun setOnHeaderClickListener(onHeaderClickListener: ((header: View, position: Int, headerId: Long) -> Unit)?) {
        this.onHeaderClickListener = onHeaderClickListener
    }

    fun getAdapter(): StickyHeaderRecyclerViewAdapter<*>? {
        return (recyclerView.adapter as? StickyHeaderRecyclerViewAdapter<*>) ?: throw IllegalStateException("")
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        /*do nothing*/
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        if (onHeaderClickListener != null) {
            val tabDetectorResponse = tabDetector.onTouchEvent(e)
            if (tabDetectorResponse) {
                return true
            }
            if (e.action == MotionEvent.ACTION_DOWN) {
                val position = decoration.findHeaderPositionUnder(e.x.toInt(), e.y.toInt())
                return position != -1
            }
        }
        return false
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        /*do nothing*/
    }


    inner class SingleTapDetector : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            val position = decoration.findHeaderPositionUnder(e.x.toInt(), e.y.toInt())
            if (position != -1) {
                val headerView = decoration.getHeaderView(recyclerView, position)
                val headerId = getAdapter()?.getHeaderId(position) ?: -1L
                onHeaderClickListener?.invoke(headerView, position, headerId)
                recyclerView.playSoundEffect(SoundEffectConstants.CLICK)
                headerView.onTouchEvent(e)
                return true
            }
            return false
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            return true
        }
    }
}

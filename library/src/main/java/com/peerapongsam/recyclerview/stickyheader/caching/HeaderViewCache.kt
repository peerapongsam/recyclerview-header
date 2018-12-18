package com.peerapongsam.recyclerview.stickyheader.caching

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.collection.LongSparseArray
import androidx.recyclerview.widget.RecyclerView
import com.peerapongsam.recyclerview.stickyheader.StickyHeaderRecyclerViewAdapter
import com.peerapongsam.recyclerview.stickyheader.orientation.OrientationProvider

class HeaderViewCache<VH : RecyclerView.ViewHolder>(
    private val adapter: StickyHeaderRecyclerViewAdapter<VH>,
    private val orientationProvider: OrientationProvider
) :
    HeaderProvider {

    private val headerViews: LongSparseArray<View> = LongSparseArray()

    override fun getHeader(recyclerView: RecyclerView, position: Int): View {
        val headerId = adapter.getHeaderId(position)
        var header: View? = headerViews.get(headerId)
        if (header == null) {
            val viewHolder = adapter.onCreateHeaderViewHolder(recyclerView)
            adapter.onBindHeaderViewHolder(viewHolder, position)
            header = viewHolder.itemView
            if (header.layoutParams == null) {
                header.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            val widthSpec: Int
            val heightSpec: Int

            if (orientationProvider.getOrientation(recyclerView) == LinearLayout.VERTICAL) {
                widthSpec = View.MeasureSpec.makeMeasureSpec(recyclerView.width, View.MeasureSpec.EXACTLY)
                heightSpec = View.MeasureSpec.makeMeasureSpec(recyclerView.height, View.MeasureSpec.UNSPECIFIED)
            } else {
                widthSpec = View.MeasureSpec.makeMeasureSpec(recyclerView.width, View.MeasureSpec.UNSPECIFIED)
                heightSpec = View.MeasureSpec.makeMeasureSpec(recyclerView.height, View.MeasureSpec.EXACTLY)
            }

            val childWidth = ViewGroup.getChildMeasureSpec(
                widthSpec,
                recyclerView.paddingLeft + recyclerView.paddingRight,
                header.layoutParams.width
            )
            val childHeight = ViewGroup.getChildMeasureSpec(
                heightSpec,
                recyclerView.paddingTop + recyclerView.paddingBottom,
                header.layoutParams.height
            )
            header.measure(childWidth, childHeight)
            header.layout(0, 0, header.measuredWidth, header.measuredHeight)
            headerViews.put(headerId, header)
        }
        return header
    }

    override fun invalidate() {
        headerViews.clear()
    }
}

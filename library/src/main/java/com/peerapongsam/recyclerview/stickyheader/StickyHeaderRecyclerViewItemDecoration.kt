package com.peerapongsam.recyclerview.stickyheader

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import android.widget.LinearLayout
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView
import com.peerapongsam.recyclerview.stickyheader.caching.HeaderProvider
import com.peerapongsam.recyclerview.stickyheader.caching.HeaderViewCache
import com.peerapongsam.recyclerview.stickyheader.calculation.DimensionCalculator
import com.peerapongsam.recyclerview.stickyheader.orientation.LinearLayoutOrientationProvider
import com.peerapongsam.recyclerview.stickyheader.orientation.OrientationProvider
import com.peerapongsam.recyclerview.stickyheader.renderer.HeaderRenderer

class StickyHeaderRecyclerViewItemDecoration(
    private val adapter: StickyHeaderRecyclerViewAdapter<*>,
    private val orientationProvider: OrientationProvider = LinearLayoutOrientationProvider(),
    private val dimensionCalculator: DimensionCalculator = DimensionCalculator(),
    private val headerRenderer: HeaderRenderer = HeaderRenderer(orientationProvider),
    private val headerProvider: HeaderProvider = HeaderViewCache(adapter, orientationProvider),
    private val headerPositionCalculator: HeaderPositionCalculator = HeaderPositionCalculator(
        adapter,
        headerProvider,
        orientationProvider,
        dimensionCalculator
    ),
    private val visibilityAdapter: ItemVisibilityAdapter? = null

) : RecyclerView.ItemDecoration() {

    private val headerRects: SparseArrayCompat<Rect> = SparseArrayCompat()

    private val tempRect = Rect()

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val itemPosition = parent.getChildAdapterPosition(view)
        if (itemPosition == RecyclerView.NO_POSITION) {
            return
        }

        if (headerPositionCalculator.hasNewHeader(itemPosition, orientationProvider.isReverseLayout(parent))) {
            val header = getHeaderView(parent, itemPosition)
            setItemOffsetForHeader(outRect, header, orientationProvider.getOrientation(parent))
        }
    }

    private fun setItemOffsetForHeader(itemOffsets: Rect, header: View, orientation: Int) {
        dimensionCalculator.initMargins(tempRect, header)
        if (orientation == LinearLayout.VERTICAL) {
            itemOffsets.top = header.height + tempRect.top + tempRect.bottom
        } else {
            itemOffsets.left = header.width + tempRect.left + tempRect.right
        }
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(canvas, parent, state)
        val childCount = parent.childCount
        if (childCount <= 0 || adapter.getItemCount() <= 0) {
            return
        }

        for (i in 0 until childCount) {
            val itemView = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(itemView)
            if (position == RecyclerView.NO_POSITION) {
                continue
            }

            val hasStickyHeader =
                headerPositionCalculator.hasStickyHeader(itemView, orientationProvider.getOrientation(parent), position)
            if (hasStickyHeader ||
                headerPositionCalculator.hasNewHeader(position, orientationProvider.isReverseLayout(parent))
            ) {
                val header = headerProvider.getHeader(parent, position)
                var headerOffset = headerRects.get(position)
                if (headerOffset == null) {
                    headerOffset = Rect()
                    headerRects.put(position, headerOffset)
                }
                headerPositionCalculator.initHeaderBounds(headerOffset, parent, header, itemView, hasStickyHeader)
                headerRenderer.drawHeader(parent, canvas, header, headerOffset)
            }
        }
    }

    fun findHeaderPositionUnder(x: Int, y: Int): Int {
        for (i in 0..headerRects.size()) {
            val rect = headerRects.get(headerRects.keyAt(i))
            if (rect?.contains(x, y) == true) {
                val position = headerRects.keyAt(i)
                if (visibilityAdapter == null || visibilityAdapter.isPositionVisible(position)) {
                    return position
                }
            }
        }
        return -1
    }

    fun getHeaderView(recyclerView: RecyclerView, position: Int): View {
        return headerProvider.getHeader(recyclerView, position)
    }

    fun invalidateHeaders() {
        headerProvider.invalidate()
        headerRects.clear()
    }
}

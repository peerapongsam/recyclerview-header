package com.peerapongsam.recyclerview.stickyheader

interface ItemVisibilityAdapter {
    /**
     * Return true the specified adapter position is visible, false otherwise
     *
     * The implementation of this method will typically return true if
     * the position is between the layout manager's findFirstVisibleItemPosition
     * and findLastVisibleItemPosition (inclusive).
     *
     * @param position the adapter position
     */
    fun isPositionVisible(position: Int): Boolean
}

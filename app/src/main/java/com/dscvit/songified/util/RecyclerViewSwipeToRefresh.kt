package com.dscvit.songified.util

import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class RecyclerViewSwipeToRefresh(private val refreshLayout: SwipeRefreshLayout) :
    RecyclerView.OnScrollListener() {
    companion object {
        private const val DIRECTION_UP = -1
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        refreshLayout.isEnabled = !(recyclerView.canScrollVertically(DIRECTION_UP))
    }
}
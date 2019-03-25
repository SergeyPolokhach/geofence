package com.polohach.geofence.example.ui.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.cleveroad.bootstrap.kotlin_core.ui.BaseLifecycleViewModel
import com.cleveroad.bootstrap.kotlin_core.ui.adapter.BaseRecyclerViewAdapter
import com.cleveroad.bootstrap.kotlin_core.ui.adapter.EndlessScrollListener
import com.cleveroad.bootstrap.kotlin_core.ui.adapter.PaginationListView
import com.cleveroad.bootstrap.kotlin_ext.hide
import com.cleveroad.bootstrap.kotlin_ext.show
import com.polohach.geofence.example.ui.PAGE_LIMIT


abstract class BaseListFragment<ViewModel : BaseLifecycleViewModel, M : Any> :
        BaseFragment<ViewModel>(),
        SwipeRefreshLayout.OnRefreshListener,
        EndlessScrollListener.OnLoadMoreListener,
        PaginationListView {

    companion object {
        const val NO_ID = -1
        private const val VISIBLE_THRESHOLD = 10
    }

    protected abstract val recyclerViewId: Int

    protected abstract val noResultViewId: Int

    protected abstract val refreshLayoutId: Int

    protected open var pageLimit = PAGE_LIMIT

    protected open var visibleThreshold = VISIBLE_THRESHOLD

    private var endlessScrollListener: EndlessScrollListener? = null

    private var llNoResults: View? = null
    private var refreshLayout: SwipeRefreshLayout? = null
    private lateinit var rvList: RecyclerView

    protected abstract fun getAdapter(): BaseRecyclerViewAdapter<M, *>?

    protected abstract fun loadInitial()

    protected abstract fun loadMoreData()

    @SuppressLint("WrongConstant")
    protected open fun getLayoutManager() = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

    protected open fun getScrollDirection() = EndlessScrollListener.ScrollDirection.SCROLL_DIRECTION_DOWN

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        llNoResults = if (noResultViewId != NO_ID) view.findViewById(noResultViewId) else null
        refreshLayout = if (refreshLayoutId != NO_ID) view.findViewById(refreshLayoutId) else null
        refreshLayout?.apply {
            setOnRefreshListener(this@BaseListFragment)
        }
        initList(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideLoadingProgress()
    }

    override fun onRefresh() {
        loadInitial()
    }

    override fun loadMore() {
        loadMoreData()
    }

    protected open fun showLoadingProgress() {
        refreshLayout?.isRefreshing = true
    }

    protected open fun hideLoadingProgress() {
        refreshLayout?.isRefreshing = false
    }

    override fun onPaginationError() {
        hideLoadingProgress()
        endlessScrollListener?.updateNeedToLoad(true)
    }

    protected open fun onInitialDataLoaded(newData: List<M>) {
        hideLoadingProgress()
        endlessScrollListener?.reset()
        checkEndlessScroll(newData)
        getAdapter()?.apply {
            clear()
            addAll(newData)
            notifyDataSetChanged()
        }
        checkNoResults()
    }

    protected open fun invalidateNoResults() {
        checkNoResults()
    }

    protected open fun onDataRangeLoaded(newData: List<M>) {
        checkEndlessScroll(newData)
        getAdapter()?.apply {
            addAll(newData)
            if (newData.isNotEmpty()) notifyItemRangeInserted(itemCount, newData.size)
        }
        endlessScrollListener?.updateNeedToLoad(true)
    }

    protected fun enablePagination() {
        endlessScrollListener?.enable()
    }

    protected fun disablePagination() {
        endlessScrollListener?.disable()
    }

    private fun initList(view: View) {
        rvList = view.findViewById(recyclerViewId)
        with(rvList) {
            adapter = this@BaseListFragment.getAdapter()
            setHasFixedSize(false)
            layoutManager = this@BaseListFragment.getLayoutManager()
            endlessScrollListener = EndlessScrollListener.create(this,
                    visibleThreshold,
                    getScrollDirection())
        }
    }

    private fun checkEndlessScroll(newData: List<M>) {
        endlessScrollListener?.onLoadMoreListener(if (newData.size < pageLimit) null else this)
    }

    private fun checkNoResults() {
        llNoResults?.apply { if (getAdapter()?.isEmpty() == true) show() else hide() }
    }
}
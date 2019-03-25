package com.polohach.geofence.example.ui.screens.main.chat

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.polohach.geofence.example.R
import com.polohach.geofence.example.models.ActionType
import com.polohach.geofence.example.models.Message
import com.polohach.geofence.example.ui.base.BaseListFragment
import com.polohach.geofence.example.ui.screens.main.chat.adapter.MessageAdapter


class ChatFragment : BaseListFragment<ChatViewModel, Message>() {

    override val viewModelClass = ChatViewModel::class.java
    override val layoutId = R.layout.fragment_chat
    override val recyclerViewId = R.id.rvRequest
    override val noResultViewId = R.id.tvEmptyState
    override val refreshLayoutId = NO_ID
    override fun getScreenTitle() = NO_TITLE
    override fun getToolbarId() = NO_TOOLBAR
    override fun hasToolbar() = false
    override fun hasVersions() = false

    companion object {
        fun newInstance() = ChatFragment().apply { arguments = Bundle() }
    }

    private var adapter: MessageAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter?.isEmpty().takeIf { it == true }
                ?.apply { loadInitial() }
    }

    override fun onPause() {
        viewModel.getMessageLD(ActionType.UNSUBSCRIBE)
        super.onPause()
    }

    override fun observeLiveData(viewModel: ChatViewModel) {
        viewModel.getMessageLD(ActionType.SUBSCRIBE)
                .observe(this, Observer { onInitialDataLoaded(it) })
    }

    override fun getAdapter() = adapter
            ?: context?.let { MessageAdapter(it) }.apply { adapter = this }

    override fun loadInitial() {
        // do nothing
    }

    override fun loadMoreData() {
        // do nothing
    }
}

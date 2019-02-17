package com.avoscloud.chat.friends;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.avoscloud.chat.R;
import com.avoscloud.chat.activity.ChatRoomActivity;
import com.avoscloud.chat.event.GroupItemClickEvent;
import com.avoscloud.chat.fragment.BaseFragment;
import com.avoscloud.chat.util.ConversationUtils;
import com.avoscloud.chat.viewholder.GroupItemHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.leancloud.chatkit.adapter.LCIMCommonListAdapter;
import cn.leancloud.chatkit.utils.LCIMConstants;
import de.greenrobot.event.EventBus;

/**
 * Created by lenovo on 2018/5/3.
 */

public class GroupFragment extends BaseFragment {
    @Bind(R.id.activity_group_list_srl_view)
    protected RecyclerView recyclerView;

    @Bind(R.id.activity_group_list_srl_pullrefresh)
    protected SwipeRefreshLayout refreshLayout;

    LinearLayoutManager layoutManager;
    private LCIMCommonListAdapter<AVIMConversation> itemAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.group_list_activity, container, false);
        ButterKnife.bind(this, view);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        itemAdapter = new LCIMCommonListAdapter<>(GroupItemHolder.class);
        recyclerView.setAdapter(itemAdapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshGroupList();
            }
        });
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshGroupList();
        EventBus.getDefault().register(this);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    private void refreshGroupList() {
        ConversationUtils.findGroupConversationsIncludeMe(new AVIMConversationQueryCallback() {
            @Override
            public void done(List<AVIMConversation> conversations, AVIMException e) {
                if (filterException(e)) {
                    refreshLayout.setRefreshing(false);
                    itemAdapter.setDataList(conversations);
                    itemAdapter.notifyDataSetChanged();
                }
            }
        });
    }
    @Override
    public void onResume() {//返回当前activity时调用
        super.onResume();
        refreshGroupList();
    }
    public void onEvent(GroupItemClickEvent event) {//接受来自GroupitemHolder的post，并作相应处理
        Intent intent = new Intent(getActivity(), ChatRoomActivity.class);
        intent.putExtra(LCIMConstants.CONVERSATION_ID, event.conversationId);
        startActivity(intent);
    }

}

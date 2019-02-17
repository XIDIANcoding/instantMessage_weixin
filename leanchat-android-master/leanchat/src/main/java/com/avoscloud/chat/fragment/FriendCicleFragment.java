package com.avoscloud.chat.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avoscloud.chat.R;
import com.avoscloud.chat.activity.PublishActivity;
import com.avoscloud.chat.adapter.TaskArrayAdapter;
import com.avoscloud.chat.friends.FriendsManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by lenovo on 2018/4/25.
 */

public class FriendCicleFragment extends BaseFragment {
    @Bind(R.id.fragment_friend_pullrefresh)
    protected SwipeRefreshLayout refreshLayout;

    @Bind(R.id.fragment_friend_view)
    protected RecyclerView recyclerView;

    protected LinearLayoutManager layoutManager;
    public static Context context;
   // private List<TaskItem> taskItemList = new ArrayList<>();
   private List<AVObject> taskItemList = new ArrayList<>();
    private TaskArrayAdapter arrayAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friendsquare_fragment, container, false);
        ButterKnife.bind(this, view);

        context = getActivity();
        layoutManager = new LinearLayoutManager(getActivity());
        arrayAdapter =new TaskArrayAdapter(taskItemList);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(arrayAdapter);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initTaskItem();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initHeader();
    }
    @Override
    public void onResume() {//返回当前activity时调用
        super.onResume();
        AVAnalytics.onResume(context);
        initTaskItem();
    }
    private void initHeader() {//导航栏
        headerLayout.showTitle("广场");
        headerLayout.showRightImageButton(R.drawable.base_action_bar_add_bg_selector, new View.OnClickListener() {//添加新朋友加号按钮
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, PublishActivity.class);//进入发布界面
                ctx.startActivity(intent);
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    private void initTaskItem(){
        taskItemList.clear();
        AVQuery<AVObject> avQuery = new AVQuery<>("Task");
        avQuery.orderByDescending("createdAt");
        avQuery.include("owner");
        avQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    List<String> cacheFriends = FriendsManager.getFriendIds();
                    for(AVObject avObject:list){
                        if(cacheFriends.contains(avObject.getAVUser("owner").getObjectId())||avObject.getAVUser("owner").getObjectId().equals(AVUser.getCurrentUser().getObjectId())){
                            taskItemList.add(avObject);
                        }
                    }
                    refreshLayout.setRefreshing(false);
                    arrayAdapter.notifyDataSetChanged();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}

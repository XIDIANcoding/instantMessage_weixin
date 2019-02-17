package com.avoscloud.chat.friends;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avoscloud.chat.App;
import com.avoscloud.chat.R;
import com.avoscloud.chat.activity.AVBaseActivity;
import com.avoscloud.chat.adapter.HeaderListAdapter;
import com.avoscloud.chat.model.LeanchatUser;
import com.avoscloud.chat.util.Constants;
import com.avoscloud.chat.util.UserCacheUtils;
import com.avoscloud.chat.view.RefreshableRecyclerView;
import com.avoscloud.chat.viewholder.SearchUserItemHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 查找好友页面
 */
public class ContactAddFriendActivity extends AVBaseActivity {

  @Bind(R.id.search_user_rv_layout)//搜索清单
  protected RefreshableRecyclerView recyclerView;

  @Bind(R.id.searchNameEdit)//输入框
  EditText searchNameEdit;

  private HeaderListAdapter<LeanchatUser> adapter;
  private String searchName = "";
  int skip=0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.contact_add_friend_activity);
    init();
    recyclerView.refreshData();//初始化时缓存本地数据
  }

  private void init() {
    setTitle(App.ctx.getString(R.string.contact_findFriends));
    adapter = new HeaderListAdapter<>(SearchUserItemHolder.class);

    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setOnLoadDataListener(new RefreshableRecyclerView.OnLoadDataListener() {
      @Override
      public void onLoad(int skip, int limit, boolean isRefresh) {
        loadMoreFriend(skip, limit, isRefresh);
      }
    });
    recyclerView.setAdapter(adapter);
  }

  private void loadMoreFriend(int skip, final int limit, final boolean isRefresh) {
    AVQuery<LeanchatUser> q = LeanchatUser.getQuery(LeanchatUser.class);
    q.whereEqualTo(LeanchatUser.USERNAME, searchName);
    q.limit(3);
    q.skip(skip);
    LeanchatUser user = LeanchatUser.getCurrentUser();
    List<String> friendIds = new ArrayList<String>(FriendsManager.getFriendIds());
    friendIds.add(user.getObjectId());
    q.whereNotContainedIn(Constants.OBJECT_ID, friendIds);//不包括自己和已添加好友的信息，节省流量
    q.orderByDescending(Constants.UPDATED_AT);
    q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
    q.findInBackground(new FindCallback<LeanchatUser>() {
      @Override
      public void done(List<LeanchatUser> list, AVException e) {
        UserCacheUtils.cacheUsers(list);//缓存到本地
        recyclerView.setLoadComplete(null == list ? null : list.toArray(), false);// isReresh 为 false，则把 datas 叠加到现有数据中
        if (null != e) {
          showToast(e.getMessage());
        }
      }
    });
  }

  @OnClick(R.id.searchBtn)
  public void search(View view) {
    searchName = searchNameEdit.getText().toString();
    recyclerView.refreshData();// 执行refreshdata，调用onload，设置参数，onload执行loadMoreFriend(0, 5, true);
  }
}

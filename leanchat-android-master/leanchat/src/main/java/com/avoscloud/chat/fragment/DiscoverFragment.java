package com.avoscloud.chat.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avoscloud.chat.App;
import com.avoscloud.chat.R;
import com.avoscloud.chat.adapter.HeaderListAdapter;
import com.avoscloud.chat.model.LeanchatUser;
import com.avoscloud.chat.service.PreferenceMap;
import com.avoscloud.chat.util.Constants;
import com.avoscloud.chat.util.LogUtils;
import com.avoscloud.chat.util.UserCacheUtils;
import com.avoscloud.chat.view.RefreshableRecyclerView;
import com.avoscloud.chat.viewholder.DiscoverItemHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by lzw on 14-9-17.
 */
public class DiscoverFragment extends BaseFragment {
  private static final double EARTH_RADIUS = 6378137;
  private final SortDialogListener distanceListener = new SortDialogListener(Constants.ORDER_DISTANCE);
  private final SortDialogListener updatedAtListener = new SortDialogListener(Constants.ORDER_UPDATED_AT);

  @Bind(R.id.fragment_near_srl_pullrefresh)
  protected SwipeRefreshLayout refreshLayout;

  @Bind(R.id.fragment_near_srl_view)
  protected RefreshableRecyclerView recyclerView;

  protected LinearLayoutManager layoutManager;

  HeaderListAdapter<LeanchatUser> discoverAdapter;
  int orderType;
  PreferenceMap preferenceMap;
  List<LeanchatUser> dislist=new ArrayList<>();


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.discover_fragment, container, false);
    ButterKnife.bind(this, view);

    layoutManager = new LinearLayoutManager(getActivity());
    discoverAdapter = new HeaderListAdapter<>(DiscoverItemHolder.class);
    recyclerView.setOnLoadDataListener(new RefreshableRecyclerView.OnLoadDataListener() {
      @Override
      public void onLoad(int skip, int limit, boolean isRefresh) {;
        loadMoreDiscoverData(skip, limit, isRefresh);
      }
    });
    recyclerView.setRelationSwipeLayout(refreshLayout);
    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(discoverAdapter);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    preferenceMap = PreferenceMap.getCurUserPrefDao(getActivity());
    orderType = preferenceMap.getNearbyOrder();
    headerLayout.showTitle("附近的人");
    headerLayout.showRightImageButton(R.drawable.nearby_order, new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.discover_fragment_sort).setPositiveButton(R.string.discover_fragment_loginTime,
          updatedAtListener).setNegativeButton(R.string.discover_fragment_distance, distanceListener).show();
      }
    });
    recyclerView.refreshData();
  }

  /**
   * 加载数据
   * @param skip
   * @param limit
   * @param isRefresh
   */
  private void loadMoreDiscoverData(final int skip, final int limit, final boolean isRefresh) {
    PreferenceMap preferenceMap = PreferenceMap.getCurUserPrefDao(App.ctx);
   final AVGeoPoint location = preferenceMap.getLocation();
    if (location == null) {
      LogUtils.i("geo point is null");
      return;
    }
    AVQuery<LeanchatUser> q = LeanchatUser.getQuery(LeanchatUser.class);
    LeanchatUser user = LeanchatUser.getCurrentUser();
    q.whereNotEqualTo(Constants.OBJECT_ID, user.getObjectId());
    if (orderType == Constants.ORDER_DISTANCE) {
      q.whereNear(LeanchatUser.LOCATION, location);
    } else {
      q.orderByDescending(Constants.UPDATED_AT);
    }
    q.skip(skip);
    q.limit(limit);
    q.setCachePolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
    q.findInBackground(new FindCallback<LeanchatUser>() {
      @Override
      public void done(List<LeanchatUser> list, AVException e) {
        for(LeanchatUser user:list){
          AVGeoPoint geoPoint = user.getAVGeoPoint(LeanchatUser.LOCATION);
          String currentLat = String.valueOf(location.getLatitude());
          String currentLong = String.valueOf(location.getLongitude());
          if (geoPoint != null && !currentLat.equals("") && !currentLong.equals("")) {
            double distance = DistanceOfTwoPoints(Double.parseDouble(currentLat), Double.parseDouble(currentLong),
                    user.getAVGeoPoint(LeanchatUser.LOCATION).getLatitude(),
                    user.getAVGeoPoint(LeanchatUser.LOCATION).getLongitude());
            if(distance<=1000)dislist.add(user);
          }
        }
        UserCacheUtils.cacheUsers(dislist);
        recyclerView.setLoadComplete(null == dislist ? null : dislist.toArray(),true);
        if (null != e) {
          Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
      }
    });
  }
  public static double DistanceOfTwoPoints(double lat1, double lng1, double lat2, double lng2) {
    double radLat1 = rad(lat1);
    double radLat2 = rad(lat2);
    double a = radLat1 - radLat2;
    double b = rad(lng1) - rad(lng2);
    double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
            + Math.cos(radLat1) * Math.cos(radLat2)
            * Math.pow(Math.sin(b / 2), 2)));
    s = s * EARTH_RADIUS;
    s = Math.round(s * 10000) / 10000;
    return s;
  }

  private static double rad(double d) {
    return d * Math.PI / 180.0;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    preferenceMap.setNearbyOrder(orderType);
  }

  public class SortDialogListener implements DialogInterface.OnClickListener {
    int orderType;

    public SortDialogListener(int orderType) {
      this.orderType = orderType;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
      DiscoverFragment.this.orderType = orderType;
      recyclerView.refreshData();
    }
  }
}

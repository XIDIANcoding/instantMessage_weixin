package com.avoscloud.chat.friends;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avoscloud.chat.App;
import com.avoscloud.chat.R;
import com.avoscloud.chat.fragment.BaseFragment;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by lenovo on 2018/5/3.
 */

public class ContactFragmentAll extends BaseFragment {
    private ViewPager viewPager;
    private TabLayout tabLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.contacttragmenttll, container, false);
        viewPager = (ViewPager)view.findViewById(R.id.pager);
        tabLayout = (TabLayout)view.findViewById(R.id.tablayout);
        ButterKnife.bind(this, view);
        initTabLayout();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initHeader();
    }

    private void initHeader() {//导航栏
        headerLayout.showTitle(App.ctx.getString(R.string.contact));
        headerLayout.showRightImageButton(R.drawable.base_action_bar_add_bg_selector, new View.OnClickListener() {//添加新朋友加号按钮
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, ContactAddFriendActivity.class);//进入添加好友界面
                ctx.startActivity(intent);
            }
        });
    }

    private void initTabLayout() {
        String[] tabList = new String[]{"好友", "群组"};
        final Fragment[] fragmentList = new Fragment[] {new ContactFragment(),
                new GroupFragment()};

        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        for (int i = 0; i < tabList.length; i++) {
            tabLayout.addTab(tabLayout.newTab().setText(tabList[i]));
        }//tablayout布局

        TabFragmentAdapter adapter = new TabFragmentAdapter(this.getActivity().getSupportFragmentManager(),
                Arrays.asList(fragmentList), Arrays.asList(tabList));
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (0 == position) {
//          EventBus.getDefault().post(new ConversationFragmentUpdateEvent());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(adapter);
    }

    public class TabFragmentAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> mFragments;
        private List<String> mTitles;

        public TabFragmentAdapter(FragmentManager fm, List<Fragment> fragments, List<String> titles) {
            super(fm);
            mFragments = fragments;
            mTitles = titles;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }
    }

}

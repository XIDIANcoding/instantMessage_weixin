package com.avoscloud.chat.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.avoscloud.chat.R;
import com.avoscloud.chat.model.LeanchatUser;
import com.avoscloud.chat.model.UserState;

import java.util.ArrayList;
import java.util.List;

public class StateActivity extends AppCompatActivity {


    private ListView mListView; //首页的ListView
    private List<UserState> namesList; //用于装载数据的集合
    private int selectPosition = -1;//用于记录用户选择的变量
    private UserState selectBrand; //用户选择的状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state);
        initView();
        initDatas();
    }

    private void initView(){
        mListView = (ListView)findViewById(R.id.id_myList);
    }

    private void initDatas(){
        //初始化ListView适配器的数据
        namesList = new ArrayList<>();
        UserState brand0 = new UserState("(在线)");
        UserState brand1 = new UserState("(忙碌)");
        UserState brand2 = new UserState("(离线)");
        namesList.add(brand0);
        namesList.add(brand1);
        namesList.add(brand2);
        final MyAdapter myAdapter = new MyAdapter(this,namesList);
        mListView.setAdapter(myAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取选中的参数
                selectPosition = position;
                myAdapter.notifyDataSetChanged();
                selectBrand = namesList.get(position);
                AVObject todo = AVObject.createWithoutData("_User", LeanchatUser.getCurrentUserId());
                todo.put("state",selectBrand.getUserState());
                todo.saveInBackground();
            }
        });
    }

    public class MyAdapter extends BaseAdapter {
        Context context;
        List<UserState> brandsList;
        LayoutInflater mInflater;
        public MyAdapter(Context context,List<UserState> mList){
            this.context = context;
            this.brandsList = mList;
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            return brandsList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = null;
            if(convertView == null){
                convertView = mInflater.inflate(R.layout.adapter_item,parent,false);
                viewHolder = new ViewHolder();
                viewHolder.name = (TextView)convertView.findViewById(R.id.id_name);
                viewHolder.select = (RadioButton)convertView.findViewById(R.id.id_select);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }
            viewHolder.name.setText(brandsList.get(position).getUserState());

            if(selectPosition == position){
                viewHolder.select.setChecked(true);
            }
            else{
                viewHolder.select.setChecked(false);
            }
            return convertView;
        }
    }


    public class ViewHolder{
        TextView name;
        RadioButton select;
    }
}

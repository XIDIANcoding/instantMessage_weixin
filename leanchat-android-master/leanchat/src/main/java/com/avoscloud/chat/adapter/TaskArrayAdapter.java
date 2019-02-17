package com.avoscloud.chat.adapter;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.avoscloud.chat.R;
import com.avoscloud.chat.activity.RoundedTransformation;
import com.avoscloud.chat.fragment.FriendCicleFragment;
import com.avoscloud.chat.model.Task_full_infomation;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.avoscloud.chat.model.LeanchatUser.AVATAR;

/**
 * Created by lenovo on 2018/4/25.
 */

public class TaskArrayAdapter extends RecyclerView.Adapter<TaskArrayAdapter.ViewHolder> {

    private List<AVObject> taskItemList= new ArrayList<>();

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView taskowner;
        TextView taskTitle;
        TextView taskTime;
        private CardView mItem;
        private ImageView mPicture;
        ImageView userimg;
        View taskItemView;


        public ViewHolder(View itemView) {
            super(itemView);
            taskItemView = itemView;
            taskTitle = (TextView)itemView.findViewById(R.id.title_item_main);
            taskTime = (TextView)itemView.findViewById(R.id.time_item_main);
            taskowner=(TextView)itemView.findViewById(R.id.name_item_main);
            mPicture = (ImageView) itemView.findViewById(R.id.picture_item_main);
            userimg=(ImageView)itemView.findViewById(R.id.userimg_item_main_item_main);
            mItem = (CardView) itemView.findViewById(R.id.item_main);
        }
    }

    public TaskArrayAdapter(List<AVObject> itemList) {
        this.taskItemList = itemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_main,parent,false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return  viewHolder;

    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.taskTitle.setText((CharSequence) taskItemList.get(position).get("title"));
        SimpleDateFormat simpleDateFormat = null;
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        holder.taskTime.setText("creattime：" + simpleDateFormat.format(taskItemList.get(position).getDate("createdAt")));
        //holder.taskTime.setText( "creattime: "+taskItemList.get(position).getDate("createdAt").toString());
        holder.taskowner.setText(taskItemList.get(position).getAVUser("owner").getUsername());
        Picasso.with(FriendCicleFragment.context).load(taskItemList.get(position).getAVFile("image") == null ? "www" : taskItemList.get(position).getAVFile("image").getUrl()).transform(new RoundedTransformation(9, 0)).into(holder.mPicture);
        Picasso.with(FriendCicleFragment.context).load(taskItemList.get(position).getAVUser("owner").getAVFile(AVATAR).getUrl())
                .placeholder(R.drawable.lcim_default_avatar_icon).into(holder.userimg);//头像
        holder.mItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FriendCicleFragment.context, Task_full_infomation.class);//进入详情
                intent.putExtra("taskId", taskItemList.get(position).getObjectId());
                FriendCicleFragment.context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
//        return 0;
        return taskItemList.size();
    }
}


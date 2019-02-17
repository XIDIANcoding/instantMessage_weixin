package com.avoscloud.chat.model;

/**
 * Created by lenovo on 2018/4/27.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.avoscloud.chat.R;
import com.avoscloud.chat.activity.AVBaseActivity;
import com.avoscloud.chat.activity.RoundedTransformation;
import com.avoscloud.chat.activity.ZoneActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;

import static com.avoscloud.chat.model.LeanchatUser.AVATAR;

/**
 * Created by lenovo on 2018/4/27.
 */

public class Task_full_infomation extends AVBaseActivity implements View.OnClickListener{
    private TextView username;
    private TextView task_information_zan_number;
    private TextView task_information_view_number;
    private TextView describe;
    public  ImageView pictueitem;
    public  ImageView circleImageView;
    public String objectuser;
    public List<Liuyan> liuyanList=new ArrayList<>();

    public Button LiuyanButton;


    public LinearLayout commentInput;
    public EditText commentinputEditText;
    public Button commentButton;
    public  LiuyanArrayAdapter arrayAdapter;

    @Bind(R.id.fragment_liuyan_fefresh)
    protected SwipeRefreshLayout refreshLayout;
    @Bind(R.id.task_information_liuyan)
    protected RecyclerView liuyanRecycle;

    public String taskIdthis;

    public static final int zanNormalBackId=R.drawable.zan;
    public static final int liuyanBackId=R.drawable.message;
    public static final int zanActiveBackId=R.drawable.zanactive;
    public Button DianzanButton;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        username = (TextView)findViewById(R.id.task_information_name);
        circleImageView = (ImageView)findViewById(R.id.contentuser_image);
        task_information_zan_number = (TextView)findViewById(R.id.task_information_zan_number);
        task_information_view_number = (TextView)findViewById(R.id.task_information_view_number);
        describe = (TextView)findViewById(R.id.task_information_described_text);
        pictueitem=(ImageView)findViewById(R.id.picture_item_main);


        LiuyanButton= (Button)findViewById(R.id.task_information_liuyan_button);
        DianzanButton=(Button)findViewById(R.id.task_information_dianzan_button);
        LiuyanButton.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(liuyanBackId), null, null);
        commentInput = (LinearLayout)findViewById(R.id.comment_input);
        commentButton=(Button)findViewById(R.id.comment_button);
        commentinputEditText=(EditText)findViewById(R.id.comment_input_edittext);



        try{
            Intent intent = getIntent();
            final String taskId= intent.getStringExtra("taskId");//获取当前任务的objectid
            taskIdthis = taskId;

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            liuyanRecycle.setLayoutManager(linearLayoutManager);
            arrayAdapter =new LiuyanArrayAdapter(liuyanList);
            liuyanRecycle.setAdapter(arrayAdapter);
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    initLiuyan();
                }
            });


            AVQuery<AVObject> avQuery = new AVQuery<>("Task");//显示具体信息
            avQuery.include("owner");//级联查询user
            avQuery.getInBackground(taskIdthis, new GetCallback<AVObject>() {
                @Override
                public void done(AVObject avObject, AVException e) {
                    if (e==null){
                        int taskView= (int)avObject.getNumber("taskView");
                        describe.setText(avObject.getString("description"));
                        username.setText(avObject.getAVUser("owner").getUsername());
                        objectuser=avObject.getAVUser("owner").getUsername();
                        Picasso.with(Task_full_infomation.this).load(avObject.getAVUser("owner").getAVFile(AVATAR).getUrl()).transform(new RoundedTransformation(9, 0))
                               .into(circleImageView);
                        Picasso.with(Task_full_infomation.this).load(avObject.getAVFile("image").getUrl()).transform(new RoundedTransformation(9, 0))
                                .into(pictueitem);
                        task_information_view_number.setText(taskView+"");
                    }else {
                        Toast.makeText(Task_full_infomation.this,"稍后再试",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            });
            dianZanView();
            initDianzan();
            initLiuyan();
            initLiulan();
            LiuyanButton.setOnClickListener(this);
            DianzanButton.setOnClickListener(this);
            username.setOnClickListener(this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void initLiuyan(){
        AVQuery<AVObject> avQuery = new AVQuery<>("Liuyan");
        avQuery.whereEqualTo("taskId",taskIdthis);
        avQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e==null){
                    liuyanList.clear();
                    for (AVObject object:list) {
                        Liuyan liuyan = new Liuyan();
                        liuyan.setLiuyancontent(object.getString("comment"));
                        liuyan.setLiuyaner(object.getString("pushUserName"));
                        liuyan.setLiuyanReceiver(object.getString("receiverUser"));
                        liuyanList.add(liuyan);
                    }
                    refreshLayout.setRefreshing(false);
                    arrayAdapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(Task_full_infomation.this,"留言加载有问题",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }

    public void initDianzan(){
        AVQuery<AVObject> avQueryZanNum = new AVQuery<>("Dianzan");
        avQueryZanNum.whereEqualTo("taskId",taskIdthis);
        avQueryZanNum.countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                task_information_zan_number.setText(i+"");
            }
        });
    }

    public void initLiulan(){
        /**
         * 实现浏览的次数
         */
        final AVObject theTaskLiulan = AVObject.createWithoutData("Task",taskIdthis);
        theTaskLiulan.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                theTaskLiulan.increment("taskView");
                theTaskLiulan.setFetchWhenSave(true);
                theTaskLiulan.saveInBackground();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.task_information_liuyan_button:
                liuyanfun();
                break;
            case R.id.task_information_dianzan_button:
                dianzanfun();
                break;
            case R.id.task_information_name:
                visitUser();
        }
    }

    public void liuyanfun(){
        commentInput.setVisibility(View.VISIBLE);//隐藏的layout
        commentButton.setOnClickListener(new View.OnClickListener() {//留言提交
            @Override
            public void onClick(View view) {
                AVObject comment = new AVObject("Liuyan");
                final String inputContent = commentinputEditText.getText().toString();
                comment.put("comment",inputContent);
                comment.put("pushUser", AVUser.getCurrentUser().getObjectId());
                comment.put("pushUserName",AVUser.getCurrentUser().getUsername());
                comment.put("taskId",taskIdthis);
                comment.put("receiverUser","");
                commentinputEditText.setText("");
                commentInput.setVisibility(View.GONE);//输入完成后隐藏layout
                comment.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e==null){
                            Liuyan liuyan = new Liuyan();
                            liuyan.setLiuyancontent(inputContent);
                            liuyan.setLiuyaner(AVUser.getCurrentUser().getUsername() );
                            liuyan.setLiuyanReceiver("");
                            liuyanList.add(liuyan);
                            arrayAdapter.notifyItemInserted(liuyanList.size()-1);
                        }else {
                            Toast.makeText(Task_full_infomation.this,"普通评论好像出了点问题",Toast.LENGTH_SHORT).show();
                            e.printStackTrace();

                        }
                    }
                });
            }
        });

    }
    public void dianZanView(){
        AVQuery<AVObject> queryzanuser = new AVQuery<>("Dianzan");
        queryzanuser.whereEqualTo("DianzanUser",AVUser.getCurrentUser().getObjectId());

        AVQuery<AVObject> queryzantask= new AVQuery<>("Dianzan");
        queryzantask.whereEqualTo("taskId",taskIdthis);

        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(queryzanuser,queryzantask));

        query.countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                if (e==null){
                    if (i==1){
                        DianzanButton.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(zanActiveBackId), null, null);
                    }
                    if (i==0){
                        DianzanButton.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(zanNormalBackId), null, null);
                    }
                }else {
                    Toast.makeText(Task_full_infomation.this,"不好意思好像有点问题出现了！",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }
    public void dianzanfun(){
        AVQuery<AVObject> queryzanuser = new AVQuery<>("Dianzan");
        queryzanuser.whereEqualTo("DianzanUser",AVUser.getCurrentUser().getObjectId());

        AVQuery<AVObject> queryzantask= new AVQuery<>("Dianzan");
        queryzantask.whereEqualTo("taskId",taskIdthis);

        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(queryzanuser,queryzantask));

        query.countInBackground(new CountCallback() {
            @Override
            public void done(int i, AVException e) {
                if (e==null){
                    if (i==1){
                        Toast.makeText(Task_full_infomation.this,"您已赞过该内容",Toast.LENGTH_SHORT).show();
                    }
                    if (i==0){
                        AVObject dianzan = new AVObject("Dianzan");
                        dianzan.put("taskId",taskIdthis);
                        dianzan.put("DianzanUser",AVUser.getCurrentUser().getObjectId());
                        dianzan.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e==null){
                                    int dianzannumber = Integer.parseInt(task_information_zan_number.getText().toString());
                                    dianzannumber++;
                                    task_information_zan_number.setText(dianzannumber+"");
                                }
                                else {
                                    Toast.makeText(Task_full_infomation.this,"貌似出了一点错误。",Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                        });
                        DianzanButton.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(zanActiveBackId), null, null);
                    }
                }else {
                    Toast.makeText(Task_full_infomation.this,"不好意思好像有点问题出现了！",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }
    public void visitUser(){
        AVQuery<AVObject> avQuery = new AVQuery<>("Task");//显示具体信息
        avQuery.include("owner");//级联查询user
        avQuery.getInBackground(taskIdthis, new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                if (e==null){
                   String objectuserId=avObject.getAVUser("owner").getObjectId();
                    Intent intent=new Intent(Task_full_infomation.this,ZoneActivity.class);
                    intent.putExtra("lookId",objectuserId );
                    Task_full_infomation.this.startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(Task_full_infomation.this,"稍后再试",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }
//一下为留言的adapter
    class LiuyanArrayAdapter extends RecyclerView.Adapter<LiuyanArrayAdapter.ViewHoder> {

        public List<Liuyan> newliuyanList = new ArrayList<>();

        class ViewHoder extends RecyclerView.ViewHolder{
            View liuyan;
            TextView liuyanuser;
            TextView liuyancontent;
            TextView liuyanReceiver;
            TextView To;

            public ViewHoder(View itemView) {
                super(itemView);
                this.liuyan= itemView;
                this.liuyanuser = (TextView) itemView.findViewById(R.id.liuyan_user_item);
                this.liuyancontent=(TextView)itemView.findViewById(R.id.liuyan_content_item);
                this.liuyanReceiver= (TextView)itemView.findViewById(R.id.liuyan_receiveruser_item);
                this.To= (TextView)itemView.findViewById(R.id.liuyan_user_to_user);
            }
        }

        public LiuyanArrayAdapter(List<Liuyan> liuyanList) {
            this.newliuyanList = liuyanList;
        }

        @Override
        public ViewHoder onCreateViewHolder(final ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.liuyan_item,parent,false);
            final ViewHoder viewHoder = new ViewHoder(view);

            /**
             * 为留言中添加点击功能
             */

            viewHoder.liuyan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    commentInput.setVisibility(View.VISIBLE);
                    commentinputEditText.setText("");
                    commentinputEditText.setHint("@"+viewHoder.liuyanuser.getText().toString());
                    commentButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            commentInput.setVisibility(View.GONE);
                            AVObject comment = new AVObject("Liuyan");
                            String inputContent = commentinputEditText.getText().toString();
                            comment.put("comment",inputContent);
                            comment.put("pushUser",AVUser.getCurrentUser().getObjectId());
                            comment.put("pushUserName",AVUser.getCurrentUser().getUsername());
                            comment.put("taskId",taskIdthis);
                            comment.put("receiverUser",viewHoder.liuyanuser.getText().toString());
                            comment.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e==null){
                                        Liuyan liuyan = new Liuyan();
                                        liuyan.setLiuyancontent(commentinputEditText.getText().toString());
                                        liuyan.setLiuyaner(AVUser.getCurrentUser().getUsername() );
                                        liuyan.setLiuyanReceiver(viewHoder.liuyanuser.getText().toString());
                                        liuyanList.add(liuyan);
                                        arrayAdapter.notifyItemInserted(liuyanList.size()-1);
                                    }else {
                                        Toast.makeText(Task_full_infomation.this,"@评论好像出了点问题",Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();

                                    }
                                }
                            });
                        }
                    });
                }
            });

            return viewHoder;
        }

        @Override
        public void onBindViewHolder(ViewHoder holder, final int position) {
            holder.liuyanuser.setText(liuyanList.get(position).getLiuyaner());

            if (!TextUtils.isEmpty(liuyanList.get(position).getLiuyanReceiver())){
                holder.liuyanReceiver.setText(liuyanList.get(position).getLiuyanReceiver()+":");
                holder.To.setText(" @ ");
                holder.liuyancontent.setText(liuyanList.get(position).getLiuyancontent());
            }
            else {
                holder.liuyancontent.setText(":"+liuyanList.get(position).getLiuyancontent());
            }
        }

        @Override
        public int getItemCount() {
//        return 0;
            return liuyanList.size();
        }
    }


}


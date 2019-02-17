package com.avoscloud.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avoscloud.chat.R;
import com.avoscloud.chat.adapter.ZoneAdaper;
import com.avoscloud.chat.model.LeanchatUser;
import com.avoscloud.chat.util.UserCacheUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2018/5/4.
 */

public class ZoneActivity extends AVBaseActivity {

    protected SwipeRefreshLayout refreshLayout;
    protected RecyclerView recyclerView;
    protected LinearLayoutManager layoutManager;

    private List<AVObject> taskItemList = new ArrayList<>();
    private ZoneAdaper arrayAdapter;

    public String lookIdthis;
    public LeanchatUser lookuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zone);
        setTitle("个人主页");
        Intent intent = getIntent();
        final String lookId= intent.getStringExtra("lookId");//获取当前任务的objectid

        lookIdthis= lookId;
        lookuser = UserCacheUtils.getCachedUser(lookIdthis);

        refreshLayout=(SwipeRefreshLayout)findViewById(R.id.fragment_zone_pullrefresh);
        recyclerView=(RecyclerView)findViewById(R.id.fragment_zone_view);
        layoutManager = new LinearLayoutManager(this);
        arrayAdapter =new ZoneAdaper(taskItemList,ZoneActivity.this);
        //refresh();

//        if(LeanchatUser.getCurrentUser().getObjectId().equals(lookId)){
//            zonepicture.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(Intent.ACTION_PICK, null);
//                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                    startActivityForResult(intent, IMAGE_PICK_REQUEST);
//                }
//            });
//        }
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(arrayAdapter);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initTaskItem();
            }
        });
    }
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == IMAGE_PICK_REQUEST) {
//                Uri uri = data.getData();
//                startImageCrop(uri, 200, 200, CROP_REQUEST);
//            } else if (requestCode == CROP_REQUEST) {
//                final String path = saveCropAvatar(data);
//                LeanchatUser user = LeanchatUser.getCurrentUser();
//                user.saveZoneAvatar(path, null);
//            }
//        }
//    }
//
//    public Uri startImageCrop(Uri uri, int outputX, int outputY, int requestCode) {
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(uri, "image/*");
//        intent.putExtra("crop", "true");
//        intent.putExtra("aspectX", 1);
//        intent.putExtra("aspectY", 1);
//        intent.putExtra("outputX", outputX);
//        intent.putExtra("outputY", outputY);
//        intent.putExtra("scale", true);
//        String outputPath = PathUtils.getAvatarTmpPath();
//        Uri outputUri = Uri.fromFile(new File(outputPath));
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
//        intent.putExtra("return-data", true);
//        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//        intent.putExtra("noFaceDetection", false); // face detection
//        startActivityForResult(intent, requestCode);
//        return outputUri;
//    }
//
//    private String saveCropAvatar(Intent data) {
//        Bundle extras = data.getExtras();
//        String path = null;
//        if (extras != null) {
//            Bitmap bitmap = extras.getParcelable("data");
//            if (bitmap != null) {
//                path = PathUtils.getAvatarCropPath();
//                Utils.saveBitmap(path, bitmap);
//                if (bitmap != null && bitmap.isRecycled() == false) {
//                    bitmap.recycle();
//                }
//            }
//        }
//        return path;
//    }
    @Override
    public void onResume() {
    super.onResume();
    initTaskItem();
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
                    for(AVObject avObject:list){
                        if(avObject.getAVUser("owner").getObjectId().equals(lookIdthis)){
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

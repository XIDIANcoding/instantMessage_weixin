package com.avoscloud.chat.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.CountCallback;
import com.avos.avoscloud.RequestPasswordResetCallback;
import com.avoscloud.chat.R;

import java.util.Arrays;

public class FindBackPasswordActivity extends Activity implements View.OnClickListener{

    public EditText email;
    public LinearLayout backButton;
    public Button sureButton;
    public  EditText nameView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_back_password);
        email = (EditText)findViewById(R.id.email_verify_emailText);
        backButton = (LinearLayout) findViewById(R.id.email_verify_back);
        sureButton=(Button)findViewById(R.id.email_verify_sure);
        nameView=(EditText)findViewById(R.id.name_verify);


        backButton.setOnClickListener(this);
        sureButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.email_verify_back:
                finish();
                Intent intent = new Intent(FindBackPasswordActivity.this,EntryLoginActivity.class);
                startActivity(intent);
                break;
            case R.id.email_verify_sure:
                if (EntryRegisterActivity.emailValidation(email.getText().toString().trim())){
                    AVQuery<AVObject> name = new AVQuery<>("_User");
                    name.whereEqualTo("username",nameView.getText().toString());

                    AVQuery<AVObject> emails= new AVQuery<>("_User");
                    emails.whereEqualTo("email",email.getText().toString());

                    AVQuery<AVObject> query = AVQuery.and(Arrays.asList(name,emails));

                    query.countInBackground(new CountCallback() {
                        @Override
                        public void done(int i, AVException e) {
                            if (e==null){
                                if (i==1){
                                    AVUser.requestPasswordResetInBackground(email.getText().toString().trim(), new RequestPasswordResetCallback() {
                                        @Override
                                        public void done(AVException e) {
                                            if (e == null) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(FindBackPasswordActivity.this);
                                                builder.setTitle("邮箱重置密码");
                                                builder.setMessage("你好，我们已经向你的邮箱发送了一封邮件用于修改密码，请注意查收");
                                                builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        finish();
                                                        Intent intent = new Intent(FindBackPasswordActivity.this,EntryLoginActivity.class);
                                                        startActivity(intent);
                                                    }
                                                });
                                                builder.show();
                                            } else {
                                                Toast.makeText(FindBackPasswordActivity.this,"不好意思出错了！", Toast.LENGTH_SHORT).show();
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                                else if(i==0){
                                    Toast.makeText(FindBackPasswordActivity.this,"请输入注册邮箱",Toast.LENGTH_SHORT).show();
                                }
                                }
                            else {
                                Toast.makeText(FindBackPasswordActivity.this,"不好意思出错了！", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    });
                }
                else{
                                email.setError("邮箱有错误，请检查你的邮箱填写");
                    }
        }
    }
}

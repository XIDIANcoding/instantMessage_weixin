package com.avoscloud.chat.viewholder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avoscloud.chat.R;
import com.avoscloud.chat.friends.ContactPersonInfoActivity;
import com.avoscloud.chat.model.LeanchatUser;
import com.avoscloud.chat.util.Constants;
import com.squareup.picasso.Picasso;

import cn.leancloud.chatkit.viewholder.LCIMCommonViewHolder;

/**
 * Created by wli on 15/12/3.
 */
public class SearchUserItemHolder extends LCIMCommonViewHolder<LeanchatUser> {

  private TextView nameView;
  private ImageView avatarView;
  private LeanchatUser leanchatUser;

  public SearchUserItemHolder(Context context, ViewGroup root) {
    super(context, root, R.layout.search_user_item_layout);//将search_user_item_layout作为view填充布局
    //super的方法为
    // public LCIMCommonViewHolder(Context context, ViewGroup root, int layoutRes) {
    ///super(LayoutInflater.from(context).inflate(layoutRes, root, false));}
    nameView = (TextView)itemView.findViewById(R.id.search_user_item_tv_name);
    //itemView是super的super即ViewHolder的参数，等同于View itemView=（View）LayoutInflater.from(context).inflate(R.layout.search_user_item_layout, root, false)
    avatarView = (ImageView)itemView.findViewById(R.id.search_user_item_im_avatar);

    itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getContext(), ContactPersonInfoActivity.class);//进入联系人资料界面
        intent.putExtra(Constants.LEANCHAT_USER_ID, leanchatUser.getObjectId());
        getContext().startActivity(intent);
      }
    });
  }

  @Override
  public void bindData(final LeanchatUser leanchatUser) {
    this.leanchatUser = leanchatUser;
    Picasso.with(getContext()).load(leanchatUser.getAvatarUrl()).into(avatarView);
    nameView.setText(leanchatUser.getUsername());
  }

  public static ViewHolderCreator HOLDER_CREATOR = new ViewHolderCreator<SearchUserItemHolder>() {
    @Override
    public SearchUserItemHolder createByViewGroupAndType(ViewGroup parent, int viewType) {
      return new SearchUserItemHolder(parent.getContext(), parent);
    }
  };
}


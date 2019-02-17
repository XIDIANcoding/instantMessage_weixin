package com.avoscloud.chat.viewholder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avoscloud.chat.R;
import com.avoscloud.chat.event.ConversationMemberClickEvent;
import com.avoscloud.chat.model.LeanchatUser;
import com.squareup.picasso.Picasso;

import cn.leancloud.chatkit.viewholder.LCIMCommonViewHolder;
import de.greenrobot.event.EventBus;

/**
 * Created by wli on 15/12/2.
 */
public class ConversationDetailItemHolder extends LCIMCommonViewHolder<LeanchatUser> {

  ImageView avatarView;
  TextView nameView;
  LeanchatUser leanchatUser;

  public ConversationDetailItemHolder(Context context, ViewGroup root) {
    super(context, root, R.layout.conversation_member_item);
    avatarView = (ImageView)itemView.findViewById(R.id.avatar);
    nameView = (TextView)itemView.findViewById(R.id.username);

    itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (null != leanchatUser) {
          EventBus.getDefault().post(new ConversationMemberClickEvent(leanchatUser.getObjectId(), false));
        }
      }
    });

    itemView.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        if (null != leanchatUser) {
          EventBus.getDefault().post(new ConversationMemberClickEvent(leanchatUser.getObjectId(), true));
        }
        return true;
      }
    });
  }

  @Override
  public void bindData(LeanchatUser user) {
    leanchatUser = user;
    if (null != user) {
      Picasso.with(getContext()).load(user.getAvatarUrl()).into(avatarView);
      nameView.setText(user.getUsername());
    } else {
      avatarView.setImageResource(0);
      nameView.setText("");
    }
  }

  public static ViewHolderCreator HOLDER_CREATOR = new ViewHolderCreator<ConversationDetailItemHolder>() {
    @Override
    public ConversationDetailItemHolder createByViewGroupAndType(ViewGroup parent, int viewType) {
      return new ConversationDetailItemHolder(parent.getContext(), parent);
    }
  };
}

package com.zeus.socketchat;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zeus.socketchat.dataModels.ChatMsg;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Zeus on 6/22/2016.
 */
public class ChatListAdapter extends ArrayAdapter<ChatMsg> {
    Context context;
    ArrayList<ChatMsg> chatMsgArrayList;

    public ChatListAdapter(Context context,ArrayList<ChatMsg> chatMsgArrayList){
        super(context,0,chatMsgArrayList);
        this.chatMsgArrayList=chatMsgArrayList;
        this.context=context;
    }
    private static class ChatMsgViewHolder{
        LinearLayout msgOutline;
        TextView senderTextView;
        TextView msgContentTextView;
        TextView dateTextView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=View.inflate(context,R.layout.chat_item_layout,null);
            ChatMsgViewHolder vh=new ChatMsgViewHolder();
            vh.msgOutline=(LinearLayout) convertView.findViewById(R.id.chatMsgOutline);
            vh.dateTextView=(TextView) convertView.findViewById(R.id.DateTextView);
            vh.msgContentTextView= (TextView) convertView.findViewById(R.id.MsgContentTextView);
            vh.senderTextView=(TextView) convertView.findViewById(R.id.SenderTextView);
            convertView.setTag(vh);
        }
        ChatMsgViewHolder vh=(ChatMsgViewHolder)
                convertView.getTag();
        ChatMsg curMsg=chatMsgArrayList.get(position);
        if(curMsg.sender.equals(Client.sender)){
            LinearLayout chatItemContainer= (LinearLayout) convertView.findViewById(R.id.chatItemContainer);
            chatItemContainer.setGravity(Gravity.END);
            vh.senderTextView.setText("You :");
            vh.msgOutline.setBackgroundResource(R.drawable.bubble_a);
        }

        else{
            LinearLayout chatItemContainer= (LinearLayout) convertView.findViewById(R.id.chatItemContainer);
            chatItemContainer.setGravity(Gravity.START);
            vh.senderTextView.setText(curMsg.sender+" :");
            vh.msgOutline.setBackgroundResource(R.drawable.bubble_b);
        }

        DateFormat dateFormat = new SimpleDateFormat("'at' HH:mm 'on' dd/MM/yyyy", Locale.US);

        vh.dateTextView.setText(dateFormat.format(curMsg.date));
        vh.msgContentTextView.setText(curMsg.msgContent);

        return convertView;
    }
}

package com.zeus.socketchat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zeus.socketchat.dataModels.OtherUsersInfo;

import java.util.ArrayList;

/**
 * Adapter to fill ListView with the details(name,status) of users registered on the server
 * Created by Zeus on 6/30/2016.
 */
public class UsersListAdapter extends ArrayAdapter<OtherUsersInfo> {

    Context context;
    ArrayList<OtherUsersInfo> friendsList;

    public UsersListAdapter(Context context, ArrayList<OtherUsersInfo> objects) {
        super(context, 0, objects);
        this.context=context;
        this.friendsList=objects;
    }

    /**
     * ViewHolder to store the TextView for username and ImageView to denote user's status
     */
    private static class ViewHolder{
        TextView friendUsername;
        ImageView friendOnline;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=View.inflate(context,R.layout.user_list_item_layout,null);
            ViewHolder vh=new ViewHolder();
            vh.friendUsername=(TextView) convertView.findViewById(R.id.friendUsernameTv);
            vh.friendOnline=(ImageView) convertView.findViewById(R.id.friendOnlineImageView);

            convertView.setTag(vh);
        }
        ViewHolder vh= (ViewHolder) convertView.getTag();
        OtherUsersInfo curFriend=friendsList.get(position);
        vh.friendUsername.setText(curFriend.friendUsername);
        if(curFriend.isOnline)
            vh.friendOnline.setImageDrawable(getContext().getResources().getDrawable(R.drawable.green));
        else
            vh.friendOnline.setImageDrawable(getContext().getResources().getDrawable(R.drawable.gray));

        return convertView;
    }
}

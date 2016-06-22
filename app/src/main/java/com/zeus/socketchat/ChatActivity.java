package com.zeus.socketchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Select;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

//    Intent sendMsgIntent;
    ChatListAdapter chatListAdapter;
    ArrayList<ChatMsg> chatMsgList;
    ListView msgListView;
    TextView recipientNameTV;
//    ArrayAdapter<String> arrayAdapter;
    Button sendMsgButton;
    EditText msgEditText;
    String recipient;
    Intent receiveMsgIntent;
    private MsgReceiver msgReceiver;

    @Override
    protected void onDestroy() {
        unregisterReceiver(msgReceiver);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Client.currentlyChattingWith=null;
        super.onPause();
    }

    @Override
    protected void onResume() {
        Client.currentlyChattingWith=recipient;
        super.onResume();
    }

    public class MsgReceiver extends BroadcastReceiver{
        public static final String ACTION_RESP="com.zeus.socketchat.intent.action.MESSAGE_PROCESSED";
        @Override
        public void onReceive(Context context, Intent intent) {

            ChatMsg msgRec= (ChatMsg) intent.getSerializableExtra("msg");

            if(msgRec!=null&&msgRec.sender.equals(recipient)){
                chatMsgList.add(msgRec);
                chatListAdapter.notifyDataSetChanged();
            }


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent i=getIntent();
        recipient=i.getStringExtra("recipient");
        Client.currentlyChattingWith=recipient;
        List<ChatMsg> databaseList=  new Select().from(ChatMsg.class)
                .where("recipient=? or sender=?",recipient,recipient).execute();
        recipientNameTV= (TextView) findViewById(R.id.chatActivityRecipientNameTV);
        recipientNameTV.setText("Chat with: "+recipient);
        chatMsgList=new ArrayList<>();
        if(databaseList!=null)
            for(int j=0;j<databaseList.size();++j)
                chatMsgList.add(databaseList.get(j));

//        boolean pendingMsg=i.getBooleanExtra("isPending",false);
//        if(pendingMsg){
//            String str=i.getStringExtra("pending");
//            msgList.add(recipient+": "+str);
//        }
        msgEditText= (EditText) findViewById(R.id.msgToBeSentEditText);
        msgListView= (ListView) findViewById(R.id.messagesListView);
        chatListAdapter=new ChatListAdapter(ChatActivity.this,chatMsgList);
        msgListView.setAdapter(chatListAdapter);
//        arrayAdapter=new ArrayAdapter<String>(ChatActivity.this,
//                android.R.layout.simple_list_item_1,msgList);
//        msgListView.setAdapter(arrayAdapter);

        msgReceiver=new MsgReceiver();



        IntentFilter intentFilter=new IntentFilter(MsgReceiver.ACTION_RESP);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        intentFilter.setPriority(15);
        registerReceiver(msgReceiver,intentFilter);



        sendMsgButton= (Button) findViewById(R.id.sendMessageButton);
        sendMsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msgToBeSent=msgEditText.getText().toString();
//                msgList.add("You: "+msgToBeSent);
//                arrayAdapter.notifyDataSetChanged();
                msgEditText.setText("");
                Date date = new Date();
                ChatMsg msg1=new ChatMsg(date,ChatMsg.CHAT,Client.sender,recipient,msgToBeSent,null);
                chatMsgList.add(msg1);
                chatListAdapter.notifyDataSetChanged();

                SendMsgAsyncTask sendMsgAsyncTask=new SendMsgAsyncTask();
                sendMsgAsyncTask.execute(msg1);
                msg1.save();
            }
        });
        receiveMsgIntent=new Intent(ChatActivity.this,ChatReceiveIntentService.class);
        startService(receiveMsgIntent);
    }
}

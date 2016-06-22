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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

public class ChatActivity extends AppCompatActivity {

    Intent sendMsgIntent;
    ArrayList<String> msgList;
    ListView msgListView;
    ArrayAdapter<String> arrayAdapter;
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

            if(msgRec!=null&&msgRec.Sender.equals(recipient)){
                msgList.add(msgRec.Sender+": "+msgRec.msgContent);
                arrayAdapter.notifyDataSetChanged();
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
        msgList=new ArrayList<>();
        boolean pendingMsg=i.getBooleanExtra("isPending",false);
        if(pendingMsg){
            String str=i.getStringExtra("pending");
            msgList.add(recipient+": "+str);
        }
        msgEditText= (EditText) findViewById(R.id.msgToBeSentEditText);
        msgListView= (ListView) findViewById(R.id.messagesListView);
        arrayAdapter=new ArrayAdapter<String>(ChatActivity.this,
                android.R.layout.simple_list_item_1,msgList);
        msgListView.setAdapter(arrayAdapter);

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
                msgList.add("You: "+msgToBeSent);
                arrayAdapter.notifyDataSetChanged();
                msgEditText.setText("");
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                ChatMsg msg1=new ChatMsg(date,ChatMsg.CHAT,Client.sender,recipient,msgToBeSent,null);

//                msgToBeSent=recipient+" DATA "+Client.sender+": "+msgToBeSent;
                SendMsgAsyncTask sendMsgAsyncTask=new SendMsgAsyncTask();
                sendMsgAsyncTask.execute(msg1);
            }
        });
        receiveMsgIntent=new Intent(ChatActivity.this,ChatReceiveIntentService.class);
        startService(receiveMsgIntent);
    }
}

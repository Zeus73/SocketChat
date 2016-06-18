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

import java.util.ArrayList;
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

    public class MsgReceiver extends BroadcastReceiver{
        public static final String ACTION_RESP="com.zeus.socketchat.intent.action.MESSAGE_PROCESSED";
        @Override
        public void onReceive(Context context, Intent intent) {

            String msgRec=intent.getStringExtra("msg");

            StringTokenizer st=new StringTokenizer(msgRec);
            String byWhom=st.nextToken();
            byWhom=byWhom.substring(0,byWhom.length()-1);
            if(byWhom.equals(recipient)){
                msgList.add(msgRec);
                arrayAdapter.notifyDataSetChanged();
                setResultData(null);
                abortBroadcast();
            }


//            receiveMsgIntent=new Intent(ChatActivity.this,ChatReceiveIntentService.class);
            context.startService(receiveMsgIntent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent i=getIntent();
        recipient=i.getStringExtra("recipient");

        msgList=new ArrayList<>();
        boolean pendingMsg=i.getBooleanExtra("isPending",false);
        if(pendingMsg){
            String str=i.getStringExtra("pending");
            msgList.add(str);
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
                msgToBeSent=recipient+" DATA "+Client.sender+": "+msgToBeSent;
                SendMsgAsyncTask sendMsgAsyncTask=new SendMsgAsyncTask();
                sendMsgAsyncTask.execute(msgToBeSent);
//                sendMsgIntent=new Intent(ChatActivity.this,ChatReceiveIntentService.class);
//                sendMsgIntent.setAction(ChatReceiveIntentService.ACTION_MSG_SEND);
//                sendMsgIntent.putExtra(ChatReceiveIntentService.EXTRA_MSGTOSEND,msgToBeSent);
//                startService(sendMsgIntent);
//                Log.i("initiate","success");
            }
        });
        receiveMsgIntent=new Intent(ChatActivity.this,ChatReceiveIntentService.class);
        startService(receiveMsgIntent);
    }
}

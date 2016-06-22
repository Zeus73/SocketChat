package com.zeus.socketchat;

        import java.nio.*;
        import java.nio.channels.ServerSocketChannel;
        import java.nio.channels.SocketChannel;
        import java.util.ArrayList;
        import java.util.Vector;


        import java.io.IOException;
        import java.net.*;

public class NioServer {

    static Vector<UserDetails> users;

    private class UserDetails{
        private String username;
        private String password;
        private boolean isOnline;
        private SocketChannel socketChannel;
        public UserDetails(String clientLoginName, String clientPassword, boolean b, SocketChannel socketChannel) {
            this.username=clientLoginName;
            this.password=clientPassword;
            this.isOnline=b;
            this.socketChannel=socketChannel;
        }
    }

    public NioServer() {
        try{
            users=new Vector();
            ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(7777));
            serverSocketChannel.configureBlocking(false);
            System.out.println("The Server is Online!");
            AcceptClient newClient;
            while(true){
                SocketChannel socketChannel=serverSocketChannel.accept();
                if(socketChannel!=null){
                    //					System.out.println("New Client request!");
                    newClient=new AcceptClient(socketChannel);
                }

            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        NioServer nioServer=new NioServer();
    }




    private class AcceptClient extends Thread{

        static final int USER_VALIDATED=0;
        static final int USER_AUTH_FAILED=1;
        static final int USERNAME_ALREADY_EXISTS=2;

        SocketChannel socketChannel;
        ByteBuffer buf;


        public void run(String username){

            while(true){
                buf.clear();
                try {
                    socketChannel.read(buf);
                    buf.flip();
                    byte[] byteArray=buf.array();
                    ChatMsg chatMsg=ChatMsg.deserialize(byteArray);
                    if(chatMsg.msgType==ChatMsg.CHAT){
                        int i;
                        for(i=0;i<users.size();++i){
                            UserDetails curUser=users.get(i);
                            if(curUser.username.equals(chatMsg.recipient)){
                                if(curUser.isOnline){
                                    SocketChannel recipientSocketChannel=curUser.socketChannel;
                                    buf.rewind();
                                    recipientSocketChannel.write(buf);
                                }else{
                                    buf.clear();
                                    ChatMsg retMsg=new ChatMsg(null, ChatMsg.USER_OFFLINE, chatMsg.recipient, null, "Requested user is offline", null);
                                    buf.wrap(ChatMsg.serialize(retMsg));
                                    socketChannel.write(buf);
                                }
                                break;
                            }
                        }
                        if(i==users.size()){
                            buf.clear();
                            ChatMsg retMsg=new ChatMsg(null, ChatMsg.NO_SUCH_USER, chatMsg.recipient, null, "No such user exists", null);
                            buf.wrap(ChatMsg.serialize(retMsg));
                            socketChannel.write(buf);
                        }
                    }else if(chatMsg.msgType==ChatMsg.LIST_USERS){
                        ArrayList<String> usersList=new ArrayList<>();
                        for(int i=0;i<users.size();++i)
                            if(!users.get(i).username.equals(username))
                                usersList.add(users.get(i).username);
                        buf.clear();
                        ChatMsg retMsg=new ChatMsg(null, ChatMsg.LIST_USERS, chatMsg.sender, null, null, usersList);
                        buf.wrap(ChatMsg.serialize(retMsg));
                        socketChannel.write(buf);
                    }else if(chatMsg.msgType==ChatMsg.LOGOUT){
                        for(int i=0;i<users.size();++i){
                            UserDetails curTraversal=users.get(i);
                            if(curTraversal.username.equals(username)){
                                curTraversal.isOnline=false;
                                curTraversal.socketChannel.close();
                                curTraversal.socketChannel=null;
                                System.out.println("User "+username+" Logged out");
                                break;
                            }
                        }
                        break;		// Stop this thread
                    }

                } catch (IOException e) {
                    for(int i=0;i<users.size();++i){
                        UserDetails curTraversal=users.get(i);
                        if(curTraversal.username.equals(username)){
                            curTraversal.isOnline=false;
                            curTraversal.socketChannel=null;
                            System.out.println("User "+username+" Logged out");
                            break;
                        }
                    }
                    break; //close this thread
                }

            }
        }



        public AcceptClient(SocketChannel socketChannel){
            this.socketChannel=socketChannel;
            buf=ByteBuffer.allocate(10240);
            try {

                int bytesRead=socketChannel.read(buf);
                System.out.println(bytesRead);
                buf.flip();
                byte[] byteArray=buf.array();
                InitialiseMsg msg1= InitialiseMsg.deserialize(byteArray);

                if(msg1.isNewUser){
                    UserDetails curUserDetails=new UserDetails(msg1.username, msg1.password, true, socketChannel);
                    boolean registered=true;
                    for(int i=0;i<users.size();++i){
                        if(users.get(i).username.equals(msg1.username)){
                            registered=false;
                            break;
                        }

                    }
                    if(registered){
                        users.add(curUserDetails);
                        System.out.println("User Registered & Logged In: "+msg1.username);
                        buf.putInt(USER_VALIDATED);
                        socketChannel.write(buf);
                        start();
                    }else{
                        buf.putInt(USERNAME_ALREADY_EXISTS);
                        socketChannel.write(buf);
                        socketChannel.close();
                    }


                }else{
                    int i;
                    for(i=0;i<users.size();++i){
                        if((users.get(i).username.equals(msg1.username))&&(users.get(i).password.equals(msg1.password))){
                            System.out.println("Access granted: "+msg1.password);

                            buf.putInt(USER_VALIDATED);

                            users.get(i).isOnline=true;
                            users.get(i).socketChannel=socketChannel;
                            start();
                            break;
                        }
                    }
                    if(i==users.size()){
                        System.out.println("Invalid Login attempt by "+msg1.username);
                        buf.putInt(USER_AUTH_FAILED);
                        socketChannel.write(buf);
                        socketChannel.close();

                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }
}


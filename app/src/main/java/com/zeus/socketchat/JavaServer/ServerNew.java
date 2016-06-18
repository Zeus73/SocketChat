package com.zeus.socketchat.JavaServer;

import java.util.*;
import java.net.*;
import java.io.*;
public class ServerNew {

	static Vector<UserDetails> users;

	private class UserDetails{
		private String username;
		private String password;
		private boolean isOnline;
		private Socket socket;
		public UserDetails(String clientLoginName, String clientPassword, boolean b, Socket clientSoc) {
			this.username=clientLoginName;
			this.password=clientPassword;
			this.isOnline=b;
			this.socket=clientSoc;
		}
		public String getUsername() {
			return username;
		}
		public void setUsername(String username) {
			this.username = username;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public boolean isOnline() {
			return isOnline;
		}
		public void setOnline(boolean isOnline) {
			this.isOnline = isOnline;
		}
		public Socket getSocket() {
			return socket;
		}
		public void setSocket(Socket socket) {
			this.socket = socket;
		}
	}

	public ServerNew() throws Exception {
		// TODO Auto-generated constructor stub
		System.out.println("The Server is Online!");
		ServerSocket serverSoc=new ServerSocket(7777);
		users=new Vector<>();

		while(true){
			Socket clientSoc=serverSoc.accept();
			AcceptClient obClient=new AcceptClient(clientSoc);
		}
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		ServerNew instance=new ServerNew();
	}

	class AcceptClient extends Thread{
		Socket clientSoc;
		DataInputStream din;
		DataOutputStream dout;
		String sender;
		public AcceptClient(Socket cSoc) {
			clientSoc=cSoc;
			try {
				din=new DataInputStream(clientSoc.getInputStream());
				dout=new DataOutputStream(clientSoc.getOutputStream());

				String clientLoginName=din.readUTF();

				String clientPassword=din.readUTF();
				boolean isNewUser=din.readBoolean();

				if(isNewUser){
					UserDetails curUserDetails=new UserDetails(clientLoginName,clientPassword,true,clientSoc);
					boolean registered=true;
					for(int i=0;i<users.size();++i){
						if(users.get(i).username.equals(clientLoginName)){
							registered=false;
							break;
						}

					}
					if(registered){
						users.add(curUserDetails);
						System.out.println("User Registered & Logged In: "+clientLoginName);
						dout.writeInt(0);
						sender=clientLoginName;
						start();
					}else{
						dout.writeInt(2);
					}

				}else{
					int i;
					for(i=0;i<users.size();++i){
						if((users.get(i).getUsername().equals(clientLoginName))&&(users.get(i).getPassword().equals(clientPassword))){
							System.out.println("Access granted: "+clientLoginName);
							dout.writeInt(0);
							users.get(i).setOnline(true);
							users.get(i).setSocket(clientSoc);
							sender=clientLoginName;
							start();
							break;
						}	
					}
					if(i==users.size()){
						System.out.println("Invalid Login attempt by "+clientLoginName);
						dout.writeInt(1);
						dout.close();
						din.close();
						clientSoc.close();
					}
				}


			} catch (IOException e) {

			}
		}
		public void run(){
			while(true){
				String msgFromClient=new String();

				try {
					msgFromClient=din.readUTF();
					StringTokenizer st=new StringTokenizer(msgFromClient);
					String sendTo=st.nextToken();
					String msgType=st.nextToken();
					int iCount=0;

					if(msgType.toLowerCase().equals("listusers")){

						String ret="";
						for(int i=0;i<users.size();++i){
							ret=ret+(users.get(i).username+" ");
						}

						dout.writeUTF(ret);
					}

					else if(msgType.toLowerCase().equals("logout")){
						for(iCount=0;iCount<users.size();++iCount)
							if(users.elementAt(iCount).getUsername().equals(sendTo)){
								users.elementAt(iCount).setOnline(false);

								dout.close();
								din.close();
								users.get(iCount).getSocket().close();
								users.get(iCount).socket=null;

								System.out.println("User "+sendTo+" Logged out");
								break;
							}
						break;		//stop this thread

					}else{
						String msgBody="";
						while(st.hasMoreTokens()){
							msgBody=msgBody+" "+st.nextToken();
						}
						for(iCount=0;iCount<users.size();++iCount){
							if(users.elementAt(iCount).username.equals(sendTo)){
								//found the recipient of the msg
								if(users.get(iCount).isOnline==true){
									Socket recipientSocket=users.get(iCount).socket;

									DataOutputStream recipientDout=new DataOutputStream(recipientSocket.getOutputStream());
									recipientDout.writeUTF(msgBody);
									break;

								}else{

									dout.writeUTF("The Selected Recipient "+sendTo+" is Offline");
									break;

								}
								
							}
						}

						if(iCount==users.size()){
							//recipient not found

							dout.writeUTF("The Selected Recipient "+sendTo+" does not Exist");

						}
					}



				} catch (IOException e) {
					for(int i=0;i<users.size();++i){
						if(users.get(i).username.equals(sender)){
							users.get(i).isOnline=false;
							System.out.println(sender+" logged out");
						}
					}
					break; //close this thread

				}catch (Exception e){
					e.printStackTrace();
					break;

				}




			}
		}
	}

}
//

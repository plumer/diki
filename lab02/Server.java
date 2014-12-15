package lab02;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.*;
public class Server {
	private static Database db = new Database();

	public static void main( String[] args ) {
		try {
			ServerSocket serverSocket = new ServerSocket(23333);
			int clientNo = 1;
			while (true) {
				Socket connectToClient = serverSocket.accept();
				System.out.println("Start thread for client " + clientNo);
				InetAddress clientInetAddress = connectToClient.getInetAddress();
				
				System.out.println("Client " + clientNo + "\'s hostname is " +
						clientInetAddress.getHostName());

				System.out.println("Client " + clientNo + "\'s IP Address is " +
						clientInetAddress.getHostAddress());
				System.out.println("Client " + clientNo + "\'s port is " +
						connectToClient.getPort());
				HandleTask task = new HandleTask(connectToClient);
				new Thread(task).start();

				clientNo++;
			}
		} catch (IOException ex) {
			System.err.println(ex);
		}
	}

	public static class HandleTask implements Runnable {
		private Socket connectionSocket;
		private String currentUserName;
		private int transactionCounter = 0;

		public HandleTask(Socket socket) {
			this.connectionSocket = socket;
		}
		
		public void run() {
			try {
				DataInputStream isFromClient = new DataInputStream(
						connectionSocket.getInputStream());
				DataOutputStream osToClient = new DataOutputStream(
						connectionSocket.getOutputStream());
				while (true) {
					String buf = isFromClient.readUTF();
					String [] s = buf.substring(3).split("\\^");
					String opcode = buf.substring(1, 3);
					log("info", "opcode: \"" + buf.substring(0, 3) + "\"\n"+
						s.length + "operands: ");
					for (int i = 0; i < s.length; ++i) 
						System.out.print("\t"+s[i]);
					System.out.print("\n");
					/*
					 * operands are stored in String array s[] (line 52)
					 * server respond to request packages depending on the opcode
					 */
					if (buf.charAt(0) == 'q') {
						if (opcode.equals("li")) {					
							// login request
							// s[0] - username, s[1] - password
							// assumption: the user is currently offline
							// if username exists in the database, and password matches
							//   return true
							// else return false
							if (s.length != 2) {
								log("error", "login request invalid");
								osToClient.writeUTF("rlifalse");
								osToClient.flush();
							} else {
								String userNames = new String();
								Set<String> onlineUserList =
									db.login(s[0], s[1],
									connectionSocket.getInetAddress(), // to be deprecated
									connectionSocket.getPort());		// to be deprecated
								if (onlineUserList == null)
									osToClient.writeUTF("rli"+false);
								else {
									Iterator<String> iterator = onlineUserList.iterator();
									while (iterator.hasNext())
										userNames = userNames + "^" + 	iterator.next();
									osToClient.writeUTF("rlitrue"+userNames);
									currentUserName = s[0];
								}
								osToClient.flush();
								log("info", "reply login: "+onlineUserList);
							}

						} else if (opcode.equals("rg")) {			
							// register request
							// s[0] - username, s[1] - password
							// if username CONFLICTS with some existing user
							//   return false
							// else 
							//   insert a new user to the database
							//   return true
							boolean result = false;
							if (s.length == 2) {
								result = db.register(s[0], s[1]);
								osToClient.writeUTF("rrg" + result);
							} else {
								log("error", "register request operands error:");
								printStringArray(s);
								log("info", "false sent back");
								osToClient.writeUTF("rrgfalse");
							}
							osToClient.flush();
							log("info", "reply register: "+result);

						} else if (opcode.equals("lo")) {
							// logout request
							// s[0] - username
							// if user exists in the database and is currently online
							//   return true
							// else return false
							log("info", "logging out request on user [" + s[0] + "]");
							boolean result = db.logout(s[0]);
							osToClient.writeUTF("rlo" + result);
							osToClient.flush();
							log("info", "reply logout: "+result);

						} else if (opcode.equals("se")) {			
							// search request
							// s[0] - keyword
							// if keyword exists in the database
							//   make an entry and return
							// else
							//   search it online and insert it into the database
							//   return the entry

							Entry result = db.request(s[0]);
							boolean [] zanFlag = new boolean[3];
							boolean [] unzanFlag = new boolean[3];
							for (int i = 0; i < 3; i++) {
								zanFlag[i] = result.getInformation(i).isZannedBy(currentUserName);
								unzanFlag[i] = result.getInformation(i).isUnzannedBy(currentUserName);
							}
							String flags =
								zanFlag[0] +"^"+ zanFlag[1] +"^"+ zanFlag[2] +"^"+ 
								unzanFlag[0] +"^"+ unzanFlag[1] +"^"+ unzanFlag[2];
							osToClient.writeUTF("rse" + result +"^"+ flags);
							osToClient.flush();
							log("info", "reply search: "+result+"^"+flags);

						} else if (opcode.equals("za")) {			
							// zan request
							// s[0] username, s[1] keyword, s[2] source
							// if this record already exists in the database
							//   return false
							// else write this record into the database
							//   return true
							log("info", "zan on word [" + s[1] + "] on source [" +
								s[2] +"] by user [" + s[0] +"]");
							boolean result = db.clickZan(s[0],s[1],s[2]);
							osToClient.writeUTF("rza" + result);
							osToClient.flush();
							log("info", "reply zan: "+result);

						} else if (opcode.equals("uz")) {			// unzan
							// similar to zan
							log("info", "unzan on word [" + s[1] + "] on source [" +
								s[2] +"] by user [" + s[0] +"]");
							boolean result = db.clickUnzan(s[0], s[1], s[2]);
							osToClient.writeUTF("ruz" + result);
							osToClient.flush();
							log("info", "reply unzan: "+result);
						} else if (opcode.equals("ou")) {			
							// get online users request
							// s[0] - requester
							// return all users that are online
							// should we exclude the requester?
							
							log("info", "request online users except [" + s[0] + "]");
							Set<String> onlineUserList = db.getOnlineUsers(null);
							String userNames = new String();
							Iterator<String> iterator = onlineUserList.iterator();
							while (iterator.hasNext())
								userNames = userNames + "^" + iterator.next();
							if (userNames.length() > 0)
								userNames = userNames.substring(1);
							osToClient.writeUTF("rou"+userNames);
							osToClient.flush();
							log("info", "reply qou: " + userNames);
						} else if (opcode.equals("sc")) {			
							// send card
							// s[0] - sender, s[1] - receiver, s[2] - keyword, s[3] - provider
							// insert this to the database....
							// BTW, [fetch cards] function unimplemented
							log("info", "sending card from [" + s[0] + "] to [" +
								s[1] + "] on word [" + s[2] + "] provided by [" + s[3] + "]");
							boolean result = db.sendCard(s[0], s[1], s[2], s[3]);
							osToClient.writeUTF("rsc" + result);
							osToClient.flush();
							log("info", "reply qsc: " + result);
						}
					} // end of if
					transactionCounter++;
				} // end of while
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} // end of function run()

		void printStringArray( String[] s) {
			for (int i = 0; i < s.length; ++i) {
				System.out.print("\t" + s[i]);
			}
		}

		void log(String type, String message) {
			System.out.println("Server #"+transactionCounter+", " + type + ": " + message);
		}
	}
	

}

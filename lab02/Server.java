package lab02;
import java.io.*;
import java.net.*;
public class Server {
	private static Database db = new Database();

	public static void main( String[] args ) {
		System.out.println("你服务器大爷开啦！");
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
				HandleTask task = new HandleTask(connectToClient, clientNo);
				new Thread(task).start();

				clientNo++;
			}
		} catch (IOException ex) {
			System.out.println("server system down!!");
			ex.printStackTrace();
		}
	}

	public static class HandleTask implements Runnable {
		private Socket connectionSocket;
		private String currentUserName;
		int clientNo;
		private int transactionCounter = 0;

		public HandleTask(Socket socket, int clientNo) {
			this.connectionSocket = socket;
			this.clientNo = clientNo;
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
							} else {
								String onlineUserList = null;
								if ( db.sqlNameIsExist(s[0]) ) {
									User quester = db.sqlGetUserByName(s[0]);
									if ( quester.getPassword().equals(s[1]) ) {
/* this may fail					*/	db.sqlUpdateUserStatus(s[0], User.ONLINE);
										onlineUserList = db.sqlGetOnlineUser();
										currentUserName = s[0];
										osToClient.writeUTF("rlitrue^" + onlineUserList);
									} else {
										osToClient.writeUTF("rlifalse");
										log("error", "login password incorrect");
									}
								} else {
									log("error", "login: user ["+s[0]+"] not exist");
									osToClient.writeUTF("rlifalse");
								}
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
								if ( db.sqlNameIsExist(s[0]) ) {
									log("error", "register: user ["+s[0]+"] already exists");
									osToClient.writeUTF("rrgfalse");
								} else {
									if (!db.sqlInsertUser(new User(s[0], s[1]))) {
										log("error", "register: write into database error");
										osToClient.writeUTF("rrgfalse");
									} else {
										osToClient.writeUTF("rrgtrue");
									}
								}
							} else {
								log("error", "register request operands error:");
								printStringArray(s);
								log("info", "false sent back");
								osToClient.writeUTF("rrgfalse");
							}
							log("info", "reply register: "+result);

						} else if (opcode.equals("lo")) {
							// logout request
							// s[0] - username
							// if user exists in the database and is currently online
							//   return true
							// else return false
							log("info", "logging out request on user [" + s[0] + "]");
							boolean result = false;
							if ( db.sqlNameIsExist(s[0]) ) {
								result = db.sqlUpdateUserStatus(s[0], User.OFFLINE);
								currentUserName = null;
							} else {
								log("error", "user does not exist");
							}
							osToClient.writeUTF("rlo" + result);
							log("info", "reply logout: "+result);

						} else if (opcode.equals("se")) {			
							// search request
							// s[0] - keyword
							// if keyword exists in the database
							//   make an entry and return
							// else
							//   search it online and insert it into the database
							//   return the entry

							Entry result = null;
							if ( db.sqlEntryIsExist(s[0]) ) {
								result = db.sqlGetEntry(s[0]);
								boolean [] incomplete = new boolean[3];
								boolean refetch = false;
								for (int i = 0; i < 3; i++) {
									incomplete[i] = (result.getInformation(i) == null);
									refetch = refetch | incomplete[i];
								}
								if (refetch) {
									OnlineSearcher oser = new OnlineSearcher();
									result = oser.search(s[0]);
									for (int i = 0; i < 3; i++) if (incomplete[i])
										db.sqlInsertInformation(
											result.getKeyword(),
											result.getInformation(i)
										);
								}
							} else {
								OnlineSearcher oser = new OnlineSearcher();
								result = oser.search(s[0]);
								db.sqlInsertEntry(result);
							}
							boolean [] zanFlag = new boolean[3];
							boolean [] unzanFlag = new boolean[3];
							for (int i = 0; i < 3; i++) {
								zanFlag[i] = db.sqlZanlogIsExist(currentUserName, s[0], Entry.sourceString[i]);
								unzanFlag[i] = db.sqlUnzanlogIsExist(currentUserName, s[0], Entry.sourceString[i]);
							}
							String flags =
								zanFlag[0] +"^"+ zanFlag[1] +"^"+ zanFlag[2] +"^"+ 
								unzanFlag[0] +"^"+ unzanFlag[1] +"^"+ unzanFlag[2];
							osToClient.writeUTF("rse" + result +"^"+ flags);
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
							boolean result = false;
							if ( !db.sqlZanlogIsExist(s[0], s[1], s[2]) ) {
								result = db.sqlInsertZanlog(s[1], s[0], s[2]);
							}
							osToClient.writeUTF("rza" + result);
							log("info", "reply zan: "+result);

						} else if (opcode.equals("uz")) {			// unzan
							// similar to zan
							log("info", "unzan on word [" + s[1] + "] on source [" +
								s[2] +"] by user [" + s[0] +"]");
							boolean result = false;
							if ( !db.sqlUnzanlogIsExist(s[0], s[1], s[2]) ) {
								result = db.sqlInsertUnzanlog(s[1], s[0], s[2]);
							}
							osToClient.writeUTF("ruz" + result);
							log("info", "reply unzan: "+result);
						} else if (opcode.equals("ou")) {			
							// get online users request
							// s[0] - requester
							// return all users that are online
							// should we exclude the requester?
							log("info", "request online users except [" + s[0] + "]");
							String onlineUserList = db.sqlGetOnlineUser().replaceAll("\\$", "\\^");
							
							osToClient.writeUTF("rou"+onlineUserList);
							log("info", "reply qou: " + onlineUserList);
						} else if (opcode.equals("sc")) {			
							// send card
							// s[0] - sender, s[1] - receiver, s[2] - keyword, s[3] - provider
							// insert this to the database....
							// BTW, [fetch cards] function unimplemented
							log("info", "sending card from [" + s[0] + "] to [" +
								s[1] + "] on word [" + s[2] + "] provided by [" + s[3] + "]");
							boolean result = false;
							if ( !db.sqlCardIsExist(s[0], s[1], s[2], s[3]) ) {
								result = db.sqlInsertCard(s[0], s[2], s[2], s[3]);
							} else {
								log("error", "send card log already exists");
							}
							osToClient.writeUTF("rsc" + result);
							log("info", "reply qsc: " + result);
						} else if (opcode.equals("gc")) {
							// get all cards
							// s[0] - sender
							String cardsInfo = db.sqlGetMyCard(s[0]);
							osToClient.writeUTF("rgc"+cardsInfo);
							log("info", "reply to get card request: " + cardsInfo);
						}
					} // end of if
					osToClient.flush();
					transactionCounter++;
				} // end of while
			} catch (Exception ex) {
				ex.printStackTrace();
				if (currentUserName != null) {
					db.sqlUpdateUserStatus(currentUserName, User.OFFLINE);
				}
			}
		} // end of function run()

		void printStringArray( String[] s) {
			for (int i = 0; i < s.length; ++i) {
				System.out.print("\t" + s[i]);
			}
		}

		void log(String type, String message) {
			System.out.println("[Server] Client #" + clientNo +
			" trans #" + transactionCounter+", " + type + ": " + message);
		}
	}
	

}

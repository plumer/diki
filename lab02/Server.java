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
					log("info", "opcode: \"" + buf.substring(0, 3) + "\"\noperands: ");
					for (int i = 0; i < s.length; ++i) 
						System.out.print("\t"+s[i]);

					if (buf.charAt(0) == 'q') {
						if (opcode.equals("li")) {					// login

							String userNames = new String();
							Set<String> onlineUserList =
								db.login(s[0], s[1],
								connectionSocket.getInetAddress(),
								connectionSocket.getPort());
							if (onlineUserList == null)
								osToClient.writeUTF("rli"+false);
							else {
								Iterator<String> iterator = onlineUserList.iterator();
								while (iterator.hasNext())
									userNames = userNames + "^" + iterator.next();
								osToClient.writeUTF("rlitrue"+userNames);
								currentUserName = s[0];
							}
							log("info", "reply login: "+onlineUserList);

						} else if (opcode.equals("rg")) {			// register

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
							log("info", "reply register: "+result);

						} else if (opcode.equals("lo")) {			// logout

							boolean result = db.logout(s[0]);
							osToClient.writeUTF("rlo" + result);
							log("info", "reply logout: "+result);

						} else if (opcode.equals("se")) {			// search

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
							log("info", "reply search: "+result+"^"+flags);

						} else if (opcode.equals("za")) {			// zan

							boolean result = db.clickZan(s[0],s[1],s[2]);
							osToClient.writeUTF("rza" + result);
							log("info", "reply zan: "+result);

						} else if (opcode.equals("uz")) {			// unzan

							boolean result = db.clickUnzan(s[0], s[1], s[2]);
							osToClient.writeUTF("ruz" + result);
							log("info", "reply unzan: "+result);
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

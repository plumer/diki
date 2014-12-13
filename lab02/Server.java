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
							}
							System.out.println("reply login: "+onlineUserList);
						} else if (opcode.equals("rg")) {			// register
							boolean result = db.register(s[0], s[1]);
							osToClient.writeUTF("rrg" + result);
							System.out.println("reply register: "+result);
						} else if (opcode.equals("lo")) {			// logout
							boolean result = db.logout(s[0]);
							osToClient.writeUTF("rlo" + result);
							System.out.println("reply logout: "+result);
						} else if (opcode.equals("se")) {			// search
							String result = db.request(s[0]);
							osToClient.writeUTF("rse" + result);
							System.out.println("reply search: "+result);
						} else if (opcode.equals("za")) {			// zan
							boolean result = db.clickZan(s[0],s[1],s[2]);
							osToClient.writeUTF("rza" + result);
							System.out.println("reply zan: "+result);
						} else if (opcode.equals("uz")) {			// unzan
							boolean result = db.clickUnzan(s[0], s[1], s[2]);
							osToClient.writeUTF("ruz" + result);
							System.out.println("reply unzan: "+result);
						}
					}

				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	

}

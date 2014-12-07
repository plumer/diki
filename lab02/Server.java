package lab02;

import java.io.*;
import java.net.*;

public class Server {
	private Database db = new Database;

	public static void main( String[] args ) {
		try {
			ServerSocket serverSocket = new ServerSocket(23333);
			int clientNo = 1;
			while (true) {
				Socket connectToClient = serverSocket.accept();
				System.out.println("Start thread for client " + clientNo);
				InetAddress clientInetAddress = connectToClient.getInetAddress();
				
				System.out.println("Client " + clientNo + "\'s hostname is " +
						clientInetAddresss.getHostName());
				System.out.println("Client " + clientNo + "\'s IP Address is " +
						clientInetAddress.getHostAaddress());
				System.out.println("Client " + clientNo + "\'s port is " +
						clientInetAddress.getPort());

				clientNo++;
			}
		} catch (IOException ex) {
			System.err.println(ex);
		}
	}
}

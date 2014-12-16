/**
 * 
 */
package lab02;

import java.net.*;
/**
 * @author Diki
 * removed user id field [since it seems useless]
 * changed constructor
 */
class User {
	public static boolean ONLINE = true;
	public static boolean OFFLINE = false;
	private boolean status;
	private String name;
	private String password;
	private InetAddress ip;
	private int port;
	
	User(String name, String password){
		this.name = name;
//		this.status = status;
		this.password = password;
		// only password and name is necessary in creating (i.e. registering) a new user
//		this.ip = ip;
//		this.port = port;
	}
	/** 增加了一个完全的构造函数 */
	User(String name, String password, InetAddress ip, int port, boolean status) {
		this.name = name;
		this.password = password;
		this.ip = ip;
		this.port = port;
		this.status = status;
	}

	/**
	 * @return the status
	 * suggestion: change method name to isOnline()
	 */
	boolean isOnline() {
		return status;
	}

	/**
	 * @param status the status to set
	 * suggestion: split setStatus() method into two methods:
	 * login() and logout()
	 * suggestion adopted in Server
	 */
	void setStatus(boolean status) {
		this.status = status;
	}

	/**
	 * @return the name
	 */
	String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the password
	 */
	String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the ip
	 */
	InetAddress getIp() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	void setIp(InetAddress ip) {
		this.ip = ip;
	}

	/**
	 * @return the port
	 */
	int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	void setPort(int port) {
		this.port = port;
	}
	
	public String toString() {
		return name + '#' + password + '#' + ip.toString() + '#' + Integer.toString(port) + '#' + status;
	}
}

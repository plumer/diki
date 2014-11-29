/**
 * 
 */
package lab02;

import java.net.*;
/**
 * @author Diki
 *
 */
class User {
	private int id;
	private boolean status;
	private String name;
	private String password;
	private Inet4Address ip;
	private int port;
	
	User(int id, boolean status, String name, String password, Inet4Address ip, int port){
		this.id = id;
		this.status = status;
		this.password = password;
		this.ip = ip;
		this.port = port;
	}
	
	/**
	 * @return the id
	 */
	int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the status
	 * suggestion: change method name to isOnline()
	 */
	boolean isStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 * suggestion: split setStatus() method into two methods:
	 * login() and logout()
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
	Inet4Address getIp() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	void setIp(Inet4Address ip) {
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
}

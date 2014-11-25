/**
 * 
 */
package lab02;

import java.net.*;
/**
 * @author diki
 *
 */
class User {
	private int id;
	private boolean status;	// 看这里! 我嫌名字太长改了!
	private String name;
	private String password;
	private Inet4Address ip;	// 看这里! 我嫌大小写不统一改了!
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
	 */
	boolean isStatus() {
		return status;
	}

	/**
	 * @param status the status to set
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

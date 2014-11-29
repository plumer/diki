/**
 * 
 */
package lab02;

/**
 * @author diki
 *
 */

import java.net.*;

class Server {
	private final int MAX_USER_NUMBER = 128;
	private final int MAX_ENTRY_NUMBER = 65536;
	private User[] userDB = new User[MAX_USER_NUMBER];
	private Entry[] entryDB = new Entry[MAX_USER_NUMBER];

	private static void init() {

	}
	
	public static void main() {
		init();
		/*
		 * while (true)
		 *   receive a packet from user port
		 *   get type from packet content
		 *   switch (type)
		 *     register: a new
		 */
	}



	// invoke me when register request is received
	private boolean register(String userName, String password) {
        /*
         * if the username exists in the userDB
         *   return false
         * else
         *   add to userDB
         *   return true
         */
		return false;
	}

	// invoke me when login request is received
	private boolean login(String userName, String password, Inet4Address ip, int port) {
        /*
         * if the username exists int userDB
         *   check password
         *   if match
         *     modify user status,ip,port
         *     return true
         * return false
         */
		return false;
	}

	private boolean logout(String userName) {
        /*
         * if the username exists in the userDB
         *   modify user status
         *   return true
         * else
         *   return false
         */
		return false;
	}

	//
	private String request(String keyword) {
		/*
		 *
		 */
		return null;
	}


	private boolean clickZan(String userName, String keyword, String source) {
		/*
		 * find the entry according to the keyword
		 * allocate the source
		 * if the userName exists in the zanList
		 *   return false
		 * else
		 *   add number of zan
		 *   add the userName into the zanList
		 *   return true
		 */
		return false;
	}

	private boolean clickUnzan(String userName, String keyword, String source) {
		/*
		 * find the entry according to the keyword
		 * allocate the source
		 * if the userName exists in the unzanList
		 *   return false
		 * else
		 *   add number of unzan
		 *   add the userName into the unzanList
		 *   return true
		 */
		return false;
	}

	private boolean sendCard(String sourceUser, String destinationUser, String keyword, String source) {
		/*
		 * find the entry according to the keyword
		 * new Card with sourceUser and keyword and source
		 * send to destinationUser
		 * return true
		 */
		return false;
	}
}
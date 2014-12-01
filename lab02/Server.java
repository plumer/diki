/**
 * 
 */
package lab02;

/**
 *	@author diki
 *	modified by vocryan Dec 1 2014 19:30
 *		implemented methods:
 *			register
 *			login, logout
 *			clickZan, clickUnzan
 * 		remain unimplemented:
 *			search for an Explanation
 *			send card
 */

import java.net.*;
import java.util.*;

class Server {
	private final int MAX_USER_NUMBER = 128;
	private final int MAX_ENTRY_NUMBER = 65536;
	private HashMap<String, User> userDB = new HashMap<String, User>();
	private HashMap<String, Entry> entryDB = new HashMap<String, Entry>();

	private static void init() {

	}
	
	public static void main() {
		init();
		/**
		 * while (true)
		 *   receive a packet from user port
		 *   get type from packet content
		 *   switch (type)
		 *     register: a new
		 */
	}



	// invoke me when register request is received
	private boolean register(String userName, String password) {
        /**
         * if the username exists in the userDB
         *   return false
         * else
         *   add to userDB
         *   return true
         */
        if (userDB.get(userName) == null) {
			userDB.put(userName, new User(userName, password));
			return true;
		} else {
			return false;
		}
	}

	// invoke me when login request is received
	private boolean login(String userName, String password, Inet4Address ip, int port) {
        /**
         * if the username exists int userDB
         *   check password
         *   if match
         *     modify user status,ip,port
         *     return true
         * return false
         */
        User quester = userDB.get(userName);
        if (quester != null && quester.getPassword() == password) {
			quester.setStatus(ONLINE);
			quester.setIp(ip);
			quester.setPort(port);
		} else {
			return false;
		}
	}

	private boolean logout(String userName) {
        /**
         * if the username exists in the userDB
         *   modify user status
         *   return true
         * else
         *   return false
         */
        User quester = userDB.get(userName);
        if (quester != null) {
			quester.setStatus(OFFLINE);
			return true;
		} else {
			return false;
		}
	}

	//
	private String request(String keyword) {
		/**
		 *
		 */
		Entry entry = entryDB.get(keyword);
		if (entry == null)
			// yes!
		else
			// uh-oh. we have to search the Internet.
		return null;
	}


	private boolean clickZan(String userName, String keyword, String source) {
		/**
		 * find the entry according to the keyword
		 * locate the source
		 * if the userName exists in the zanList
		 *   return false
		 * else
		 *   add number of zan
		 *   add the userName into the zanList
		 *   return true
		 * !! checking zanList is done in Vote
		 */
		User devil = userDB.get(userName);
		if ( devil == null )
			return false;
		Entry entry = entryDB.get(keyword);
		if ( entry == null )
			return false;
		return entry.getInformation(source).clickZan(UserName);
	}

	private boolean clickUnzan(String userName, String keyword, String source) {
		/**
		 * find the entry according to the keyword
		 * allocate the source
		 * if the userName exists in the unzanList
		 *   return false
		 * else
		 *   add number of unzan
		 *   add the userName into the unzanList
		 *   return true
		 */
		User devil = userDB.get(userName);
		if ( devil == null )
			return false;
		Entry entry = entryDB.get(keyword);
		if ( entry == null )
			return false;
		return entry.getInformation(source).clickUnzan(userName);
		return false;
	}

	private boolean sendCard(String sourceUser, String destinationUser, String keyword, String source) {
		/**
		 * find the entry according to the keyword
		 * new Card with sourceUser and keyword and source
		 * send to destinationUser
		 * return true
		 */
		Entry entry = entryDB.get(keyword);
		if (entry == null)
			return false;
		Card card = new Card(keyword, sourceUser, source);
		
		return false;
	}
}
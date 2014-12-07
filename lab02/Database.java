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
 *			search
 * 		remain unimplemented:
 *			send card
 */

import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;
import java.sql.*;

class Database {
	private final int MAX_USER_NUMBER = 128;
	private final int MAX_ENTRY_NUMBER = 65536;
	private HashMap<String, User> userDB = new HashMap<String, User>();
	private HashMap<String, Entry> entryDB = new HashMap<String, Entry>();

	public static void main(String[] args) {
		// only for testing
		Database db = new Database();
		db.request("document");

		// database test
		try {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				Connection conn = DriverManager
						.getConnection(
								"jdbc:mysql://127.0.0.1:3306/java_try?useUnicode=true&characterEncoding=UTF-8",
								"root", "");
				if (!conn.isClosed())
					System.out.println("connect ok!");
				Statement statement = conn.createStatement();
				/*
				 * statement.execute(
				 * "create table Employee(ename char(20),eno int primary key NOT NULL,edate date,eaddr char(50),esalary float,emgno int,edno int)default charset=utf8;"
				 * ); statement.execute(
				 * "insert into Employee values('吴欢', 12001, '1980-10-01', '南京', 100000, null, null);"
				 * ); statement.execute(
				 * "insert into Employee values('王伟', 12002, '1981-10-02', '杭州', 100000, null, null);"
				 * ); statement.execute(
				 * "insert into Employee values('王芳', 12003, '1980-10-03', '重庆', 100000, null, null);"
				 * ); statement.execute(
				 * "insert into Employee values('李伟', 12004, '1980-10-04', '哈尔滨', 100000, null, null);"
				 * ); statement.execute(
				 * "insert into Employee values('王静', 12005, '1980-10-05', '嘉兴', 100000, null, null);"
				 * );
				 */
				ResultSet rs = statement
						.executeQuery("select * from Employee;");
				while (rs.next()) {
					System.out.println(rs.getString(1) + "\t" + rs.getString(2)
							+ "\t" + rs.getString(3) + "\t" + rs.getString(4)
							+ "\t" + rs.getString(5));
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// invoke me when register request is received
	public boolean register(String userName, String password) {
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
	public boolean login(String userName, String password, InetAddress ip, int port) {
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
			quester.setStatus(User.ONLINE);
			quester.setIp(ip);
			quester.setPort(port);
			return true;
		} else {
			return false;
		}
	}

	public boolean logout(String userName) {
        /**
         * if the username exists in the userDB
         *   modify user status
         *   return true
         * else
         *   return false
         */
        User quester = userDB.get(userName);
        if (quester != null) {
			quester.setStatus(User.OFFLINE);
			return true;
		} else {
			return false;
		}
	}

	//
	public String request(String keyword) {
		/**
		 *
		 */
		String[] buf1;
		String[] buf2;
		OnlineSearcher oser = new OnlineSearcher();
		Entry result = oser.search(keyword);

/*		Information info = result.getInformation("baidu");
		System.out.println(info.getSource() + " " + info.getZan() + " likes " + info.getUnzan() + " unlikes");
		buf1 = info.getPhonetic().split("#");
		for (int i = 0; i < buf1.length; ++i) {
			System.out.print(buf1[i] + "\t");
		}
		System.out.print("\n");
		buf1 = info.getAttribute().split("#");
		buf2 = info.getExplanation().split("#");
		if (buf1.length != buf2.length) {
			System.out.println("uh-oh");
		} else {
			for (int i = 0; i < buf1.length; ++i)
				System.out.println("\t" + buf1[i] + "\t" + buf2[i]);
		}

		info = result.getInformation("youdao");
		System.out.println(info.getSource() + " " + info.getZan() + " likes "
				+ info.getUnzan() + " unlikes");
		buf1 = info.getPhonetic().split("#");
		for (int i = 0; i < buf1.length; ++i) {
			System.out.print(buf1[i] + "\t");
		}
		System.out.print("\n");
		buf1 = info.getAttribute().split("#");
		buf2 = info.getExplanation().split("#");
		if (buf1.length != buf2.length) {
			System.out.println("uh-oh");
		} else {
			for (int i = 0; i < buf1.length; ++i)
				System.out.println("\t" + buf1[i] + "\t" + buf2[i]);
		}

		info = result.getInformation("bing");
		System.out.println(info.getSource() + " " + info.getZan() + " likes "
				+ info.getUnzan() + " unlikes");
		buf1 = info.getPhonetic().split("#");
		for (int i = 0; i < buf1.length; ++i) {
			System.out.print(buf1[i] + "\t");
		}
		System.out.print("\n");
		buf1 = info.getAttribute().split("#");
		buf2 = info.getExplanation().split("#");
		if (buf1.length != buf2.length) {
			System.out.println("uh-oh");
		} else {
			for (int i = 0; i < buf1.length; ++i)
				System.out.println("\t" + buf1[i] + "\t" + buf2[i]);
		}
		*/
		return result.toString();
	}

	public boolean clickZan(String userName, String keyword, String source) {
		/**
		 * find the entry according to the keyword locate the source if the
		 * userName exists in the zanList return false else add number of zan
		 * add the userName into the zanList return true !! checking zanList is
		 * done in Vote
		 */
		User devil = userDB.get(userName);
		if (devil == null)
			return false;
		Entry entry = entryDB.get(keyword);
		if (entry == null)
			return false;
		return entry.getInformation(source).clickZan(userName);
	}

	public boolean clickUnzan(String userName, String keyword, String source) {
		/**
		 * find the entry according to the keyword allocate the source if the
		 * userName exists in the unzanList return false else add number of
		 * unzan add the userName into the unzanList return true
		 */
		User devil = userDB.get(userName);
		if (devil == null)
			return false;
		Entry entry = entryDB.get(keyword);
		if (entry == null)
			return false;
		return entry.getInformation(source).clickUnzan(userName);
	}

	public boolean sendCard(String sourceUser, String destinationUser, String keyword, String source) {
		/**
		 * find the entry according to the keyword new Card with sourceUser and
		 * keyword and source send to destinationUser return true
		 */
		Entry entry = entryDB.get(keyword);
		if (entry == null)
			return false;
		Card card = new Card(keyword, sourceUser, source);

		return false;
	}
}

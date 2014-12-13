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

import java.io.*;
import java.net.*;
import java.util.*;
import java.sql.*;

class Database {
	private Connection connect;

	public Database() {
		// 做了下面这些修改: 
		// 1. 数据库地址默认为本地的127.0.0.1:3306, 但是可以通过构造函数设置为其他的
		// 2. 数据库为test, user为root, 密码为空[mysql的默认设置]
		// 3. 数据库编码为utf8
		String host = "127.0.0.1:3306";
		String database = "test";
		String user = "root";
		String password = "";
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager
					.getConnection(
							"jdbc:mysql://" + host + "/test?useUnicode=true&characterEncoding=UTF-8",
							user, password);
			// 检查连接是否成功
			if (!connect.isClosed()) {
				//System.out.println("connect ok!");
				Statement stm = connect.createStatement();
				
				// 检查entry表是否存在
				// entry表: keyword
				ResultSet rs = stm.executeQuery("select * from user_all_tables where table_name = 'entry';");
				if (!rs.next()) {
					stm.execute("create table entry(keyword char(50) primary key not null)default charset=utf8;");
				}
				// 检查information表是否存在
				// information表: keyword | source | phonetic | attribute | zancount | unzancount
				rs = stm.executeQuery("select * from user_all_tables where table_name = 'information';");
				if (!rs.next()) {
					stm.execute("create table information(keyword char(50) primary key not null references entry(keyword), source char(20) not null, phonetic char(20), attribute char(20), explanation char(200), zan int, unzan int)default charset=utf8;");
				}
				// 检查user表是否存在
				// user表: username | passowrd | ip | port | status
				rs = stm.executeQuery("select * from user_all_tables where table_name = 'user';");
				if (!rs.next()) {
					stm.execute("create table user(username char(20) primary key not null, password char(20) not null, ip char(20), port int, status bit);");
				}
				// 检查zanlog表是否存在
				// zanlog表: keyword | username | source
				rs = stm.executeQuery("select * from user_all_tables where table_name = 'zanlog';");
				if (!rs.next()) {
					stm.execute("create table zanlog(keyword char(50) references entry(keyword), username char(20) references user(username), source char(20) references information(source))default charset=utf8;");
				}
				// 检查unzanlog表是否存在
				// unzanlog表: keyword | username | source
				rs = stm.executeQuery("select * from user_all_tables where table_name = 'unzanlog';");
				if (!rs.next()) {
					stm.execute("create table unzanlog(keyword char(50) references entry(keyword), username char(20) references user(username), source char(20) references information(source))default charset=utf8;");
				}
			} else {
				System.out.println("error connect to database!(check whether database " + database + " is exist!)");
				throw(new SQLException());
			}
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Database(String host, String database, String user, String password) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager
					.getConnection(
							"jdbc:mysql://" + host + "/test?useUnicode=true&characterEncoding=UTF-8",
							user, password);
			// 检查连接是否成功
			if (!connect.isClosed()) {
				//System.out.println("connect ok!");
				Statement stm = connect.createStatement();
				
				// 检查entry表是否存在
				// entry表: keyword
				ResultSet rs = stm.executeQuery("select * from user_all_tables where table_name = 'entry';");
				if (!rs.next()) {
					stm.execute("create table entry(keyword char(50) primary key not null)default charset=utf8;");
				}
				// 检查information表是否存在
				// information表: keyword | source | phonetic | attribute | zancount | unzancount
				rs = stm.executeQuery("select * from user_all_tables where table_name = 'information';");
				if (!rs.next()) {
					stm.execute("create table information(keyword char(50) primary key not null references entry(keyword), source char(20) not null, phonetic char(20), attribute char(20), explanation char(200), zan int, unzan int)default charset=utf8;");
				}
				// 检查user表是否存在
				// user表: username | passowrd | ip | port | status
				rs = stm.executeQuery("select * from user_all_tables where table_name = 'user';");
				if (!rs.next()) {
					stm.execute("create table user(username char(20) primary key not null, password char(20) not null, ip char(20), port int, status bit);");
				}
				// 检查zanlog表是否存在
				// zanlog表: keyword | username | source
				rs = stm.executeQuery("select * from user_all_tables where table_name = 'zanlog';");
				if (!rs.next()) {
					stm.execute("create table zanlog(keyword char(50) references entry(keyword), username char(20) references user(username), source char(20) references information(source))default charset=utf8;");
				}
				// 检查unzanlog表是否存在
				// unzanlog表: keyword | username | source
				rs = stm.executeQuery("select * from user_all_tables where table_name = 'unzanlog';");
				if (!rs.next()) {
					stm.execute("create table unzanlog(keyword char(50) references entry(keyword), username char(20) references user(username), source char(20) references information(source))default charset=utf8;");
				}
			} else {
				System.out.println("error connect to database!(check whether database " + database + " is exist!)");
				throw(new SQLException());
			}
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	boolean insertEntry(Entry entry) {
		
		return false;
	}
	
	boolean insertUser(User user) {
		return false;
	}

	User[] getAllUser() {//是User[]好呢, 还是String[]好呢?
		return null;
	}
	
	User[] getOnlineUser() {//是User[]好呢, 还是String[]好呢?
		return null;
	}
	
	User getUser(String username) {
		return null;
	}
	
	String[] getZanList(String keyword, String source) {
		return null;
	}
	
	String[] getUnzanList(String keyword, String source) {
		return null;
	}

	Entry getEntry(String keyword) {
		return null;
	}

	boolean insertZanLog(String name, String keyword, String source) {
		return false;
	}
	
	boolean insertUnzanLog(String name, String keyword, String source) {
		return false;
	}

//	public static void main(String[] args) {
//		// only for testing
//		Database db = new Database();
//		db.request("document");
//
//		// database test
//		/*
//		 * try { try { Class.forName("com.mysql.jdbc.Driver"); Connection conn =
//		 * DriverManager .getConnection(
//		 * "jdbc:mysql://127.0.0.1:3306/java_try?useUnicode=true&characterEncoding=UTF-8"
//		 * , "root", ""); if (!conn.isClosed())
//		 * System.out.println("connect ok!"); Statement statement =
//		 * conn.createStatement(); ResultSet rs = statement
//		 * .executeQuery("select * from Employee;"); while (rs.next()) {
//		 * System.out.println(rs.getString(1) + "\t" + rs.getString(2) + "\t" +
//		 * rs.getString(3) + "\t" + rs.getString(4) + "\t" + rs.getString(5)); }
//		 * } catch (ClassNotFoundException e) { // TODO Auto-generated catch
//		 * block e.printStackTrace(); } } catch (SQLException e) { // TODO
//		 * Auto-generated catch block e.printStackTrace(); }
//		 */
//	}
//
//	// invoke me when register request is received
//	public boolean register(String userName, String password) {
//		/**
//		 * if the username exists in the userDB return false else add to userDB
//		 * return true
//		 */
//		if (userDB.get(userName) == null) {
//			userDB.put(userName, new User(userName, password));
//			return true;
//		} else {
//			return false;
//		}
//	}
//
//	// invoke me when login request is received
//	public boolean login(String userName, String password, InetAddress ip,
//			int port) {
//		/**
//		 * if the username exists int userDB check password if match modify user
//		 * status,ip,port return true return false
//		 */
//		User quester = userDB.get(userName);
//		if (quester != null && quester.getPassword() == password) {
//			quester.setStatus(User.ONLINE);
//			quester.setIp(ip);
//			quester.setPort(port);
//			return true;
//		} else {
//			return false;
//		}
//	}
//
//	public boolean logout(String userName) {
//		/**
//		 * if the username exists in the userDB modify user status return true
//		 * else return false
//		 */
//		User quester = userDB.get(userName);
//		if (quester != null) {
//			quester.setStatus(User.OFFLINE);
//			return true;
//		} else {
//			return false;
//		}
//	}
//
//	//
//	public String request(String keyword) {
//		/**
//		 *
//		 */
//		String[] buf1;
//		String[] buf2;
//		OnlineSearcher oser = new OnlineSearcher();
//		Entry result = oser.search(keyword);
//
//		/*
//		 * Information info = result.getInformation("baidu");
//		 * System.out.println(info.getSource() + " " + info.getZan() + " likes "
//		 * + info.getUnzan() + " unlikes"); buf1 =
//		 * info.getPhonetic().split("#"); for (int i = 0; i < buf1.length; ++i)
//		 * { System.out.print(buf1[i] + "\t"); } System.out.print("\n"); buf1 =
//		 * info.getAttribute().split("#"); buf2 =
//		 * info.getExplanation().split("#"); if (buf1.length != buf2.length) {
//		 * System.out.println("uh-oh"); } else { for (int i = 0; i <
//		 * buf1.length; ++i) System.out.println("\t" + buf1[i] + "\t" +
//		 * buf2[i]); }
//		 * 
//		 * info = result.getInformation("youdao");
//		 * System.out.println(info.getSource() + " " + info.getZan() + " likes "
//		 * + info.getUnzan() + " unlikes"); buf1 =
//		 * info.getPhonetic().split("#"); for (int i = 0; i < buf1.length; ++i)
//		 * { System.out.print(buf1[i] + "\t"); } System.out.print("\n"); buf1 =
//		 * info.getAttribute().split("#"); buf2 =
//		 * info.getExplanation().split("#"); if (buf1.length != buf2.length) {
//		 * System.out.println("uh-oh"); } else { for (int i = 0; i <
//		 * buf1.length; ++i) System.out.println("\t" + buf1[i] + "\t" +
//		 * buf2[i]); }
//		 * 
//		 * info = result.getInformation("bing");
//		 * System.out.println(info.getSource() + " " + info.getZan() + " likes "
//		 * + info.getUnzan() + " unlikes"); buf1 =
//		 * info.getPhonetic().split("#"); for (int i = 0; i < buf1.length; ++i)
//		 * { System.out.print(buf1[i] + "\t"); } System.out.print("\n"); buf1 =
//		 * info.getAttribute().split("#"); buf2 =
//		 * info.getExplanation().split("#"); if (buf1.length != buf2.length) {
//		 * System.out.println("uh-oh"); } else { for (int i = 0; i <
//		 * buf1.length; ++i) System.out.println("\t" + buf1[i] + "\t" +
//		 * buf2[i]); }
//		 */
//		return result.toString();
//	}
//
//	public boolean clickZan(String userName, String keyword, String source) {
//		/**
//		 * find the entry according to the keyword locate the source if the
//		 * userName exists in the zanList return false else add number of zan
//		 * add the userName into the zanList return true !! checking zanList is
//		 * done in Vote
//		 */
//		User devil = userDB.get(userName);
//		if (devil == null)
//			return false;
//		Entry entry = entryDB.get(keyword);
//		if (entry == null)
//			return false;
//		return entry.getInformation(source).clickZan(userName);
//	}
//
//	public boolean clickUnzan(String userName, String keyword, String source) {
//		/**
//		 * find the entry according to the keyword allocate the source if the
//		 * userName exists in the unzanList return false else add number of
//		 * unzan add the userName into the unzanList return true
//		 */
//		User devil = userDB.get(userName);
//		if (devil == null)
//			return false;
//		Entry entry = entryDB.get(keyword);
//		if (entry == null)
//			return false;
//		return entry.getInformation(source).clickUnzan(userName);
//	}
//
//	public boolean sendCard(String sourceUser, String destinationUser,
//			String keyword, String source) {
//		/**
//		 * find the entry according to the keyword new Card with sourceUser and
//		 * keyword and source send to destinationUser return true
//		 */
//		Entry entry = entryDB.get(keyword);
//		if (entry == null)
//			return false;
//		Card card = new Card(keyword, sourceUser, source);
//
//		return false;
//	}
}

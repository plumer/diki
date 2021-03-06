package lab02;

import java.net.*;
import java.sql.*;

class Database {
	private Connection connect;

	// 做了下面这些修改: 
	// 1. 数据库地址默认为本地的127.0.0.1:3306, 但是可以通过构造函数设置为其他的
	// 2. 数据库为test, user为root, 密码为空[mysql的默认设置]
	// 3. 数据库编码为utf8
	/** 默认构造函数 */
	public Database() {
		//String host = "127.0.0.1:3306";
		//String database = "test";
		//String user = "root";
		//String password = "thispasswordiswrong";
		this("127.0.0.1:3306","test", "root",  "");
	}

	/** 指定数据库参数的构造函数 */
	// 一般来说是 new Database("127.0.0.1:3306", "diki", "root", "");
	public Database(String host, String database, String user, String password) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager
					.getConnection(
							"jdbc:mysql://" + host + "/" + database +"?useUnicode=true&characterEncoding=UTF-8",
							user, password);
			// 检查连接是否成功
			if (!connect.isClosed()) {
				//System.out.println("connect ok!");
				Statement stm = connect.createStatement();
				
				// 检查entry表是否存在
				// entry表: keyword
				ResultSet rs = stm.executeQuery("select `table_name` from `information_schema`.`tables` where `table_schema`='" + database + "' and `TABLE_NAME`='entry';");
				if (!rs.next()) {
					stm.execute("create table entry(keyword char(50) primary key not null)default charset=utf8;");
				}
				// 检查information表是否存在
				// information表: keyword | source | phonetic | attribute | explanation | zancount | unzancount
				rs = stm.executeQuery("select `table_name` from `information_schema`.`tables` where `table_schema`='" + database + "' and `TABLE_NAME`='information';");
				if (!rs.next()) {
					stm.execute("create table information(keyword char(50) references entry(keyword), source char(20), phonetic char(50), attribute char(20), explanation char(200), zan int, unzan int, primary key(keyword, source))default charset=utf8;");
				}
				// 检查user表是否存在
				// user表: username | passowrd | ip | port | status
				rs = stm.executeQuery("select `table_name` from `information_schema`.`tables` where `table_schema`='" + database + "' and `TABLE_NAME`='user';");
				if (!rs.next()) {
					stm.execute("create table user(username char(20) primary key not null, password char(20) not null, ip char(20), port int, status bit);");
				}
				// 检查zanlog表是否存在
				// zanlog表: keyword | username | source
				rs = stm.executeQuery("select `table_name` from `information_schema`.`tables` where `table_schema`='" + database + "' and `TABLE_NAME`='zanlog';");
				if (!rs.next()) {
					stm.execute("create table zanlog(keyword char(50) references entry(keyword), username char(20) references user(username), source char(20) references information(source))default charset=utf8;");
				}
				// 检查unzanlog表是否存在
				// unzanlog表: keyword | username | source
				rs = stm.executeQuery("select `table_name` from `information_schema`.`tables` where `table_schema`='" + database + "' and `TABLE_NAME`='unzanlog';");
				if (!rs.next()) {
					stm.execute("create table unzanlog(keyword char(50) references entry(keyword), username char(20) references user(username), source char(20) references information(source))default charset=utf8;");
				}
				// 检查card表是否存在
				// card表: sender | owner | keyword | source
				rs = stm.executeQuery("select `table_name` from `information_schema`.`tables` where `table_schema`='" + database + "' and `TABLE_NAME`='card';");
				if (!rs.next()) {
					stm.execute("create table card(sender char(20) references user(username), owner char(20) references user(username), keyword char(50) references entry(keyword), source char(20) references information(source))default charset=utf8;");
				}
			} else {
				System.out.println("error connect to database!(check whether database " + database + " is exist!)");
				throw(new SQLException());
			}
		}catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** entry表插入 测试ok*/
	boolean sqlInsertEntry(Entry entry) {
		try {
			Statement stm = connect.createStatement();
			if (entry.getKeyword() == null) return false;
			else {
				stm.execute("insert into entry values('"+ entry.getKeyword() + "');");
				sqlInsertInformation(entry.getKeyword(),entry.getInformation("baidu"));
				sqlInsertInformation(entry.getKeyword(),entry.getInformation("youdao"));
				sqlInsertInformation(entry.getKeyword(),entry.getInformation("bing"));
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/** information表插入 测试ok*/
	boolean sqlInsertInformation(String keyword, Information info) {
		if (info.getAttribute() == null && info.getExplanation() == null
				&& info.getPhonetic() == null)
			return false;
		else {
			try {
				Statement stm = connect.createStatement();
				// keyword | source | phonetic | attribute | zancount |
				// unzancount
				stm.execute("insert into information values('" + keyword
						+ "', '" + info.getSource() + "', '"
						+ info.getPhonetic() + "', '" + info.getAttribute()
						+ "', '" + info.getExplanation() + "', 0, 0);");
				return true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/** user表插入 测试ok*/
	boolean sqlInsertUser(User user) {
		try {
			Statement stm = connect.createStatement();
			stm.execute("insert into user values('" + user.getName()
					+ "', '" + user.getPassword() + "', '" + user.getIp()
					+ "', '" + user.getPort() + (user.isOnline() ? "', 1);" : "', 0);"));
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/** zanlog表插入 测试ok*/
	boolean sqlInsertZanlog(String keyword, String name, String source) {
		try {
			Statement stm = connect.createStatement();
			stm.execute("insert into zanlog values('" + keyword + "', '" + name
					+ "', '" + source + "');");
			stm.execute("update information set zan = zan + 1 where keyword = '"
					+ keyword + "' and source = '" + source + "'");
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/** unzanlog表插入 测试ok*/
	boolean sqlInsertUnzanlog(String keyword, String name, String source) {
		try {
			Statement stm = connect.createStatement();
			stm.execute("insert into unzanlog values('" + keyword + "', '"
					+ name + "', '" + source + "');");
			stm.execute("update information set unzan = unzan + 1 where keyword = '"
					+ keyword + "' and source = '" + source + "'");
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/** card表插入 测试ok*/
	boolean sqlInsertCard(String sender, String owner, String keyword, String source) {
		try {
			Statement stm = connect.createStatement();
			stm.execute("insert into card values('" + sender + "', '" + owner + "', '" + keyword + "', '" + source +"')");
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/** regist 需要用到的功能 */
	// 1. sqlNameIsExist: 判断用户名是否存在
	// 2. sqlInsertUser: user表插入
	/** 判断用户名是否存在 测试ok*/
	boolean sqlNameIsExist(String username) {
		try {
			Statement stm = connect.createStatement();
			ResultSet rs = stm.executeQuery("select * from user where username = '" + username + "';");
			if (rs.next()) return true;
			else return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/** login 需要用到的功能 */
	// 1. sqlNameIsExist: 用户名不存在
	// 2. sqlGetUserByName: 如果密码校验通过[根据用户名get到完整的User对象]
	// 3. sqlUpdateUserStatus: 更新用户在线状态
	// 4. sqlGetOnlineUser: 获取在线用户名
	/** 根据用户名获取完整的用户 测试ok*/
	User sqlGetUserByName(String username) {
		User result = null;
		ResultSet rs = null;
		try {
			Statement stm = connect.createStatement();
			rs = stm.executeQuery("select * from user where username = '" + username + "';");
			if (rs.next()) { /** 注意从此处开始的变化： rs.getString（3）可能为空 */
				result = new User(rs.getString(1), rs.getString(2));
				boolean ol = rs.getBoolean(5);
				result.setStatus(ol);
				if (ol) {
					result.setIp(InetAddress.getByName(rs.getString(3)));
					result.setPort(rs.getInt(4));
				}
			}
		} catch (SQLException | UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	/** 更新用户在线/不在线状态 测试ok*/
	boolean sqlUpdateUserStatus(String username, boolean status) {
		try {
			Statement stm = connect.createStatement();
			stm.execute("update user set status = " + 
				(status?"1":"0") + " where username = '" + username + "'");
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	/** 返回所有online user[比如有两个人(hello和hi)在线, 就返回"hello#hi"] 测试ok*/
	String sqlGetOnlineUser() {
		StringBuffer result = null;
		try {
			Statement stm = connect.createStatement();
			ResultSet rs = stm.executeQuery("select username from user where status = 1;");
			while (rs.next()) {
				if (result == null) result = new StringBuffer(rs.getString(1));
				else result.append("^" + rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result.toString();
	}
	
	/** 注销需要用到的功能 */
	// 1. sqlUpdateUserStatus
	
	/** 查询需要用到的功能 */
	// 1. sqlEntryIsExist(keyword) 
	// 2. sqlGetEntry(keyword)
	// 3. 插入entry
	/** 判断Entry是否已经在数据库中 测试ok*/
	boolean sqlEntryIsExist(String keyword) {
		try {
			Statement stm = connect.createStatement();
			ResultSet rs = stm.executeQuery("select * from entry where keyword = '" + keyword + "';");
			if (rs.next()) return true;
			else return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	/** 根据关键词获取Entry 测试ok*/
	Entry sqlGetEntry(String keyword) {
		Entry result = new Entry(keyword);
		try {
			Statement stm = connect.createStatement();
			ResultSet rs = stm.executeQuery("select * from information where keyword = '" + keyword + "';");
			while (rs.next()) {
				// information表: keyword | source | phonetic | attribute | explanation | zancount | unzancount
				result.setInformation(new Information(rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getInt(6), rs.getInt(7)));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	/** 赞和不赞需要用到的功能 */
	// 1. sqlZanlogIsExist
	// 2. sqlUpdateZancount
	/** 判断zanlog记录是否存在 测试ok*/
	boolean sqlZanlogIsExist(String username, String keyword, String source) {
		try {
			Statement stm = connect.createStatement();
			ResultSet rs = stm
					.executeQuery("select * from zanlog where keyword = '"
							+ keyword + "' and username = '" + username
							+ "' and source = '" + source + "';");
			if (rs.next()) return true;
			else return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	/** 判断unzanlog记录是否存在 测试ok*/
	boolean sqlUnzanlogIsExist(String username, String keyword, String source) {
		try {
			Statement stm = connect.createStatement();
			ResultSet rs = stm
					.executeQuery("select * from unzanlog where keyword = '"
							+ keyword + "' and username = '" + username
							+ "' and source = '" + source + "';");
			if (rs.next()) return true;
			else return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	/** zan数目加1 */
	/** unzan数目加1 */
	
	/** 发卡需要用到的功能 */
	// 1. sqlCardIsExist
	// 2. sqlGetMyCard
	// 3. 插入card记录
	/** 判断这个卡是不是已经被发送过 测试ok*/
	boolean sqlCardIsExist(String sender, String owner, String keyword, String source) {
		try {
			Statement stm = connect.createStatement();
			ResultSet rs = stm
					.executeQuery("select * from card where sender = '"
							+ sender + "' and owner = '" + owner
							+ "' and keyword = '" + keyword
							+ "' and source = '" + source + "';");
			if (rs.next()) return true;
			else return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	String sqlGetCard(String sender, String owner, String keyword, String source) {
		StringBuffer result = null;
		try {
			// card表: sender | owner | keyword | source
			StringBuffer temp = new StringBuffer(sender + "^" + keyword + "^");
			Statement infostm = connect.createStatement();
			ResultSet infors = infostm
					.executeQuery("select * from information where keyword = '"
							+ keyword + "' and source = '" + source + "'");
			// information表: keyword | source | phonetic | attribute | explanation | zancount | unzancount
			if (infors.next()) {
				temp.append(new Information(infors.getString(2), infors
						.getString(3), infors.getString(4),
						infors.getString(5), infors.getInt(6), infors.getInt(7))
						.toString());
				result = new StringBuffer(temp);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result.toString();
	}
	/** 获取我的所有单词卡, 比如我有两张卡[card1#card2] card1的格式是[keyword^sender^ownder^information] 测试ok*/
	String sqlGetMyCard(String owner) {
		StringBuffer result = null;
		try {
			Statement stm = connect.createStatement();
			ResultSet rs = stm.executeQuery("select * from card where owner = '" + owner + "';");
			while (rs.next()) {
				// card表: sender | owner | keyword | source
				StringBuffer temp = new StringBuffer(rs.getString(1) + "^" + rs.getString(3) + "^");
				Statement infostm = connect.createStatement();
				ResultSet infors = infostm
						.executeQuery("select * from information where keyword = '"
								+ rs.getString(3)
								+ "' and source = '"
								+ rs.getString(4) + "'");
				// information表: keyword | source | phonetic | attribute | explanation | zancount | unzancount
				if (infors.next()) {
					temp.append(new Information(infors.getString(2), infors
							.getString(3), infors.getString(4), infors
							.getString(5), infors.getInt(6), infors
							.getInt(7)).toString());
					if (result == null) /**注意此处的变化：将外层的if放到内部*/
						result = new StringBuffer(temp);
					else
						result.append("^" + temp);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result.toString();
	}

//	public static void main(String[] args) {
//		// only for testing
//		Database db = new Database();
//		System.out.println("db is normal");
//		
//		OnlineSearcher os = new OnlineSearcher();
//		Entry entry = os.search("world");
//		db.sqlInsertEntry(entry);
//		entry = os.search("phone");
//		db.sqlInsertEntry(entry);
//		entry = os.search("great");
//		db.sqlInsertEntry(entry);
//		
//		System.out.println(db.sqlEntryIsExist("phone"));
//		System.out.println(db.sqlGetEntry("world").toString());
		
//					User user = new User("bendan", "bendan", InetAddress.getByName("192.168.1.1"), 12345, User.OFFLINE);
//					db.sqlInsertUser(user);
//					user = new User("benben", "bendan", InetAddress.getByName("192.168.1.1"), 12345, User.OFFLINE);
//					db.sqlInsertUser(user);
//					user = new User("aaa", "bendan", InetAddress.getByName("192.168.1.1"), 12345, User.OFFLINE);
//					db.sqlInsertUser(user);
//					
//					System.out.println(user.toString());
//					db.sqlUpdateUserStatus("benben", User.ONLINE);
//					db.sqlUpdateUserStatus("aaa", User.ONLINE);
//					System.out.println(db.sqlGetOnlineUser());
//		System.out.println(db.sqlGetUserByName("puhhh").toString());
//		
//		System.out.println(db.sqlNameIsExist("puhhh"));
		
//		db.sqlInsertCard("aaa", "benben", "phone", "baidu");
//		db.sqlInsertCard("aaa", "bendan", "phone", "baidu");
//		db.sqlInsertCard("puhhh", "benben", "world", "youdao");
//		db.sqlInsertCard("bendan", "benben", "great", "bing");
//		
//		db.sqlInsertZanlog("phone", "aaa", "baidu");
//		db.sqlInsertZanlog("world", "aaa", "youdao");
//		db.sqlInsertZanlog("great", "aaa", "bing");
//		db.sqlInsertZanlog("phone", "benben", "baidu");
//		
//		db.sqlInsertUnzanlog("world", "bendan", "youdao");
//		db.sqlInsertUnzanlog("world", "bendan", "bing");
//		db.sqlInsertUnzanlog("great", "benben", "youdao");
//		db.sqlInsertUnzanlog("phone", "puhhh", "youdao");
		
//		System.out.println(db.sqlZanlogIsExist("aaa", "phone", "baidu"));
//		System.out.println(db.sqlZanlogIsExist("aaa", "phone", "youdao"));
//		System.out.println(db.sqlUnzanlogIsExist("bendan", "world", "baidu"));
//		System.out.println(db.sqlUnzanlogIsExist("bendan", "world", "youdao"));
//		System.out.println(db.sqlCardIsExist("aaa", "benben", "phone", "baidu"));
//		System.out.println(db.sqlCardIsExist("aaa", "benben", "phone", "youdao"));
//		
//		System.out.println(db.sqlGetMyCard("benben"));
//	}
}

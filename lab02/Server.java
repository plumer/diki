package lab02;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;

/**
 * 服务器端
 * */
public class Server implements Runnable {
	private static Database db = new Database("127.0.0.1:3306", "diki", "root", "");
	/**
	 * 服务器端的入口函数
	 * */
	public static void main( String[] args ) throws InterruptedException {
		Server s = new Server();
		Thread serverThread = new Thread(s);
		serverThread.run();
		serverThread.join();
	}
	/**
	 * 服务器的构造函数
	 * 添加了一个相应终端Ctrl+C终端的处理线程
	 * 初始化图形化界面
	 * */
	public Server() {
		Runtime.getRuntime().addShutdownHook(new LogoutAllUsers());
		initializeUI();
	}
	/**
	 * 服务器意外终止时的处理线程类
	 * 将所有数据库中存在的在线用户下线
	 * */
	private class LogoutAllUsers extends Thread {
		public LogoutAllUsers() {
			super("logout all users");
		}
		
		public void run() {
			String [] onlineUserList = db.sqlGetOnlineUser().split("\\^");
			for (int i = 0; i < onlineUserList.length; ++i) {
				System.out.println("logging out on user [" + onlineUserList[i] + "]");
				db.sqlUpdateUserStatus(onlineUserList[i], User.OFFLINE);
			}
			System.out.println("All users logged out");
		}
	}
	/**
	 * 服务器的主运行逻辑：
	 * 侦听本机的23333号端口，
	 * 每一次连接就创建一个Socket并传给一个新的HandleTask对象，
	 * 具体服务逻辑交给HandleTask完成。
	 * @see HandleTask
	 * */
	public void run() {
		System.out.println("你服务器大爷开啦！");
		try {
			ServerSocket serverSocket = new ServerSocket(23333);
			int clientNo = 1;
			while (true) {
				Socket connectToClient = serverSocket.accept();
				mainLog("Start thread for client " + clientNo);
				InetAddress clientInetAddress = connectToClient.getInetAddress();
				
				mainLog("Client " + clientNo + "\'s hostname is " +
						clientInetAddress.getHostName());

				mainLog("Client " + clientNo + "\'s IP Address is " +
						clientInetAddress.getHostAddress());
				mainLog("Client " + clientNo + "\'s port is " +
						connectToClient.getPort());
				HandleTask task = new HandleTask(connectToClient, clientNo);
				new Thread(task).start();

				clientNo++;
			}
		} catch (IOException ex) {
			System.out.println("server system down!!");
			
			ex.printStackTrace();
		}
	}
	
	/**
	 * 服务器日志打印函数：
	 * 既打印在终端，也打印在图形化界面上。
	 * @param message 需要输出的日志消息
	 * */
	public void mainLog(String message) {
		Server.appendMessage(message+"\n");
		System.out.println(message);
	}
	
	/** 服务器端的内部类 - HandleTask：
	 * 每一个HandleTask对象对应一个和客户端的服务操作。
	 * */
	public static class HandleTask implements Runnable {
		private Socket connectionSocket;
		private String currentUserName;
		int clientNo;
		private int transactionCounter = 0;
		/**
		 * 构造器：
		 * @param socket 和客户连接所使用的套接字
		 * @param clientNo 会话编号
		 * */
		public HandleTask(Socket socket, int clientNo) {
			this.connectionSocket = socket;
			this.clientNo = clientNo;
		}
		
		/**
		 * 服务程序：
		 * 从客户端接收消息，根据消息头三个字符判断服务类型。
		 * 注册请求 - 检查是否存在相同用户名的帐号，如果是则向客户发送失败回应，否则将新用户加入数据库。
		 * 登录请求 - 检查该用户是否存在、当前是否离线、密码是否正确，全是则回应成功并且发送所有在线用户列表，否则回应失败。
		 * 下线请求 - 检查该用户是否存在、当前是否在线，全是则回应成功并且修改数据库中对应用户的状态，否则回应失败。
		 * 搜索请求 - 检查该词条是否在本机数据库存在，是则回应该单词，否则调用OnlineSearcher的搜索功能，搜到这个单词再回应，并且写入本机数据库。
		 * 点赞请求 - 检查用户是否存在、单词是否存在，是则返回写入点赞记录的结果，否则返回失败。
		 * 拍砖请求 - 类似点赞请求。
		 * 发卡请求 - 检查发送方用户和接收方用户是否存在，是则返回写入发卡记录的结果，否则返回失败。
		 * 获取卡片请求 - 返回该用户所有的单词卡。
		 * @see OnlineSearcher#search(String)
		 * */
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
					log("info", "opcode: \"" + buf.substring(0, 3) + "\"\n"+
						s.length + "operands: ");
					printStringArray(s);
					/**
					 * operands are stored in String array s[] (line 52)
					 * server respond to request packages depending on the opcode
					 */
					if (buf.charAt(0) == 'q') {
						if (opcode.equals("li")) {					
							/** login request
								s[0] - username, s[1] - password
								assumption: the user is currently offline
								if username exists in the database, and password matches
									return true
								else return false
							*/
							if (s.length != 2) {
								log("error", "login request invalid");
								osToClient.writeUTF("rlifalse");
							} else {
								String onlineUserList = null;
								if ( db.sqlNameIsExist(s[0]) ) {
									User quester = db.sqlGetUserByName(s[0]);
									if ( quester.isOnline() ) {
										osToClient.writeUTF("rlifalse");
									} else if ( quester.getPassword().equals(s[1]) ) {
/* this may fail					*/	db.sqlUpdateUserStatus(s[0], User.ONLINE);
										onlineUserList = db.sqlGetOnlineUser();
										currentUserName = s[0];
										osToClient.writeUTF("rlitrue^" + onlineUserList);
										Server.newOnlineUser(s[0]);
									} else {
										osToClient.writeUTF("rlifalse");
										log("error", "login password incorrect");
									}
								} else {
									log("error", "login: user ["+s[0]+"] not exist");
									osToClient.writeUTF("rlifalse");
								}
								log("info", "reply login: \n\t"+onlineUserList);
							}

						} else if (opcode.equals("rg")) {			
							/** register request
								s[0] - username, s[1] - password
								if username CONFLICTS with some existing user
									return false
								else 
									insert a new user to the database
									return true
							*/
							boolean result = false;
							if (s.length == 2) {
								if ( db.sqlNameIsExist(s[0]) ) {
									log("error", "register: user ["+s[0]+"] already exists");
									osToClient.writeUTF("rrgfalse");
								} else {
									if (!db.sqlInsertUser(new User(s[0], s[1]))) {
										log("error", "register: write into database error");
										osToClient.writeUTF("rrgfalse");
									} else {
										osToClient.writeUTF("rrgtrue");
										result = true;
									}
								}
							} else {
								log("error", "register request operands error:");
								printStringArray(s);
								log("info", "false sent back");
								osToClient.writeUTF("rrgfalse");
							}
							log("info", "reply register: \n\t"+result);

						} else if (opcode.equals("lo")) {
							/** logout request
								s[0] - username
								if user exists in the database and is currently online
									return true
								else return false
							*/
							log("info", "logging out request on user [" + s[0] + "]");
							boolean result = false;
							if ( db.sqlNameIsExist(s[0]) ) {
								if (!currentUserName.equals(s[0])) {
									log("error", "logging out: mismatch on current user name with operand");
								} else {
									result = db.sqlUpdateUserStatus(s[0], User.OFFLINE);
									Server.newOfflineUser(currentUserName);
									currentUserName = null;
								}
								
							} else {
								log("error", "user does not exist");
							}
							osToClient.writeUTF("rlo" + result);
							log("info", "reply logout: "+result);

						} else if (opcode.equals("se")) {			
							// search request
							// s[0] - keyword
							// if keyword exists in the database
							//   make an entry and return
							// else
							//   search it online and insert it into the database
							//   return the entry

							Entry result = null;
							if ( db.sqlEntryIsExist(s[0]) ) {
								result = db.sqlGetEntry(s[0]);
								boolean [] incomplete = new boolean[3];
								boolean refetch = false;
								for (int i = 0; i < 3; i++) {
									incomplete[i] = (result.getInformation(i) == null);
									refetch = refetch | incomplete[i];
								}
								if (refetch) {
									OnlineSearcher oser = new OnlineSearcher();
									result = oser.search(s[0]);
									for (int i = 0; i < 3; i++) if (incomplete[i])
										db.sqlInsertInformation(
											result.getKeyword(),
											result.getInformation(i)
										);
								}
							} else {
								OnlineSearcher oser = new OnlineSearcher();
								result = oser.search(s[0]);
								db.sqlInsertEntry(result);
							}
							boolean [] zanFlag = new boolean[3];
							boolean [] unzanFlag = new boolean[3];
							for (int i = 0; i < 3; i++) {
								zanFlag[i] = db.sqlZanlogIsExist(currentUserName, s[0], Entry.sourceString[i]);
								unzanFlag[i] = db.sqlUnzanlogIsExist(currentUserName, s[0], Entry.sourceString[i]);
							}
							String flags =
								zanFlag[0] +"^"+ zanFlag[1] +"^"+ zanFlag[2] +"^"+ 
								unzanFlag[0] +"^"+ unzanFlag[1] +"^"+ unzanFlag[2];
							osToClient.writeUTF("rse" + result +"^"+ flags);
							log("info", "reply search: \n\t" + 
								result.toString().replaceAll("\\^", "\n\t\t")+"^"+flags);

						} else if (opcode.equals("za")) {			
							// zan request
							// s[0] username, s[1] keyword, s[2] source
							// if this record already exists in the database
							//   return false
							// else write this record into the database
							//   return true
							log("info", "zan on word [" + s[1] + "] on source [" +
								s[2] +"] by user [" + s[0] +"]");
							boolean result = false;
							if ( !db.sqlZanlogIsExist(s[0], s[1], s[2]) ) {
								result = db.sqlInsertZanlog(s[1], s[0], s[2]);
							}
							osToClient.writeUTF("rza" + result);
							log("info", "reply zan: "+result);

						} else if (opcode.equals("uz")) {			// unzan
							// similar to zan
							log("info", "unzan on word [" + s[1] + "] on source [" +
								s[2] +"] by user [" + s[0] +"]");
							boolean result = false;
							if ( !db.sqlUnzanlogIsExist(s[0], s[1], s[2]) ) {
								result = db.sqlInsertUnzanlog(s[1], s[0], s[2]);
							}
							osToClient.writeUTF("ruz" + result);
							log("info", "reply unzan: "+result);
						} else if (opcode.equals("ou")) {			
							// get online users request
							// s[0] - requester
							// return all users that are online
							// should we exclude the requester?
							log("info", "request online users except [" + s[0] + "]");
							String onlineUserList = db.sqlGetOnlineUser().replaceAll("\\$", "\\^");
							
							osToClient.writeUTF("rou"+onlineUserList);
							log("info", "reply qou: \n\t" + onlineUserList);
						} else if (opcode.equals("sc")) {			
							// send card
							// s[0] - sender, s[1] - receiver, s[2] - keyword, s[3] - provider
							// insert this to the database....
							log("info", "sending card from [" + s[0] + "] to [" +
								s[1] + "] on word [" + s[2] + "] provided by [" + s[3] + "]");
							boolean result = false;
							if ( !db.sqlCardIsExist(s[0], s[1], s[2], s[3]) ) {
								result = db.sqlInsertCard(s[0], s[1], s[2], s[3]);
							} else {
								log("error", "send card log already exists");
							}
							osToClient.writeUTF("rsc" + result);
							log("info", "reply qsc: " + result);
						} else if (opcode.equals("gc")) {
							// get all cards
							// s[0] - sender
							String cardsInfo = db.sqlGetMyCard(s[0]);
							osToClient.writeUTF("rgc"+cardsInfo);
							log("info", "reply to get card request: \n" + cardsInfo.replaceAll("\\^", "\n"));
						}
					} // end of if
					osToClient.flush();
					transactionCounter++;
				} // end of while
			} catch (Exception ex) {
				ex.printStackTrace();
				if (currentUserName != null) {
					db.sqlUpdateUserStatus(currentUserName, User.OFFLINE);
					Server.newOfflineUser(currentUserName);
				}
			}
		} // end of function run()

		/**
		 * 在服务器端的终端和图形界面上输出一个字符串数组，
		 * 用途为打印参数列表等，方便调试。
		 * @param s 待打印的字符串数组
		 * */
		void printStringArray( String[] s) {
			for (int i = 0; i < s.length; ++i) {
				Server.appendMessage("\t" + s[i]);
			}
			Server.appendMessage("\n");
		}
		
		/**
		 * 输出日志信息最终调用的函数：
		 * 打印格式为[server] client #+会话编号+trans #+服务编号+type+message
		 * @param type 日志类型，一般为info或error
		 * @param message 信息内容
		 * */
		void log(String type, String message) {
			System.out.println("[Server] Client #" + clientNo +
			" trans #" + transactionCounter+", " + type + ": " + message);
			Server.appendMessage("[Server] Client #" + clientNo +
			" trans #" + transactionCounter+", " + type + ": \n\t" + message+"\n");
		}
	} // end of class HandleTask
	
	/**
	 * 初始化图形界面：
	 * 左侧为在线用户列表，右侧为日志信息。
	 * */
	public void initializeUI() {
		jfMainPanel = new JFrame();
		jfMainPanel.setLayout(new BorderLayout());
		jlOnlineUsers = new JList(model);
		jtaLog = new JTextArea();
//		jpTopBar = new JPanel();
//		jbtKick = new JButton("Kick Down The User");
//		jpTopBar.add(jbtKick);
		
//		jfMainPanel.add(jpTopBar, BorderLayout.NORTH);
		jfMainPanel.add(new JScrollPane(jlOnlineUsers), BorderLayout.WEST);
		jfMainPanel.add(new JScrollPane(jtaLog), BorderLayout.CENTER);
		
		jfMainPanel.setTitle("Server Monitor");
		jfMainPanel.setLocationRelativeTo(null);
		jfMainPanel.setSize(600, 400);
		jfMainPanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jfMainPanel.setVisible(true);
	}
	
	private static DefaultListModel model = new DefaultListModel();
	private static JFrame jfMainPanel;
	private static JList jlOnlineUsers;
	private static JTextArea jtaLog;
	private static JButton jbtKick;
	private static JPanel jpTopBar;
	
	/**
	 * 往用户界面的日志信息里添加信息
	 * @param m 信息内容
	 * */
	public static void appendMessage(String m) {
		jtaLog.append(m);
	}
	
	/**
	 * 往用户界面的在线用户列表里添加新用户，以表明该用户上线。
	 * @param userName 待添加用户的用户名
	 * */
	public static void newOnlineUser(String userName) {
		model.addElement(userName);
	}
	
	/** 从用户界面的在线用户列表里删除某个用户，以表明该用户下线。
	 * @param userName 待删除用户的用户名
	 * */
	public static void newOfflineUser(String userName) {
		model.removeElement(userName);
	}
}

package lab02;
/**
 * 主要功能：
 * 		实现Client后台功能函数，实现客户端登陆、注册、注销、搜索、显示、
 * 	     发送单词卡、接收单词卡、显示单词卡、点赞、点不赞、刷新在线用户列表的功能
 */
import java.io.*; 
import java.net.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.*;

class ClientBackground {
	private Socket socket;
	//net IO stream
	private DataOutputStream toServer;
	private DataInputStream fromServer;
	private OnlineSearcher onlineSearcher;
	
	private User currentUser; // 当前用户
	private Entry currentEntry; //当前词条
	private String[] notebook = new String[100]; // 单词本
	private int notebookNumber = 0;
	private int [] displayOrder = {0,1,2};//初始的显示顺序是0（baidu）1（youdao）2（bing）
	private int [] zanSum = {0,0,0};//每个显示面板的点赞数
	private int [] unzanSum = {0,0,0};//不赞数
	private boolean [] enableZan = {false,false,false};
	private boolean [] enableUnzan = {false,false,false};
	ClientBackground(){
			
	}
	
	/**
	 * 功能：主界面的login触发的事件
	 * 1.弹出登陆窗口
	 *   有六个组件：用户名标签，用户名输入框
	 *   		   密码标签，密码输入框（添加键盘监听事件）
	 *   		 login按钮（添加按钮监听事件），cancel按钮（添加按钮监听事件）
	 * 2.输完用户名和密码之后
	 *   （1）一旦按下login按钮或者按下Enter键
	 *       调用loginComfirm函数（具体见loginConfirm函数解释）
	 *   （2）一旦按下cancel按钮，不显示登录窗口
	 */
	public void login(DefaultListModel defaultListModel,JButton logout,JButton register,JButton note,JButton refreshOnlineUserList,
			JButton login,JButton []sendCard) {//从主界面传来的参数
		//登陆窗口
		JFrame loginFrame = new JFrame();//登陆窗口
		JLabel loginUserName = new JLabel("User Name");
		JTextField jtfLoginUserName = new JTextField(8);
		JLabel loginPassword = new JLabel("Password");
		JPasswordField jtfLoginPassword = new JPasswordField();
		jtfLoginPassword.setEchoChar('*');
		JButton lfLogin = new JButton("login");//登陆面板的登陆按钮
		JButton lfCancel = new JButton("cancel");//登陆面板的取消按钮
		loginFrame.setResizable(false);//禁用最大化
		loginFrame.setSize(250,125);
		loginFrame.setLocationRelativeTo(null);
		loginFrame.setTitle("login");
		loginFrame.setLayout(new GridLayout(3,2,5,5));
		loginFrame.add(loginUserName);
		loginFrame.add(jtfLoginUserName);
		loginFrame.add(loginPassword);
		loginFrame.add(jtfLoginPassword);
		loginFrame.add(lfLogin);
		loginFrame.add(lfCancel);
		loginFrame.setVisible(true);
		loginFrame.setEnabled(true);
		lfLogin.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String userName = jtfLoginUserName.getText();
				String userPassword = String.valueOf(jtfLoginPassword.getPassword());
				loginConfirm(userName,userPassword,defaultListModel,
						logout,register,note,refreshOnlineUserList,login,
						sendCard,lfLogin,loginFrame,jtfLoginUserName,jtfLoginPassword);
			
			}
		});
		
		jtfLoginPassword.addKeyListener(new KeyAdapter() {
    		public void keyReleased(KeyEvent e){
    			if(e.getKeyCode() == 10){//按下enter键就进行单词查询
    				String userName = jtfLoginUserName.getText();
    				String userPassword = String.valueOf(jtfLoginPassword.getPassword());
    				loginConfirm(userName,userPassword,defaultListModel,
    						logout,register,note,refreshOnlineUserList,login,
    						sendCard,lfLogin,loginFrame,jtfLoginUserName,jtfLoginPassword);
    			}
    		}
    	}); 
		
		lfCancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				jtfLoginUserName.setText("");
				jtfLoginPassword.setText("");
				loginFrame.setVisible(false);//登陆面板不可见
			}
		});
	}
	
	/**
	 * 功能：按下登陆界面的login按钮或Enter键者引发的事件（即登录功能）
	 * 1.建立一个与服务器相连的socket，以便传输数据包
	 * 2.将登陆请求数据包发送给服务器，并等待服务器回复
	 * 3.收到服务器登陆回复数据报
	 * 4.数据包显示登陆成功
	 *    （1）回复数据报中包括当前在线用户名，解析数据包，并显示在线用户列表
	 *    （2）更新当前用户信息，并设置主界面各个按钮的状态（是否可以点击）
	 *    （3）不显示登录窗口
	 *    （4）弹出“登陆成功”的消息提示框
	 * 5.数据包显示登陆不成功
	 *     将登陆界面上的用户名输入框和密码输入框清空，并弹出“登录失败”的消息提示框
	 */
	public void loginConfirm(String userName,String userPassword,DefaultListModel defaultListModel,
			JButton logout,JButton register,JButton note,JButton refreshOnlineUserList,JButton login,
			JButton []sendCard,JButton lfLogin,JFrame loginFrame,JTextField jtfLoginUserName,JPasswordField jtfLoginPassword){
		//发送请求登陆数据包
		String replyLoginPackage;
		try {
			try{//create a socket to connect to the server
				socket = new Socket("114.212.129.39",23333);//yushen:114.212.129.39
				//create an input stream to receive data from the server
				fromServer = new DataInputStream(socket.getInputStream());
				//create an output stream to send data to the server
				toServer = new DataOutputStream(socket.getOutputStream());
			}
			catch(Exception ex){
				ex.printStackTrace();
			}	
			//send package to server
			toServer.writeUTF("qli" + userName + "^" + userPassword);
			toServer.flush();
			//recv package from server
			replyLoginPackage = fromServer.readUTF();
			//fresh the onlineUserList
			defaultListModel.clear();
			if(replyLoginPackage.substring(0, 3).equalsIgnoreCase("rli")){
				//说明收到的是回复登陆数据包
				String temp = replyLoginPackage.substring(3);//获取用户名字符串
				String [] usersName = temp.split("\\^");
				if(usersName[0].equalsIgnoreCase("true")){//登陆成功
					lfLogin.setEnabled(false);//注意：在用户退出之后再设置为true，允许下一个用户登陆！
					for(int i = 1; i < usersName.length; i++){
						if(usersName[i].length() > 0){
							//添加在线用户
							defaultListModel.addElement(usersName[i]);
						}
					}
					//更新当前用户信息
					currentUser = new User(userName,userPassword);
					currentUser.setStatus(true);//在线状态
					currentUser.setIp(socket.getLocalAddress());
					currentUser.setPort(socket.getLocalPort());
					//logout和note可以使用
					logout.setEnabled(true);
					note.setEnabled(true);
					refreshOnlineUserList.setEnabled(true);
					for(int i = 0; i < 3; i++){
						sendCard[i].setEnabled(true);
					}	
					//login和register禁用
					login.setEnabled(false);
					login.setText(currentUser.getName());
					register.setEnabled(false);
					//关闭登陆面板
					lfLogin.setEnabled(false);
					loginFrame.setVisible(false);
					JOptionPane.showMessageDialog(null,"登陆成功，欢迎您！");
				}
				else{//登录失败，提示信息用户名或者密码错误
					jtfLoginUserName.setText("");
					jtfLoginPassword.setText("");
					//弹出提示框
					JOptionPane.showMessageDialog(null,"登陆失败，请重新输入！");
					
				}
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	/**
	 * 功能：主界面的logout按键触发的事件（logout功能实现）
	 * 1.判断是否有当前用户存在
	 * 2.若有，获取当前用户名封装成注销请求数据包发送给服务器，等待服务器回复
	 * 3.收到服务器注销回复数据报
	 * 4.如果显示注销成功
	 *    （1）设置当前用户在线状态为false
	 *    （2）设置主界面的按钮的状态，清空在线用户列表
	 *    （3）弹出消息提示框，显示注销成功
	 */
	public void logout(JButton login,JButton logout,JButton register,JButton note,
			JButton []zan,JButton[] unzan,JButton[] sendCard,JTextField[] whoToSend,JButton refreshOnlineUserList, 
			DefaultListModel defaultListModel) {
		if(currentUser != null &&currentUser.isOnline()){//判断有用户在线
		String userNameString = currentUser.getName();
		StringBuilder requestLogoutPackage = new StringBuilder();
		String replyLogoutPackage;
		requestLogoutPackage.append("qlo");
		requestLogoutPackage.append(userNameString);
		try {//send
			toServer.writeUTF(requestLogoutPackage.toString());toServer.flush();
			toServer.flush();
			//receive
			replyLogoutPackage = fromServer.readUTF();
			if(replyLogoutPackage.substring(0, 3).equalsIgnoreCase("rlo")){//判断如果是logout的回复数据包
				if(replyLogoutPackage.substring(3).equalsIgnoreCase("true")){//如果服务器允许退出，一般也不会不允许的啊= =
					//用户注销，将状态置为false，其他应该可以不用管
					currentUser.setStatus(false);
					//disable currentUser and button "logout" 
					logout.setEnabled(false);
					//display buttons "login" and "register"
					login.setEnabled(true);
					login.setText("login");
					register.setEnabled(true);
					//clear onlineUserList
					defaultListModel.clear();
					//clear notes 
					//disable buttons "notes"
					note.setEnabled(false);
					for(int i = 0; i < 3; i++){
						whoToSend[i].setText("");
						zan[i].setEnabled(false);
						unzan[i].setEnabled(false);
						sendCard[i].setEnabled(false);
					}
					refreshOnlineUserList.setEnabled(false);
					JOptionPane.showMessageDialog(null,"注销成功，goodbye~");
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}
	
	/**
	 * 功能：主界面register键按下之后触发的事件
	 * 1.弹出注册窗口
	 *   有8个组件：用户名标签，用户名输入框
	 *   		  密码标签，密码输入框
	 *   		  确认密码标签，确认密码输入框
	 *   		 login按钮，cancel按钮
	 * 2.输入完之后，一旦按下register按钮或者按下Enter键,调用registerConfirm函数
	 *   按下cancel按钮之后，注册窗口设置不可见
	 */
	public void register() {
		JFrame registerFrame = new JFrame();
		JLabel regUserName = new JLabel("User Name");
		JTextField jtfRegUserName = new JTextField(8);
		JLabel regPassword = new JLabel("Password");
		JPasswordField jtfRegPassword = new JPasswordField(8);
		jtfRegPassword.setEchoChar('*');
		JLabel regPasswordConfirm = new JLabel("Password Confirm");
		JPasswordField jtfRegPasswordConfirm = new JPasswordField(8);
		jtfRegPasswordConfirm.setEchoChar('*');
		JButton rfRegister = new JButton("register");
		JButton rfCancel = new JButton("cancel");
		registerFrame.setResizable(false);//禁用最大化
		registerFrame.setSize(300,150);
		registerFrame.setLocationRelativeTo(null);
		registerFrame.setTitle("register");
		registerFrame.setLayout(new GridLayout(4,2,5,5));
		registerFrame.add(regUserName);
		registerFrame.add(jtfRegUserName);
		registerFrame.add(regPassword);
		registerFrame.add(jtfRegPassword);
		registerFrame.add(regPasswordConfirm);
		registerFrame.add(jtfRegPasswordConfirm);
		registerFrame.add(rfRegister);
		registerFrame.add(rfCancel);
		registerFrame.setVisible(true);
		rfRegister.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String userName = jtfRegUserName.getText();
		        String password = (String.valueOf(jtfRegPassword.getPassword()));
		        String passwordConfirm = (String.valueOf(jtfRegPasswordConfirm.getPassword()));
		        registerConfirm(userName,password,passwordConfirm,
		   			 registerFrame, jtfRegUserName, jtfRegPassword, jtfRegPasswordConfirm);
			}
		});
		
		jtfRegPasswordConfirm.addKeyListener(new KeyAdapter() {
    		public void keyReleased(KeyEvent e){
    			if(e.getKeyCode() == 10){//按下enter键就进行单词查询
    				String userName = jtfRegUserName.getText();
    		        String password = (String.valueOf(jtfRegPassword.getPassword()));
    		        String passwordConfirm = (String.valueOf(jtfRegPasswordConfirm.getPassword()));
    		        registerConfirm(userName,password,passwordConfirm,
    		   			 registerFrame, jtfRegUserName, jtfRegPassword, jtfRegPasswordConfirm);
    			}
    		}
    	}); 
		
		rfCancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				jtfRegUserName.setText("");
				jtfRegPassword.setText("");
				jtfRegPasswordConfirm.setText("");
				registerFrame.setVisible(false);//登陆面板不可见
			}
		});	
	}
	
	/**
	 * 功能：register界面按下确认键引发的事件（即注册功能）
	 * 1.判断输入的密码和确认密码是否一致
	 *     若不一致，清空密码输入框和确认密码输入框，并弹出消息提示框显示“注册失败”
	 * 2.建立跟服务器连接的socket
	 * 3.将用户名和密码封装成注册请求数据包，并发送给服务器，等待服务器回复
	 * 4.获取服务器回复的注册回复数据报   
	 *    （1）如果显示注册成功，将注册窗口设置为不可见，弹出消息提示框显示“注册成功”
	 * 	      （2）如果失败，就将注册面板上的输入框全部清空，弹出消息提示框显示“注册失败”
	 */
	public void registerConfirm(String userName,String password,String passwordConfirm,
			JFrame registerFrame,JTextField jtfRegUserName,JTextField jtfRegPassword,JTextField jtfRegPasswordConfirm){
        //判断输入的密码跟确认密码是否一致
        if(!password.equals(passwordConfirm)){
        	//不一致，就提示说重新输入密码
        	jtfRegPassword.setText("");
        	jtfRegPasswordConfirm.setText("");
        	JOptionPane.showMessageDialog(null, "注册失败，请重新确认密码！");
        }
        else{
        	if(userName.length() <= 20 && password.length() <= 20){//用户名和密码不超过20个字符)
        	StringBuilder requestRegPackage = new StringBuilder();
        	String replyRegPackage;
        	requestRegPackage.append("qrg");
        	requestRegPackage.append(userName);
        	requestRegPackage.append("^");
        	requestRegPackage.append(password);
        	try {//send
        		try{//create a socket to connect to the server
        			socket = new Socket("114.212.129.39",23333);//yushen:114.212.129.39
        			//create an input stream to receive data from the server
        			fromServer = new DataInputStream(socket.getInputStream());
        			//create an output stream to send data to the server
        			toServer = new DataOutputStream(socket.getOutputStream());
        		}
        		catch(Exception ex){
        			ex.printStackTrace();
        		}	
        		toServer.writeUTF(requestRegPackage.toString());
        		toServer.flush();
        		//receive
        		replyRegPackage = fromServer.readUTF();
        		if(replyRegPackage.substring(0, 3).equalsIgnoreCase("rrg")){//判断是login的回复数据报
        			if(replyRegPackage.substring(3).equalsIgnoreCase("true")){//判断是否成功，一般也是成功的吧 = =
        				jtfRegUserName.setText("");
        				jtfRegPassword.setText("");
        				jtfRegPasswordConfirm.setText("");
        				registerFrame.setVisible(false);
        				//弹出提示框，已经注册成功
        				JOptionPane.showMessageDialog(null,"注册成功，欢迎您加入diki！");
        			}
        			else{//没有成功
        				//clear password fieldS
        				jtfRegUserName.setText("");
        				jtfRegPassword.setText("");
        				jtfRegPasswordConfirm.setText("");
        				JOptionPane.showMessageDialog(null,"注册失败，请重新注册！");
        				
        			}
        		}
			
        	} catch (Exception e1) {
        		// TODO Auto-generated catch block
        		e1.printStackTrace();
        	}
        	}
        	else{//输入字符超出20个
        		jtfRegUserName.setText("");
				jtfRegPassword.setText("");
				jtfRegPasswordConfirm.setText("");
        		JOptionPane.showMessageDialog(null,"用户名和密码不能超出20个字符，请重新输入！");
		    }
        }
	}
	
	/**
	 * 功能：主界面按下search按键之后触发的事件
	 * 1.获取输入框的关键词
	 * 2.用正则表达式判断这是不是一个单词，若不是，弹出消息框提示用户
	 * 3.如果当前用户在线
	 *     （1）将封装好的搜索请求数据包发送给服务器，等待服务器回复
	 *     （2）得到服务器的搜索回复数据包
	 *     （3）根据数据包设置当前词条
	 *     （4）根据词条不同来源解释的点赞情况，进行排序（调用displayOrderSort函数）
	 *     （5）根据回复数据报中的当前用户给这个单词的点赞情况，设置点赞按钮是否可以被点击
	 *     （6）将词条解释根据排序结果和勾选来源结果显示在面板上
	 * 4.如果是离线
	 *     进行离线搜索，调用OnlineSearcher类中search函数和displayThreePanel函数
	 */
	public void search(String keyword, JButton [] zan,JButton [] unzan,
			JTextArea[] result, JCheckBox baidu,JCheckBox youdao,JCheckBox bing,
			JPanel [] showThreePanel,JPanel [] showSelectPanel, JButton [] sendCard) {
		onlineSearcher = new OnlineSearcher();
		//用正则表达式判断输入的是一个英文单词
		String normalInput = "[a-zA-Z]+";
		if(keyword.matches(normalInput)){
		StringBuilder requestSearchPackage = new StringBuilder();
		String replySearchPackage;
		if(currentUser != null && currentUser.isOnline()){//用户不为空并且用户在线
			requestSearchPackage.append("qse");
			requestSearchPackage.append(keyword);
			try {
				//send
				toServer.writeUTF(requestSearchPackage.toString());
				toServer.flush();
				//receive
				replySearchPackage = fromServer.readUTF();
				if(replySearchPackage.substring(0, 3).equalsIgnoreCase("rse")){//判断是search回复数据报
					String [] tempStr = replySearchPackage.substring(3).split("\\^");
					String [] info = new String[3];
					int index = 0;
					currentEntry = new Entry(tempStr[0]);//给当前词条进行赋值 keyword
					for(int i = 1; i < tempStr.length && index < 3; i++){
						if(tempStr[i].length() > 0){//判断非空
							info[index] = tempStr[i];
							index++;
						}
					}
					for(int i = 0; i < 3; i++){
							String [] ex = info[i].split("\\$");
							Information information = new Information(ex[0],ex[1],ex[2],ex[3]);
							currentEntry.setInformation(information);
							//给zan、unzan数目赋值
							zanSum[i] = Integer.parseInt(ex[4]);
							unzanSum[i] = Integer.parseInt(ex[5]);
					}
					//设置默认显示顺序
					displayOrder[0] = 0;
					displayOrder[1] = 1;
					displayOrder[2] = 2;
					displayOrderSort();
					//下面控制zan和unzan按钮是否能够使用，就是用户是否已经点过这个单词的zan或者unzan
					if(tempStr[4].equalsIgnoreCase("true"))  {zan[displayOrder[0]].setEnabled(false);enableZan[displayOrder[0]] = false;}
					else									  {zan[displayOrder[0]].setEnabled(true);enableZan[displayOrder[0]] = true;}
					if(tempStr[5].equalsIgnoreCase("true"))  {zan[displayOrder[1]].setEnabled(false);enableZan[displayOrder[1]] = false;} 
					else									  {zan[displayOrder[1]].setEnabled(true);enableZan[displayOrder[1]] = true;}
					if(tempStr[6].equalsIgnoreCase("true"))  {zan[displayOrder[2]].setEnabled(false);enableZan[displayOrder[2]] = false;}
					else									  {zan[displayOrder[2]].setEnabled(true);enableZan[displayOrder[2]] = true;}
					if(tempStr[7].equalsIgnoreCase("true"))  {unzan[displayOrder[0]].setEnabled(false);enableUnzan[displayOrder[0]] = false;}
					else									  {unzan[displayOrder[0]].setEnabled(true);enableUnzan[displayOrder[0]] = true;}
					if(tempStr[8].equalsIgnoreCase("true"))  {unzan[displayOrder[1]].setEnabled(false);enableUnzan[displayOrder[1]] = false;}
					else									  {unzan[displayOrder[1]].setEnabled(true);enableUnzan[displayOrder[1]] = true;}
					if(tempStr[9].equalsIgnoreCase("true"))  {unzan[displayOrder[2]].setEnabled(false);enableUnzan[displayOrder[2]] = false;}
					else									  {unzan[displayOrder[2]].setEnabled(true);enableUnzan[displayOrder[2]] = true;}
					//解释显示在面板上
					displayThreePanel(result, baidu, youdao, bing,
							showThreePanel, showSelectPanel,
							sendCard, zan,unzan);
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{//用户未创建或者用户不在线
			currentEntry = onlineSearcher.search(keyword);
			displayOrder[0] = 0;
			displayOrder[1] = 1;
			displayOrder[2] = 2;//设置显示顺序
			displayThreePanel(result, baidu, youdao, bing,
					showThreePanel, showSelectPanel,
					sendCard,zan,unzan);
		}
		}
		else{
			JOptionPane.showMessageDialog(null, "您输入的单词不合法，请重新输入！");
		}
	}
	
	/**
	 * 功能：根据排序结果和来源勾选结果显示三个来源的解释
	 */
	public void displayThreePanel(JTextArea [] result, JCheckBox baidu,JCheckBox youdao,JCheckBox bing,
			JPanel [] showThreePanel, JPanel [] showSelectPanel,
			JButton [] sendCard, JButton [] zan,JButton [] unzan){
		for(int i = 0; i < 3; i++){
			result[i].setText("");
		}
		boolean selectNone = false;
		if(!baidu.isSelected() && !youdao.isSelected() && !bing.isSelected())
			selectNone = true;
		//int index = 0;//标记现在应该在哪一块面板上面显示了
		for(int i = 0; i < 3; i++){
			//先判断是否勾选了该来源(顺序是baidu,youdao,bing)
			//用户在线就显示赞和不赞的数目，否则不显示
			//获取0（baidu）1（youdao）2（bing）显示的面板标号
			int panelIndex = displayOrder[i];
			boolean isSelected = true;
			String [] ex = currentEntry.getInformation(i).toString().split("\\$");
			switch(ex[0]){
				case "baidu": if(!baidu.isSelected())  isSelected = false;break;
				case "youdao": if(!youdao.isSelected())  isSelected = false;break;
				case "bing": if(!bing.isSelected())  isSelected = false;break;
			}
			showThreePanel[panelIndex].setBorder(BorderFactory.createTitledBorder (ex[0]));
			showSelectPanel[panelIndex].setBorder(BorderFactory.createTitledBorder (ex[0]));
			//全部不选择，显示全部
			if(isSelected || selectNone){
				if(enableZan[panelIndex] && currentUser != null && currentUser.isOnline())
					zan[panelIndex].setEnabled(true);
				if(enableUnzan[panelIndex] && currentUser != null && currentUser.isOnline())
					unzan[panelIndex].setEnabled(true);
				if(currentUser != null && currentUser.isOnline()) 
					sendCard[panelIndex].setEnabled(true);
				//选中了就进行显示
				//keyword
				result[ panelIndex].append(currentEntry.getKeyword() + '\n');
				//音标
				if(!ex[1].equalsIgnoreCase("null")){
					String [] pho = ex[1].split("#");
					for(int j = 0; j < pho.length; j++){
						result[panelIndex].append(pho[j] + ",");
					}
					result[ panelIndex].append("\n");
				}
				//词性
				if(!ex[2].equalsIgnoreCase("null") && !ex[3].equalsIgnoreCase("null")){
					String [] attri = ex[2].split("#");
					//解释
					String [] exp = ex[3].split("#");
					for(int k = 0; k < attri.length; k++){
						result[panelIndex].append(attri[k] + " " + exp[k] + '\n');
					}
				}
			    if(currentUser!= null && currentUser.isOnline()){
			    	//zan
			    	result[panelIndex].append("zan: " + zanSum[i] + "\n");
			    	//unzan
			    	result[panelIndex].append("unzan: " + unzanSum[i] + "\n");
			    }
				//下一次要显示哪一块面板
				//index++;
			}
			else{
				zan[panelIndex].setEnabled(false);
				unzan[panelIndex].setEnabled(false);
				sendCard[panelIndex].setEnabled(false);
			}
		}
	}
	
	/**
	 * 功能：根据zan和unzan个数，对三个来源的显示顺序进行排序
	 */
	public void displayOrderSort(){//进行排序后显示
		//将获取的释义显示在面板上
		//根据zan和unzan的数量进行排序显示
		//用123代表三个information
		int []allzanSum = new int[3];//zan和unzan综合
		allzanSum[0] = zanSum[0] -  unzanSum[0];
		allzanSum[1] = zanSum[1] -  unzanSum[1];
		allzanSum[2] = zanSum[2] -  unzanSum[2];
		int [] index = {0,1,2};//每个面板对应的来源
		
		int maxIndex = 0;
		for(int i = 0; i < 3; i++){
			if(allzanSum[i] >= allzanSum[maxIndex]) maxIndex = i;
		}
		index[0] = maxIndex;
		displayOrder[maxIndex] = 0;
		
		int secondMaxIndex = 0;
		for(int i = 0; i < 3 ; i++){
			if(maxIndex == 0) {secondMaxIndex = 1;continue;}
			if(allzanSum[i] >= allzanSum[secondMaxIndex] && i != maxIndex) secondMaxIndex = i;
		}
		index[1] = secondMaxIndex;
		displayOrder[secondMaxIndex] = 1;
		
		for(int i = 0; i < 3; i++){
			if(i != maxIndex && i != secondMaxIndex)  {index[2] = i;displayOrder[i] = 2;}
		}
	    
	   /* for(int i = 0; i < 3; i++){
	    	switch(i){
	    		case 0: System.out.println("baidu");break;
	    		case 1: System.out.println("youdao");break;
	    		case 2: System.out.println("bing");break;
	    	}
	    	System.out.println("显示在面板" + displayOrder[i]);
	    }
	    System.out.println();*/
	}
	
	/**
	 * 功能：弹出单词本窗口，显示用户收到的单词卡
	 * 单词本窗口有两个组件：单词列表（添加选中监听事件）和单词卡显示框
	 */
	public void showNotes() { 
		//相当于是显示收到的单词卡（收卡的功能）
		//shownote面板
		JFrame showNoteFrame = new JFrame();
		JLabel noteTitle = new JLabel("My notebook");
		JList noteList = new JList();
		DefaultListModel<String> defaultListModel = new DefaultListModel<String>();
		noteList.setModel(defaultListModel);
		JScrollPane scrollPaneOfNoteList = new JScrollPane(noteList);	//滚轮
		JTextArea wordExplaination = new JTextArea();
		JScrollPane scrollPaneOfWordEx = new JScrollPane(wordExplaination);	//滚轮
		//note面板设置
		showNoteFrame.setResizable(false);//禁用最大化
		showNoteFrame.setSize(300,300);
		showNoteFrame.setLocationRelativeTo(null);
		showNoteFrame.setTitle("My note");
		showNoteFrame.setLayout(new BorderLayout(5,5));
		noteList.setFixedCellWidth(100);
		noteList.setFixedCellHeight(50);
		noteTitle.setHorizontalAlignment(JLabel.CENTER);
		showNoteFrame.add(noteTitle,BorderLayout.NORTH);
		showNoteFrame.add(scrollPaneOfNoteList,BorderLayout.WEST);
		showNoteFrame.add(scrollPaneOfWordEx,BorderLayout.CENTER);
		for(int i = 0; i < notebookNumber; i++){
			String [] temp = notebook[i].split("\\^");
			defaultListModel.addElement(temp[1]);
		}
		//将note中的单词加入列表中，点击即可显示具体内容
		noteList.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent e){
                selectWord(noteList, wordExplaination);
            }     
        });
		showNoteFrame.setVisible(true);
	}

	/**
	 * 功能：选中单词本列表中的单词，显示单词卡
	 */
	public void selectWord(JList noteList, JTextArea wordExplaination){
		//选中要发送的用户之后，显示在左边三个textfield中
		wordExplaination.setText("");
    	int selectedIndex = noteList.getSelectedIndex();//返回选中多少行
    	String[] temp = notebook[selectedIndex].split("\\^");
    	//来自哪个用户 temp[0]
    	//keyword temp[1]
    	//info 	temp[2]
    	wordExplaination.append("FROM " + "\t" + temp[0] + "\n" 
				+ temp[1] + "\n");
    	//info
    	String [] info = temp[2].split("\\$");
    	//source
    	wordExplaination.append(info[0] + '\n');
		//音标
		String [] pho = info[1].split("#");
		for(int j = 0; j < pho.length; j++){
			wordExplaination.append(pho[j] + ",");
		}
		wordExplaination.append("\n");
		//词性
		String [] attri = info[2].split("#");
		//解释
	    String [] exp = info[3].split("#");
	    for(int k = 0; k < attri.length; k++){
	    	wordExplaination.append(attri[k] + " " + exp[k] + '\n');
	    }
	    if(currentUser!= null && currentUser.isOnline()){
	    	//zan
	    	int currentZan = Integer.parseInt(info[4]);
	    	wordExplaination.append("zan: " + currentZan + "\n");
	    	//unzan
	    	int currentUnzan = Integer.parseInt(info[5]);
	    	wordExplaination.append("unzan: " + currentUnzan + "\n");
	    }
	}
	
	/**
	 * 功能：点zan
	 * 1.根据panelID获取是给哪个来源点zan
	 * 2.发送点zan请求数据包给服务器，等待回复
	 * 3.得到回复
	 * 4.点击成功，将该zan按钮设置为无法点击的状态
	 */
	public boolean clickZan(int panelID,JButton[] zan) {
		String source = "";
		int sourceIndex = 0;
		for(int i = 0; i < 3; i++){
			if(panelID == displayOrder[i]){
				sourceIndex = i;
				break;
			}
		}
		switch(sourceIndex){
			case 0 : source = "baidu";break;
			case 1 : source = "youdao";break;
			case 2 : source = "bing";break;
		}
		try {
			 //send package
			 toServer.writeUTF("qza" + currentUser.getName() + "^" + currentEntry.getKeyword() + "^" +source);
			 toServer.flush();
			 //recv package ???需要回复吗
			 String replyClickZan = fromServer.readUTF();
			 zan[panelID].setEnabled(false);//无法再点赞,出错再说？
			 enableZan[panelID] = false;
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 功能：点unzan
	 * 1.根据panelID获取是给哪个来源点unzan
	 * 2.发送点unzan请求数据包给服务器，等待回复
	 * 3.得到回复
	 * 4.点击成功，将该unzan按钮设置为无法点击的状态
	 */
	public boolean clickUnzan(int panelID,JButton[] unzan) {
		String source = "";
		int sourceIndex = 0;
		for(int i = 0; i < 3; i++){
			if(panelID == displayOrder[i]){
				sourceIndex = i;
				break;
			}
		}
		switch(sourceIndex){
			case 0 : source = "baidu";break;
			case 1 : source = "youdao";break;
			case 2 : source = "bing";break;
		}
		try {
			//send package
			 toServer.writeUTF("quz" + currentUser.getName() + "^" + currentEntry.getKeyword() + "^" +source);
			 toServer.flush();
			 //recv package 
			 String replyClickUnzan = fromServer.readUTF();
			 unzan[panelID].setEnabled(false);
			 enableUnzan[panelID] = false;
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 功能：发送单词卡
	 * 1.选中1个在线用户
	 * 2.发送请求发送单词卡数据包给服务器
	 * 3.接收回复，是否发卡成功
	 */
	public boolean sendCard(int panelID, JTextField [] whoToSend) {
		String source = "";
		int sourceIndex = 0;
		for(int i = 0; i < 3; i++){
			if(panelID == displayOrder[i]){
				sourceIndex = i;
				break;
			}
		}
		switch(sourceIndex){
			case 0 : source = "baidu";break;
			case 1 : source = "youdao";break;
			case 2 : source = "bing";break;
		}
		String dstUserName = whoToSend[panelID].getText();
		try {
			toServer.writeUTF("qsc" + currentUser.getName() + "^"
									+ dstUserName + "^" 
									+ currentEntry.getKeyword() + "^"
									+ source);
			//发卡成功与否
			String replySendCard = fromServer.readUTF();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 功能：获取单词卡
	 * 1.向服务器发送请求，等待回应
	 * 2.得到服务器回复
	 * 3.将收到的单词卡存在notebook数组中，以便显示
	 */
	public void getCard(){
		notebookNumber = 0;
		try {
			//send
			toServer.writeUTF("qgc" + currentUser.getName());
			toServer.flush();
			//recv
			String replyGetCard = fromServer.readUTF();
			//sender+keyword+info
			String [] temp = replyGetCard.substring(3).split("\\^");
			for(int i = 0; i < temp.length; i = i + 3){
				notebook[notebookNumber] = new String(temp[i] + "^" + temp[i+1] + "^" + temp[i+2]);
				notebookNumber++;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 功能：通过点击主界面refreshOnlineUserList按钮，刷新在线用户列表
	 * 1.向服务器发送请求，等待回复
	 * 2.得到服务器回复，解析数据包，更新在线用户列表
	 */
	public void refreshOnlineUserList(DefaultListModel defaultListModel){
		//刷新当前在线用户列表
		
		try {//send
			toServer.writeUTF("qou" + currentUser.getName());
			toServer.flush();
			//recv
			String replyRefreshPackage = fromServer.readUTF();
			if(replyRefreshPackage.substring(0, 3).equalsIgnoreCase("rou")){
				String [] temp = replyRefreshPackage.substring(3).split("\\^");
				//refresh
				defaultListModel.clear();
				for(int i = 0; i < temp.length; i++){
					defaultListModel.addElement(temp[i]);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
package lab02;

// ju ge lizi
import java.util.*; 
import java.awt.*;
import java.io.*; 

import javax.swing.*;

import java.awt.event.*;
import java.net.*;
import java.awt.event.KeyEvent;

import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.FontUIResource;

import java.io.IOException;



public class Client extends JFrame{
	private JButton login = new JButton("login");			//登陆按钮
	private JButton register = new JButton("register");	//注册按钮
	private JLabel title = new JLabel("My Diki");			//词典名字
	private JButton note = new JButton("note");			//单词本按钮
	private JButton logout = new JButton("logout");
	 
	private JTextField input = new JTextField(); 			//输入文本框
	private JButton search = new JButton("search");		//search 按钮
	
	private JCheckBox baidu = new JCheckBox("百度",true);		//三个复选框
	private JCheckBox youdao = new JCheckBox("有道",true);
	private JCheckBox biying = new JCheckBox("必应",true);
	
	DefaultListModel defaultListModel = new DefaultListModel();
	private JList onlineUserList = new JList();			//在线用户列表
	private JScrollPane scrollPane = new JScrollPane(onlineUserList);		//列表的滚动
	
	private JTextArea resultA = new JTextArea(5,20);		//第一个网站的搜索结果显示文本区域
	private JScrollPane scrollPaneA = new JScrollPane(resultA);	//滚轮
	private JTextField whoToSendA = new JTextField("who to send");//显示给谁发单词卡的文本框
	private JButton zanA = new JButton("zan");		//点赞按钮
	private JButton unzanA = new JButton("unzan");	//点不赞按钮
	private JButton sendCardA = new JButton("send card");	//发送单词卡按钮
	
	private JTextArea resultB = new JTextArea(5,20);		//第二个网站的搜索结果显示文本区域(与A类似)
	private JScrollPane scrollPaneB = new JScrollPane(resultB);
	private JTextField whoToSendB = new JTextField("who to send");
	private JButton zanB = new JButton("zan");
	private JButton unzanB = new JButton("unzan");
	private JButton sendCardB = new JButton("send card");
	
	private JTextArea resultC = new JTextArea(5,20);		//第三个网站的搜索显示文本区域(与A类似)
	private JScrollPane scrollPaneC = new JScrollPane(resultC);
	private JTextField whoToSendC = new JTextField("who to send");
	private JButton zanC = new JButton("zan");
	private JButton unzanC = new JButton("unzan");
	private JButton sendCardC = new JButton("send card");

	private User currentUser; // 当前用户
	private String[] notebook; // 单词本
	private Entry currentEntry; //当前词条
    
	//登陆面板
	JFrame loginFrame = new JFrame();//登陆窗口
	JLabel loginUserName = new JLabel("User Name");
	JTextField jtfLoginUserName = new JTextField(8);
	JLabel loginPassword = new JLabel("Password");
	JTextField jtfLoginPassword = new JTextField(8);
	JButton lfLogin = new JButton("login");//登陆面板的登陆按钮
	JButton lfCancel = new JButton("cancel");//登陆面板的取消按钮
	
	//注册面板
	JFrame registerFrame = new JFrame();
	JLabel regUserName = new JLabel("User Name");
	JTextField jtfRegUserName = new JTextField(8);
	JLabel regPassword = new JLabel("Password");
	JTextField jtfRegPassword = new JTextField(8);
	JLabel regPasswordConfirm = new JLabel("Password Confirm");
	JTextField jtfRegPasswordConfirm = new JTextField(8);
	JButton rfRegister = new JButton("register");
	JButton rfCancel = new JButton("cancel");
	
	//shownote面板
	JFrame showNoteFrame = new JFrame();
	JLabel noteTitle = new JLabel("1234567890");
	JList noteList = new JList();
	private JScrollPane scrollPaneOfNoteList = new JScrollPane(noteList);	//滚轮
	JTextArea wordExplaination = new JTextArea();
	private JScrollPane scrollPaneOfWordEx = new JScrollPane(wordExplaination);	//滚轮
	
	private Socket socket;
	//net IO stream
	private DataOutputStream toServer;
	private DataInputStream fromServer;
	
	// pops out another that requires user name and password from user input
	private boolean login() {
		/*
		 * pop out a new frame:
		 *   2 new textfields, 2 new buttons "cancel" and "Login"
		 * listener 1 : "cancel"
		 * 	 return false
		 * listener 2 : "login"
		 *   get input(userName and password) from textfield
		 *   send login request to server
		 *     details pending
		 *   wait server to respond
		 *     succeed asserted
		 *   refresh currentUser
		 *   disable visibility of buttons "login" and "register"
		 *   display username and buttons "logout" and "notes"
		 *   refresh onlineUserList and display
		 *   refresh notes                  这个在后面点击note按钮之后再考虑
		 *   close this frame
		 *   return true
		* */
		//return false;
		// if login succeed, change onlineUserList
		
		//实现
		loginFrame.setVisible(true);
		lfLogin.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String userName = jtfLoginUserName.getText();
				System.out.println("username: " + userName);
				String userPassword = jtfLoginPassword.getText();
				System.out.println("password: " + userPassword);
				//发送请求登陆数据包
				StringBuilder requestLoginPackage = new StringBuilder();
				requestLoginPackage.append("q");
				requestLoginPackage.append("li");
				requestLoginPackage.append(userName);
				requestLoginPackage.append("^");
				requestLoginPackage.append(userPassword);
				System.out.println("send package:" + requestLoginPackage.toString());
				String replyLoginPackage;
				try {
					//send package to server
					toServer.writeUTF(requestLoginPackage.toString());
					toServer.flush();
					//System.out.println("send package!" + requestLoginPackage.toString());
					
					
					//get package from server
					replyLoginPackage = fromServer.readUTF();
					System.out.println("receive pavkage: " + replyLoginPackage);
					//fresh the onlineUserList
					defaultListModel.clear();
					System.out.println("0");
	    			//onlineUserList.setModel(defaultListModel);
	    			if(replyLoginPackage.substring(0, 3).equalsIgnoreCase("rli")){
	    				//说明收到的是回复登陆数据包
	    				System.out.println("1");
	    				String temp = replyLoginPackage.substring(3);//获取用户名字符串
	    				System.out.println("2");
	    				String [] usersName = temp.split("\\^");
	    				System.out.println("username[0]： " + usersName[0].toString());
	    				if(usersName[0].equalsIgnoreCase("true")){//登陆成功
	    					for(int i = 1; i < usersName.length; i++){
	    						if(usersName[i].length() > 0){
	    							System.out.println(usersName[i]);
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
	    					note.setEnabled(false);
	    				
	    					//login和register禁用
	    					login.setEnabled(false);
	    					login.setEnabled(false);
	    					
	    					//关闭登陆面板
	    					loginFrame.setVisible(false);
	    					
	    				}
	    				else{//登录失败，提示信息用户名或者密码错误
	    					jtfLoginUserName.setText("");
	    					jtfLoginPassword.setText("");
	    					//弹出提示框
	    					JOptionPane.showMessageDialog(null,"登陆失败，请重新输入！");
	    				}
	    			}
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
			}
		});
		
		lfCancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				loginFrame.setVisible(false);//登陆面板不可见
			}
		});
	
		return true;//不知道如何返回false或者true？
	}

	private boolean logout() {
		/*
		 * get username from currentUser
		 * send logout request to server
		 *   details pending
		 * wait server to respond
		 *   succeed asserted
		 * disable currentUser and button "logout"
		 * display buttons "login" and "register"
		 * clear onlineUserList
		 * clear notes
		 * disable buttons "notes"
		 * return true
		 */
		
		//实现
		String userNameString = currentUser.getName();
		StringBuilder requestLogoutPackage = new StringBuilder();
		String replyLogoutPackage;
		requestLogoutPackage.append("qlo");
		requestLogoutPackage.append("^");
		requestLogoutPackage.append(userNameString);
		System.out.println("send request package: " + requestLogoutPackage.toString());
		
		
		try {//send
			toServer.writeUTF(requestLogoutPackage.toString());toServer.flush();
			
			//receive
			replyLogoutPackage = fromServer.readUTF();
			
			if(replyLogoutPackage.substring(0, 2).equalsIgnoreCase("rlo")){//判断如果是logout的回复数据包
				if(replyLogoutPackage.substring(3).equalsIgnoreCase("true")){//如果服务器允许退出，一般也不会不允许的啊= =
					//用户注销，将状态置为false，其他应该可以不用管
					currentUser.setStatus(false);
					//disable currentUser and button "logout" 还没有实现可以再用户登陆之后显示用户姓名的功能？？？？？？
					logout.setEnabled(false);
					//display buttons "login" and "register"
					login.setEnabled(true);register.setEnabled(true);
					//clear onlineUserList
					defaultListModel.clear();
					//clear notes 需要将notebook数组清空吗？？？？？？
					//disable buttons "notes"
					note.setEnabled(false);
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}

	// pops out another panel that requires registration information
	private boolean register() {
		/*
		 * pop out a new frame:
		 *   3 new textfields, 2 new buttons "cancel" and "register"
		 *
		 * listener 1 : "cancel"
		 * 	 return false
		 * listener 2 : "register"
		 *   get input(userName and password, recheck-password) from textfield
		 *   if recheck unsuccess
		 *     clear password fieldS
		 *     do not respond
		 *   else
		 *     send register request to server
		 *       details pending
		 *     wait server to respond
		 *       succeed asserted
		 *     clear current frame
		 *     display message and button "OK"
		 *     listener 3: "OK"
		 *       close this frame
		 *       return true
		* */
		//return false;
		
		//实现如下
		registerFrame.setVisible(true);
		rfRegister.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
		        String userName = jtfRegUserName.getText();
		        String password = jtfRegPassword.getText();
		        String passwordConfirm = jtfRegPasswordConfirm.getText();
		        
		        //判断输入的密码跟确认密码是否一致
		        if(!password.equals(passwordConfirm)){
		        	//不一致，就提示说重新输入密码
		        	jtfRegPassword.setText("");
		        	jtfRegPasswordConfirm.setText("");
		        	JOptionPane.showMessageDialog(null, "注册失败", "请重新注册！", JOptionPane.ERROR_MESSAGE);
		        }
		        else{
		        	StringBuilder requestRegPackage = new StringBuilder();
		        	String replyRegPackage;
		        	requestRegPackage.append("qrg");
		        	requestRegPackage.append(userName);
		        	requestRegPackage.append("^");
		        	requestRegPackage.append(password);
		        	System.out.println("send package: " + requestRegPackage.toString());
		        
		        	try {//send
		        		toServer.writeUTF(requestRegPackage.toString());
		        		
		        		//receive
		        		replyRegPackage = fromServer.readUTF();
		        		System.out.println("recv package: " + replyRegPackage);
		        		if(replyRegPackage.substring(0, 3).equalsIgnoreCase("rrg")){//判断是login的回复数据报
		        			if(replyRegPackage.substring(3).equalsIgnoreCase("true")){//判断是否成功，一般也是成功的吧 = =
		        				registerFrame.setVisible(false);
		        				//弹出提示框，已经注册成功
		        				//System.out.println("弹出提示框！");
		        				JOptionPane.showMessageDialog(null,"注册成功","欢迎您加入diki！",JOptionPane.OK_OPTION);
		        			}
		        			else{//没有成功
		        				//clear password fieldS
		        				JOptionPane.showMessageDialog(null, "注册失败", "请重新注册！", JOptionPane.ERROR_MESSAGE);
		        				jtfRegUserName.setText("");
		        				jtfRegPassword.setText("");
		        				jtfRegPasswordConfirm.setText("");
		        			}
		        		}
					
		        	} catch (IOException e1) {
		        		// TODO Auto-generated catch block
		        		e1.printStackTrace();
		        	}
		        }
			}
		});
		rfCancel.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				registerFrame.setVisible(false);//登陆面板不可见
			}
		});	
		return true;
	}

	// pops out another panel that shows the list of entries received
	private void showNotes() { //待实现
		/*
		 * pop a new frame
		 * display JList(notebook)
		 */
		
		//实现如下
		showNoteFrame.setVisible(true);
	}

	// panelID: which result? A? B? C?
	private boolean clickZan(int panelID) {
		/* get explanation id
		 * send clickZan request to server
		 *   assert success
		 * disable button
		 * change button text to #ofZan
		 * return true
		 *
		 */
		return false;
	}

	private boolean clickUnzan(int panelID) {
		/* get explanation id
		 * send clickUnzan request to server
		 *   assert success
		 * disable button
		 * change button text to #ofUnzan
		 * return true
		 */
		return false;
	}

	private boolean sendCard(int panelID) {
		/* get user name (from textField)
		 * get explanation id
		 * send sendCard request to server
		 *   assert success
		 * return true
		 */
		return false;
	}



	// fills in all result panels
	private void search() {
		//String keyword; /* = textfield.getinput()*/
		/*
		 * if user is online
		 *   send search request to server
		 *   wait server to respond
		 *     assert success
		 *   extract explanation from packet from server
		 *   refresh currentEntry
		 *   display currentEntry according to checkbox
		 *     in the order of #ofZan
		 * else
		 *   send search from online dicts
		 *   refresh currentEntry
		 *   display currentEntry according to checkbox
		 */
		
		//实现
		String keyword = input.getText();
		StringBuilder requestSearchPackage = new StringBuilder();
		String replySearchPackage;
		System.out.println("input: " + keyword);
		if(currentUser !=null && currentUser.isOnline()){//用户不为空并且用户在线
			requestSearchPackage.append("qse");
			requestSearchPackage.append(keyword);
			
			try {//send
				toServer.writeUTF(requestSearchPackage.toString());
				
				//receive
				replySearchPackage = fromServer.readUTF();
				if(replySearchPackage.substring(0, 2).equalsIgnoreCase("rse")){//判断是search回复数据报
					String [] tempStr = replySearchPackage.substring(3).split("\\^");
					String []info = new String[3];
					int index = 0;
					currentEntry = new Entry(tempStr[0]);
					System.out.println("reply: keyword" + tempStr[0]);
					for(int i = 1; i < tempStr.length; i++){
						if(tempStr[i].length() > 0){//判断非空
							info[index] = tempStr[i];
							index++;
						}
					}
					//............................................拆了又合，合了又拆 = =
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{//用户未创建或者用户不在线
			
		}
	}
	
	
	public static void main(String[] args){
		
		/* 下面的这个函数完全从陈冬杰的代码那里复制来的 */
		EventQueue.invokeLater(new Runnable() 
		{
			@Override
			public void run() {
<<<<<<< HEAD
		/*		JFrame.setDefaultLookAndFeelDecorated(true);
=======
			/*	JFrame.setDefaultLookAndFeelDecorated(true);
>>>>>>> d16c0355fe32aaaf13b4388abd17ecff568919ce
				try 
				{
					//* 想要修改皮肤的话，只需要更改，下面这个函数的参数，具体改成什么样，
					// * 可以打开substance.jar, 找到org.jvnet.substance.skin这个包
					// * 将下面的SubstanceDustCoffeeLookAndFeel替换成刚刚打开的包下的任意一个“Substance....LookAndFeel”即可 
					UIManager.setLookAndFeel(new org.jvnet.substance.skin.SubstanceEmeraldDuskLookAndFeel());
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
<<<<<<< HEAD
		*/		//----------------如果想删除substance效果，只保留下面部分--------------------------
=======
			*/	//----------------如果想删除substance效果，只保留下面部分--------------------------
>>>>>>> d16c0355fe32aaaf13b4388abd17ecff568919ce
				Client frame = new Client();
				frame.setResizable(false);
				frame.setSize(600,600);
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("English-Chinese Dictionary");
				frame.setVisible(true);
			}
		});
	}
	
	public Client(){
		/* 主要的四个panel（有的pannel是由更小的panel构成的）
		 * 控件有：登陆按钮，注册按钮，字典名字，单词本按钮
		 * GridLayout 
		 */
		JOptionPane.showMessageDialog(null, "alert", "alert", JOptionPane.ERROR_MESSAGE);
		JPanel logPanel = new JPanel();
		
		/* 控件有： input，输入单词的文本框，search 按钮
		 *      三个网站的复选框(selectSourcePanel (使用FlowLayout))
		 * BorderLayout
		 */
		JPanel searchPanel = new JPanel();
		
		/* 控件有： 在线用户列表，三个网站的搜索结果，其中有单词的解释选择给谁发送单词卡、赞按钮、不赞按钮和发送单词卡按钮
		 * 三个网站的搜索结构(showResultPanel (使用 BorderLayout))       
		 * 每个网站的搜索结构(showPenelA/B/C (使用 BorderLayout))         单词的解释和选择给谁发送单词卡、赞按钮、不赞按钮和发送单词卡按钮
		 * 其中三个按钮和一个文本框(showSelectPanelA/B/C (使用GridLayout)) 选择给谁发送单词卡、赞按钮、不赞按钮和发送单词卡按钮
		 * BorderLayout
		 */
		JPanel showPanel = new JPanel();
		
		/* 以下是更小的panel的定义，在上面已经解释过
		 * 控件有：百度、有道和必应三个复选框
		 */ 
		JPanel selectSourcePanel = new JPanel();
		
		JPanel showResultPanel = new JPanel();
		
		JPanel showPanelA = new JPanel();
		JPanel showSelectPanelA = new JPanel();
		JPanel showPanelB = new JPanel();
		JPanel showSelectPanelB = new JPanel();
		JPanel showPanelC = new JPanel();
		JPanel showSelectPanelC = new JPanel();
		
		logPanel.setLayout(new GridLayout(1,5,40,40));
		logPanel.add(login);
		logPanel.add(logout);
		logout.setEnabled(false);//未登陆时不能使用logout
		logPanel.add(register);
		logPanel.add(title);
		logPanel.add(note);
		note.setEnabled(false);//未登录时不能使用note
        
		selectSourcePanel.setLayout(new FlowLayout());
        selectSourcePanel.add(baidu);
		selectSourcePanel.add(youdao);
		selectSourcePanel.add(biying);
		
		searchPanel.setLayout(new BorderLayout(20,10));
		searchPanel.add(new JLabel("Input"),BorderLayout.WEST);
		searchPanel.add(input,BorderLayout.CENTER);
		searchPanel.add(search,BorderLayout.EAST);
		searchPanel.add(selectSourcePanel,BorderLayout.SOUTH);
		
		showSelectPanelA.setLayout(new GridLayout(4,1));
		showSelectPanelA.add(whoToSendA);
		showSelectPanelA.add(zanA);
		showSelectPanelA.add(unzanA);
		showSelectPanelA.add(sendCardA);
		showPanelA.setLayout(new BorderLayout());
		showPanelA.add(scrollPaneA,BorderLayout.CENTER);
		showPanelA.add(showSelectPanelA,BorderLayout.EAST);
		
		showSelectPanelB.setLayout(new GridLayout(4,1));
		showSelectPanelB.add(whoToSendB);
		showSelectPanelB.add(zanB);
		showSelectPanelB.add(unzanB);
		showSelectPanelB.add(sendCardB);
		showPanelB.setLayout(new BorderLayout());
		showPanelB.add(scrollPaneB,BorderLayout.CENTER);
		showPanelB.add(showSelectPanelB,BorderLayout.EAST);
		
		showSelectPanelC.setLayout(new GridLayout(4,1));
		showSelectPanelC.add(whoToSendC);
		showSelectPanelC.add(zanC);
		showSelectPanelC.add(unzanC);
		showSelectPanelC.add(sendCardC);
		showPanelC.setLayout(new BorderLayout());
		showPanelC.add(scrollPaneC,BorderLayout.CENTER);
		showPanelC.add(showSelectPanelC,BorderLayout.EAST);
		
		showResultPanel.setLayout(new GridLayout(3,1));
		showResultPanel.add(showPanelA);
		showResultPanel.add(showPanelB);
		showResultPanel.add(showPanelC);
		
		showPanel.setLayout(new BorderLayout());
		onlineUserList.setFixedCellWidth(100);
		onlineUserList.setFixedCellHeight(50);
		onlineUserList.setModel(defaultListModel);
		showPanel.add(scrollPane,BorderLayout.WEST);
		showPanel.add(showResultPanel,BorderLayout.CENTER);
		
		setLayout(new BorderLayout(20,20));
		add(logPanel,BorderLayout.NORTH);
		add(searchPanel,BorderLayout.CENTER);
		add(showPanel,BorderLayout.SOUTH);
		
		//以下是其他窗口的部件设置
		//登陆面板设置
		loginFrame.setResizable(false);//禁用最大化
		loginFrame.setSize(300,150);
		loginFrame.setLocationRelativeTo(null);
		loginFrame.setTitle("login");
		loginFrame.setLayout(new GridLayout(3,2,5,5));
		loginFrame.add(loginUserName);
		loginFrame.add(jtfLoginUserName);
		loginFrame.add(loginPassword);
		loginFrame.add(jtfLoginPassword);
		loginFrame.add(lfLogin);
		loginFrame.add(lfCancel);
		
		//注册面板设置
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
		
		//添加login的监听事件，调用login函数
		login.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				login();
			}
		});
		
		//添加register的监听事件
		register.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				register();
			}
		});
		
		//添加note的监听事件
		note.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				showNotes();
			}
		});
		
		//添加输入框监听事件
		input.addKeyListener(new KeyAdapter() {
    		public void keyReleased(KeyEvent e){
    			String word = input.getText();
    			if(e.getKeyCode() == 10){//按下enter键就进行单词查询
    				search();
    			}
    		}
    	}); 
		
		//添加search按钮的监听事件
		search.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				search();
			}
		});
		
		//添加在线用户列表框的监听事件
		onlineUserList.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent e){
                //选中要发送的用户之后，显示在左边三个textfield中
            	 String sendUserName = (String)onlineUserList.getSelectedValue();
            	 whoToSendA.setText(sendUserName);
            	 whoToSendB.setText(sendUserName);
            	 whoToSendC.setText(sendUserName);
            }     
        });
		
		//添加zan的监听事件（三个panel）
		zanA.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clickZan(0);
			}
		});
		
		zanB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clickZan(1);
			}
		});
		
		zanC.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clickZan(2);
			}
		});
		
		//添加unzan的监听事件（三个panel）
		unzanA.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clickUnzan(0);
			}
		});
		
		unzanB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clickUnzan(1);
			}
		});
		
		unzanC.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clickUnzan(2);
			}
		});
		
		//添加sendCard的监听事件（三个panel）
		sendCardA.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				sendCard(0);
			}
		});
		
		sendCardB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				sendCard(1);
			}
		});
		
		sendCardC.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				sendCard(2);
			}
		});
		
		try{//create a socket to connect to the server
			
			socket = new Socket("114.212.129.39",23333);
			//System.out.println(socket.getInetAddress().getAddress());
			
			//create an input stream to receive data from the server
			fromServer = new DataInputStream(socket.getInputStream());
			
			//create an output stream to send data to the server
			toServer = new DataOutputStream(socket.getOutputStream());
			//toServer.write(123);
			//toServer.write(123);
			
			
		}
		catch(IOException ex){
			System.out.println(ex.toString());
		}
		
	}
	
}

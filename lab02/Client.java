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
	private JCheckBox bing = new JCheckBox("必应",true);
	
	DefaultListModel defaultListModel = new DefaultListModel();
	private JList onlineUserList = new JList();			//在线用户列表
	private JScrollPane scrollPane = new JScrollPane(onlineUserList);		//列表的滚动
	
	private JTextArea [] result;		//3个网站的搜索结果显示文本区域
	private JScrollPane [] scrollpane;	//滚轮
	private JTextField [] whoToSend;//显示给谁发单词卡的文本框
	private JButton [] zan;		//点赞按钮
	private JButton [] unzan;	//点不赞按钮
	private JButton [] sendCard;	//发送单词卡按钮

	private User currentUser; // 当前用户
	private String[] notebook; // 单词本
	private Entry currentEntry; //当前词条
	private OnlineSearcher onlineSearcher;
    
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
	JLabel noteTitle = new JLabel("My notebook");
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
	    					System.out.println("当前在线用户： " + currentUser.getName());
	    					
	    					//logout和note可以使用
	    					logout.setEnabled(true);
	    					note.setEnabled(true);
	    				
	    					//login和register禁用
	    					login.setEnabled(false);
	    					register.setEnabled(false);
	    					
	    					//关闭登陆面板
	    					jtfLoginUserName.setText("");
	    					jtfLoginPassword.setText("");
	    					loginFrame.setVisible(false);
	    					JOptionPane.showMessageDialog(null,"登陆成功，欢迎您！","登录",JOptionPane.OK_OPTION);
	    					
	    				}
	    				else{//登录失败，提示信息用户名或者密码错误
	    					jtfLoginUserName.setText("");
	    					jtfLoginPassword.setText("");
	    					//弹出提示框
	    					JOptionPane.showMessageDialog(null,"登陆失败，请重新输入！","登录",JOptionPane.ERROR_MESSAGE);
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
				jtfLoginUserName.setText("");
				jtfLoginPassword.setText("");
				loginFrame.setVisible(false);//登陆面板不可见
			}
		});
	
		return true;//不知道如何返回false或者true？
	}

	private boolean logout() {
		System.out.println("start logout!");
		String userNameString = currentUser.getName();
		System.out.println("currentUser: " + userNameString);
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
			
			if(replyLogoutPackage.substring(0, 3).equalsIgnoreCase("rlo")){//判断如果是logout的回复数据包
				if(replyLogoutPackage.substring(3).equalsIgnoreCase("true")){//如果服务器允许退出，一般也不会不允许的啊= =
					//用户注销，将状态置为false，其他应该可以不用管
					currentUser.setStatus(false);
					//disable currentUser and button "logout" 还没有实现可以再用户登陆之后显示用户姓名的功能？？？？？？
					logout.setEnabled(false);
					//display buttons "login" and "register"
					login.setEnabled(true);
					register.setEnabled(true);
					//clear onlineUserList
					defaultListModel.clear();
					//clear notes 需要将notebook数组清空吗？？？？？？
					//disable buttons "notes"
					note.setEnabled(false);
					JOptionPane.showMessageDialog(null,"注销成功，goodbye~","注销",JOptionPane.OK_OPTION);
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
		        	JOptionPane.showMessageDialog(null, "注册失败，请重新确认密码！","注册",  JOptionPane.ERROR_MESSAGE);
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
		        				jtfRegUserName.setText("");
		        				jtfRegPassword.setText("");
		        				jtfRegPasswordConfirm.setText("");
		        				registerFrame.setVisible(false);
		        				//弹出提示框，已经注册成功
		        				//System.out.println("弹出提示框！");
		        				JOptionPane.showMessageDialog(null,"注册成功，欢迎您加入diki！","注册",JOptionPane.OK_OPTION);
		        			}
		        			else{//没有成功
		        				//clear password fieldS
		        				jtfRegUserName.setText("");
		        				jtfRegPassword.setText("");
		        				jtfRegPasswordConfirm.setText("");
		        				JOptionPane.showMessageDialog(null,"注册失败，请重新注册！","注册", JOptionPane.ERROR_MESSAGE);
		        				
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
				jtfRegUserName.setText("");
				jtfRegPassword.setText("");
				jtfRegPasswordConfirm.setText("");
				registerFrame.setVisible(false);//登陆面板不可见
			}
		});	
		return true;
	}

	// pops out another panel that shows the list of entries received
	private void showNotes() { 
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
		onlineSearcher = new OnlineSearcher();
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
				if(replySearchPackage.substring(0, 3).equalsIgnoreCase("rse")){//判断是search回复数据报
					String [] tempStr = replySearchPackage.substring(3).split("\\^");
					String []info = new String[3];
					int index = 0;
					currentEntry = new Entry(tempStr[0]);//给当前词条进行赋值 keyword
					System.out.println("reply: keyword" + tempStr[0]);
					for(int i = 1; i < tempStr.length && index < 3; i++){
						if(tempStr[i].length() > 0){//判断非空
							info[index] = tempStr[i];
							index++;
						}
					}
					//............................................拆了又合，合了又拆 = =
					//现在来处理3个info,就当做没有空格吧
					for(int i = 0; i < 3; i++){
						String [] ex = info[i].split("\\$");//?
						//for(int j = 0; j < 6; j++){
							//还有赞和不赞的初始化没有写？
							Information information = new Information(ex[0],ex[1],ex[2],ex[3]);
							currentEntry.setInformation(ex[0],information);
							
						//}
					}
					//下面控制zan和unzan按钮是否能够使用，就是用户是否已经点过这个单词的zan或者unzan
					if(tempStr[4].equalsIgnoreCase("false"))  zan[0].setEnabled(false);
					if(tempStr[5].equalsIgnoreCase("false"))  zan[1].setEnabled(false);
					if(tempStr[6].equalsIgnoreCase("false"))  zan[2].setEnabled(false);
					if(tempStr[7].equalsIgnoreCase("false"))  unzan[0].setEnabled(false);
					if(tempStr[8].equalsIgnoreCase("false"))  unzan[1].setEnabled(false);
					if(tempStr[9].equalsIgnoreCase("false"))  unzan[2].setEnabled(false);
					//解释显示在面板上
					displayExplaination();
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{//用户未创建或者用户不在线
			currentEntry = onlineSearcher.search(keyword);
			int [] displayOrder = {0,1,2};
			displayThreePanel(displayOrder);
		}
	}
	
	public void displayThreePanel(int [] disOrder){
		for(int i = 0; i < 3; i++){
			result[i].setText("");
		}
		int index = 0;//标记现在应该在哪一块面板上面显示了
		for(int i = 0; i < 3; i++){
			//先判断是否勾选了该来源(顺序是baidu,youdao,bing)
			//用户在线就显示赞和不赞的数目，否则不显示
			boolean isSelected = true;
			String [] ex = currentEntry.getInformation(disOrder[i]).toString().split("\\$");
			System.out.println(currentEntry.getInformation(disOrder[i]).toString());
			switch(ex[0]){
				case "baidu": if(!baidu.isSelected())  isSelected = false;break;
				case "youdao": if(!youdao.isSelected())  isSelected = false;break;
				case "bing": if(!bing.isSelected())  isSelected = false;break;
			}
			
			if(isSelected){
				//选中了就进行显示
				result[index].append(ex[0] + '\n');
				//keyword
				result[index].append(currentEntry.getKeyword() + '\n');
				//音标
				String [] pho = ex[1].split("#");
				for(int j = 0; j < pho.length; j++){
					result[index].append(pho[j] + ",");
				}
				result[index].append("\n");
				//词性
				String [] attri = ex[2].split("#");
				//解释
			    String [] exp = ex[3].split("#");
			    for(int k = 0; k < attri.length; k++){
			    	result[index].append(attri[k] + " " + exp[k] + '\n');
			    }
			    if(currentUser!= null && currentUser.isOnline()){
			    	//zan
			    	int currentZan = currentEntry.getInformation(disOrder[0]).getZan();
			    	result[index].append("zan: " + currentZan + "\n");
			    	//unzan
			    	int currentUnzan = currentEntry.getInformation(disOrder[0]).getUnzan();
			    	result[index].append("zan: " + currentUnzan + "\n");
			    }
				//下一次要显示哪一块面板
				index++;
			}
		}
	}
	
	public void displayExplaination(){//进行排序后显示
		//将获取的释义显示在面板上
		//根据zan和unzan的数量进行排序显示
		//用123代表三个information
		int []zanSum = new int[3];//zan和unzan综合
		int []dis = {0,1,2};//显示顺序
		zanSum[0] = currentEntry.getInformation("baidu").getZan() - currentEntry.getInformation("baidu").getUnzan(); 
		zanSum[1] = currentEntry.getInformation("youdao").getZan() - currentEntry.getInformation("youdao").getUnzan();
		zanSum[2] = currentEntry.getInformation("bing").getZan() - currentEntry.getInformation("bing").getUnzan();
	    for(int i = 0; i < 2; i++){
	    	for(int j = i+1; j < 3; j++){
	    		if(zanSum[i] < zanSum[j]){
	    			int temp = dis[j];
	    			dis[j] = dis[i];
	    			dis[i] = temp;
	    		}
	    	}
	    }
	    System.out.print("Display Order: ");
	    for(int i = 0; i < 3; i++){
	    	System.out.println(i + '\t');
	    }
	    System.out.println();
		displayThreePanel(dis);
	}
	
	public static void main(String[] args){
		
		/* 下面的这个函数完全从陈冬杰的代码那里复制来的 */
		/*		JFrame.setDefaultLookAndFeelDecorated(true);
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
		*/		//----------------如果想删除substance效果，只保留下面部分--------------------------
				Client frame = new Client();
				frame.setResizable(false);
				frame.setSize(600,600);
				frame.setLocationRelativeTo(null);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setTitle("DIKI");
				frame.setVisible(true);
			}
	
	public Client(){
		result = new JTextArea[3];
		scrollpane = new JScrollPane[3];
		whoToSend = new JTextField[3];
		zan = new JButton[3];
		unzan = new JButton[3];
		sendCard = new JButton[3];
		for(int i = 0; i < 3; i++){
			result[i] = new JTextArea(5,20);
			scrollpane[i] = new JScrollPane(result[i]);
			whoToSend[i] = new JTextField("who to send");
			zan[i] = new JButton("zan");
			unzan[i] = new JButton("unzan");
			sendCard[i] = new JButton("send card");
		}
		
		/* 主要的四个panel（有的pannel是由更小的panel构成的）
		 * 控件有：登陆按钮，注册按钮，字典名字，单词本按钮
		 * GridLayout 
		 */
		
		JOptionPane.showMessageDialog(null, "begin...","Diki", JOptionPane.OK_OPTION);
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
		selectSourcePanel.add(bing);
		
		searchPanel.setLayout(new BorderLayout(20,10));
		searchPanel.add(new JLabel("Input"),BorderLayout.WEST);
		searchPanel.add(input,BorderLayout.CENTER);
		searchPanel.add(search,BorderLayout.EAST);
		searchPanel.add(selectSourcePanel,BorderLayout.SOUTH);
		
		showSelectPanelA.setLayout(new GridLayout(4,1));
		showSelectPanelA.add(whoToSend[0]);
		showSelectPanelA.add(zan[0]);
		showSelectPanelA.add(unzan[0]);
		showSelectPanelA.add(sendCard[0]);
		showPanelA.setLayout(new BorderLayout());
		showPanelA.add(scrollpane[0],BorderLayout.CENTER);
		showPanelA.add(showSelectPanelA,BorderLayout.EAST);
		
		showSelectPanelB.setLayout(new GridLayout(4,1));
		showSelectPanelB.add(whoToSend[1]);
		showSelectPanelB.add(zan[1]);
		showSelectPanelB.add(unzan[1]);
		showSelectPanelB.add(sendCard[1]);
		showPanelB.setLayout(new BorderLayout());
		showPanelB.add(scrollpane[1],BorderLayout.CENTER);
		showPanelB.add(showSelectPanelB,BorderLayout.EAST);
		
		showSelectPanelC.setLayout(new GridLayout(4,1));
		showSelectPanelC.add(whoToSend[2]);
		showSelectPanelC.add(zan[2]);
		showSelectPanelC.add(unzan[2]);
		showSelectPanelC.add(sendCard[2]);
		showPanelC.setLayout(new BorderLayout());
		showPanelC.add(scrollpane[2],BorderLayout.CENTER);
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
		
		logout.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				logout();
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
    			else{//在输入和撤销单词过程中将显示框清除
    				for(int i = 0; i < 3; i++){
    					result[i].setText("");
    				}
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
            	 whoToSend[0].setText(sendUserName);
            	 whoToSend[1].setText(sendUserName);
            	 whoToSend[2].setText(sendUserName);
            }     
        });
		
		//添加zan的监听事件（三个panel）
		zan[0].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clickZan(0);
			}
		});
		
		zan[1].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clickZan(1);
			}
		});
		
		zan[2].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clickZan(2);
			}
		});
		
		//添加unzan的监听事件（三个panel）
		unzan[0].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clickUnzan(0);
			}
		});
		
		unzan[1].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clickUnzan(1);
			}
		});
		
		unzan[2].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clickUnzan(2);
			}
		});
		
		//添加sendCard的监听事件（三个panel）
		sendCard[0].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				sendCard(0);
			}
		});
		
		sendCard[1].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				sendCard(1);
			}
		});
		
		sendCard[2].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				sendCard(2);
			}
		});
		
		//复选框的监听器
		baidu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String keyword = input.getText();
				if(currentEntry != null && currentEntry.getKeyword().equalsIgnoreCase(keyword) 
						&& currentUser != null && currentUser.isOnline() )
					displayExplaination();
				else if(currentEntry != null && currentEntry.getKeyword().equalsIgnoreCase(keyword) ){
					//不在线
					int [] disOrder = {0,1,2};
					displayThreePanel(disOrder);
				}
			}
		});
		
		youdao.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String keyword = input.getText();
				if(currentEntry != null && currentEntry.getKeyword().equalsIgnoreCase(keyword) 
						&& currentUser != null && currentUser.isOnline() )
					displayExplaination();
				else if(currentEntry != null && currentEntry.getKeyword().equalsIgnoreCase(keyword) ){
					//不在线
					int [] disOrder = {0,1,2};
					displayThreePanel(disOrder);
				}
			
			}
		});
		
		bing.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String keyword = input.getText();
				if(currentEntry != null && currentEntry.getKeyword().equalsIgnoreCase(keyword) 
						&& currentUser != null && currentUser.isOnline() )
					displayExplaination();
				else if(currentEntry != null && currentEntry.getKeyword().equalsIgnoreCase(keyword) ){
					//不在线
					int [] disOrder = {0,1,2};
					displayThreePanel(disOrder);
				}
			}
		});
		
		try{//create a socket to connect to the server
			
			socket = new Socket("114.212.132.146",23333);//yushen:114.212.129.39
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

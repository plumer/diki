package lab02;
/**
 * client中具体功能函数实现
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
	
	ClientBackground(){
		try{//create a socket to connect to the server
			
			socket = new Socket("114.212.129.39",23333);//yushen:114.212.129.39
			//System.out.println(socket.getInetAddress().getAddress());
			
			//create an input stream to receive data from the server
			fromServer = new DataInputStream(socket.getInputStream());
			
			//create an output stream to send data to the server
			toServer = new DataOutputStream(socket.getOutputStream());
		}
		catch(Exception ex){
			ex.printStackTrace();
		}		
	}
	
	/**
	 * 主界面的login按键触发的事件
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
				//curUser = getUser();
				String userName = jtfLoginUserName.getText();
				System.out.println("login username: " + userName);
				String userPassword = String.valueOf(jtfLoginPassword.getPassword());
				System.out.println("login password: " + userPassword);
				loginConfirm(userName,userPassword,defaultListModel,
						logout,register,note,refreshOnlineUserList,login,
						sendCard,lfLogin,loginFrame,jtfLoginUserName,jtfLoginPassword);
			
			}
		});
		
		jtfLoginPassword.addKeyListener(new KeyAdapter() {
    		public void keyReleased(KeyEvent e){
    			if(e.getKeyCode() == 10){//按下enter键就进行单词查询
    				String userName = jtfLoginUserName.getText();
    				System.out.println("login username: " + userName);
    				String userPassword = String.valueOf(jtfLoginPassword.getPassword());
    				System.out.println("login password: " + userPassword);
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
	 * 按下登陆界面的确认键引发的事件
	 */
	public void loginConfirm(String userName,String userPassword,DefaultListModel defaultListModel,
			JButton logout,JButton register,JButton note,JButton refreshOnlineUserList,JButton login,
			JButton []sendCard,JButton lfLogin,JFrame loginFrame,JTextField jtfLoginUserName,JPasswordField jtfLoginPassword){
		//发送请求登陆数据包
		String replyLoginPackage;
		try {
			//send package to server
			toServer.writeUTF("qli" + userName + "^" + userPassword);
			toServer.flush();
			System.out.println("send package: " + "qli" + userName + "^" + userPassword);
			
			//recv package from server
			replyLoginPackage = fromServer.readUTF();
			System.out.println("receive pavkage: " + replyLoginPackage);
			
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
					//jtfLoginUserName.setText("");
					//jtfLoginPassword.setText("");
					loginFrame.setVisible(false);
					JOptionPane.showMessageDialog(null,"登陆成功，欢迎您！","登录",JOptionPane.OK_OPTION);
					
					//return currentUser;
					
				}
				else{//登录失败，提示信息用户名或者密码错误
					jtfLoginUserName.setText("");
					jtfLoginPassword.setText("");
					//弹出提示框
					JOptionPane.showMessageDialog(null,"登陆失败，请重新输入！","登录",JOptionPane.ERROR_MESSAGE);
					
				}
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//return currentUser;
	}
	
	/**
	 * 主界面的logout按键触发的事件
	 */
	public void logout(JButton login,JButton logout,JButton register,JButton note,
			JButton []zan,JButton[] unzan,JButton[] sendCard,JTextField[] whoToSend,JButton refreshOnlineUserList, 
			DefaultListModel defaultListModel) {
		if(currentUser != null &&currentUser.isOnline()){//判断有用户在线
		System.out.println("start logout!");
		String userNameString = currentUser.getName();
		System.out.println("currentUser: " + userNameString);
		StringBuilder requestLogoutPackage = new StringBuilder();
		String replyLogoutPackage;
		requestLogoutPackage.append("qlo");
		requestLogoutPackage.append(userNameString);
		System.out.println("send request package: " + requestLogoutPackage.toString());
		
		
		try {//send
			toServer.writeUTF(requestLogoutPackage.toString());toServer.flush();
			toServer.flush();
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
					login.setText("login");
					register.setEnabled(true);
					//lfLogin.setEnabled(true);
					//clear onlineUserList
					defaultListModel.clear();
					//clear notes 需要将notebook数组清空吗？？？？？？
					//disable buttons "notes"
					note.setEnabled(false);
					for(int i = 0; i < 3; i++){
						whoToSend[i].setText("");
						zan[i].setEnabled(false);
						unzan[i].setEnabled(false);
						sendCard[i].setEnabled(false);
						//result[i].setText("");
						//input.setText("");;
					}
					refreshOnlineUserList.setEnabled(false);
					JOptionPane.showMessageDialog(null,"注销成功，goodbye~","注销",JOptionPane.OK_OPTION);
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}
	
	/**
	 * 主界面register键按下之后触发的事件
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
				System.out.println("userName: " + userName);
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
    				System.out.println("userName: " + userName);
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
	 * register界面按下确认键引发的事件
	 */
	public void registerConfirm(String userName,String password,String passwordConfirm,
			JFrame registerFrame,JTextField jtfRegUserName,JTextField jtfRegPassword,JTextField jtfRegPasswordConfirm){
        //判断输入的密码跟确认密码是否一致
        if(!password.equals(passwordConfirm)){
        	//不一致，就提示说重新输入密码
        	jtfRegPassword.setText("");
        	jtfRegPasswordConfirm.setText("");
        	JOptionPane.showMessageDialog(null, "注册失败，请重新确认密码！","注册",  JOptionPane.ERROR_MESSAGE);
        }
        else{
        	if(userName.length() <= 20 && password.length() <= 20){//用户名和密码不超过20个字符)
        	StringBuilder requestRegPackage = new StringBuilder();
        	String replyRegPackage;
        	requestRegPackage.append("qrg");
        	requestRegPackage.append(userName);
        	requestRegPackage.append("^");
        	requestRegPackage.append(password);
        	System.out.println("send package: " + requestRegPackage.toString());
        
        	try {//send
        		toServer.writeUTF(requestRegPackage.toString());
        		toServer.flush();
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
	 * 主界面按下search按键之后触发的事件
	 */
	public void search(String keyword, JButton [] zan,JButton [] unzan,
			JTextArea[] result, JCheckBox baidu,JCheckBox youdao,JCheckBox bing,
			JPanel [] showThreePanel,JPanel [] showSelectPanel, JButton [] sendCard) {
		System.out.println("Start search");
		onlineSearcher = new OnlineSearcher();
		//String keyword = input.getText().trim();
		//用正则表达式判断输入的是一个英文单词
		String normalInput = "[a-zA-Z]+";
		if(keyword.matches(normalInput)){
		StringBuilder requestSearchPackage = new StringBuilder();
		String replySearchPackage;
		System.out.println("input: " + keyword);
		//f(currentUser == null ) System.out.println("nononono0!");
		//if(!currentUser.isOnline()) System.out.println("nononono1!");
		if(currentUser != null && currentUser.isOnline()){//用户不为空并且用户在线
			requestSearchPackage.append("qse");
			requestSearchPackage.append(keyword);
			System.out.println("q:search online: " + requestSearchPackage.toString());
			try {
				//send
				toServer.writeUTF(requestSearchPackage.toString());
				toServer.flush();
				//receive
				   replySearchPackage = fromServer.readUTF();
				System.out.println("r:search online: " + replySearchPackage);
				if(replySearchPackage.substring(0, 3).equalsIgnoreCase("rse")){//判断是search回复数据报
					String [] tempStr = replySearchPackage.substring(3).split("\\^");
					String [] info = new String[3];
					int index = 0;
					currentEntry = new Entry(tempStr[0]);//给当前词条进行赋值 keyword
					//System.out.println("reply: keyword" + tempStr[0]);
					for(int i = 1; i < tempStr.length && index < 3; i++){
						if(tempStr[i].length() > 0){//判断非空
							info[index] = tempStr[i];
							index++;
						}
					}
					for(int i = 0; i < 3; i++){
							System.out.println("info" + i + ": " + info[i]);
							String [] ex = info[i].split("\\$");
							System.out.println(ex[0]);//source
							System.out.println(ex[1]);//yinbiao
							System.out.println(ex[2]);//cixing
							System.out.println(ex[3]);//jieshi
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
					if(tempStr[4].equalsIgnoreCase("true"))  zan[displayOrder[0]].setEnabled(false);
					else									  zan[displayOrder[0]].setEnabled(true);
					if(tempStr[5].equalsIgnoreCase("true"))  zan[displayOrder[1]].setEnabled(false); 
					else									  zan[displayOrder[1]].setEnabled(true);
					if(tempStr[6].equalsIgnoreCase("true"))  zan[displayOrder[2]].setEnabled(false);
					else									  zan[displayOrder[2]].setEnabled(true);
					if(tempStr[7].equalsIgnoreCase("true"))  unzan[displayOrder[0]].setEnabled(false);
					else									  unzan[displayOrder[0]].setEnabled(true);
					if(tempStr[8].equalsIgnoreCase("true"))  unzan[displayOrder[1]].setEnabled(false);
					else									  unzan[displayOrder[1]].setEnabled(true);
					if(tempStr[9].equalsIgnoreCase("true"))  unzan[displayOrder[2]].setEnabled(false);
					else									  unzan[displayOrder[2]].setEnabled(true);
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
			//System.out.println("输入不合法，不是一个英文单词！");
			JOptionPane.showMessageDialog(null, "您输入的单词不合法，请重新输入！");
			//input.setText("");
		}
	}
	
	public void displayThreePanel(JTextArea [] result, JCheckBox baidu,JCheckBox youdao,JCheckBox bing,
			JPanel [] showThreePanel, JPanel [] showSelectPanel,
			JButton [] sendCard, JButton [] zan,JButton [] unzan){
		for(int i = 0; i < 3; i++){
			result[i].setText("");
		}
		//int index = 0;//标记现在应该在哪一块面板上面显示了
		for(int i = 0; i < 3; i++){
			//先判断是否勾选了该来源(顺序是baidu,youdao,bing)
			//用户在线就显示赞和不赞的数目，否则不显示
			//获取0（baidu）1（youdao）2（bing）显示的面板标号
			int panelIndex = displayOrder[i];
			
			boolean isSelected = true;
			String [] ex = currentEntry.getInformation(i).toString().split("\\$");
			System.out.println(currentEntry.getInformation(i).toString());
			switch(ex[0]){
				case "baidu": if(!baidu.isSelected())  isSelected = false;break;
				case "youdao": if(!youdao.isSelected())  isSelected = false;break;
				case "bing": if(!bing.isSelected())  isSelected = false;break;
			}
			System.out.println("panelindex: " + panelIndex);
			System.out.println("xianshi: " + ex[0]);
			showThreePanel[panelIndex].setBorder(BorderFactory.createTitledBorder (ex[0]));
			showSelectPanel[panelIndex].setBorder(BorderFactory.createTitledBorder (ex[0]));
			if(isSelected){
				//选中了就进行显示
				//result[ panelIndex].append(ex[0] + '\n');
				//keyword
				result[ panelIndex].append(currentEntry.getKeyword() + '\n');
				//音标
				if(!ex[1].equalsIgnoreCase("null")){
					//System.out.println("yinbiao length: " + ex[1].length() + " " + ex[1]);
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
				//有个问题，怎么样算此不存在呢，不存在是不是也不能够点赞？OMG
				
			    if(currentUser!= null && currentUser.isOnline()){
			    	//zan
			    	//int currentZan = Integer.parseInt(ex[4]);
			    	System.out.println("zan: " + zanSum[i]);
			    	result[panelIndex].append("zan: " + zanSum[i] + "\n");
			    	//unzan
			    	//int currentUnzan = Integer.parseInt(ex[5]);
			    	System.out.println("unzan: " + unzanSum[i]);
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
	
	public void displayOrderSort(){//进行排序后显示
		//将获取的释义显示在面板上
		//根据zan和unzan的数量进行排序显示
		//用123代表三个information
		int []allzanSum = new int[3];//zan和unzan综合
		//int []dis = {0,1,2};//显示顺序
		allzanSum[0] = zanSum[0] -  unzanSum[0];
		System.out.println("baidu: " + zanSum[0] + " - " + unzanSum[0] + " = " + allzanSum[0] );
		allzanSum[1] = zanSum[1] -  unzanSum[1];
		System.out.println("youdao: " + zanSum[1] + " - " + unzanSum[1]+ " = " + allzanSum[1] );
		allzanSum[2] = zanSum[2] -  unzanSum[2];
		System.out.println("bing: " + zanSum[2] + " - " + unzanSum[2] + " = " + allzanSum[2] );
		int [] index = {0,1,2};//每个面板对应的来源
		int maxIndex = 0;
		for(int i = 0; i < 3; i++){
			if(allzanSum[i] >= allzanSum[maxIndex]) maxIndex = i;
		}
		index[0] = maxIndex;
		displayOrder[maxIndex] = 0;
		System.out.println("maxIndex: " + maxIndex);
		
		int secondMaxIndex = 0;
		for(int i = 0; i < 3 ; i++){
			if(maxIndex == 0) {secondMaxIndex = 1;continue;}
			if(allzanSum[i] >= allzanSum[secondMaxIndex] && i != maxIndex) secondMaxIndex = i;
		}
		index[1] = secondMaxIndex;
		displayOrder[secondMaxIndex] = 1;
		System.out.println("secondMaxIndex: " + secondMaxIndex);
		for(int i = 0; i < 3; i++){
			if(i != maxIndex && i != secondMaxIndex)  {index[2] = i;displayOrder[i] = 2;System.out.println("last: " + i);}
		}

	    System.out.println("Display Order: "+index[0]+" "+index[1]+" "+index[2]);
	    
	    for(int i = 0; i < 3; i++){
	    	switch(i){
	    		case 0: System.out.println("baidu");break;
	    		case 1: System.out.println("youdao");break;
	    		case 2: System.out.println("bing");break;
	    	}
	    	System.out.println("显示在面板" + displayOrder[i]);
	    }
	    System.out.println();
	}
	
	/**
	 * 显示单词本
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
			System.out.println(temp[1]);
		}
		
		//将note中的单词加入列表中，点击即可显示具体内容
		noteList.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent e){
                selectWord(noteList, wordExplaination);
            }     
        });
		showNoteFrame.setVisible(true);
	}

	public void selectWord(JList noteList, JTextArea wordExplaination){
		//选中要发送的用户之后，显示在左边三个textfield中
		wordExplaination.setText("");
    	int selectedIndex = noteList.getSelectedIndex();//返回选中多少行
    	String[] temp = notebook[selectedIndex].split("\\^");
    	//来自哪个用户 temp[0]
    	//keyword temp[1]
    	//info 	temp[2]
    	//System.out.println("temp[0]: " + temp[0]);
    	//System.out.println("temp[1]: " + temp[1]);
    	//System.out.println("temp[2]: " + temp[2]);
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
	
	// panelID: which result? A? B? C?
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
			 System.out.println("zan: " + "qza" + currentUser.getName() + "^" + currentEntry.getKeyword() + "^" +source);
			 toServer.flush();
			 //recv package ???需要回复吗
			 String replyClickZan = fromServer.readUTF();
			 System.out.println(replyClickZan);
			 zan[panelID].setEnabled(false);//无法再点赞,出错再说？
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

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
			 System.out.println("unzan:" + "quz" + currentUser.getName() + "^" + currentEntry.getKeyword() + "^" +source);
			 toServer.flush();
			 //recv package ???需要回复吗
			 String replyClickUnzan = fromServer.readUTF();
			 System.out.println(replyClickUnzan);
			 unzan[panelID].setEnabled(false);//无法再点赞,出错再说？
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

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
		System.out.println("sendcard " + panelID + " to " + dstUserName);
		try {
			toServer.writeUTF("qsc" + currentUser.getName() + "^"
									+ dstUserName + "^" 
									+ currentEntry.getKeyword() + "^"
									+ source);
			System.out.println("qsc" + currentUser.getName() + "^"
									 + dstUserName + "^" 
									 + currentEntry.getKeyword() + "^"
									 + source);
			//发卡成功与否
			String replySendCard = fromServer.readUTF();
			System.out.println("recv sendCard: " + replySendCard);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public void getCard(){
		notebookNumber = 0;
		try {
			//send
			toServer.writeUTF("qgc" + currentUser.getName());
			System.out.println("qgc" + currentUser.getName());
			toServer.flush();
			
			//recv
			String replyGetCard = fromServer.readUTF();
			System.out.println(replyGetCard);
			//sender+keyword+info
			//int wordIndex = 0;
			String [] temp = replyGetCard.substring(3).split("\\^");
			for(int i = 0; i < temp.length; i = i + 3){
				System.out.println("chai: " + temp[i]);
				System.out.println(temp[i+1]);
				System.out.println(temp[i+2]);
				notebook[notebookNumber] = new String(temp[i] + "^" + temp[i+1] + "^" + temp[i+2]);
				System.out.println("note: "+ notebook[notebookNumber]);
				notebookNumber++;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void refreshOnlineUserList(DefaultListModel defaultListModel){
		//刷新当前在线用户列表
		
		try {//send
			toServer.writeUTF("qou" + currentUser.getName());
			toServer.flush();
			System.out.println("qou" + currentUser.getName());
			//recv
			String replyRefreshPackage = fromServer.readUTF();
			if(replyRefreshPackage.substring(0, 3).equalsIgnoreCase("rou")){
				System.out.println("Start fresh!");
				String [] temp = replyRefreshPackage.substring(3).split("\\^");
				System.out.println(replyRefreshPackage);
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



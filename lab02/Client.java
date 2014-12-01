package lab02;

// ju ge lizi
import java.util.*; 
import java.awt.*;
import java.io.*; 
import java.awt.event.*;

import javax.swing.*;

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

	private User currentUser; // current online user
	private String[] notebook;
	private Entry currentEntry;
    
	//登陆面板
	JFrame loginFrame = new JFrame();//登陆窗口
	JButton lfLogin = new JButton("login");//登陆面板的登陆按钮
	JButton lfCancel = new JButton("cancel");//登陆面板的取消按钮
	
	//注册面板
	JFrame registerFrame = new JFrame();
	JButton rfRegister = new JButton("register");
	JButton rfCancel = new JButton("cancel");
	
	//shownote面板
	JFrame shownoteFrame = new JFrame();
	JList noteList = new JList();
	private JScrollPane scrollPaneOfNoteList = new JScrollPane(noteList);	//滚轮
	JTextArea wordExplaination = new JTextArea();
	private JScrollPane scrollPaneOfWordEx = new JScrollPane(wordExplaination);	//滚轮
	
	
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
		 *   refresh notes
		 *   close this frame
		 *   return true
		* */
		//return false;
		// if login succeed, change onlineUserList
		
		//实现如下
		loginFrame.setVisible(true);
		lfLogin.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
                //?????????
				
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
		 * close this frame
		 * return true
		 */
		return false;
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
		        //?????????
						
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
	private void showNotes() {
		/*
		 * pop a new frame
		 * display JList(notebook)
		 */
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
		String keyword; /* = textfield.getinput()*/
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
	}
	
	public static void main(String[] args){
		/* 下面的这个函数完全从陈冬杰的代码那里复制来的 */
		EventQueue.invokeLater(new Runnable() 
		{
			@Override
			public void run() {
				JFrame.setDefaultLookAndFeelDecorated(true);
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
				//----------------如果想删除substance效果，只保留下面部分--------------------------
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
		logout.setEnabled(false);
		logPanel.add(register);
		logPanel.add(title);
		logPanel.add(note);
		note.setEnabled(false);
        
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
		showPanel.add(scrollPane,BorderLayout.WEST);
		showPanel.add(showResultPanel,BorderLayout.CENTER);
		
		setLayout(new BorderLayout(20,20));
		add(logPanel,BorderLayout.NORTH);
		add(searchPanel,BorderLayout.CENTER);
		add(showPanel,BorderLayout.SOUTH);
		
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
		
		//登陆面板设置
		loginFrame.setResizable(false);
		loginFrame.setSize(300,150);
		loginFrame.setLocationRelativeTo(null);
		loginFrame.setTitle("login");
		loginFrame.setLayout(new GridLayout(3,2,5,5));
		loginFrame.add(new JLabel("UserName"));
		loginFrame.add(new TextField(8));
		loginFrame.add(new JLabel("Password"));
		loginFrame.add(new TextField(8));
		loginFrame.add(lfLogin);
		loginFrame.add(lfCancel);
		
		//注册面板设置
		registerFrame.setResizable(false);
		registerFrame.setSize(300,150);
		registerFrame.setLocationRelativeTo(null);
		registerFrame.setTitle("register");
		registerFrame.setLayout(new GridLayout(4,2,5,5));
		registerFrame.add(new JLabel("UserName"));
		registerFrame.add(new TextField(8));
		registerFrame.add(new JLabel("Password"));
		registerFrame.add(new TextField(8));
		registerFrame.add(new JLabel("Confirm Password"));
		registerFrame.add(new TextField(8));
		registerFrame.add(rfRegister);
		registerFrame.add(rfCancel);
		
		//note面板设置
		
		
	}
	
}

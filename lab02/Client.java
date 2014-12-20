package lab02;

import java.awt.*; 

import javax.swing.*;

import java.awt.event.*;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JOptionPane;

public class Client extends JFrame{
	private static final long serialVersionUID = 1L;
	private JButton login = new JButton("login");			//登陆按钮
	private JButton register = new JButton("register");	//注册按钮
	private JLabel title = new JLabel("My Diki");			//词典名字
	private JButton note = new JButton("note");			//单词本按钮
	private JButton logout = new JButton("logout");
	 
	private JTextField input = new JTextField(); 			//输入文本框
	private JButton search = new JButton("search");		//search 按钮
	
	private JCheckBox baidu = new JCheckBox("百度",true);	//三个复选框
	private JCheckBox youdao = new JCheckBox("有道",true);
	private JCheckBox bing = new JCheckBox("必应",true);
	
	private JButton refreshOnlineUserList = new JButton("refresh");
	DefaultListModel<String> defaultListModel = new DefaultListModel<String>();
	private JList<String> onlineUserList = new JList<String>();			//在线用户列表
	private JScrollPane scrollPane = new JScrollPane(onlineUserList);		//列表的滚动
	
	private JTextArea [] result;			//3个网站的搜索结果显示文本区域
	private JScrollPane [] scrollpane;		//滚轮
	private JTextField [] whoToSend;		//显示给谁发单词卡的文本框
	private JButton [] zan;				//点赞按钮
	private JButton [] unzan;				//点不赞按钮
	private JButton [] sendCard;			//发送单词卡按钮

	JPanel [] showThreePanel = new JPanel[3];
	private ClientBackground clientBackground;

	ImageIcon background;
	static JLabel setbg;
  
	public static void main(String[] args) {
		Client frame = new Client();
		
		// 参考陈冬杰的代码
		Client.setDefaultLookAndFeelDecorated(true);
		//* 想要修改皮肤的话，只需要更改，下面这个函数的参数，具体改成什么样，
		// * 可以打开substance.jar, 找到org.jvnet.substance.skin这个包
		// * 将下面的SubstanceDustCoffeeLookAndFeel替换成刚刚打开的包下的任意一个“Substance....LookAndFeel”即可 
		try {
			UIManager.setLookAndFeel(new org.jvnet.substance.skin.SubstanceMistSilverLookAndFeel());
		} catch (UnsupportedLookAndFeelException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		frame.setFont(new Font("微软雅黑", 1, 15));
		
		frame.getLayeredPane().add(setbg, new Integer(Integer.MIN_VALUE));
		((JPanel)frame.getContentPane()).setOpaque(false);
		
		// frame.setResizable(true);
		frame.setSize(800, 600);// frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setTitle("DIKI");
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int n = JOptionPane.showConfirmDialog(null, "乃真的要走了吗？", "退出",
						JOptionPane.YES_NO_OPTION);
				// 选中“是”返回0,选中“否”返回1。
				if (n == 0) {
					// 用户退出（如果在线）
					frame.logout();
					System.exit(0);
				}
			}
		});
	}
	
	//logout
	private void logout(){
		clientBackground.logout(login, logout, register, note, zan, unzan, sendCard, whoToSend, refreshOnlineUserList, defaultListModel);
	}
	
	public Client(){
		baidu.setOpaque(false);
		youdao.setOpaque(false);
		bing.setOpaque(false);
		
		background = new ImageIcon("bg.jpg");
		setbg = new JLabel(background);
		setbg.setBounds(0, 0, background.getIconWidth(), background.getIconHeight());
		
		clientBackground = new ClientBackground();
		
		result = new JTextArea[3];
		
		scrollpane = new JScrollPane[3];
		
		whoToSend = new JTextField[3];
		zan = new JButton[3];
		unzan = new JButton[3];
		sendCard = new JButton[3];
		
		for(int i = 0; i < 3; i++){
			result[i] = new JTextArea(5,20);
			result[i].setEditable(false);
			scrollpane[i] = new JScrollPane(result[i]);
			scrollpane[i].setOpaque(false);
			
			whoToSend[i] = new JTextField("who to send");
			zan[i] = new JButton("zan");
			zan[i].setEnabled(false);//未登陆时无法点赞
			unzan[i] = new JButton("unzan");
			unzan[i].setEnabled(false);//同上
			sendCard[i] = new JButton("send card");
			sendCard[i].setEnabled(false);
		}
		
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
		searchPanel.setOpaque(false);
		
		/* 控件有： 在线用户列表，三个网站的搜索结果，其中有单词的解释选择给谁发送单词卡、赞按钮、不赞按钮和发送单词卡按钮
		 * 三个网站的搜索结构(showResultPanel (使用 BorderLayout))       
		 * 每个网站的搜索结构(showPenelA/B/C (使用 BorderLayout))         单词的解释和选择给谁发送单词卡、赞按钮、不赞按钮和发送单词卡按钮
		 * 其中三个按钮和一个文本框(showSelectPanelA/B/C (使用GridLayout)) 选择给谁发送单词卡、赞按钮、不赞按钮和发送单词卡按钮
		 * BorderLayout
		 */
		JPanel showPanel = new JPanel();
		showPanel.setOpaque(false);
		
		/* 以下是更小的panel的定义，在上面已经解释过
		 * 控件有：百度、有道和必应三个复选框
		 */ 
		JPanel selectSourcePanel = new JPanel();
		selectSourcePanel.setOpaque(false);
		JPanel showResultPanel = new JPanel();
		showResultPanel.setOpaque(false);
		
		//JPanel [] showThreePanel = new JPanel[3];
		JPanel [] showSelectPanel = new JPanel[3];
		for(int i = 0; i < 3; i++){
			showThreePanel[i] = new JPanel();
			showSelectPanel[i] = new JPanel();
			showThreePanel[i].setOpaque(false);
			showSelectPanel[i].setOpaque(false);
		}
		
		logPanel.setOpaque(false);
		
		logPanel.setLayout(new GridLayout(1,5,5,5));
		logPanel.add(login);
		//logPanel.add(login);
		logPanel.add(logout);
		logout.setEnabled(false);//未登陆时不能使用logout
		logPanel.add(register);
		logPanel.add(title);
		logPanel.add(note);
		note.setEnabled(false);//未登录时不能使用note
        
		selectSourcePanel.setLayout(new FlowLayout());
		selectSourcePanel.add(refreshOnlineUserList);
		refreshOnlineUserList.setEnabled(false);
        selectSourcePanel.add(baidu);
		selectSourcePanel.add(youdao);
		selectSourcePanel.add(bing);
		
		searchPanel.setLayout(new BorderLayout(5, 5));
		JPanel subSearchPanel = new JPanel(new BorderLayout(5, 5));
		
		subSearchPanel.setOpaque(false);
		subSearchPanel.add(new JLabel("    Input    "), BorderLayout.WEST);
		subSearchPanel.add(input, BorderLayout.CENTER);
		subSearchPanel.add(search, BorderLayout.EAST);
		
		searchPanel.add(subSearchPanel, BorderLayout.NORTH);
		searchPanel.add(selectSourcePanel,BorderLayout.SOUTH);
		
		for(int i = 0; i < 3; i++){
			showSelectPanel[i].setLayout(new GridLayout(4,1));
			showSelectPanel[i].add(whoToSend[i]);
			showSelectPanel[i].add(zan[i]);
			showSelectPanel[i].add(unzan[i]);
			showSelectPanel[i].add(sendCard[i]);
			showThreePanel[i].setLayout(new BorderLayout());
			showThreePanel[i].add(scrollpane[i],BorderLayout.CENTER);
			showThreePanel[i].add(showSelectPanel[i],BorderLayout.EAST);
			switch(i){
				case 0: break;
				case 1: break;
				case 2: break;
			}
			showThreePanel[i].setBorder(BorderFactory.createTitledBorder (subTitle));
			showSelectPanel[i].setBorder(BorderFactory.createTitledBorder (subTitle));
		}
		
		showResultPanel.setLayout(new GridLayout(3,1));
		showResultPanel.add(showThreePanel[0]);
		showResultPanel.add(showThreePanel[1]);
		showResultPanel.add(showThreePanel[2]);
		
		showPanel.setLayout(new BorderLayout());
		onlineUserList.setFixedCellWidth(100);
		onlineUserList.setFixedCellHeight(50);
		onlineUserList.setModel(defaultListModel);
		scrollPane.setBorder(BorderFactory.createTitledBorder ("OnlineUserList"));
		scrollPane.setOpaque(false);
		showPanel.add(scrollPane,BorderLayout.WEST);
		showPanel.add(showResultPanel,BorderLayout.CENTER);
		
		
		setLayout(new BorderLayout());
		logPanel.setBounds(0, 0, 800, 30);
		JPanel subPanel = new JPanel(new BorderLayout());
		subPanel.setOpaque(false);
		add(logPanel,BorderLayout.NORTH);
		subPanel.add(searchPanel,BorderLayout.CENTER);
		subPanel.add(showPanel,BorderLayout.SOUTH);
		add(subPanel);
		
		//添加login的监听事件，调用login函数
		login.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clientBackground.login(defaultListModel, logout, register, note, refreshOnlineUserList, login, sendCard);
			}
		});
		
		logout.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				logout();//clientBackground.logout(currentUser, login, logout, register, note, zan, unzan, sendCard, whoToSend, refreshOnlineUserList, defaultListModel);
			}
		});
		
		//添加register的监听事件
		register.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clientBackground.register();
			}
		});
		
		//添加note的监听事件
		note.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clientBackground.getCard();
				clientBackground.showNotes();
			}
		});
		
		//添加输入框监听事件
		input.addKeyListener(new KeyAdapter() {
    		public void keyReleased(KeyEvent e){
    			String word = input.getText();
    			if(e.getKeyCode() == 10){//按下enter键就进行单词查询
    				clientBackground.search(word ,zan, unzan, result, baidu, youdao, bing, showThreePanel, showSelectPanel, sendCard);
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
				String word = input.getText();
				clientBackground.search(word , zan, unzan, result, baidu, youdao, bing, showThreePanel, showSelectPanel, sendCard);
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
				clientBackground.clickZan(0, zan);
			}
		});
		
		zan[1].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clientBackground.clickZan(1, zan);
			}
		});
		
		zan[2].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clientBackground.clickZan(2,  zan);
			}
		});
		
		//添加unzan的监听事件（三个panel）
		unzan[0].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clientBackground.clickUnzan(0, unzan);
			}
		});
		
		unzan[1].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clientBackground.clickUnzan(1, unzan);
			}
		});
		
		unzan[2].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clientBackground.clickUnzan(2,  unzan);
			}
		});
		
		//添加sendCard的监听事件（三个panel）
		sendCard[0].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clientBackground.sendCard(0, whoToSend);//sendCard(0);
			}
		});
		
		sendCard[1].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clientBackground.sendCard(1, whoToSend);//
			}
		});
		
		sendCard[2].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clientBackground.sendCard(2,  whoToSend);//
			}
		});
		
		//复选框的监听器
		baidu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clientBackground.displayThreePanel(result, baidu, youdao, bing, showThreePanel,showSelectPanel, sendCard, zan, unzan);
				
			}
		});
		
		youdao.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clientBackground.displayThreePanel(result, baidu, youdao, bing, showThreePanel,showSelectPanel, sendCard, zan, unzan);
				
			}
		});
		
		bing.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clientBackground.displayThreePanel(result, baidu, youdao, bing, showThreePanel,showSelectPanel, sendCard, zan, unzan);
				
			}
		});
		
		//添加发送单词卡的触发事件，使用多线程实现单词卡的发送和接收
		sendCard[0].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				System.out.println("sendcard 0");
				clientBackground.sendCard(0,  whoToSend);
			}
		});
		
		sendCard[1].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clientBackground.sendCard(1,whoToSend);
			}
		});
		
		sendCard[2].addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clientBackground.sendCard(2, whoToSend);
			}
		});
		
		refreshOnlineUserList.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				clientBackground.refreshOnlineUserList(defaultListModel);
			}
		});
	}	
}

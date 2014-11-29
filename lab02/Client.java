package lab02;

// ju ge lizi
import java.awt.*;
import javax.swing.*;
public class Client extends JFrame{
	private JButton login = new JButton("login");//登陆按钮
	private JButton register = new JButton("register");//注册按钮
	private JLabel title = new JLabel("My Diki");//词典名字
	private JButton note = new JButton("note");//单词本按钮
	 
	private JTextField input = new JTextField(); //输入文本框
	private JButton search = new JButton("search");//search 按钮
	
	private JCheckBox baidu = new JCheckBox("百度");//三个复选框
	private JCheckBox youdao = new JCheckBox("有道");
	private JCheckBox biying = new JCheckBox("必应");
	
	private JList onlineUserList = new JList();//在线用户列表
	private JScrollPane scrollPane = new JScrollPane(onlineUserList);//列表的滚轮
	
	private JTextArea resultA = new JTextArea(5,20);//第一个网站的搜索结果显示文本区域
	private JScrollPane scrollPaneA = new JScrollPane(resultA);//滚轮
	private JTextField whoToSendA = new JTextField("who to send");//显示给谁发单词卡的文本框
	private JButton zanA = new JButton("zan");//点赞 按钮
	private JButton unzanA = new JButton("unzan");//点不赞 按钮
	private JButton sendCardA = new JButton("send card");//发送单词卡 按钮
	
	private JTextArea resultB = new JTextArea(5,20);//第二个网站的搜索结果显示文本区域（与A类似）
	private JScrollPane scrollPaneB = new JScrollPane(resultB);
	private JTextField whoToSendB = new JTextField("who to send");
	private JButton zanB = new JButton("zan");
	private JButton unzanB = new JButton("unzan");
	private JButton sendCardB = new JButton("send card");
	
	private JTextArea resultC = new JTextArea(5,20);//第三个网站的搜索显示文本区域（与A类似）
	private JScrollPane scrollPaneC = new JScrollPane(resultC);
	private JTextField whoToSendC = new JTextField("who to send");
	private JButton zanC = new JButton("zan");
	private JButton unzanC = new JButton("unzan");
	private JButton sendCardC = new JButton("send card");
	
	public static void main(String[] args){
		Client frame = new Client();
    	frame.setSize(600,600);
    	frame.setLocationRelativeTo(null);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setTitle("English-Chinese Dictionary");
    	frame.setVisible(true);
	}
	public Client(){
		//主要的四个panel（有的pannel是由更小的panel构成的）
		//控件有：登陆按钮，注册按钮，字典名字，单词本按钮     
		//GridLayout
		JPanel logPanel = new JPanel();
		
		//控件有： input，输入单词的文本框，search 按钮，
		//      三个网站的复选框(selectSourcePanel (使用FlowLayout))
		//BorderLayout
		JPanel searchPanel = new JPanel();
		
		//控件有： 在线用户列表，三个网站的搜索结果，其中有单词的解释、选择给谁发送单词卡、赞按钮、不赞按钮、发送单词卡按钮
		  //三个网站的搜索结果(showResultPanel (使用 BorderLayout))       
		  //每个网站的搜索结果(showPenelA/B/C (使用 BorderLayout))         单词的解释、选择给谁发送单词卡、赞按钮、不赞按钮、发送单词卡按钮
		  //其中三个按钮和一个文本框(showSelectPanelA/B/C (使用GridLayout)) 选择给谁发送单词卡、赞按钮、不赞按钮、发送单词卡按钮
		//BorderLayout
		JPanel showPanel = new JPanel();
		
		//以下是更小的panel的定义，在上面已经解释过了
		//控件有：百度、有道、必应三个复选框 
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
		logPanel.add(register);
		logPanel.add(title);
		logPanel.add(note);
        
		selectSourcePanel.setLayout(new FlowLayout());
        selectSourcePanel.add(baidu);
		selectSourcePanel.add(youdao);
		selectSourcePanel.add(biying);
		
		searchPanel.setLayout(new BorderLayout(20,20));
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
		
	}
}

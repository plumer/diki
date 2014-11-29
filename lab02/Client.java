package lab02;

// ju ge lizi
import java.awt.*;
import javax.swing.*;
public class Client extends JFrame{
	private JButton login = new JButton("login");//鐧婚檰鎸夐挳
	private JButton register = new JButton("register");//娉ㄥ唽鎸夐挳
	private JLabel title = new JLabel("My Diki");//璇嶅吀鍚嶅瓧
	private JButton note = new JButton("note");//鍗曡瘝鏈寜閽�
	 
	private JTextField input = new JTextField(); //杈撳叆鏂囨湰妗�
	private JButton search = new JButton("search");//search 鎸夐挳
	
	private JCheckBox baidu = new JCheckBox("鐧惧害");//涓変釜澶嶉�夋
	private JCheckBox youdao = new JCheckBox("鏈夐亾");
	private JCheckBox biying = new JCheckBox("蹇呭簲");
	
	private JList onlineUserList = new JList();//鍦ㄧ嚎鐢ㄦ埛鍒楄〃
	private JScrollPane scrollPane = new JScrollPane(onlineUserList);//鍒楄〃鐨勬粴杞�
	
	private JTextArea resultA = new JTextArea(5,20);//绗竴涓綉绔欑殑鎼滅储缁撴灉鏄剧ず鏂囨湰鍖哄煙
	private JScrollPane scrollPaneA = new JScrollPane(resultA);//婊氳疆
	private JTextField whoToSendA = new JTextField("who to send");//鏄剧ず缁欒皝鍙戝崟璇嶅崱鐨勬枃鏈
	private JButton zanA = new JButton("zan");//鐐硅禐 鎸夐挳
	private JButton unzanA = new JButton("unzan");//鐐逛笉璧� 鎸夐挳
	private JButton sendCardA = new JButton("send card");//鍙戦�佸崟璇嶅崱 鎸夐挳
	
	private JTextArea resultB = new JTextArea(5,20);//绗簩涓綉绔欑殑鎼滅储缁撴灉鏄剧ず鏂囨湰鍖哄煙锛堜笌A绫讳技锛�
	private JScrollPane scrollPaneB = new JScrollPane(resultB);
	private JTextField whoToSendB = new JTextField("who to send");
	private JButton zanB = new JButton("zan");
	private JButton unzanB = new JButton("unzan");
	private JButton sendCardB = new JButton("send card");
	
	private JTextArea resultC = new JTextArea(5,20);//绗笁涓綉绔欑殑鎼滅储鏄剧ず鏂囨湰鍖哄煙锛堜笌A绫讳技锛�
	private JScrollPane scrollPaneC = new JScrollPane(resultC);
	private JTextField whoToSendC = new JTextField("who to send");
	private JButton zanC = new JButton("zan");
	private JButton unzanC = new JButton("unzan");
	private JButton sendCardC = new JButton("send card");

	private User currentUser; // current online user
	private String[] notebook;
	private Entry currentEntry;

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
		return false;
		// if login succeed, change onlineUserList
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
		 * listener 2 : "login"
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
		return false;
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
	}

	private boolean clickUnzan(int panelID) {
		/* get explanation id
		 * send clickUnzan request to server
		 *   assert success
		 * disable button
		 * change button text to #ofUnzan
		 * return true
		 */
	}

	private boolean sendCard(int panelID) {
		/* get user name (from textField ? onlineUserList ?)
		 * get explanation id
		 * send sendCard request to server
		 *   assert success
		 * return true
		 */
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
		Client frame = new Client();
    	frame.setSize(600,600);
    	frame.setLocationRelativeTo(null);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setTitle("English-Chinese Dictionary");
    	frame.setVisible(true);
	}
	public Client(){
		//涓昏鐨勫洓涓猵anel锛堟湁鐨刾annel鏄敱鏇村皬鐨刾anel鏋勬垚鐨勶級
		//鎺т欢鏈夛細鐧婚檰鎸夐挳锛屾敞鍐屾寜閽紝瀛楀吀鍚嶅瓧锛屽崟璇嶆湰鎸夐挳     
		//GridLayout
		JPanel logPanel = new JPanel();
		
		//鎺т欢鏈夛細 input锛岃緭鍏ュ崟璇嶇殑鏂囨湰妗嗭紝search 鎸夐挳锛�
		//      涓変釜缃戠珯鐨勫閫夋(selectSourcePanel (浣跨敤FlowLayout))
		//BorderLayout
		JPanel searchPanel = new JPanel();
		
		//鎺т欢鏈夛細 鍦ㄧ嚎鐢ㄦ埛鍒楄〃锛屼笁涓綉绔欑殑鎼滅储缁撴灉锛屽叾涓湁鍗曡瘝鐨勮В閲娿�侀�夋嫨缁欒皝鍙戦�佸崟璇嶅崱銆佽禐鎸夐挳銆佷笉璧炴寜閽�佸彂閫佸崟璇嶅崱鎸夐挳
		  //涓変釜缃戠珯鐨勬悳绱㈢粨鏋�(showResultPanel (浣跨敤 BorderLayout))       
		  //姣忎釜缃戠珯鐨勬悳绱㈢粨鏋�(showPenelA/B/C (浣跨敤 BorderLayout))         鍗曡瘝鐨勮В閲娿�侀�夋嫨缁欒皝鍙戦�佸崟璇嶅崱銆佽禐鎸夐挳銆佷笉璧炴寜閽�佸彂閫佸崟璇嶅崱鎸夐挳
		  //鍏朵腑涓変釜鎸夐挳鍜屼竴涓枃鏈(showSelectPanelA/B/C (浣跨敤GridLayout)) 閫夋嫨缁欒皝鍙戦�佸崟璇嶅崱銆佽禐鎸夐挳銆佷笉璧炴寜閽�佸彂閫佸崟璇嶅崱鎸夐挳
		//BorderLayout
		JPanel showPanel = new JPanel();
		
		//浠ヤ笅鏄洿灏忕殑panel鐨勫畾涔夛紝鍦ㄤ笂闈㈠凡缁忚В閲婅繃浜�
		//鎺т欢鏈夛細鐧惧害銆佹湁閬撱�佸繀搴斾笁涓閫夋 
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
		
	}
}

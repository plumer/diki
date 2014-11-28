package lab02;

// ju ge lizi
import java.awt.*;
import javax.swing.*;
public class Client extends JFrame{
	private JButton login = new JButton("login");//��½��ť
	private JButton register = new JButton("register");//ע�ᰴť
	private JLabel title = new JLabel("My Diki");//�ʵ�����
	private JButton note = new JButton("note");//���ʱ���ť
	 
	private JTextField input = new JTextField(); //�����ı���
	private JButton search = new JButton("search");//search ��ť
	
	private JCheckBox baidu = new JCheckBox("�ٶ�");//������ѡ��
	private JCheckBox youdao = new JCheckBox("�е�");
	private JCheckBox biying = new JCheckBox("��Ӧ");
	
	private JList onlineUserList = new JList();//�����û��б�
	private JScrollPane scrollPane = new JScrollPane(onlineUserList);//�б�Ĺ���
	
	private JTextArea resultA = new JTextArea(5,20);//��һ����վ�����������ʾ�ı�����
	private JScrollPane scrollPaneA = new JScrollPane(resultA);//����
	private JTextField whoToSendA = new JTextField("who to send");//��ʾ��˭�����ʿ����ı���
	private JButton zanA = new JButton("zan");//���� ��ť
	private JButton unzanA = new JButton("unzan");//�㲻�� ��ť
	private JButton sendCardA = new JButton("send card");//���͵��ʿ� ��ť
	
	private JTextArea resultB = new JTextArea(5,20);//�ڶ�����վ�����������ʾ�ı�������A���ƣ�
	private JScrollPane scrollPaneB = new JScrollPane(resultB);
	private JTextField whoToSendB = new JTextField("who to send");
	private JButton zanB = new JButton("zan");
	private JButton unzanB = new JButton("unzan");
	private JButton sendCardB = new JButton("send card");
	
	private JTextArea resultC = new JTextArea(5,20);//��������վ��������ʾ�ı�������A���ƣ�
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
		//��Ҫ���ĸ�panel���е�pannel���ɸ�С��panel���ɵģ�
		//�ؼ��У���½��ť��ע�ᰴť���ֵ����֣����ʱ���ť     
		//GridLayout
		JPanel logPanel = new JPanel();
		
		//�ؼ��У� input�����뵥�ʵ��ı���search ��ť��
		//      ������վ�ĸ�ѡ��(selectSourcePanel (ʹ��FlowLayout))
		//BorderLayout
		JPanel searchPanel = new JPanel();
		
		//�ؼ��У� �����û��б�������վ����������������е��ʵĽ��͡�ѡ���˭���͵��ʿ����ް�ť�����ް�ť�����͵��ʿ���ť
		  //������վ���������(showResultPanel (ʹ�� BorderLayout))       
		  //ÿ����վ���������(showPenelA/B/C (ʹ�� BorderLayout))         ���ʵĽ��͡�ѡ���˭���͵��ʿ����ް�ť�����ް�ť�����͵��ʿ���ť
		  //����������ť��һ���ı���(showSelectPanelA/B/C (ʹ��GridLayout)) ѡ���˭���͵��ʿ����ް�ť�����ް�ť�����͵��ʿ���ť
		//BorderLayout
		JPanel showPanel = new JPanel();
		
		//�����Ǹ�С��panel�Ķ��壬�������Ѿ����͹���
		//�ؼ��У��ٶȡ��е�����Ӧ������ѡ�� 
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

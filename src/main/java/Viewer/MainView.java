package main_view;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.border.EtchedBorder;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JToggleButton;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;
import javax.swing.JList;
import javax.swing.border.LineBorder;
import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.AbstractListModel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainView extends JFrame {

	private JPanel contentPane;
	private JTable table;
	private Team team;
	private User user;
	private Profile profile;
	
	JButton btn_reg;		//�� ���Խ�û ��ư
	JList<Team> list;		//�� ����Ʈ


	///db ��������
	public MainView(User user) {
		team = User.getTeam();		//���� team�� ���� ��� null�̶� ����
		profile = User.getProfile();		////////////////User�� profile �ʵ� ����
		
		ButtonActionListener listener = new ButtonActionListener()
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel_team = new JPanel();
		panel_team.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		panel_team.setBounds(12, 10, 362, 176);
		contentPane.add(panel_team);
		panel_team.setLayout(null);
		
		
		//db
		/***���� ���̸� �Ǵ� ���� �����ش޶�� ��****/
		JLabel lb_team = new JLabel("(team name)");
		lb_team.setFont(new Font("����", Font.PLAIN, 16));
		lb_team.setBounds(125, 20, 212, 41);
		
		if(team!=null)
			lb_team.setText("�� �̸�: "+team.getName());
		else
			lb_team.setText("���� �������ּ���");
		panel_team.add(lb_team);
		
		/***�� ���� �Ǵ� �� ���� ��ư****/
		JButton btn_team = new JButton("�� ����");
		btn_team.setFont(new Font("����", Font.PLAIN, 20));
		if(team!=null)
			btn_team.setText("�� ����");
		btn_team.addActionListener(listener);
		btn_team.setBounds(186, 71, 151, 33);
		panel_team.add(btn_team);
		
		/***������ ��ư****/
		JButton btn_profile = new JButton("������ ����");
		btn_profile.setFont(new Font("����", Font.PLAIN, 20));
		btn_profile.setBounds(186, 114, 151, 33);
		btn_profile.addActionListener(listener);
		panel_team.add(btn_profile);
		
		/***������ �̹���****/
		JLabel lb_image = new JLabel("profile image");
		lb_image.setBackground(Color.WHITE);
		lb_image.setIcon(null);
		lb_image.setBounds(12, 20, 101, 133);
		panel_team.add(lb_image);
		
		/***��õ�ޱ� üũ�ڽ�****/
		JCheckBox chckbx_recommend = new JCheckBox("\uCD94\uCC9C \uBC1B\uAE30");
		chckbx_recommend.setBounds(279, 198, 95, 23);
		chckbx_recommend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(chckbx_recommend.isSelected()) {
					updateTeamList(true);		//�� ����Ʈ �ٽ� �޾ƿ���
				}
			}
			
		});
		contentPane.add(chckbx_recommend);
		
		
		/***�� ���*****/
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 227, 362, 244);
		contentPane.add(scrollPane);

		updateTeamList(false);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				List<TeamRole> teamRoles = list.getSelectedValue().getTeamRole();
				
				//�� ����ǥ ������Ʈ
				for(int i=0; i<3; i++) {
					table.setValueAt(teamRoles[i].getCurrent(), i+1, 1);	//teamRole[0]���� ���ʴ�� ������, ��ȹ, �������̶� ����
					table.setValueAt(teamRoles[i].getCurrent(), i+1, 2);	//([������/��ȹ��/������], [���� �ο�], [���� �ο�])
				}
				
				if(team!=null)
					btn_reg.setVisible(true);
			}
		});
		scrollPane.setViewportView(list);
		
		JLabel lb_teamList = new JLabel("�� ���");
		lb_teamList.setHorizontalAlignment(SwingConstants.CENTER);
		scrollPane.setColumnHeaderView(lb_teamList);
		lb_teamList.setFont(new Font("����", Font.PLAIN, 20));
		
		
		/****����Ʈ���� ������ ���� ���Խ�û ��ư*****/
		btn_reg = new JButton("�� ����");
		btn_reg.setFont(new Font("����", Font.PLAIN, 16));
		btn_reg.addActionListener(listener);
		btn_reg.setBounds(267, 494, 107, 37);
		btn_reg.setVisible(false);
		contentPane.add(btn_reg);
		
		/****����Ʈ���� ������ ���� ���� ǥ****/
		setOrganizationTable();
		contentPane.add(table);
		
		
		
		setSize(400,600);
	}

	
	private void setOrganizationTable() {
		table = new JTable();
		table.setModel(new DefaultTableModel(
			new Object[][] {
				{null, "\uD604\uC7AC \uC778\uC6D0", "\uBAA8\uC9D1 \uC778\uC6D0"},
				{"\uAC1C\uBC1C\uC790", null, null},
				{"\uAE30\uD68D\uC790", null, null},
				{"\uB514\uC790\uC778", null, null},
			},
			new String[] {
				" ", "current", "maximum"
			}
		) {
			Class[] columnTypes = new Class[] {
				String.class, String.class, String.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			boolean[] columnEditables = new boolean[] {
				false, false, false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getColumnModel().getColumn(0).setPreferredWidth(45);
		table.getColumnModel().getColumn(1).setResizable(false);
		table.getColumnModel().getColumn(1).setPreferredWidth(71);
		table.getColumnModel().getColumn(2).setResizable(false);
		table.setCellSelectionEnabled(true);
		table.setColumnSelectionAllowed(true);
		table.setBounds(22, 481, 238, 64);
		
	}
	
	private class ButtonActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			JButton b = (JButton) e.getSource();
			
			switch(b.getText()) {
			case "�� ����":
				////////
				break;
			case "�� ����":
				///////
				break;
			case "������ ����":
				///////
				break;
			case "�� ����":
				///////
				break;
			}
		}
	}
	
	private void updateTeamList(boolean isChecked) {
		DefaultListModel<Team> listModel = new DefaultListModel<Team>();
		
		if(!isChecked) {		//��õ �ޱ� üũ �ȵ�������
			for(int i=0; i<20; i++) {		//db �����ϱ�
				listModel.addElement(new Team(0, "team name"+i, "team info"));
			}
		}
		
		else {		//��õ �ޱ� üũ�϶�
			/////////////////////////
		}
		
		list.setModel(listModel);
	}
	


}



package Viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Team.Role;
import Team.Team;
import kr.ac.konkuk.ccslab.cm.entity.CMUser;
import kr.ac.konkuk.ccslab.cm.event.CMUserEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.info.CMInteractionInfo;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;
import mdlaf.MaterialLookAndFeel;
import mdlaf.themes.MaterialLiteTheme;



public class ClientApp extends JFrame {
	private static final long serialVersionUID = 1L;

	public CMClientStub clientStub;
	public String user_id;
	public String team_id;
	private ClientEventHandler clientEventHandler;
	public JLabel message = new JLabel("message will print here");
    public String token = null;
	
    private String email = null;
    private String pw = null;
    private TeamsView mainView = null;
    
    public Team my_team = null;
    public ObjectMapper objectMapper = new ObjectMapper();
    
    
	public ClientApp() {
		super("Team No3 - Matching System");
		setMinimumSize (new Dimension (UIConst.WIDTH, UIConst.HEIGHT));
		setResizable(false);
		setVisible (true);

		clientStub = new CMClientStub();
		clientEventHandler = new ClientEventHandler(this);
		clientStub.setAppEventHandler(clientEventHandler);
		
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        clientStub.terminateCM();
		        System.out.println("CM Terminated");
		    }
		});
		
		clientStub.startCM();
		print("message will print here");
		ChangeView(new LoginView(this));
		
		message.setBounds(30, 0, 500, 30);
	}
	
	public void print(String txt) {
		message.setText(txt);
	}
	
	public void exit() {
		clientStub.logoutCM();
		clientStub.disconnectFromServer();
		System.exit(0);
	}
	
	public void requestConnection(String id, String pw) {
		this.email = id;
		this.pw = pw;
		
		if(this.email == null) {
			this.print("email is null");
			return;
		}
		if(this.pw == null) {
			this.print("password is null");
			return;
		}

		clientStub.loginCM(this.email, this.pw);
		System.out.println("requestConnection email : " + this.email);
		System.out.println("requestConnection pw : " + this.pw);
	}
	
	public void requestLogin() {
		CMUserEvent ue = new CMUserEvent();
		CMInteractionInfo info = clientStub.getCMInfo().getInteractionInfo();
		CMUser user = info.getMyself();
		ue.setStringID("SIGN-IN");
		
		System.out.println("requestLogin email : " + email);
		System.out.println("requestLogin pw : " + pw);
		ue.setEventField(CMInfo.CM_STR, "email", email);
		ue.setEventField(CMInfo.CM_STR, "password", pw);
		
		ue.setSender(user.getName());
		ue.setDistributionGroup(user.getCurrentGroup());
		ue.setDistributionSession(user.getCurrentSession());
		
		clientStub.send(ue, "SERVER");
		this.print("Request Login ...");
	}
	
	public void ChangeView(JPanel view) {
		this.getContentPane().removeAll();
		view.add(message);
		this.getContentPane().add(view, BorderLayout.CENTER);
		pack ();
	}
	
	public void requestSignUp(String name, String id, String pw, Role role, String introduce) {
		
	}

	private CMUserEvent GetUE(String id) {
		CMUserEvent ue = new CMUserEvent();
		CMInteractionInfo info = clientStub.getCMInfo().getInteractionInfo();
		CMUser user = info.getMyself();
		
		if(token == null) {
			System.out.println("FAIL FETCH TOKEN");
		}
		
		ue.setStringID(id);
		ue.setEventField(CMInfo.CM_STR, "token", this.token);
		ue.setSender(user.getName());
		ue.setDistributionGroup(user.getCurrentGroup());
		ue.setDistributionSession(user.getCurrentSession());
		return ue;
	}

	public void requestTeamList() {
		CMUserEvent ue = new CMUserEvent();
		CMInteractionInfo info = clientStub.getCMInfo().getInteractionInfo();
		CMUser user = info.getMyself();
		
		ue.setStringID("GET-TEAMS");
		ue.setEventField(CMInfo.CM_STR, "token", token);
		clientStub.send(ue, "SERVER");
		this.print("GET TEAM LIST (GET-TEAMS) REQEUSTED");
	}
	
	public void requestCreateTeam(Map<Role, Integer> limits, String teamName) {
		CMUserEvent ue = new CMUserEvent();
		ue.setStringID("CREATE-TEAM");
		Map<Role, Integer> rolelimits = limits;
		String json;
		try {
			json = objectMapper.writeValueAsString(rolelimits);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ue.setEventField(CMInfo.CM_STR, "team_name", teamName);
		ue.setEventField(CMInfo.CM_STR, "token", token);
		ue.setEventField(CMInfo.CM_STR, "teamlimit", json);
	}
	
	public void applyTeam(String teamName) {
	      CMUserEvent ue = new CMUserEvent();
	      CMInteractionInfo info = clientStub.getCMInfo().getInteractionInfo();
	      CMUser user = info.getMyself();
	      
	      ue.setStringID("APPLY-TEAM");
	      ue.setEventField(CMInfo.CM_STR, "team_name", teamName);
	      ue.setEventField(CMInfo.CM_STR, "token", token);
	      
	      clientStub.send(ue, "SERVER");
	      this.print("Successfully applied ...");
	   }
	
	public static void main (String[] args) {
		try {
			UIManager.setLookAndFeel (new MaterialLookAndFeel ());
		}
		catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace ();
		}
		new ClientApp();
	}

	public void reqeustNewPost(Long team_id, String content) {
		CMUserEvent ue = GetUE("POST-BOARD");
		ue.setEventField(CMInfo.CM_LONG, "team_id", String.valueOf(team_id));
		ue.setEventField(CMInfo.CM_STR, "content", content);
		clientStub.send(ue, "SERVER");
		print("New Post Requested");
	}

	public void requestApplications() {
		CMUserEvent ue = GetUE("GET-APPLICATIONS");
		clientStub.send(ue, "SERVER");
		print("GET-APPLICATIONS requested");
	}

	public void requestProcessApplication(String user_id, String team_id) {
		CMUserEvent ue = GetUE("PROCESS-APPLICATION");
		ue.setEventField(CMInfo.CM_LONG, "user_id", user_id);
		ue.setEventField(CMInfo.CM_LONG, "team_id", team_id);
		ue.setEventField(CMInfo.CM_INT, "yesTeam", "1");
		clientStub.send(ue, "SERVER");
	}

	public void requestMyTeam(String team_id) {
		CMUserEvent ue = GetUE("GET-TEAM");
		ue.setEventField(CMInfo.CM_LONG, "team_id", team_id);
		clientStub.send(ue, "SERVER");
		this.print("GET TEAM REQEUSTED : " + team_id);
	}

	public void requestGetTeams() {
		// TODO Auto-generated method stub
		
	}

	public void requestGetUser(String user_id) {
		CMUserEvent ue = GetUE("GET-PROFILE");
		ue.setEventField(CMInfo.CM_LONG, "user_id", user_id);
		clientStub.send(ue, "SERVER");
		this.print("GET-PROFILE : " + user_id);
	}
}
package dao;

import dataobject.*;
import util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class UserDao {
	private User user;
	private Connection connection;
	
	public UserDao() {
		this.connection = Conn.getConnection();
	}
	
	public UserDao(User u) { 
		this();
		this.setUser(u);
	}
	
	public User getUser() {
		return this.user;
	}
	
	public void setUser(User u) {
		this.user = u;
	}
	
	public boolean checkUser(String username, String passwd) {
		boolean checkFlag = false;
		this.user = new User();
		this.user.setUsername(username);
		this.user.setPasswd(passwd);
		PreparedStatement state = null;
		
		try {
			state = connection.prepareStatement("call check_user_passwd(?, ?);");
			state.setString(1, this.user.getUsername());
			state.setString(2, passwd);
			ResultSet rs = state.executeQuery();

			if (rs.next() && rs.getLong("result") == 1) {
				state = connection.prepareStatement(
						"select uid, username, passwd, nickname, admin from q_user where username = ?;");
				state.setString(1, this.user.getUsername());
				rs = state.executeQuery();

				if (rs.next()) {
					this.user.setUid(rs.getLong("uid"));
					this.user.setNickname(rs.getString("nickname"));
					this.user.setAdmin(rs.getBoolean("admin"));
					checkFlag = true;
				} else {
					checkFlag = false;
				}
			} else {
				checkFlag = false;
			}
		} catch (SQLException e) {
			System.out.println("Unknown SQLException!");
			e.printStackTrace();
			System.exit(-3);
		}

		return checkFlag;
	}
	
	public String addUser(User user) {
		String addResult = null;
		PreparedStatement test = null;
		PreparedStatement state = null;
		try {
			// if username already exists
			test = connection.prepareStatement("select * from q_user where username = ?");
			test.setString(1,  user.getUsername());
			if (test.executeQuery().next()) {
				addResult = "Username dumplicate!";
			} else {
				state = connection.prepareStatement(
						"insert into q_user (uid, username, passwd, nickname, admin) values (default, ?, ?, ?, ?);");
				state.setString(1, user.getUsername());
				state.setString(2, user.getPasswd());
				state.setString(3, user.getNickname());
				state.setBoolean(4, user.isAdmin());
				state.executeQuery();
				
				// reset uid and other thing;
				this.checkUser(user.getUsername(), user.getPasswd());
				addResult = null;
			}
		} catch (SQLException e) {
			System.out.println("Unknown SQLException!");
			e.printStackTrace();
			System.exit(-4);
		}
		return addResult;
	}
	
	public String addUser() {
		return this.addUser(this.user);
	}
	
	public ArrayList<Paper> getOwnPapers() {
		ArrayList<Paper> ownPapers = new ArrayList<Paper>();
		PreparedStatement state = null;
		ResultSet rs = null;
		try {
			state = connection.prepareStatement(
					"select paperid, paper_name, publish_time, cutoff_time from q_paper where uid = ?");
			state.setLong(1, user.getUid());
			rs = state.executeQuery();
			while (rs.next()) {
				Paper paper = new Paper();
				paper.setPaperid(rs.getLong("paperid"));
				paper.setPapername(rs.getString("paper_name"));
				paper.setOwnerid(user.getUid());
				paper.setPublish_time(rs.getTimestamp("publish_time"));
				paper.setCutoff_time(rs.getTimestamp("cutoff_time"));
				ownPapers.add(paper);
			}
		} catch (SQLException e) {
			System.out.println("Unknown SQLException!");
			e.printStackTrace();
			System.exit(-1);
		}
		return ownPapers;
	}
	
	public ArrayList<Paper> getAllowedPaper() {
		PreparedStatement state = null;
		ArrayList<Paper> allowedPaper = new ArrayList<Paper>();
		ResultSet rs = null;
		try {
			state = connection.prepareStatement(
					"select q_paper.paperid as paperid, paper_name, publish_time, cutoff_time " +
					"from q_paper join q_user on q_paper.uid = q_user.uid " +
					"where q_user.uid = ?");
			state.setLong(1, user.getUid());
			rs = state.executeQuery();
			while (rs.next()) {
				Paper p = new Paper();
				p.setPaperid(rs.getLong("paperid"));
				p.setOwnerid(user.getUid());
				p.setPapername(rs.getString("paper_name"));
				p.setPublish_time(rs.getTimestamp("publish_time"));
				p.setCutoff_time(rs.getTimestamp("cutoff_time"));
				allowedPaper.add(p);
			}
		} catch (SQLException e) {
			System.out.println("Unknown SQLException!");
			e.printStackTrace();
			System.exit(-11);
		}
		
		return allowedPaper;
	}
	
	public static void main(String[] args) {
		User u = new User();
		u.setPasswd("feng");
		u.setUsername("leafee");
		u.setNickname("Leafee");
		u.setAdmin(true);
		UserDao ud = new UserDao();
		String msg;
		if ((msg = ud.addUser(u)) == null) {
			System.out.println("Succeed!");
		} else {
			System.out.println(msg);
		}
		
		System.out.println(ud.getUser().getUid());
		Paper paper = new Paper();
		paper.setPapername("added paper");
		paper.setOwnerid(ud.getUser().getUid());
		paper.setPublish_time(Timestamp.valueOf("2019-06-01 09:00:00"));
		paper.setCutoff_time(Timestamp.valueOf("2019-07-01 00:00:00"));
		PaperDao papd = new PaperDao(paper);
		papd.addPaper();
		
		Question quest = new Question();
		quest.setPaperid(papd.getPaper().getPaperid());
		quest.setQuestion("added question");
		quest.setType("raido");
		QuestionDao questD = new QuestionDao(quest);
		questD.addQuestion();
		
		Selection selec1 = new Selection();
		selec1.setQuestionid(questD.getQuestion().getQuestionid());
		selec1.setSelection_describe("added selection");
		questD.addSelection(selec1);
		
		Answer ans = new Answer();
		ans.setAnswer("test answer");
		ans.setQuestionid(questD.getQuestion().getQuestionid());
		ans.setRespondent(ud.getUser().getUid());
		questD.addAnswer(ans);
		
		questD.addSelection("added selection by string");
		questD.addAnswer("added answerContent by string", ud.getUser().getUid());
		
		papd.addAllowUser(ud.getUser().getUid());
		
		System.out.println("======");
		System.out.println(ud.getAllowedPaper().get(0).getPaperid());
		System.out.println(ud.getOwnPapers().get(0).getPaperid());
		
		System.out.println("========");
		System.out.println(papd.getQuestion().get(0).getQuestion());
		System.out.println(papd.getAllowedUser().get(0).getNickname());

		System.out.println("=========");
		System.out.println(questD.getAllAnswers().get(0).getAnswer());
		System.out.println(questD.getSpecificAnswers(ud.getUser().getUid()).get(0).getAnswer());
		System.out.println(questD.getSelection().get(0).getSelection_describe());
	}
}

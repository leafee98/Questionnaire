package dao;

import dataobject.*;
import util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Scanner;

public class UserDao {
	private User user;
	private Connection connection;
	
	/*
	 * init the UserDao class, and init a blank user inside it.
	 */
	public UserDao() {
		this.connection = Conn.getConnection();
		this.user = new User();
	}
	
	/*
	 * init the UserDao class, and init a user with User passed in;
	 */
	public UserDao(User u) { 
		this();
		this.setUser(u);
	}
	
	/*
	 * get the user inside UserDao
	 */
	public User getUser() {
		return this.user;
	}
	
	/*
	 * set a new user to UserDao
	 */
	public void setUser(User u) {
		this.user = u;
	}
	
	/*
	 * check if the username and passwd accurate, if accurate, the user inside
	 * UserDao will be set to the user checked in database
	 */
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
					this.user.setPasswd(rs.getString("passwd"));
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
	
	/*
	 * add a new user to database with the user passed in as parameter;
	 * if succeed, the user inside UserDao will be set to the user just added.
	 * if failed, the user inside will not change.
	 */
	public boolean addUser(User user) {
		boolean addResult = true;
		PreparedStatement test = null;
		PreparedStatement state = null;
		try {
			// if username already exists
			test = connection.prepareStatement("select * from q_user where username = ?");
			test.setString(1,  user.getUsername());
			if (test.executeQuery().next()) {
				addResult = false;
			} else {
				state = connection.prepareStatement(
						"insert into q_user (uid, username, passwd, nickname, admin) values (default, ?, ?, ?, ?);");
				state.setString(1, user.getUsername());
				state.setString(2, user.getPasswd());
				state.setString(3, user.getNickname());
				state.setBoolean(4, user.isAdmin());
				state.executeUpdate();
				
				// reset uid and other thing;
				this.checkUser(user.getUsername(), user.getPasswd());
				addResult = true;
			}
		} catch (SQLException e) {
			System.out.println("Unknown SQLException!");
			e.printStackTrace();
			System.exit(-4);
		}
		return addResult;
	}
	
	/*
	 * add a new user to database, using the user inside UserDao.
	 * if succeed, the user inside UserDao will be set to the user just added.
	 * if failed, the user inside will not change.
	 */
	public boolean addUser() {
		return this.addUser(this.user);
	}
	
	/*
	 * you can just pass username, password, nickname and boolean admin as parameter;
	 */
	public boolean addUser(String username, String passwd, String nickname, boolean admin) {
		User u = new User();
		u.setUsername(username);
		u.setPasswd(passwd);
		u.setNickname(nickname);
		u.setAdmin(admin);
		return this.addUser(u);
	}
	
	/*
	 * update the user's information, using the user inside UserDao,
	 * you can update everything except the uid.
	 */
	public boolean updateUser() {
		PreparedStatement state = null;
		long result = 0;
		try {
			state = connection.prepareStatement("update q_user set username = ?, admin = ?, passwd = ?, nickname = ? where uid = ?");
			state.setString(1, user.getUsername());
			state.setBoolean(2, user.isAdmin());
			state.setString(3, user.getPasswd());
			state.setString(4, user.getNickname());
			state.setLong(5, user.getUid());
			result = state.executeUpdate();
		} catch (SQLException e) {
			System.out.println("update user failed!");
			e.printStackTrace();
		}
		if (result == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	/*
	 * retrun all papers own to the user inside UserDao.
	 */
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
	
	/*
	 * return all papers allowed to do by the user inside UserDao,
	 * means the papaer not allowed to do on the current time will not be shown
	 */
	public ArrayList<Paper> getAllowedPaper() {
		PreparedStatement state = null;
		ArrayList<Paper> allowedPaper = new ArrayList<Paper>();
		ResultSet rs = null;
		try {
			state = connection.prepareStatement(
					"select q_paper.paperid as paperid, paper_name, publish_time, cutoff_time " +
					"from q_paper join q_user on q_paper.uid = q_user.uid " +
					"where q_user.uid = ? and NOW() between publish_time and cutoff_time;");
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
		Scanner input = new Scanner(System.in);

		User u = new User();
		u.setPasswd("feng");
		u.setUsername("leafee1");
		u.setNickname("Leafee");
		u.setAdmin(true);
		UserDao ud = new UserDao();
		if (ud.addUser(u)) {
			System.out.println("Succeed!");
		} else {
			System.out.println("Error!");
		}

		System.out.println("created user, before updating user");
		input.nextInt();;
		
		u = ud.getUser();
		u.setUsername("test_update");
		u.setPasswd("test_update");
		u.setNickname("test_update");
		ud.setUser(u);
		if (ud.updateUser()) {
			System.out.println("Update Succeed!");
		}
		
		System.out.println(ud.getUser().getUid());
		Paper paper = new Paper();
		paper.setPapername("added paper0");
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
		
		input.close();
	}
}

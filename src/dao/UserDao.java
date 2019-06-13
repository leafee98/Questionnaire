package dao;

import dataobject.*;
import util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
						"select uid, username, passwd, nickname from q_user where username = ?;");
				state.setString(1, this.user.getUsername());
				rs = state.executeQuery();

				if (rs.next()) {
					this.user.setUid(rs.getLong("uid"));
					this.user.setNickname(rs.getString("nickname"));
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
	
	public String addUser() {
		String addResult = null;
		PreparedStatement test = null;
		PreparedStatement state = null;
		try {
			// if username already exists
			test = connection.prepareStatement("select * from q_user where username = ?");
			test.setString(1,  user.getUsername());
			if (!test.execute()) {
				addResult = "Username dumplicate!";
			} else {
				state = connection.prepareStatement(
						"insert into q_user (uid, username, passwd, nickname) values (default, ?, ?, ?);");
				state.setString(1, user.getUsername());
				state.setString(2, user.getPasswd());
				state.setString(3, user.getNickname());
				
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
	
	public ArrayList<Paper> getOwnPapers() {
		ArrayList<Paper> ownPapers = new ArrayList<Paper>();
		PreparedStatement state = null;
		ResultSet rs = null;
		try {
			state = connection.prepareStatement(
					"select paperid, paper_name from q_paper where uid = ?");
			state.setLong(1, user.getUid());
			rs = state.executeQuery();
			while (rs.next()) {
				Paper paper = new Paper();
				paper.setPaperid(rs.getLong("paperid"));
				paper.setPapername(rs.getString("paper_name"));
				paper.setOwnerid(user.getUid());
				ownPapers.add(paper);
			}
		} catch (SQLException e) {
			System.out.println("Unknown SQLException!");
			e.printStackTrace();
			System.exit(-1);
		}
		return ownPapers;
	}
	
	public static void main(String[] args) {
//		UserDao ud = new UserDao();
//		if (ud.checkUser("leafee", "feng")) {
//			System.out.println("current user and passwd");
//		}
//		ArrayList<Paper> papers = ud.getOwnPapers();
//		for (Paper x : papers) {
//			for (Question y : new PaperDao(x).getQuestion()) {
//				System.out.println(y.getQuestion());
//				for (Selection z : new QuestionDao(y).getSelection()) {
//					System.out.println(z.getSelection_describe());
//				}
//				System.out.println();
//				for (Answer z : new QuestionDao(y).getAnswers()) {
//					System.out.println(z.getAnswer());
//				}
//				System.out.println("==========");
//			}
//		}

		User u = new User();
		u.setPasswd("feng");
		u.setUsername("leafee");
		u.setNickname("Leafee");
		UserDao ud = new UserDao(u);
		String msg;
		if ((msg = ud.addUser()) == null) {
			System.out.println("Succeed!");
		} else {
			System.out.println(msg);
		}
		
		
	}
}

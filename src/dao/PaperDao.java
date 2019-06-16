package dao;

import dataobject.*;
import util.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PaperDao {
	private Paper paper;
	private Connection connection;
	
	/*
	 * init the PaperDao, init the paper inside PaperDao with everythin default
	 */
	public PaperDao() {
		this.connection = Conn.getConnection();
		paper = new Paper();
	}
	
	/*
	 * init the PaperDao, init the paper inside PaperDao with the paper
	 * passed in as parameter
	 */
	public PaperDao(Paper p) {
		this();
		this.setPaper(p);
	}
	
	/*
	 * get the paper inside the PaperDao
	 */
	public Paper getPaper() {
		return this.paper;
	}
	
	/*
	 * set the paper inside the PaperDao to the paper passed in.
	 */
	public Paper setPaper(Paper paper) {
		return this.paper = paper;
	}

	/*
	 * set the paper inside the PaperDao, using parameter paperid to get paper from database
	 */
	public Paper setPaper(long paperid) {
		PreparedStatement state = null;
		ResultSet rs = null;
		try {
			state = connection.prepareStatement("select paperid, paper_name from q_paper where paperid = ?");
			state.setLong(1, paperid);
			rs = state.executeQuery();
			if (rs.next()) {
				paper.setPaperid(rs.getLong("paperid"));
				paper.setOwnerid(rs.getLong("uid"));
				paper.setPapername(rs.getString("papername"));
			} else {
				paper = null;
			}
		} catch (SQLException e) {
			System.out.println("Unknown SQLException!");
			e.printStackTrace();
			System.exit(-5);
		}
		return this.paper;
	}
	
	/*
	 * add a new paper to database, after adding, the paper inside the PaperDao
	 * will be set to the paper passed in as parameter
	 */
	public void addPaper(Paper paper) {
		PreparedStatement state = null;
		ResultSet rs = null;
		try {
			state = connection.prepareStatement(
					"insert into q_paper (paperid, uid, paper_name, publish_time, cutoff_time) values (default, ?, ?, ?, ?);");
			state.setLong(1, paper.getOwnerid());
			state.setString(2, paper.getPapername());
			state.setTimestamp(3, paper.getPublish_time());
			state.setTimestamp(4, paper.getCutoff_time());
			state.executeUpdate();
			
			state = connection.prepareStatement(
					"select paperid from q_paper where uid = ? and paper_name = ? and publish_time = ? and cutoff_time = ?");
			state.setLong(1, paper.getOwnerid());
			state.setString(2, paper.getPapername());
			state.setTimestamp(3, paper.getPublish_time());
			state.setTimestamp(4, paper.getCutoff_time());
			rs = state.executeQuery();

			rs.next();
			paper.setPaperid(rs.getLong("paperid"));
		} catch (SQLException e) {
			System.out.println("Unknown SQLException!");
			e.printStackTrace();
		}
	}
	
	/*
	 * add paper to database, using the paper inside the PaperDao
	 */
	public void addPaper() {
		this.addPaper(this.paper);
	}
	
	/*
	 * you can update new uid, which mean give the owner ship to another user,
	 * paper name, publish_time and cutoff_time of the Paper
	 */
	public boolean updatePaper() {
		PreparedStatement state = null;
		int result = 0;
		try {
			state = connection.prepareStatement(
					"update q_paper set uid = ?, paper_name = ?, publish_time = ?, curoff_time = ? where paperid = ?;");
			state.setLong(1, paper.getOwnerid());
			state.setString(2, paper.getPapername());
			state.setTimestamp(3, paper.getPublish_time());
			state.setTimestamp(4, paper.getCutoff_time());
			state.setLong(5,  paper.getPaperid());
			result = state.executeUpdate();
		} catch (SQLException e) {
			result = 0;
			e.printStackTrace();
		}
		if (result == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	/*
	 * get all queston belong to the paper inside the PaperDao
	 */
	public ArrayList<Question> getQuestion() {
		ArrayList<Question> questions = new ArrayList<Question>();
		ResultSet rs = null;
		PreparedStatement state = null;
		try {
			state = connection.prepareStatement(
					"select questionid, paperid, question_type, question from q_question where paperid = ?");
			state.setLong(1, paper.getPaperid());
			rs = state.executeQuery();
			while (rs.next()) {
				Question que = new Question();
				que.setQuestionid(rs.getLong("questionid"));
				que.setPaperid(rs.getLong("paperid"));
				que.setQuestion(rs.getString("question"));
				que.setType(rs.getString("question_type"));
				questions.add(que);
			}
		} catch (SQLException e) {
			System.out.println("Unknown SQLException!");
			e.printStackTrace();
			System.exit(-5);
		}
		return questions;
	}
	
	/*
	 * allow a user to do the paper inside the PaperDao
	 */
	public boolean addAllowUser(Long uid) {
		PreparedStatement state = null;
		boolean addFlag = true;
		try {
			state = connection.prepareStatement(
					"insert into q_allow_answer (paperid, uid) values (?, ?);");
			state.setLong(1, paper.getPaperid());
			state.setLong(2, uid);
			state.executeQuery();
		} catch (SQLException e) {
			addFlag = false;

			System.out.println("UnKnown SQLException!");
			e.printStackTrace();
			System.exit(-12);
		}
		return addFlag;
	}
	
	/*
	 * get all user allowed to do the paper inside the PaperDao
	 */
	public ArrayList<User> getAllowedUser() {
		ArrayList<User> allowedUser = new ArrayList<User>();
		PreparedStatement state = null;
		ResultSet rs = null;
		try {
			state = connection.prepareStatement(
					"select q_user.uid as uid, q_user.username as username, q_user.nickname as nickname " + 
					"from q_allow_answer join q_user on q_allow_answer.uid = q_user.uid " +
					"where paperid = ?;");
			state.setLong(1, paper.getPaperid());
			rs = state.executeQuery();
			while (rs.next()) {
				User u = new User();
				u.setUsername(rs.getString("username"));
				u.setNickname(rs.getString("nickname"));
				u.setUid(rs.getLong("uid"));
				allowedUser.add(u);
			}
		} catch (SQLException e) {
			System.out.println("UnKnown SQLException!");
			e.printStackTrace();
			System.exit(-13);
		}
		return allowedUser;
	}
}

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
	
	public PaperDao() {
		this.connection = Conn.getConnection();
	}
	
	public PaperDao(Paper p) {
		this();
		this.setPaper(p);
	}
	
	public Paper setPaper(Paper paper) {
		return this.paper = paper;
	}

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
	
	public void addPaper(Paper pap) {
		PreparedStatement state = null;
		try {
			state = connection.prepareStatement(
					"insert into q_paper (paper_id, uid, paper_name, publish_time, cutoff_time) values (default, ?, ?, ?, ?);");
			// state.setNull(1, java.sql.Types.INTEGER);
			state.setLong(1, pap.getOwnerid());
			state.setString(2, pap.getPapername());
			state.setTimestamp(3, pap.getPublish_time());
			state.setTimestamp(4, pap.getCutoff_time());
		} catch (SQLException e) {
			System.out.println("Unknown SQLException!");
			e.printStackTrace();
			System.exit(-5);
		}
	}
	
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
}

package dao;

import dataobject.*;
import util.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class QuestionDao {
	private Question question;
	private Connection connection;
	
	public QuestionDao() {
		this.connection = Conn.getConnection();
	}
	
	public QuestionDao(Question q) {
		this();
		this.setQuestion(q);
	}
	
	public Question setQuestion(Question question) {
		return this.question = question;
	}
	
	public Question setQuestion(Long questionid) {
		PreparedStatement state = null;
		ResultSet rs = null;
		try {
			state = connection.prepareStatement(
					"select questionid, paperid, question_type, question where questionid = ?");
			state.setLong(1, questionid);
			rs = state.executeQuery();
			question.setQuestionid(rs.getLong("questionid"));
			question.setPaperid(rs.getLong("paperid"));
			question.setQuestion(rs.getString("question"));
			question.setType(rs.getString("question_type"));
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(-6);
		}
		return this.question;
	}
	
	public void addQuestion(Question que) {
		PreparedStatement state = null;
		try {
			state = connection.prepareStatement(
					"insert into q_question (questionid, paperid, question_type, question) values (?, ?, ?, ?);");
			state.setNull(1, java.sql.Types.INTEGER);
			state.setLong(2, que.getPaperid());
			state.setString(3, que.getType());
			state.setString(4, que.getQuestion());
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-7);
		}
	}
	
	public ArrayList<Answer> getAnswers() {
		ArrayList<Answer> answers = new ArrayList<Answer>();
		ResultSet rs = null;
		PreparedStatement state = null;
		try {
			state = connection.prepareStatement(
					"select answerid, questionid, answer, respondent from q_answer where questionid = ?;");
			state.setLong(1, question.getQuestionid());
			rs = state.executeQuery();
			while (rs.next()) {
				Answer ans = new Answer();
				ans.setAnswerid(rs.getLong("answerid"));
				ans.setQuestionid(rs.getLong("questionid"));
				ans.setAnswer(rs.getString("answer"));
				ans.setRespondent(rs.getLong("respondent"));
				answers.add(ans);
			}
		} catch (SQLException e) {
			System.out.println("Unknown SQLException!");
			e.printStackTrace();
			System.exit(-8);
		}
		return answers;
	}
	
	public ArrayList<Selection> getSelection() {
		ArrayList<Selection> selections = new ArrayList<Selection>();
		ResultSet rs = null;
		PreparedStatement state = null;
		try {
			state = connection.prepareStatement(
					"select selectionid, questionid, selection_describe from q_selection where questionid = ?");
			state.setLong(1, question.getQuestionid());
			rs = state.executeQuery();
			while (rs.next()) {
				Selection sele = new Selection();
				sele.setSelectionid(rs.getLong("selectionid"));
				sele.setQuestionid(rs.getLong("questionid"));
				sele.setSelection_describe(rs.getString("selection_describe"));
				selections.add(sele);
			}
		} catch (SQLException e) {
			System.out.println("Unknown SQLException!");
			e.printStackTrace();
			System.exit(-9);
		}
		return selections;	
	}
}

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
	
	public Question getQuestion() {
		return this.question;
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
	
	public void addQuestion(Question question) {
		PreparedStatement state = null;
		ResultSet rs = null;
		try {
			state = connection.prepareStatement(
					"insert into q_question (questionid, paperid, question_type, question) values (?, ?, ?, ?);");
			state.setNull(1, java.sql.Types.INTEGER);
			state.setLong(2, question.getPaperid());
			state.setString(3, question.getType());
			state.setString(4, question.getQuestion());
			state.executeQuery();
			
			// get the question id from database;
			state = connection.prepareStatement(
					"select questionid from q_question where paperid = ? and question_type = ? and question = ?;");
			state.setLong(1, question.getPaperid());
			state.setString(2, question.getType());
			state.setString(3, question.getQuestion());
			rs = state.executeQuery();

			rs.next();
			question.setQuestionid(rs.getLong("questionid"));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-7);
		}
	}
	
	public void addQuestion() {
		this.addQuestion(this.question);
	}
	
	public ArrayList<Answer> getAllAnswers() {
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
	
	public ArrayList<Answer> getSpecificAnswers(Long respondentId) {
		ArrayList<Answer> answers = new ArrayList<Answer>();
		ResultSet rs = null;
		PreparedStatement state = null;
		try {
			state = connection.prepareStatement(
					"select answerid, questionid, answer from q_answer where questionid = ? and respondent = ?;");
			state.setLong(1, question.getQuestionid());
			state.setLong(2, respondentId);
			rs = state.executeQuery();
			while (rs.next()) {
				Answer ans = new Answer();
				ans.setAnswerid(rs.getLong("answerid"));
				ans.setQuestionid(rs.getLong("questionid"));
				ans.setAnswer(rs.getString("answer"));
				ans.setRespondent(respondentId);
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
	
	public boolean addSelection(Selection selection) {
		PreparedStatement state = null;
		boolean addFlag = true;
		try {
			state = connection.prepareStatement(
					"insert into q_selection (questionid, selection_describe) values (?, ?);");
			state.setLong(1, selection.getQuestionid());
			state.setString(2, selection.getSelection_describe());
			state.executeQuery();
		} catch (SQLException e) {
			addFlag = false;

			System.out.println("Unknown SQLException");
			e.printStackTrace();
			System.exit(-12);
		}
		return addFlag;
	}
	
	public boolean addSelection(String selectionDesc) {
		PreparedStatement state = null;
		boolean addFlag = true;
		try {
			state = connection.prepareStatement(
					"insert into q_selection (questionid, selection_describe) values (?, ?);");
			state.setLong(1, question.getQuestionid());
			state.setString(2, selectionDesc);
			state.executeQuery();
		} catch (SQLException e) {
			addFlag = false;

			System.out.println("Unknown SQLException");
			e.printStackTrace();
			System.exit(-12);
		}
		return addFlag;
	}
	
	public boolean addAnswer(Answer ans) {
		PreparedStatement state = null;
		boolean addFlag = true;
		try {
			state = connection.prepareStatement(
					"insert into q_answer (questionid, answer, respondent) values (?, ?, ?);");
			state.setLong(1, ans.getQuestionid());
			state.setString(2, ans.getAnswer());
			state.setLong(3, ans.getRespondent());
			state.executeQuery();
		} catch (SQLException e) {
			addFlag = false;

			System.out.println("Unknown SQLException");
			e.printStackTrace();
			System.exit(-12);
		}
		return addFlag;
	}
	
	public boolean addAnswer(String answerContent, Long respondentId) {
		PreparedStatement state = null;
		boolean addFlag = true;
		try {
			state = connection.prepareStatement(
					"insert into q_answer (questionid, answer, respondent) values (?, ?, ?);");
			state.setLong(1, question.getQuestionid());
			state.setString(2, answerContent);
			state.setLong(3, respondentId);
			state.executeQuery();
		} catch (SQLException e) {
			addFlag = false;

			System.out.println("Unknown SQLException");
			e.printStackTrace();
			System.exit(-12);
		}
		return addFlag;
	}
}

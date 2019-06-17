package example;


import java.sql.Timestamp;
import dao.*;

public class Ex1 {
	public static void main(String[] args) {
		UserDao udUserA = new UserDao();
		UserDao udUserB = new UserDao();
		UserDao udUserC = new UserDao();
		UserDao udUserD = new UserDao();
		UserDao udUserE = new UserDao();
		udUserA.addUser("usera", "usera", "USERA", true);
		udUserB.addUser("userb", "userb", "USERB", false);
		udUserC.addUser("userc", "userc", "USERC", false);
		udUserD.addUser("userd", "userd", "USERD", false);
		udUserE.addUser("usere", "usere", "USERE", false);
		
		PaperDao pdPaperPC = new PaperDao();
//		PaperDao pdPaperGame = new PaperDao();
		pdPaperPC.addPaper(udUserA.getUser().getUid(), "组装电脑意向调查", 
				Timestamp.valueOf("2019-06-01 00:00:00"),
				Timestamp.valueOf("2019-06-30 23:59:59"));
		QuestionDao qdQuestionPC1 = new QuestionDao();
		QuestionDao qdQuestionPC2 = new QuestionDao();
		QuestionDao qdQuestionPC3 = new QuestionDao();
		QuestionDao qdQuestionPC4 = new QuestionDao();
		QuestionDao qdQuestionPC5 = new QuestionDao();
		qdQuestionPC1.addQuestion(pdPaperPC.getPaper().getPaperid(),
				"radio", "内存容量");
		qdQuestionPC1.addSelection("4G");
		qdQuestionPC1.addSelection("8G");
		qdQuestionPC1.addSelection("16G");
		qdQuestionPC1.addSelection("32G");
		qdQuestionPC2.addQuestion(pdPaperPC.getPaper().getPaperid(),
				"radio", "显卡品牌");
		qdQuestionPC2.addSelection("AMD");
		qdQuestionPC2.addSelection("英伟达");
		qdQuestionPC3.addQuestion(pdPaperPC.getPaper().getPaperid(),
				"radio", "主板品牌");
		qdQuestionPC3.addSelection("技嘉");
		qdQuestionPC3.addSelection("华硕");
		qdQuestionPC3.addSelection("华擎");
		qdQuestionPC3.addSelection("微星");
		qdQuestionPC4.addQuestion(pdPaperPC.getPaper().getPaperid(),
				"radio", "CPU品牌");
		qdQuestionPC4.addSelection("AMD");
		qdQuestionPC4.addSelection("英特尔");
		qdQuestionPC5.addQuestion(pdPaperPC.getPaper().getPaperid(),
				"check", "看好哪些品牌机");
		qdQuestionPC5.addSelection("戴尔");
		qdQuestionPC5.addSelection("联想");
		qdQuestionPC5.addSelection("神舟");
		qdQuestionPC5.addSelection("惠普");
		qdQuestionPC5.addSelection("机械革命");

//		pdPaperGame.addPaper(udUserA.getUser().getUid(), "游戏意向调查",
//				Timestamp.valueOf("2019-06-15 00:00:00"),
//				Timestamp.valueOf("2019-06-25 23:59:59"));
//		QuestionDao qdQuestionGame1 = new QuestionDao();
//		QuestionDao qdQuestionGame2 = new QuestionDao();
//		QuestionDao qdQuestionGame3 = new QuestionDao();
//		QuestionDao qdQuestionGame4 = new QuestionDao();

	}

}

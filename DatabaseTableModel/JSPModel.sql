DROP DATABASE IF EXISTS questionnaire;
CREATE DATABASE questionnaire; USE questionnaire;
CREATE TABLE q_user (
	uid INT PRIMARY KEY AUTO_INCREMENT,
	username CHAR(30) NOT NULL UNIQUE,
	passwd CHAR(32) NOT NULL,
	nickname nchar(100) NOT NULL,
	admin BOOL NOT NULL
);
CREATE TABLE q_paper (
	paperid INT PRIMARY KEY AUTO_INCREMENT,
	uid INT,
	paper_name nchar(100) NOT NULL UNIQUE,
	publish_time DATETIME NOT NULL DEFAULT NOW(),
	cutoff_time DATETIME NOT NULL DEFAULT ADDDATE(NOW(), 3),
	
	CONSTRAINT reference_of_ownerid FOREIGN KEY(uid) REFERENCES q_user(uid),
	CONSTRAINT unique_paper_name_per_user UNIQUE (uid, paper_name)
);
CREATE TABLE q_question (
	questionid INT PRIMARY KEY AUTO_INCREMENT,
	paperid INT,
	question_type CHAR(10) NOT NULL,
	question nvarchar(1000) NOT NULL,
	
	CONSTRAINT refenence_of_paperid FOREIGN KEY (paperid) REFERENCES q_paper(paperid),
	CONSTRAINT almost_unique_question_per_paper UNIQUE (paperid, question_type, question)
);
CREATE TABLE q_selection (
	selectionid INT PRIMARY KEY AUTO_INCREMENT,
	questionid INT,
	selection_describe TEXT NOT NULL,
	
	CONSTRAINT reference_of_questionid FOREIGN KEY (questionid) REFERENCES q_question(questionid)
);
CREATE TABLE q_answer (
	answerid INT PRIMARY KEY AUTO_INCREMENT,
	questionid INT,
	answer TEXT NOT NULL,
	respondent INT,
	
	CONSTRAINT references_of_questionid FOREIGN KEY (questionid) REFERENCES q_question(questionid),
	CONSTRAINT references_of_respondent FOREIGN KEY (respondent) REFERENCES q_user(uid)
);
CREATE TABLE q_allow_answer (
	paperid INT NOT NULL,
	uid INT NOT NULL,
	
	CONSTRAINT reference_of_destination_paperid FOREIGN KEY (paperid) REFERENCES q_paper(paperid),
	CONSTRAINT reference_of_user_who_allow FOREIGN KEY (uid) REFERENCES q_user(uid),
	CONSTRAINT primarykey_q_allow_answer PRIMARY KEY (paperid, uid)
);



delimiter //
CREATE PROCEDURE check_user_passwd(IN un CHAR(30), IN p CHAR(32))
BEGIN
	SET @pass = (SELECT passwd FROM q_user WHERE username = un);
	IF @pass = MD5(p) THEN
		SELECT 1 AS result;
	ELSE
		SELECT 0 AS result;
	END IF;
END
//
delimiter ;


delimiter //
CREATE TRIGGER encry_passwd BEFORE INSERT 
	ON q_user FOR EACH ROW
BEGIN
	SET new.passwd = MD5(new.passwd);
END
//
delimiter ;


delimiter //
CREATE TRIGGER encry_passwd_on_update AFTER UPDATE
	ON q_user FOR EACH ROW
BEGIN
	SET new.passwd = MD5(new.passwd);
END
//
delimiter ;
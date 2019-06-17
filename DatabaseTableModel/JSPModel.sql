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
CREATE TABLE q_done_answer (
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
CREATE PROCEDURE delete_question(IN q_id INT)
BEGIN
	DELETE FROM q_answer WHERE questionid = q_id;
	DELETE FROM q_selection WHERE questionid = q_id;
	DELETE FROM q_question WHERE questionid = q_id;
END
//
delimiter ;

delimiter //
CREATE PROCEDURE delete_paper(IN p_id INT)
BEGIN
	DELETE FROM q_answer WHERE questionid IN (
		SELECT questionid FROM q_question WHERE paperid = p_id
	);
	DELETE FROM q_selection WHERE questionid IN (
		SELECT questionid FROM q_question WHERE paperid = p_id
	);
	DELETE FROM q_question WHERE paperid = p_id;
	DELETE FROM q_allow_answer WHERE paperid = p_id;
	DELETE FROM q_paper WHERE paperid = p_id;
END
//
delimiter ;

delimiter //
CREATE PROCEDURE delete_user(IN un CHAR(30))
BEGIN
	SET @uid = (SELECT uid FROM q_user WHERE username = un);
	
	DELETE FROM q_answer WHERE questionid IN (
		SELECT questionid FROM q_question WHERE paperid IN (
			SELECT paperid FROM q_paper WHERE uid = @uid
		)
	);
	DELETE FROM q_selection WHERE questionid IN (
		SELECT questionid FROM q_question WHERE paperid IN (
			SELECT paperid FROM q_paper WHERE uid = @uid
		)
	);
	DELETE FROM q_question WHERE paperid IN (
			SELECT paperid FROM q_paper WHERE uid = @uid
	);
	DELETE FROM q_allow_answer WHERE paperid IN (
			SELECT paperid FROM q_paper WHERE uid = @uid
	);
	DELETE FROM q_paper WHERE paperid IN (
			SELECT paperid FROM q_paper WHERE uid = @uid
	);
	DELETE FROM q_user WHERE uid = @uid;
END //
delimiter ;
/*
delimiter //
CREATE PROCEDURE delete_user(IN un CHAR(30))
BEGIN
	DECLARE u_uid INT;
	DECLARE conti INT DEFAULT TRUE;
	DECLARE current_paperid INT;
	DECLARE cur_paperid CURSOR FOR SELECT paperid FROM q_paper WHERE uid = u_uid;
	DECLARE CONTINUE handler FOR NOT FOUND SET @conti = FALSE;
	SET u_uid = (SELECT uid FROM q_user WHERE username = un);
	
	OPEN cur_paperid;
	WHILE conti = TRUE DO
		FETCH cur_paperid INTO current_paperid;
		CALL delete_paper(current_paperid);
	END WHILE;
	CLOSE cur_paperid;
END
//
delimiter ;
*/

delimiter //
CREATE TRIGGER encry_passwd BEFORE INSERT 
	ON q_user FOR EACH ROW
BEGIN
	SET new.passwd = MD5(new.passwd);
END
//
delimiter ;


delimiter //
CREATE TRIGGER encry_passwd_on_update BEFORE UPDATE
	ON q_user FOR EACH ROW
BEGIN
	SET new.passwd = MD5(new.passwd);
END
//
delimiter ;
delete from q_answer;
delete from q_selection;
delete from q_question;
delete from q_allow_answer;
delete from q_paper;
delete from q_user;

insert into q_user (q_user.uid, q_user.admin, q_user.username, q_user.passwd, q_user.nickname) values 
	(null, 1, 'leafee', 'feng', 'Leafee');
set @uid = (select uid from q_user where username = 'leafee');

insert into q_paper (paperid, uid, paper_name, publish_time, cutoff_time) values
	(null, @uid, 'test_paper', default, default);
set @paperid = (select paperid from q_paper where uid = @uid);

insert into q_question (questionid, paperid, question_type, question) values
	(null, @paperid, "radio", 'test_question, please choose the current one');
set @questionid = (select questionid from q_question where paperid = @paperid);

insert into q_selection (selectionid, questionid, selection_describe) values
	(null, @questionid, 'the current one');
insert into q_selection (selectionid, questionid, selection_describe) values
	(null, @questionid, 'the wrong one');
set @selectionid = (select selectionid from q_selection where selection_describe = 'the current one');

insert into q_allow_answer (paperid, uid) values (@paperid, @uid);
	
insert into q_answer (answerid, questionid, answer, respondent) values
	(null, @questionid, @selectionid, @uid);

drop database if exists questionnaire;
create database questionnaire;
use questionnaire;

create table q_user (
	uid int primary key auto_increment,
	username char(30) not null unique,
	passwd char(32) not null,
	nickname nchar(100) not null,
	admin bool not null,
);

create table q_paper (
	paperid int primary key auto_increment,
	uid int,
	paper_name nchar(100) not null unique,
	publish_time datetime not null default NOW(),
	cutoff_time datetime not null default adddate(NOW(), 3),
	
	constraint reference_of_ownerid
		foreign key(uid) references q_user(uid),
	constraint unique_paper_name_per_user
		unique (uid, paper_name)
);

create table q_question (
	questionid int primary key auto_increment,
	paperid int,
	question_type char(10) not null,
	question nvarchar(1000) not null,
	
	constraint refenence_of_paperid
		foreign key (paperid) references q_paper(paperid),
	constraint almost_unique_question_per_paper
		unique (paperid, question_type, question)
);

create table q_selection (
	selectionid int primary key auto_increment,
	questionid int,
	selection_describe text not null,
	
	constraint reference_of_questionid
		foreign key (questionid) references q_question(questionid)
);

create table q_answer (
	answerid int primary key auto_increment,
	questionid int,
	answer text not null,
	respondent int,
	
	constraint references_of_questionid
		foreign key (questionid) references q_question(questionid),
	constraint references_of_respondent
		foreign key (respondent) references q_user(uid)
);

create table q_allow_answer (
	paperid int not null,
	uid int not null,
	
	constraint reference_of_destination_paperid
		foreign key (paperid) references q_paper(paperid),
	constraint reference_of_user_who_allow
		foreign key (uid) references q_user(uid),
	constraint primarykey_q_allow_answer
		primary key (paperid, uid)
);



delimiter //
create procedure check_user_passwd(in un char(30), in p char(32))
begin
	set @pass = (select passwd from q_user where username = un);
	if @pass = md5(p) then
		select 1 as result;
	else
		select 0 as result;
	end if;
end
//
delimiter ;


delimiter //
create trigger encry_passwd before insert on q_user for each row
begin
	set new.passwd = md5(new.passwd);
end
//
delimiter ;
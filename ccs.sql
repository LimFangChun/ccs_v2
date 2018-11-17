--  phpMyAdmin SQL Dump
--  version 4.7.4
--  https://www.phpmyadmin.net/
-- 
--  Host: 127.0.0.1
--  Generation Time: Jan 17, 2018 at 02:49 PM
--  Server version: 10.1.26-MariaDB
--  PHP Version: 7.1.9

-- PLEASE READ mqtt_server.php for details

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+08:00";

--  drop all tables first in case you have any duplicated table with same name
--  drop in reverse order
--  Also make your life easier when you have changes in database
DROP TABLE IF EXISTS RoomSecret;
DROP TABLE IF EXISTS Friendship;
DROP TABLE IF EXISTS Message;
DROP TABLE IF EXISTS Participant;
DROP TABLE IF EXISTS Chat_Room;
DROP TABLE IF EXISTS Student;
DROP TABLE IF EXISTS UserActivityLog;
DROP TABLE IF EXISTS User;
DROP TABLE IF EXISTS City;
DROP TABLE IF EXISTS State;

--  Begin creating all the tables
--  Create from base table first
CREATE TABLE State(
	state_id 	VARCHAR(10)	NOT NULL,
	state_name 	VARCHAR(50) NOT NULL,
	PRIMARY KEY (state_id)
);

CREATE TABLE City(
	city_id 	VARCHAR(10) NOT NULL,
	city_name 	VARCHAR(50) NOT NULL,
	state_id		VARCHAR(10) NOT NULL,
	PRIMARY	KEY	(city_id),
	FOREIGN KEY (state_id) REFERENCES State(state_id)
);

CREATE TABLE User(
	user_id 	int(10) NOT NULL AUTO_INCREMENT,
	username 	varchar(200) NOT NULL,
	display_name 	varchar(200),
	position 	varchar(20),
	password 	varchar(50),
	gender 		varchar(10),
	nric 		varchar(20),
	phone_number 	varchar(20),
	email 		varchar(50),
	address 	varchar(200), 
	city_id 	varchar(10),
	status 		varchar(10) DEFAULT 'Offline',
	last_online datetime,
	last_longitude 	decimal(18, 9),
	last_latitude 	decimal(18, 9),
	public_key	varchar(256),
	PRIMARY KEY (user_id),
	FOREIGN KEY (city_id) REFERENCES City(city_id)
);

CREATE TABLE UserActivityLog(
	log_id 		int(10) NOT NULL AUTO_INCREMENT,
	user_id 	int(10) NOT NULL,
	description 	varchar(200),
	date_created 	datetime DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (log_id),
	FOREIGN KEY (user_id) REFERENCES User(user_id)
);

CREATE TABLE Student(
	student_id 	varchar(20) NOT NULL,
	faculty 	varchar(20),
	course 		varchar(10),
	tutorial_group 	int(5),
	intake 		varchar(10),
	academic_year 	int(5),
	user_id 	int(10) NOT NULL,
	PRIMARY KEY (student_id),
	FOREIGN KEY (user_id) REFERENCES User(user_id)
);

CREATE TABLE Friendship(
	user_id 	int(10) NOT NULL,
	friend_id 	int(10) NOT NULL,
	status 		varchar(20) DEFAULT 'Pending',
	date_created 	datetime DEFAULT CURRENT_TIMESTAMP,
	sender_id 	int(10),
	PRIMARY KEY (user_id, friend_id),
	FOREIGN KEY (user_id) REFERENCES User(user_id),
	FOREIGN KEY (friend_id) REFERENCES User(user_id),
	FOREIGN KEY (sender_id) REFERENCES User(user_id)
);

CREATE TABLE Chat_Room(
	room_id 	int(10) NOT NULL AUTO_INCREMENT,
	owner_id 	int(10) NOT NULL,
	room_name 	varchar(100) NOT NULL,
	date_created 	datetime DEFAULT CURRENT_TIMESTAMP,
	last_update 	datetime DEFAULT CURRENT_TIMESTAMP,
	topic_address varchar(200) NOT NULL,
	PRIMARY KEY (room_id),
	FOREIGN KEY (owner_id) REFERENCES User(user_id)
);

CREATE TABLE Participant(
	room_id 	int(10) NOT NULL,
	user_id 	int(10) NOT NULL,
	role 		varchar(50) NOT NULL DEFAULT 'Member',
	join_date 	datetime DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY (room_id, user_id),
	FOREIGN KEY (room_id) REFERENCES Chat_Room(room_id),
	FOREIGN KEY (user_id) REFERENCES User(user_id)
);

CREATE TABLE Message(
	message_id 	int(20) NOT NULL AUTO_INCREMENT,
	message 	varchar(300) NOT NULL,
	sender_id 	int(10) NOT NULL,
	date_created datetime DEFAULT CURRENT_TIMESTAMP,
	room_id 	int(10) NOT NULL,
	message_type 	varchar(10) DEFAULT 'Text',
	status 		varchar(20) DEFAULT 'Unpinned',
	PRIMARY KEY (message_id),
	FOREIGN KEY (sender_id) REFERENCES User(user_id),
	FOREIGN KEY (room_id) REFERENCES Chat_Room(room_id)
);

CREATE TABLE RoomSecret(
    room_id int(10) NOT NULL,
    user_id int(10) NOT NULL,
    secret_key  varchar(256) NOT NULL,
    status  varchar(10) NOT NULL,
	PRIMARY KEY (room_id, user_id),
	FOREIGN KEY (room_id) REFERENCES Chat_Room(room_id),
	FOREIGN KEY (user_id) REFERENCES User(user_id)
);
--  Generate dummy data
--  You may use mockaroo.com to generate dummy data, its free

--  Done by: Lim Fang Chun
--  State table records
--  Column: state_id(PK), StateName
INSERT INTO State (state_id, state_name) VALUES('001', 'Johor');
INSERT INTO State (state_id, state_name) VALUES('002', 'Kedah');
INSERT INTO State (state_id, state_name) VALUES('003', 'Kelantan');
INSERT INTO State (state_id, state_name) VALUES('004', 'Melaka');
INSERT INTO State (state_id, state_name) VALUES('005', 'Negeri Sembilan');
INSERT INTO State (state_id, state_name) VALUES('006', 'Pahang');
INSERT INTO State (state_id, state_name) VALUES('007', 'Penang');
INSERT INTO State (state_id, state_name) VALUES('008', 'Perak');
INSERT INTO State (state_id, state_name) VALUES('009', 'Perlis');
INSERT INTO State (state_id, state_name) VALUES('010', 'Sabah');
INSERT INTO State (state_id, state_name) VALUES('011', 'Sarawak');
INSERT INTO State (state_id, state_name) VALUES('012', 'Selangor');
INSERT INTO State (state_id, state_name) VALUES('013', 'Terrengganu');
INSERT INTO State (state_id, state_name) VALUES('014', 'Kuala Lumpur');
INSERT INTO State (state_id, state_name) VALUES('015', 'Putrajaya');

--  Done by: Lim Fang Chun
--  City table records
--  Column: city_id(PK), city_name, state_id(FK)
	--  For Johor state
INSERT INTO City (city_id, city_name, state_id) VALUES('C001', 'Johor Bahru', '001');
INSERT INTO City (city_id, city_name, state_id) VALUES('C002', 'Batu Pahat', '001');
INSERT INTO City (city_id, city_name, state_id) VALUES('C003', 'Kluang', '001');
INSERT INTO City (city_id, city_name, state_id) VALUES('C004', 'Kulai', '001');
INSERT INTO City (city_id, city_name, state_id) VALUES('C005', 'Muar', '001');
INSERT INTO City (city_id, city_name, state_id) VALUES('C006', 'Kota Tinggi', '001');
INSERT INTO City (city_id, city_name, state_id) VALUES('C007', 'Segamat', '001');
INSERT INTO City (city_id, city_name, state_id) VALUES('C008', 'Pontian Kechil', '001');
INSERT INTO City (city_id, city_name, state_id) VALUES('C009', 'Tangkak', '001');

	--  For Kedah State
INSERT INTO City (city_id, city_name, state_id) VALUES('C010', 'Sungai Petani', '002');
INSERT INTO City (city_id, city_name, state_id) VALUES('C011', 'Alor Setar', '002');
INSERT INTO City (city_id, city_name, state_id) VALUES('C012', 'Kulim', '002');
INSERT INTO City (city_id, city_name, state_id) VALUES('C013', 'Kubang Pasu', '002');

	--  For Kelantan State
INSERT INTO City (city_id, city_name, state_id) VALUES('C014', 'Kota Bahru', '003');
INSERT INTO City (city_id, city_name, state_id) VALUES('C015', 'Pasir Mas', '003');

	--  For Melaka State
INSERT INTO City (city_id, city_name, state_id) VALUES('C016', 'Melaka City', '004');
INSERT INTO City (city_id, city_name, state_id) VALUES('C017', 'Alor Gajah', '004');

	--  For Negeri Sembilan State
INSERT INTO City (city_id, city_name, state_id) VALUES('C018', 'Seremban', '005');
INSERT INTO City (city_id, city_name, state_id) VALUES('C019', 'Nilai', '005');

	--  For Pahang State
INSERT INTO City (city_id, city_name, state_id) VALUES('C020', 'Kuantan', '006');
INSERT INTO City (city_id, city_name, state_id) VALUES('C021', 'Temerloh', '006');

	--  For Penang State
INSERT INTO City (city_id, city_name, state_id) VALUES('C022', 'George Town', '007');
INSERT INTO City (city_id, city_name, state_id) VALUES('C023', 'Seberang Perai', '007');

	--  For Perak State
INSERT INTO City (city_id, city_name, state_id) VALUES('C024', 'Ipoh', '008');
INSERT INTO City (city_id, city_name, state_id) VALUES('C025', 'Taiping', '008');
INSERT INTO City (city_id, city_name, state_id) VALUES('C026', 'Manjung', '008');

	--  For Perlis State
INSERT INTO City (city_id, city_name, state_id) VALUES('C027', 'Kangar', '009');

	--  For Sabah State
INSERT INTO City (city_id, city_name, state_id) VALUES('C028', 'Kota Kinabalu', '010');
INSERT INTO City (city_id, city_name, state_id) VALUES('C029', 'Tawau', '010');
INSERT INTO City (city_id, city_name, state_id) VALUES('C030', 'Ranau', '010');
INSERT INTO City (city_id, city_name, state_id) VALUES('C031', 'Sandakan', '010');
INSERT INTO City (city_id, city_name, state_id) VALUES('C032', 'Lahad Datu', '010');
INSERT INTO City (city_id, city_name, state_id) VALUES('C033', 'Kinabantangan', '010');
INSERT INTO City (city_id, city_name, state_id) VALUES('C034', 'Penampang', '010');
INSERT INTO City (city_id, city_name, state_id) VALUES('C035', 'Keningau', '010');

	--  For Sarawak State
INSERT INTO City (city_id, city_name, state_id) VALUES('C036', 'Kuching', '011');
INSERT INTO City (city_id, city_name, state_id) VALUES('C037', 'Padawan', '011');
INSERT INTO City (city_id, city_name, state_id) VALUES('C038', 'Miri', '011');
INSERT INTO City (city_id, city_name, state_id) VALUES('C039', 'Bintulu', '011');
INSERT INTO City (city_id, city_name, state_id) VALUES('C040', 'Sibu', '011');

	--  For Selangor State
INSERT INTO City (city_id, city_name, state_id) VALUES('C041', 'Kajang', '012');
INSERT INTO City (city_id, city_name, state_id) VALUES('C042', 'Klang', '012');
INSERT INTO City (city_id, city_name, state_id) VALUES('C043', 'Subang Jaya', '012');
INSERT INTO City (city_id, city_name, state_id) VALUES('C044', 'Petaling Jaya', '012');
INSERT INTO City (city_id, city_name, state_id) VALUES('C045', 'Selayang', '012');
INSERT INTO City (city_id, city_name, state_id) VALUES('C046', 'Shah Alam', '012');
INSERT INTO City (city_id, city_name, state_id) VALUES('C047', 'Ampang Jaya', '012');
INSERT INTO City (city_id, city_name, state_id) VALUES('C048', 'Kuala Langat', '012');
INSERT INTO City (city_id, city_name, state_id) VALUES('C049', 'Sepang', '012');
INSERT INTO City (city_id, city_name, state_id) VALUES('C050', 'Kuala Selangor', '012');
INSERT INTO City (city_id, city_name, state_id) VALUES('C051', 'Hulu Selangor', '012');

	--  For Terrengganu State
INSERT INTO City (city_id, city_name, state_id) VALUES('C052', 'Kuala Terrengganu', '013');
INSERT INTO City (city_id, city_name, state_id) VALUES('C053', 'Kemaman', '013');

	--  For Kuala Lumpur
INSERT INTO City (city_id, city_name, state_id) VALUES('C054', 'Bukit Bintang', '014');
INSERT INTO City (city_id, city_name, state_id) VALUES('C055', 'Titiwangsa', '014');
INSERT INTO City (city_id, city_name, state_id) VALUES('C056', 'Setiawangsa', '014');
INSERT INTO City (city_id, city_name, state_id) VALUES('C057', 'Wangsa Maju', '014');
INSERT INTO City (city_id, city_name, state_id) VALUES('C058', 'Batu', '014');
INSERT INTO City (city_id, city_name, state_id) VALUES('C059', 'Kepong', '014');
INSERT INTO City (city_id, city_name, state_id) VALUES('C060', 'Segambut', '014');
INSERT INTO City (city_id, city_name, state_id) VALUES('C061', 'Lembah Pantai', '014');
INSERT INTO City (city_id, city_name, state_id) VALUES('C062', 'Seputeh', '014');
INSERT INTO City (city_id, city_name, state_id) VALUES('C063', 'Bandar Tun Razak', '014');
INSERT INTO City (city_id, city_name, state_id) VALUES('C064', 'Cheras', '014');

	--  For Putrajaya
INSERT INTO City (city_id, city_name, state_id) VALUES('C065', 'Putrajaya', '015');

--  Done by: Lim Fang Chun
--  user table records
--  Column: display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('adam', 'user1', 'Student', '12345', 'M', '901123-01-7337', '092-3231801', 'dgislebert0@blogger.com', '79 Debra Drive', 'C065', 'Offline', '2018-09-27 02:05:44', -43.6644238, -19.8852788);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('123eqe', 'user2', 'Student', '12345', 'F', '930904-50-0362', '091-5152682', 'rcuthbertson1@photobucket.com', '8694 Melody Plaza', 'C065', 'Offline', '2018-09-27 05:00:12', 41.4584783, 43.8793185);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('leo', 'user3', 'Student', '12345', 'F', '981125-59-4164', '084-8810825', 'fcardno2@hostgator.com', '578 Anderson Alley', 'C051', 'Offline', '2018-09-29 19:05:47', 34.7409293, -19.6115404);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('nani', 'user4', 'Student', '12345', 'M', '900802-38-9987', '073-6717137', 'jblackway3@amazon.co.jp', '7 Mcbride Place', 'C065', 'Offline', '2018-09-29 23:30:51', 121.368162, 32.06078);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('tungdong', 'user5', 'Student', '12345', 'M', '960903-70-0752', '097-5328276', 'ywanless4@techcrunch.com', '77986 Hayes Lane', 'C065', 'Offline', '2018-09-30 19:23:51', 15.3606459, 50.3448824);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('louis', 'user6', 'Student', '12345', 'M', '930906-30-3940', '093-5154099', 'jrings5@newsvine.com', '421 Haas Pass', 'C050', 'Offline', '2018-09-30 19:39:04', -36.970226, -10.8512419);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('asdasd', 'user7', 'Student', '12345', 'M', '980904-89-1806', '086-3294501', 'brenbold6@wisc.edu', '66 Vera Court', 'C065', 'Offline', '2018-09-28 00:32:20', 120.9522977, 13.9300945);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('monkaS', 'user8', 'Student', '12345', 'F', '921106-08-2101', '099-2692157', 'hcorgenvin7@feedburner.com', '48337 Namekagon Crossing', 'C058', 'Offline', '2018-09-30 18:05:57', 120.9522977, 13.9300945);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('AdmiralBulldog', 'phaggerwood8', 'Student', '12345', 'M', '930720-15-4166', '096-7115670', 'dcornforth8@gnu.org', '70 2nd Place', 'C008', 'Offline', '2018-09-27 01:38:07', 20.7067845, 38.7811142);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('fffff', 'twaterhous9', 'Student', '12345', 'F', '921223-87-9431', '080-4140785', 'ggaines9@tmall.com', '39845 Forest Pass', 'C065', 'Offline', '2018-09-29 17:01:49', 13.4343715, 52.4867189);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('hhd', 'psiseya', 'Student', '12345', 'M', '931028-61-0549', '090-2832421', 'htinana@amazon.de', '57958 Clyde Gallagher Parkway', 'C042', 'Offline', '2018-09-28 01:37:11', 110.08531, 23.588928);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('Johnson', 'stredinnickb', 'Student', '12345', 'F', '911025-85-1252', '080-9546033', 'fchaudretb@cafepress.com', '338 Rockefeller Court', 'C060', 'Offline', '2018-09-27 18:30:35', 16.2304168, 50.4333399);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('gg', 'rskedgec', 'Student', '12345', 'F', '960805-37-2754', '094-1134653', 'beppersonc@google.it', '28 Saint Paul Alley', 'C059', 'Offline', '2018-09-28 19:00:02', -9.4027992, 38.9516688);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('Pooh', 'rofenerd', 'Student', '12345', 'F', '980810-73-0402', '061-4772981', 'gbidgoodd@imageshack.us', '7 Waxwing Road', 'C061', 'Offline', '2018-09-29 05:40:28', 71.5249154, 35.2121805);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('hhh', 'cdeweye', 'Student', '12345', 'M', '900720-11-6067', '094-5152182', 'ddrysdalee@boston.com', '25762 Dottie Place', 'C042', 'Offline', '2018-09-28 10:53:15', 122.8277624, 14.1313261);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('helloworld', 'vrenfieldf', 'Student', '12345', 'M', '961219-06-2941', '083-6135773', 'pfrillf@netlog.com', '510 Anhalt Park', 'C063', 'Offline', '2018-09-28 12:44:04', 8.352656, 6.8907086);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('wut', 'rjacquestg', 'Student', '12345', 'M', '951102-64-2924', '089-2757358', 'sbucktroutg@dion.ne.jp', '4 Briar Crest Center', 'C057', 'Offline', '2018-09-30 04:30:42', 19.9348453, 54.4658152);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('dgarland', 'dgarlanth', 'Student', '12345', 'F', '980718-62-3349', '096-7431348', 'kgouthierh@fastcompany.com', '9 West Center', 'C063', 'Offline', '2018-09-30 06:34:48', 115.5631, -8.2464);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('kkkk', 'cmattusovi', 'Student', '12345', 'F', '970817-13-6813', '073-2463217', 'rgarberti@bbc.co.uk', '79 Cardinal Court', 'C057', 'Offline', '2018-09-30 05:10:11', 19.9348453, 54.4658152);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('userF', 'ujeanelj', 'Student', '12345', 'F', '970925-80-1936', '094-6742497', 'jcurpheyj@yahoo.co.jp', '1 Canary Avenue', 'C063', 'Offline', '2018-09-30 20:54:09', 124.6192702, 0.8554519);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('userA', 'mdraiseyk', 'Student', '12345', 'M', '970816-85-8961', '085-8782816', 'ethurlingk@bing.com', '6 Dixon Court', 'C046', 'Offline', '2018-09-30 14:02:35', 115.106609, -8.605402);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('userG', 'abettinsonl', 'Student', '12345', 'F', '991218-79-7952', '085-8024597', 'pmcconvillel@vk.com', '85221 Cascade Trail', 'C053', 'Offline', '2018-09-27 01:26:36', -36.2196853, -10.0519585);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('userB', 'plockem', 'Student', '12345', 'F', '930820-59-2088', '099-5012098', 'ehedauxm@shutterfly.com', '88380 Algoma Hill', 'C065', 'Offline', '2018-09-30 18:00:49', -71.9674626, -13.53195);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('userC', 'dcopestaken', 'Student', '12345', 'F', '990729-44-2645', '072-6726000', 'obucklesn@arizona.edu', '44691 Corry Road', 'C065', 'Offline', '2018-09-29 12:53:08', -92.0269227, 14.7536759);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('userD', 'gdavenallo', 'Student', '12345', 'M', '951125-07-7513', '097-7095715', 'aorgelo@google.co.jp', '35694 Ilene Avenue', 'C049', 'Offline', '2018-09-27 03:49:51', 33.1070309, -25.0263578);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('userE', 'csalmondp', 'Student', '12345', 'M', '941126-95-3612', '096-6215026', 'wcoursp@springer.com', '06 Erie Circle', 'C065', 'Offline', '2018-09-29 18:45:43', 80.6523081, 7.8853608);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('userH', 'llimmingq', 'Student', '12345', 'F', '920804-55-2548', '058-5965809', 'rulsterq@wikia.com', '41 Karstens Terrace', 'C056', 'Offline', '2018-09-29 23:56:31', 107.5, -7.4);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user1111 ', 'ldopsonr', 'Student', '12345', 'F', '941226-51-5893', '087-3401403', 'abeamanr@mit.edu', '789 Holy Cross Alley', 'C065', 'Offline', '2018-09-27 16:44:51', 105.8361, -6.3825);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user2222 ', 'srubinowiczs', 'Student', '12345', 'F', '930720-87-5905', '089-7590767', 'bebbless@comsenz.com', '840 American Ash Place', 'C065', 'Offline', '2018-09-29 06:50:15', -70.7578553, 19.3673204);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user3333 ', 'czecchinellit', 'Student', '12345', 'F', '901105-43-8442', '097-2902393', 'avanderbrugget@alibaba.com', '643 Luster Lane', 'C024', 'Offline', '2018-09-28 14:53:12', 22.5942366, 49.4303242);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user4444 ', 'sdefrancisciu', 'Student', '12345', 'M', '990722-98-2982', '052-3347182', 'acashfordu@ucla.edu', '29826 Namekagon Circle', 'C065', 'Offline', '2018-09-29 01:27:29', 106.9594723, 21.7757592);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user5555 ', 'slomasnyv', 'Student', '12345', 'F', '970910-84-3195', '080-5113839', 'amccomev@reuters.com', '8 Hudson Park', 'C064', 'Offline', '2018-09-30 01:21:54', -59.1078529, -25.7333842);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user6666 ', 'gglencorsew', 'Student', '12345', 'M', '991229-43-8684', '064-4122470', 'zmedcraftw@multiply.com', '873 Monica Hill', 'C056', 'Offline', '2018-09-28 15:16:45', 13.4662133, 59.3269612);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user7777 ', 'mpadillax', 'Student', '12345', 'F', '990921-68-7441', '084-9623397', 'wstovenx@mayoclinic.com', '41414 Londonderry Hill', 'C065', 'Offline', '2018-09-28 13:54:09', 102.6354187, 24.9663939);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user1238 ', 'eoldisy', 'Student', '12345', 'F', '961208-67-2029', '062-6602461', 'vwillimenty@exblog.jp', '516 Village Park', 'C063', 'Offline', '2018-09-28 01:14:09', 115.858197, 28.682892);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user2119 ', 'xskyramz', 'Student', '12345', 'F', '900926-07-7519', '080-3972444', 'lhearnz@elegantthemes.com', '148 Sunnyside Court', 'C064', 'Offline', '2018-09-27 08:25:47', -96.0996978, 41.2899133);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user10', 'cbollon10', 'Student', '12345', 'M', '900812-58-2556', '093-1954271', 'noffell10@oaic.gov.au', '90917 Summit Junction', 'C064', 'Offline', '2018-09-29 14:11:42', 70.79064, 36.5222);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user11', 'cdurbyn11', 'Student', '12345', 'M', '911001-27-7874', '089-1124030', 'jnolder11@mapy.cz', '5537 Schiller Way', 'C065', 'Offline', '2018-09-30 05:57:45', 21.828508, 52.1379258);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user12', 'galeshkov12', 'Student', '12345', 'M', '951216-19-1748', '089-4338463', 'dsilk12@mayoclinic.com', '61117 Goodland Court', 'C038', 'Offline', '2018-09-30 16:47:35', 6.3760602, 45.6629694);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user13', 'hpepler13', 'Student', '12345', 'F', '910923-78-4241', '062-4449701', 'kbraniff13@si.edu', '1 Kedzie Pass', 'C025', 'Offline', '2018-09-28 23:29:18', 14.48278, 35.8925);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user14', 'jlethibridge14', 'Student', '12345', 'M', '990907-49-3643', '096-1027384', 'kszachniewicz14@wordpress.com', '343 Mallory Avenue', 'C054', 'Offline', '2018-09-30 14:11:56', 16.2590848, 50.025483);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user15', 'sguitton15', 'Student', '12345', 'M', '930817-31-3417', '042-7411722', 'gheaslip15@fda.gov', '75839 Jay Point', 'C058', 'Offline', '2018-09-28 13:13:45', 11.8784699, 57.6670364);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user16', 'dchessman16', 'Student', '12345', 'F', '950827-70-1497', '099-1385403', 'bfitzsimons16@forbes.com', '206 Brickson Park Alley', 'C064', 'Offline', '2018-09-29 13:21:03', 23.1996378, 51.6878774);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user17', 'dmcgeechan17', 'Student', '12345', 'F', '921224-07-4874', '049-4696997', 'dwillimott17@hao123.com', '402 3rd Drive', 'C043', 'Offline', '2018-09-27 14:36:14', -37.7237678, -8.2197304);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user18', 'ajonk18', 'Student', '12345', 'F', '941229-04-1210', '094-8673042', 'jtrayhorn18@biblegateway.com', '99947 Brown Park', 'C065', 'Offline', '2018-09-27 18:34:11', -37.7237678, -8.2197304);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user19', 'mnisuis19', 'Student', '12345', 'F', '950900-35-9161', '096-8439844', 'jpantridge19@auda.org.au', '64863 Pierstorff Junction', 'C045', 'Offline', '2018-09-28 21:20:21', -61.7625424, -36.5951114);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user20', 'sespinal1a', 'Student', '12345', 'M', '900926-05-9904', '087-7170143', 'lsentinella1a@godaddy.com', '8 Del Mar Circle', 'C046', 'Offline', '2018-09-27 17:17:11', 27.7754515, -15.8228377);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user21', 'dbeston1b', 'Student', '12345', 'F', '911227-21-7169', '092-3447781', 'gsinyard1b@google.com.hk', '5 Bluejay Junction', 'C055', 'Offline', '2018-09-30 21:25:45', 116.6988851, 24.3482735);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user22', 'mmuslim1c', 'Student', '12345', 'F', '951104-04-5527', '094-3189971', 'atolomio1c@ucoz.com', '1574 Kensington Avenue', 'C061', 'Offline', '2018-09-30 01:40:42', 111.5140386, -6.791629);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user23', 'bstorch1d', 'Student', '12345', 'M', '940722-76-7269', '090-0562036', 'scapel1d@hhs.gov', '614 Southridge Junction', 'C044', 'Offline', '2018-09-30 22:24:58', 14.3737786, 45.9701086);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user24', 'cfrostick1e', 'Student', '12345', 'F', '981020-55-8518', '074-1814514', 'lpetriello1e@paypal.com', '44497 Elmside Place', 'C052', 'Offline', '2018-09-29 03:26:34', 18.3838669, 52.322296);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user25', 'pralestone1f', 'Student', '12345', 'M', '930623-98-0082', '097-5050739', 'bstarten1f@umn.edu', '7671 Mesta Alley', 'C065', 'Offline', '2018-09-29 12:59:36', 18.9221, 69.63186);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user26', 'elowle1g', 'Student', '12345', 'F', '921009-57-4049', '094-2863692', 'ptenby1g@ted.com', '058 Haas Parkway', 'C064', 'Offline', '2018-09-28 01:18:18', 36.9565089, -7.703889);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user27', 'kcamel1h', 'Student', '12345', 'M', '961002-06-0150', '081-9303250', 'wgoldberg1h@amazon.co.uk', '063 Sage Trail', 'C009', 'Offline', '2018-09-30 08:44:25', -39.2791011, -11.5576676);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user28', 'emerigeau1i', 'Student', '12345', 'F', '981203-77-2381', '090-5251268', 'tullrich1i@berkeley.edu', '09 Cherokee Place', 'C065', 'Offline', '2018-09-27 14:47:25', -39.2791011, -11.5576676);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user29', 'ppumfrey1j', 'Student', '12345', 'M', '971123-60-3215', '077-2389478', 'mducker1j@biblegateway.com', '450 Welch Parkway', 'C048', 'Offline', '2018-09-28 09:11:56', 123.5983638, 13.1487371);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user30', 'dsickert1k', 'Student', '12345', 'M', '951020-08-5313', '091-2417203', 'cduval1k@studiopress.com', '32960 Gateway Alley', 'C039', 'Offline', '2018-09-28 07:06:26', 123.5983638, 13.1487371);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user31', 'ssquire1l', 'Student', '12345', 'F', '931120-34-7154', '097-2363375', 'lnizard1l@dmoz.org', '86 Dakota Alley', 'C001', 'Offline', '2018-09-27 17:18:28', 9.65523, 59.1395);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user32', 'ewyne1m', 'Student', '12345', 'M', '940822-88-3610', '083-6601417', 'ykment1m@techcrunch.com', '124 Del Mar Way', 'C058', 'Offline', '2018-09-27 05:19:34', 9.65523, 59.1395);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user33', 'gharron1n', 'Student', '12345', 'M', '971012-94-8645', '083-2152465', 'sgostall1n@altervista.org', '4151 Mifflin Street', 'C065', 'Offline', '2018-09-27 08:21:55', -8.3143192, 31.2393596);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user34', 'spydcock1o', 'Student', '12345', 'M', '970828-60-3420', '072-6708082', 'gpoker1o@gmpg.org', '624 Eastwood Lane', 'C053', 'Offline', '2018-09-27 13:16:38', -88.0182472, 14.9836277);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user35', 'mhallede1p', 'Student', '12345', 'M', '910820-20-5708', '080-9229086', 'zgawkroge1p@springer.com', '9 Sunbrook Pass', 'C042', 'Offline', '2018-09-28 08:02:20', -88.0182472, 14.9836277);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user36', 'achurches1q', 'Student', '12345', 'F', '940823-89-2809', '092-6559023', 'mnorthfield1q@acquirethisname.com', '81 Northland Junction', 'C063', 'Offline', '2018-09-27 09:46:23', -87.0696273, 20.6523028);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user37', 'crevely1r', 'Student', '12345', 'M', '921214-80-9314', '066-6339915', 'cthorley1r@tiny.cc', '2443 Tennyson Parkway', 'C061', 'Offline', '2018-09-28 20:30:19', 124.1253751, -8.4422524);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user38', 'jarp1s', 'Student', '12345', 'F', '951216-01-8365', '094-2624234', 'ecowland1s@apple.com', '2832 Gulseth Park', 'C051', 'Offline', '2018-09-28 20:46:42', -69.8489425, 18.5060252);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user39', 'bcondie1t', 'Student', '12345', 'M', '981126-62-1579', '094-1433229', 'bfancet1t@usa.gov', '96 Bayside Junction', 'C061', 'Offline', '2018-09-28 09:38:54', -98.9735333, 22.7315115);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user40', 'ddelafont1u', 'Student', '12345', 'F', '901110-77-6992', '098-1935385', 'hportingale1u@mozilla.com', '48438 Burrows Road', 'C050', 'Offline', '2018-09-29 22:48:29', -98.9735333, 22.7315115);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user41', 'gdimock1v', 'Student', '12345', 'F', '920802-87-2337', '095-3021355', 'evertey1v@booking.com', '8613 Debs Junction', 'C064', 'Offline', '2018-09-27 06:19:09', 108.0519444, -7.4286111);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user42', 'ggauge1w', 'Student', '12345', 'F', '940805-77-2823', '094-8384734', 'mtrundler1w@a8.net', '4 Vermont Point', 'C048', 'Offline', '2018-09-28 19:35:34', 108.0519444, -7.4286111);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user43', 'dhentzeler1x', 'Student', '12345', 'M', '991018-38-6514', '060-3846994', 'amearing1x@hp.com', '07940 Loeprich Point', 'C064', 'Offline', '2018-09-27 10:57:13', 108.0519444, -7.4286111);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user44', 'bohartagan1y', 'Student', '12345', 'F', '900921-35-9049', '091-8731669', 'fgreig1y@miibeian.gov.cn', '3063 Longview Lane', 'C050', 'Offline', '2018-09-30 10:00:32', 108.0519444, -7.4286111);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user45', 'oodonohoe1z', 'Student', '12345', 'F', '981217-80-3910', '093-6424957', 'bpercival1z@webs.com', '887 Fairfield Point', 'C051', 'Offline', '2018-09-29 02:51:02', -73.203211, 7.470498);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user46', 'cmatzen20', 'Student', '12345', 'F', '990927-60-1187', '069-2364210', 'idoutch20@constantcontact.com', '4474 Roxbury Lane', 'C061', 'Offline', '2018-09-30 03:10:57', 18.946898, 53.3800563);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user47', 'njohnson21', 'Student', '12345', 'F', '950904-68-1550', '099-8959648', 'drookeby21@opensource.org', '2279 Forster Lane', 'C063', 'Offline', '2018-09-27 01:07:30', 4.287114, 45.3875142);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user48', 'ssickamore22', 'Student', '12345', 'M', '951025-52-7560', '092-5906807', 'istuddeard22@flickr.com', '6 Bashford Center', 'C055', 'Offline', '2018-09-30 15:13:46', 21.6501051, 65.8255044);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user49', 'gmillea23', 'Student', '12345', 'F', '931222-99-2523', '077-4925751', 'esoutherill23@state.tx.us', '8 Ruskin Lane', 'C055', 'Offline', '2018-09-27 10:19:59', 125.136451, 42.901533);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user50', 'tmellings24', 'Student', '12345', 'F', '940906-44-9404', '092-9721077', 'dmaytom24@printfriendly.com', '68 Village Parkway', 'C048', 'Offline', '2018-09-27 13:19:05', 125.136451, 42.901533);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user51', 'abathersby25', 'Student', '12345', 'F', '950616-94-0013', '096-4237207', 'agait25@salon.com', '672 Novick Park', 'C062', 'Offline', '2018-09-28 05:52:47', 125.136451, 42.901533);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user52', 'rplayfoot26', 'Student', '12345', 'M', '970819-20-3668', '080-3321374', 'smoxham26@chronoengine.com', '8 Spenser Place', 'C065', 'Offline', '2018-09-27 17:29:58', -94.9143239, 39.6924206);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user53', 'cokenden27', 'Student', '12345', 'F', '971129-13-0590', '070-3986311', 'phowley27@kickstarter.com', '9924 Mifflin Point', 'C040', 'Offline', '2018-09-30 09:10:26', -94.9143239, 39.6924206);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user54', 'ttimms28', 'Student', '12345', 'F', '931107-36-8441', '080-4810394', 'nfrankland28@nba.com', '21 Westridge Court', 'C052', 'Offline', '2018-09-28 02:25:44', -94.9143239, 39.6924206);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user55', 'pbohey29', 'Student', '12345', 'F', '991201-23-2255', '092-6639030', 'rnolte29@google.cn', '4610 Atwood Street', 'C065', 'Offline', '2018-09-30 03:05:02', -94.9143239, 39.6924206);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user56', 'gmunt2a', 'Student', '12345', 'F', '910926-52-8626', '093-9153134', 'kfone2a@friendfeed.com', '6 Green Ridge Avenue', 'C008', 'Offline', '2018-09-30 04:06:17', -94.9143239, 39.6924206);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user57', 'sgabbott2b', 'Student', '12345', 'M', '960924-40-7003', '089-5830038', 'kfudger2b@vkontakte.ru', '4295 Schiller Drive', 'C058', 'Offline', '2018-09-30 04:47:31', -94.9143239, 39.6924206);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user58', 'ugrishukov2c', 'Student', '12345', 'M', '910822-25-0636', '098-7010469', 'aalesin2c@phpbb.com', '10 Hintze Lane', 'C062', 'Offline', '2018-09-29 02:55:05', -94.9143239, 39.6924206);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user59', 'sbursnoll2d', 'Student', '12345', 'M', '930821-98-8132', '083-9577824', 'dcorteney2d@163.com', '41 Kensington Terrace', 'C033', 'Offline', '2018-09-28 00:54:12', -94.9143239, 39.6924206);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user60', 'dpitone2e', 'Student', '12345', 'F', '911026-77-5431', '071-9895810', 'kcouth2e@redcross.org', '0936 Anthes Alley', 'C057', 'Offline', '2018-09-28 08:23:16', -79.5125662, 9.0144214);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user61', 'ctwidale2f', 'Student', '12345', 'M', '980915-64-7562', '055-8460660', 'lrossiter2f@imdb.com', '32347 Lukken Avenue', 'C052', 'Offline', '2018-09-28 18:54:17', -79.5125662, 9.0144214);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user62', 'pjenken2g', 'Student', '12345', 'M', '910915-80-3180', '093-3602705', 'kkeenlayside2g@berkeley.edu', '22914 Waubesa Point', 'C064', 'Offline', '2018-09-30 10:56:08', -79.5125662, 9.0144214);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user63', 'aseiler2h', 'Student', '12345', 'M', '980728-59-6502', '084-0669718', 'hivanilov2h@e-recht24.de', '14 Hoffman Road', 'C064', 'Offline', '2018-09-28 04:00:48', -79.5125662, 9.0144214);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user64', 'thembery2i', 'Student', '12345', 'F', '960801-16-5962', '091-9327253', 'fsyres2i@comsenz.com', '7060 Truax Junction', 'C028', 'Offline', '2018-09-28 04:40:14', -79.5125662, 9.0144214);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user65', 'hwinfindale2j', 'Student', '12345', 'F', '991205-19-8888', '052-8917081', 'rwyllcocks2j@imageshack.us', '30162 Pearson Street', 'C053', 'Offline', '2018-09-29 07:18:54', 2.1476836, 48.8805451);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user66', 'wlittrick2k', 'Student', '12345', 'M', '911207-17-3969', '097-0067931', 'tstowe2k@eepurl.com', '1218 Brown Road', 'C055', 'Offline', '2018-09-29 18:05:05', 2.1476836, 48.8805451);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user67', 'lnewick2l', 'Student', '12345', 'F', '920924-74-2106', '061-4337212', 'ncroome2l@dion.ne.jp', '2678 Dunning Court', 'C065', 'Offline', '2018-09-28 19:44:55', 2.1476836, 48.8805451);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user68', 'cstuckford2m', 'Student', '12345', 'M', '971229-19-6284', '082-1502815', 'dspaducci2m@squarespace.com', '02 Mallard Circle', 'C042', 'Offline', '2018-09-30 15:19:42', 2.1476836, 48.8805451);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user69', 'apulsford2n', 'Student', '12345', 'M', '960822-32-7942', '096-4697166', 'cpopland2n@skyrock.com', '6301 Ridge Oak Circle', 'C058', 'Offline', '2018-09-27 05:05:49', 2.1476836, 48.8805451);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user70', 'speabody2o', 'Student', '12345', 'M', '990905-75-9137', '099-9795722', 'mpodd2o@nytimes.com', '43943 Twin Pines Terrace', 'C064', 'Offline', '2018-09-28 19:58:31', 2.1476836, 48.8805451);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user71', 'vmurr2p', 'Student', '12345', 'F', '971227-92-4219', '093-9177317', 'amcgaugey2p@businessweek.com', '43024 Kipling Street', 'C037', 'Offline', '2018-09-29 09:16:49', 2.1476836, 48.8805451);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user72', 'cwardhough2q', 'Student', '12345', 'F', '981209-72-9685', '085-6203376', 'lpycock2q@home.pl', '734 Tony Hill', 'C059', 'Offline', '2018-09-30 13:08:19', 2.1476836, 48.8805451);
insert into User (display_name, username, position, password, gender, nric, phone_number, email, address, city_id, status, last_online, last_longitude, last_latitude) values ('user73', 'fcansdill2r', 'Student', '12345', 'F', '901122-88-1116', '094-5400984', 'shembrow2r@ihg.com', '89 Loomis Drive', 'C063', 'Offline', '2018-09-28 14:59:01', 2.1476836, 48.8805451);



--  Done by: Lim Fang Chun
--  student table records
--  Column: student_id(PK), faculty, course, tutorial_group, intake, academic_year, user_id
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701001', 'FOCS', 'REI', '4', 'May', 2, 1);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701002', 'FOCS', 'RSF', '4', 'May', 3, 2);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701003', 'FOCS', 'RSF', '5', 'May', 3, 3);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701004', 'FOCS', 'RSF', '1', 'May', 3, 4);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701005', 'FOCS', 'RSF', '3', 'May', 3, 5);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701006', 'FOCS', 'RSF', '5', 'May', 3, 6);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701007', 'FOCS', 'REI', '5', 'May', 3, 7);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701008', 'FOCS', 'RSF', '5', 'May', 1, 8);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701009', 'FOCS', 'RSF', '4', 'May', 1, 9);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701010', 'FOCS', 'RSF', '4', 'May', 3, 10);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701011', 'FOCS', 'RSF', '4', 'May', 3, 11);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701012', 'FOCS', 'RSF', '5', 'May', 2, 12);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701013', 'FOCS', 'RSF', '5', 'May', 3, 13);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701014', 'FOCS', 'REI', '5', 'May', 2, 14);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701015', 'FOCS', 'RSD', '5', 'May', 2, 15);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701016', 'FOCS', 'RSD', '5', 'May', 2, 16);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701017', 'FOCS', 'RSF', '5', 'May', 3, 17);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701018', 'FOCS', 'REI', '5', 'May', 3, 18);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701019', 'FOCS', 'RSF', '4', 'May', 3, 19);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701020', 'FOCS', 'RSF', '3', 'May', 3, 20);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701021', 'FOCS', 'REI', '5', 'May', 3, 21);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701022', 'FOCS', 'RSF', '5', 'May', 2, 22);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701023', 'FOCS', 'REI', '5', 'May', 3, 23);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701024', 'FOCS', 'REI', '5', 'May', 1, 24);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701025', 'FOCS', 'RSF', '2', 'May', 1, 25);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701026', 'FOCS', 'RSF', '4', 'May', 1, 26);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701027', 'FOCS', 'REI', '5', 'May', 1, 27);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701028', 'FOCS', 'REI', '5', 'May', 1, 28);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701029', 'FOCS', 'RSD', '5', 'May', 3, 29);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701030', 'FOCS', 'RSD', '4', 'May', 1, 30);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701031', 'FOCS', 'REI', '3', 'May', 1, 31);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701032', 'FOCS', 'RSF', '5', 'May', 3, 32);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701033', 'FOCS', 'RSF', '5', 'May', 2, 33);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701034', 'FOCS', 'RSD', '2', 'May', 2, 34);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701035', 'FOCS', 'REI', '4', 'May', 3, 35);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701036', 'FOCS', 'RSF', '2', 'May', 2, 36);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701037', 'FOCS', 'RSF', '1', 'May', 3, 37);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701038', 'FOCS', 'RSF', '1', 'May', 2, 38);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701039', 'FOCS', 'REI', '3', 'May', 2, 39);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701040', 'FOCS', 'RSF', '5', 'May', 2, 40);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701041', 'FOCS', 'REI', '5', 'May', 3, 41);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701042', 'FOCS', 'RSF', '3', 'May', 1, 42);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701043', 'FOCS', 'RSF', '2', 'May', 3, 43);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701044', 'FOCS', 'RSF', '5', 'May', 1, 44);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701045', 'FOCS', 'RSD', '4', 'May', 2, 45);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701046', 'FOCS', 'RSD', '3', 'May', 3, 46);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701047', 'FOCS', 'RSD', '5', 'May', 3, 47);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701048', 'FOCS', 'REI', '3', 'May', 3, 48);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701049', 'FOCS', 'RSF', '5', 'May', 3, 49);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701050', 'FOCS', 'RSF', '3', 'May', 1, 50);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701051', 'FOCS', 'REI', '3', 'May', 1, 51);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701052', 'FOCS', 'RSD', '3', 'May', 3, 52);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701053', 'FOCS', 'RSD', '5', 'May', 3, 53);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701054', 'FOCS', 'RSF', '5', 'May', 3, 54);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701055', 'FOCS', 'RSF', '5', 'May', 2, 55);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701056', 'FOCS', 'RSD', '5', 'May', 2, 56);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701057', 'FOCS', 'REI', '2', 'May', 2, 57);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701058', 'FOCS', 'RSF', '3', 'May', 3, 58);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701059', 'FOCS', 'REI', '3', 'May', 2, 59);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701060', 'FOCS', 'RSD', '4', 'May', 3, 60);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701061', 'FOCS', 'RSF', '5', 'May', 2, 61);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701062', 'FOCS', 'RSF', '5', 'May', 1, 62);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701063', 'FOCS', 'RSF', '1', 'May', 3, 63);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701064', 'FOCS', 'RSF', '5', 'May', 3, 64);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701065', 'FOCS', 'RSF', '4', 'May', 3, 65);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701066', 'FOCS', 'REI', '5', 'May', 2, 66);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701067', 'FOCS', 'RSF', '4', 'May', 2, 67);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701068', 'FOCS', 'RSF', '5', 'May', 2, 68);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701069', 'FOCS', 'RSD', '5', 'May', 3, 69);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701070', 'FOCS', 'REI', '2', 'May', 3, 70);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701071', 'FOCS', 'RSD', '5', 'May', 1, 71);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701072', 'FOCS', 'RSF', '5', 'May', 1, 72);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701073', 'FOCS', 'REI', '5', 'May', 3, 73);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701074', 'FOCS', 'RSF', '5', 'May', 3, 74);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701075', 'FOCS', 'RSF', '4', 'May', 1, 75);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701076', 'FOCS', 'RSF', '5', 'May', 3, 76);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701077', 'FOCS', 'RSF', '5', 'May', 3, 77);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701078', 'FOCS', 'REI', '5', 'May', 3, 78);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701079', 'FOCS', 'RSF', '4', 'May', 3, 79);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701080', 'FOCS', 'RSD', '4', 'May', 3, 80);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701081', 'FOCS', 'RSF', '5', 'May', 1, 81);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701082', 'FOCS', 'RSD', '5', 'May', 1, 82);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701083', 'FOCS', 'RSD', '1', 'May', 1, 83);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701084', 'FOCS', 'RSF', '5', 'May', 3, 84);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701085', 'FOCS', 'RSF', '5', 'May', 1, 85);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701086', 'FOCS', 'RSF', '3', 'May', 3, 86);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701087', 'FOCS', 'RSD', '4', 'May', 1, 87);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701088', 'FOCS', 'RSD', '3', 'May', 3, 88);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701089', 'FOCS', 'REI', '5', 'May', 3, 89);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701090', 'FOCS', 'RSF', '4', 'May', 1, 90);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701091', 'FOCS', 'RSD', '5', 'May', 3, 91);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701092', 'FOCS', 'RSF', '3', 'May', 3, 92);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701093', 'FOCS', 'RSF', '4', 'May', 3, 93);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701094', 'FOCS', 'RSF', '5', 'May', 1, 94);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701095', 'FOCS', 'RSD', '5', 'May', 2, 95);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701096', 'FOCS', 'RSF', '5', 'May', 2, 96);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701097', 'FOCS', 'RSD', '5', 'May', 3, 97);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701098', 'FOCS', 'RSF', '4', 'May', 1, 98);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701099', 'FOCS', 'RSF', '5', 'May', 1, 99);
insert into Student (student_id, faculty, course, tutorial_group, intake, academic_year, user_id) values ('1701100', 'FOCS', 'RSD', '4', 'May', 3, 100);

--  Done by: Lim Fang Chun
--  friendship table records
-- Column: user_id(PK), friend_id, status, date_created
insert into Friendship (user_id, friend_id, status, date_created, sender_id) values (1, 2, 'Friend', '2018-10-01 14:59:01', 1);
insert into Friendship (user_id, friend_id, status, date_created, sender_id) values (1, 3, 'Friend', '2018-10-01 11:12:01', 1);
insert into Friendship (user_id, friend_id, status, date_created, sender_id) values (1, 4, 'Friend', '2018-10-01 14:59:01', 1);
insert into Friendship (user_id, friend_id, status, sender_id) values (1, 14, 'Friend', 1);
insert into Friendship (user_id, friend_id, status, sender_id) values (1, 66, 'Friend', 1);
insert into Friendship (user_id, friend_id, status, sender_id) values (1, 33, 'Friend', 1);
insert into Friendship (user_id, friend_id, status, sender_id) values (1, 54, 'Friend', 1);
insert into Friendship (friend_id, user_id, status, sender_id) values (1, 14, 'Friend', 1);
insert into Friendship (friend_id, user_id, status, sender_id) values (1, 66, 'Friend', 1);
insert into Friendship (friend_id, user_id, status, sender_id) values (1, 33, 'Friend', 1);
insert into Friendship (friend_id, user_id, status, sender_id) values (1, 54, 'Friend', 1);
insert into Friendship (user_id, friend_id, status, date_created, sender_id) values (2, 1, 'Friend', '2018-10-01 14:59:01', 1);
insert into Friendship (user_id, friend_id, status, date_created, sender_id) values (3, 1, 'Friend', '2018-10-01 11:12:01', 1);
insert into Friendship (user_id, friend_id, status, date_created, sender_id) values (4, 1, 'Friend', '2018-10-01 14:59:01', 1);
insert into Friendship (user_id, friend_id, status, sender_id) values (1, 5, 'Pending', 1);
insert into Friendship (user_id, friend_id, sender_id) values (1, 8, 1);
insert into Friendship (user_id, friend_id, status, sender_id) values (5, 1, 'Pending', 1);
insert into Friendship (user_id, friend_id, sender_id) values (8, 1, 1);
insert into Friendship (user_id, friend_id, status, sender_id) values (1, 9, 'Pending', 9);
insert into Friendship (user_id, friend_id, sender_id) values (1, 11, 11);
insert into Friendship (user_id, friend_id, status, sender_id) values (9, 1,'Pending', 9);
insert into Friendship (user_id, friend_id, sender_id) values (11, 1, 11);

-- Done by: Lim Fang Chun
-- Chat_Room table records
-- Column: room_id(PK), owner_id, room_name, date_created, last_update, topic_address
insert into Chat_Room (owner_id, room_name, date_created, last_update, topic_address) values (1, 'TestRoom1', '2018-10-01 14:59:01', '2018-10-01 13:59:01', 'room/room_1');
insert into Chat_Room (owner_id, room_name, date_created, last_update, topic_address) values (1, 'TestRoom2', '2018-09-29 14:59:01', '2018-10-01 11:59:01', 'room/room_2');

-- Done by: Lim Fang Chun
-- Participant table records
-- Column: room_id(PK), role
insert into Participant (room_id, user_id, role) values (1, 1, 'Admin');
insert into Participant (room_id, user_id, role) values (1, 2, 'Member');
insert into Participant (room_id, user_id, role) values (2, 1, 'Admin');
insert into Participant (room_id, user_id, role) values (2, 2, 'Admin');
insert into Participant (room_id, user_id, role) values (2, 3, 'Admin');
insert into Participant (room_id, user_id, role) values (2, 4, 'Member');
insert into Participant (room_id, user_id, role) values (2, 5, 'Member');

-- Setup necessary triggers
-- drop the triggers first, like we drop table before creating
DROP TRIGGER IF EXISTS Trg_Insert_New_Supply;
DROP TRIGGER IF EXISTS Trg_Log_User_Activity;
DROP TRIGGER IF EXISTS Trg_Insert_New_Message;

-- create a temporary student record for new user
-- otherwise would cause error
DELIMITER //
CREATE TRIGGER Trg_Insert_New_Supply
AFTER INSERT ON User
FOR EACH ROW
BEGIN
	INSERT INTO Student (student_id, user_id) values (NEW.user_id, NEW.user_id);
END;
//

-- this trigger is to track user activity
-- you may add more conditions in future
CREATE TRIGGER Trg_Log_User_Activity
AFTER UPDATE ON User
FOR EACH ROW
BEGIN
	IF NEW.last_online <> OLD.last_online OR NEW.status <> OLD.status THEN
		IF NEW.status LIKE 'Offline' THEN
			INSERT INTO UserActivityLog (user_id, description) VALUES (NEW.user_id, 'Logged out');
		ELSEIF NEW.status LIKE 'Online' THEN
			INSERT INTO UserActivityLog (user_id, description) VALUES (NEW.user_id, 'Logged in');
		END IF;
	END IF;
	
	IF NEW.display_name <> OLD.display_name THEN
		INSERT INTO UserActivityLog (user_id, description) VALUES (NEW.user_id, CONCAT('Changed display name to ', NEW.display_name));
	END IF;
	
	IF NEW.last_latitude <> OLD.last_latitude OR NEW.last_longitude <> OLD.last_longitude THEN
		INSERT INTO UserActivityLog (user_id, description) VALUES (NEW.user_id, CONCAT('Location updated.. Ltd: ', NEW.last_latitude, ', Lgn: ', NEW.last_longitude));
	END IF;
END;
//

-- update chat_room last_update everytime a new message is inserted
CREATE TRIGGER Trg_Insert_New_Message
AFTER INSERT ON Message
FOR EACH ROW
BEGIN
	UPDATE Chat_Room SET last_update = CURRENT_TIMESTAMP WHERE room_id = NEW.room_id;
END;
//

DELIMITER ;
COMMIT;
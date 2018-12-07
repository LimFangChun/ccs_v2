<?php
require("phpMQTT.php");

//include the other required PHP files
//Note: make sure put in htdocs
//otherwise it will not work properly
include dirname(__DIR__)."\htdocs\FindFriendModule.php";
chmod (dirname(__DIR__)."\htdocs\FindFriendModule.php", 0740);
include dirname(__DIR__)."\htdocs\FriendManagementModule.php";
chmod (dirname(__DIR__)."\htdocs\FriendManagementModule.php", 0740);
include dirname(__DIR__)."\htdocs\ChatModule.php";
chmod (dirname(__DIR__)."\htdocs\ChatModule.php", 0740);

//MQTT Server setup 
//**Change in MQTT broker address will require you to update the address in publishMessage function too.
/*
$server = "m14.cloudmqtt.com";     		// change if necessary
$port = 16672;                     		// change if necessary
$username = "vwkohpay";                 // set your username
$password = "JPG3F4XUHjRv";             // set your password
$client_id = "SERVER_1"; 				// make sure this is unique for connecting to sever - you could use uniqid()
*/

/*
Created by: Lim Fang Chun
READ ME, juniors, and calm down
This is your server script, this is where you'll deal with your database
and you'll be using this php file to setup your local server
you'll write you SQL here to deal with database
if you never learn Php before, don't panic, its more easier compare to Java, C++
Go to w3school and learn yourself
follow the steps below

Before that, I recommended you to download:
	1. Notepad++ to edit your php, sql files
		or Visual Studio Code or Atom(For MAC)
	2. XAMPP
	3. A free MQTT broker
	4. Android studio

To setup your MQTT broker:
	1. download any free broker, recommended HiveMQ
	2. Run the broker, they should come with a user manual or readme.txt, read those
	3. open a web browser, type 127.0.0.1:8080 or refer to the terminal screen
	4. now your mqtt broker is up
	5. you can observe the client conection via the browser
	6. you may need to wait for a while, the computer takes some time to setup the broker

To setup your server and database locally, you need to do this:
	1. use hotkey Windows Key + R, type cmd
	2. in cmd, type ipconfig to check your IPv4 address
	3. type your ip address to $server at below and in publishMessage()
	4. Open XAMPP, start Apache and MySQL, make sure both are green color, disable the rest
	5. click admin button next to MySQL
	6. you should now see a localhost opened in your web browser
	7. create a new database name it "ccs_master"
		create a new user with username: "ccs_main" and password: "123456"
		select import, import the ccs.sql file, and click go
	8. now your database is setup
	9. now, this one very important, open your XAMPP folder, go to htdocs
	10. htdocs is the default directory of your local server
	11. put all the php files you got in htdocs
	12. now open cmd again and type START C:\xampp\php\php.exe C:\xampp\htdocs\mqtt_server.php
	13. now you should see a php terminal open
	14. make sure the php terminal says something like "Connected to MQTT Broker @ x.x.x.x:xxxx"
		*if it does not or the php terminal shutdown, then either your broker hasn't setup or 
		you are having errors in this file, go to below session and see how to debug
	15. and tada your server is up
	16. everytime client sent something to the server, 
	17. the php terminal should show some text regarding message received from client
	
Now make your android project connect to this local server
	1. Open android studio
	2. Go to MQTTHelper and change the ip address like above
	3. now it should connect

Now, to find the syntax error in this entire server script
	1. activate XAMPP
	2. open a web browser
	3. type localhost/mqtt_server.php
	4. it should tell you where is the error
	5. if you get message like maximum 30 seconds, then you are most likely no error
	6. that 30 seconds message is just a restriction on Chrome
	7. now go back to cmd and start the server
	
Again, make sure to change the server ip address to your ip everytime before u start
Good luck!
find me at https://www.facebook.com/leo477831
if you need any more help
*/

/* $server = "172.16.122.93";     		// change to your broker's ip
$port = 1883;                     		// change if necessary, default is 1883
$username = "root";                 // set your username
$password = "";             // set your password
$client_id = "CCS_SERVER"; 				// make sure this is unique for connecting to sever - you could use uniqid()
  */
 
/* $server = "m14.cloudmqtt.com";     		// change if necessary
$port = 16672;                     		// change if necessary
$username = "vwkohpay";                 // set your username
$password = "JPG3F4XUHjRv";             // set your password
$client_id = "SERVER_1"; 
 */

/* $server = "mqtt.dioty.co";     		// change to your broker's ip
$port = 1883;                     		// change if necessary, default is 1883
$username = "leo477831@gmail.com";                 // set your username
$password = "ba6acd07";             // set your password
$client_id = "CCS_SERVER";
 */
 
//$server = "broker.hivemq.com";     		// change to your broker's ip
$server = "172.16.120.174";
$port = 1883;                     		// change if necessary, default is 1883
$username = "";                 // set your username
$password = "";             // set your password
$client_id = "CCS_SERVER";

$QOS = 1;

//Note: Very important, this topic prefix must be same as the prefix in your android project
//and the # indicates that this server will subscribe to any new topic in this directory
$subscribeTopic = "/MY/TARUC/CCS/000000001/PUB/#";

//-<<<<<<<<<<<<<<<<<<<<<<<--Do not modify---
$mqtt = new phpMQTT($server, $port, $client_id);
if(!$mqtt->connect(true, NULL, $username, $password)) {
	echo "Couldn't connect to MQTT server.";
	exit(1);
}
echo "Connected to MQTT Broker @ ".$server.":".$port."\n";
$topics[$subscribeTopic] = array("qos" => '$QOS', "function" => "procmsg");
$mqtt->subscribe($topics, $QOS);

while($mqtt->proc()){
}
$mqtt->close();
//->>>>>>>>>>>>>>>>>>>>>>--Do not modify--- END

//MQTT publish message
//DO NOT MODIFY, except ip address
function publishMessage($topic, $ack_message){
	$server = "172.16.120.174";     		// change if necessary
	$port = 1883;                     		// change if necessary
	$username = "";                 // set your username
	$password = "";             // set your password
	$client_id = "CCS_SERVER"; 
	/* $server = "172.16.122.93";     		// change if necessary
	$port = 1883;                     		// change if necessary
	$username = "";                 // set your username
	$password = "";             // set your password
	$client_id = "CCS_SERVER";  */	// make sure this is unique for connecting to sever - you could use uniqid()

	$QOS = 1;
	$mqtt = new phpMQTT($server, $port, $client_id);
	if(!$mqtt->connect(true, NULL, $username, $password)) {
		exit(1);
	}
	$mqtt->publish($topic, $ack_message , $QOS);

	echo "\nReturning to Topic :".$topic;
			
	if(strlen($ack_message) > 300){
		$ack_message = substr($ack_message, 0, 300);
		$ack_message .= "...";
	}
	echo "\nAckMessage: \"".$ack_message."\"" ." \n";
}

//Server Responses
//add your new function to here
function procmsg($topic, $msg){		
		$ack_message = "";
		echo "=====================================================";
		echo "\nReceiving message:";
		echo "\nTopic: ".$topic;
		echo "\nReceived Message: ".$msg."\n";
		
		if(!empty($msg)){
			$commandmsg = explode(",", $msg);				
			switch($commandmsg[0]){
				case "LOGIN":	{
					$ack_message = LOGIN($msg); 
					publishMessage($topic, $ack_message);
					break;}
				case "REGISTER_USER":	{
					$ack_message = REGISTER_USER($msg); 
					publishMessage($topic, $ack_message);
					break;}
				case "UPDATE_USER_STATUS": {
					$ack_message = UPDATE_USER_STATUS($msg); 
					break;}
				case "GET_CHAT_ROOM": {
					$ack_message = GET_CHAT_ROOM($msg); 
					publishMessage($topic, $ack_message);
					break;}
				case "GET_ROOM_MESSAGE": {
					$ack_message = GET_ROOM_MESSAGE($msg); 
					publishMessage($topic, $ack_message);
					break;}
				case "DELETE_CHAT_ROOM":{
					$ack_message = DELETE_CHAT_ROOM($msg); 
					publishMessage($topic, $ack_message);
					break;}
				case "GET_ROOM_INFO":{
					$ack_message = GET_ROOM_INFO($msg); 
					publishMessage($topic, $ack_message);
					break;}
				case "ADD_PEOPLE_TO_GROUP":{
					$ack_message = ADD_PEOPLE_TO_GROUP($msg); 
					publishMessage($topic, $ack_message);
					break;}
				case "REMOVE_PEOPLE_FROM_GROUP":{
					$ack_message = REMOVE_PEOPLE_FROM_GROUP($msg); 
					publishMessage($topic, $ack_message);
					break;}
				case "GET_FRIEND_LIST_FOR_PARTICIPANT_ADD":{
					$ack_message = GET_FRIEND_LIST_FOR_PARTICIPANT_ADD($msg); 
					publishMessage($topic, $ack_message);
					break;}
				case "GET_PARTICIPANT_LIST_REMOVE":{
					$ack_message = GET_PARTICIPANT_LIST_REMOVE($msg); 
					publishMessage($topic, $ack_message);
					break;}
				case "CREATE_CHAT_ROOM":{
					$ack_message = CREATE_CHAT_ROOM($msg); 
					publishMessage($topic, $ack_message);
					break;}
				case "CREATE_PUBLIC_CHAT_ROOM":{
					$ack_message = CREATE_PUBLIC_CHAT_ROOM($msg); 
					publishMessage($topic, $ack_message);
					break;}
					////////////////////////
				case "GET_FRIEND_LIST":	{
					$ack_message = GET_FRIEND_LIST($msg); 
					publishMessage($topic, $ack_message);
					break;}
				case "GET_FRIEND_REQUEST":{
					$ack_message = GET_FRIEND_REQUEST($msg); 
					publishMessage($topic, $ack_message);
					break;}
				case "COUNT_FRIEND_REQUEST":{
					$ack_message = COUNT_FRIEND_REQUEST($msg);
					publishMessage($topic, $ack_message);
					break;}
				case "ADD_FRIEND":	{
					$ack_message = ADD_FRIEND($msg); 
					publishMessage($topic, $ack_message);
					break;}
				case "REQ_ADD_FRIEND":	{
					$ack_message = REQ_ADD_FRIEND($msg); 
					publishMessage($topic, $ack_message);
					break;}
				case "DELETE_FRIEND":	{
					$ack_message = DELETE_FRIEND($msg); 
					publishMessage($topic, $ack_message);
					break;}
				case "SEARCH_USER":	{
					$ack_message = SEARCH_USER($msg); 
					publishMessage($topic, $ack_message);
					break;}
				case "UPDATE_PUBLIC_KEY": {
					$ack_message = UPDATE_PUBLIC_KEY($msg);
					publishMessage($topic, $ack_message);
					break;}
				case "GET_USER_PROFILE":	{
					$ack_message = GET_USER_PROFILE($msg); 
					publishMessage($topic, $ack_message);
					break;}
				case "GET_PUBLIC_KEY":	{
					$ack_message = GET_PUBLIC_KEY($msg);
					publishMessage($topic, $ack_message);
					break;}
			}
		}
}

//***-----internal functions---------

//push array function
function array_push_assoc($array, $key, $value){
	$array[$key] = $value;
	return $array;
}

//Database connector : input SQL and return result;
//DO NOT MODIFY
function dbResult($sql){
	$hostname_localhost = "localhost";
	$database_localhost = "ccs_master";//change to your database name
	$username_localhost = "ccs_main";//change to your database username, it is recommended to add a new user with password
	$password_localhost = "123456";//change to user's password
		$link = mysqli_connect($hostname_localhost, $username_localhost, $password_localhost, $database_localhost);
			// Check connection
			if($link === false){
				echo("ERROR: Could not connect. " . mysqli_connect_error());
			}
			else{
				mysqli_set_charset($link, "UTF8");	
				$result = mysqli_query($link, $sql);
				if($result)
					return $result;
				else
					echo mysqli_error($link);

				// Close connection
				mysqli_close($link);
				return $result;
			}
}	

function dbResult_stmt($sql, $types, $params, $param_count){
	$hostname_localhost = "localhost";
	$database_localhost = "ccs_master";//change to your database name
	$username_localhost = "ccs_main";//change to your database username, it is recommended to add a new user with password
	$password_localhost = "123456";//change to user's password
		$link = mysqli_connect($hostname_localhost, $username_localhost, $password_localhost, $database_localhost);
			// Check connection
			if($link === false){
				echo("ERROR: Could not connect. " . mysqli_connect_error());
			}
			else{
				mysqli_set_charset($link, "UTF8");
				$query = mysqli_prepare($link, $sql);
				$sql_params_array = array();
				array_push($sql_params_array, $query, $types);
				for($x = 0; $x<count($params);$x++){
					array_push($sql_params_array, $params[$x]);
				}
				call_user_func_array("mysqli_stmt_bind_param",$sql_params_array);
				$result = mysqli_stmt_execute($sql_params_array[0]);
				//$result = mysqli_query($link, $sql);
				if($result)
					return $result;
				else
					echo mysqli_error($link);
				return $result;
			// Close connection
			mysqli_close($link);
			}
}

//------Server functions------

//from client login authentication
//Update: For student LOGIN only
function LOGIN($msg){
	$ack_message = "";
	$receivedData = explode(',', $msg);		 
	echo "\nHeader: ".$receivedData[0]."\n"; 
	echo "\nUsername: ".$receivedData[1]."\n"; 
	echo "\nPassword: ".$receivedData[2]."\n";
	$username = $receivedData[1];
	$password = $receivedData[2];

	$ack_message = "LOGIN_REPLY,";
	$sql ="SELECT *
			FROM `user` 
			INNER JOIN `student` ON `user`.`user_id` = `student`.`user_id`
			WHERE BINARY `User`.`username` = '$username' 
			AND BINARY `User`.`password` = '$password'
			AND user.status = 'Offline'";
	//Note: BINARY to toggle case sensitive, by default not case sensitive
	
	$result = dbResult($sql);
	if(mysqli_num_rows($result) > 0){
		$temp = array();
		while($row = mysqli_fetch_array($result)){
			$temp[] = $row;
			//echo "\nUser $row['user_id'], $row['username'] has logged in\n";
			//update user login status
			$temp1 = "1,".$row['user_id'].",".'Online';
			UPDATE_USER_STATUS($temp1);
		}
		$ack_message .= json_encode($temp);
	} else{
		echo "\nWrong password or username: ".$username."\n";
		$ack_message .= "NO_RESULT";
	}
	return $ack_message;
}

function REGISTER_USER($msg){
	echo "\nRegistering new user...\n";
	
	//clean $ack_message
	$ack_message = "REGISTER_USER_REPLY,";
	
	$receivedData = explode(',', $msg);		 
	$username = $receivedData[1];
	$password = $receivedData[2];
	
	$sql = "SELECT * FROM User WHERE BINARY username = '$username'";
	$result = dbResult($sql);
	if(mysqli_num_rows($result) == 0){
		echo "Registering user:".$username;
		
		$sql = "INSERT INTO User (username, password, display_name) VALUES ('$username', '$password', '$username');";
		$result = dbResult($sql);
		if($result){
			echo "\nNew user registered:".$username."\n";
			$ack_message .= "SUCCESS";		
		}else{
			echo "\nCannot register user:".$username."\n";
			$ack_message .= "NO_RESULT";
		}
	}else{
		echo "\nCannot register user:".$username.", username duplicated\n";
		$ack_message .= "DUPLICATED";
	}
	return $ack_message;
}

function UPDATE_USER_STATUS(){
	$temp = func_get_arg(0);
	$ack_message = "NO_PUB, ";
	
	$temp = explode(',', $temp);
	$user_id = $temp[1];
	$status = $temp[2];
	
	$sql = "UPDATE User SET status = '$status', last_online = CURRENT_TIMESTAMP WHERE user_id = '$user_id'";
	$result = dbResult($sql);
	if($result){
		echo "\nUpdated user status: $user_id, $status\n";
		$ack_message .= "SUCCESS";
	}else{
		echo "\nFailed to update user status: $user_id, $status\n";
		echo mysqli_error($result)."\n";
		$ack_message .= "FAILED";
	}
	return $ack_message;
}

function UPDATE_STUDENT($msg){
	//TODO: GAN DO this
	//update student table, everything (except user_id) from null to something
	$receivedData = explode(',', $msg);	// 1=user_id, 2...=faculty, course, tutorial_group, intake, academic_year
	$user_id = $receivedData[1];
	$faculty = $receivedData[2];
	$course = $receivedData[3];
	$tutorial_group = $receivedData[4];
	$intake = $receivedData[5];
	$academic_year = $receivedData[6];

	$sql = "UPDATE Student SET faculty = 'faculty', course = '$course', tutorial_group = '$tutorial_group', intake = '$intake', academic_year = '$academic_year'
			WHERE user_id = '$user_id'";
	$result = dbResult($sql);

	if(mysqli_affected_rows($result) > 0){
		echo "\nUpdated student: $user_id\n";
	}else{
		echo "\nFailed to update student: $user_id, $status\n";
		echo mysqli_error($result)."\n";
	}
}

function UPDATE_USER($msg){
	//TODO: GAN DO this
	//update everything except user_id, status, last_online
	//position = student by default
	//username, nric, phone_number, email requires validation

	echo "\nupdating user...\n";
	$receivedData = explode(',', $msg);	// 1,...=user_id, username, display_name, position, password, gender, nric, phone_number, email, address, city_id
	$user_id = $receivedData[1];
	$username = $receivedData[2];
	$display_name = $receivedData[3];
	$position = $receivedData[4];
	$password = $receivedData[5];
	$gender = $receivedData[6];
	$nric = $receivedData[7];
	$phone_number = $receivedData[8];
	$email = $receivedData[9];
	$address = $receivedData[10];
	$city_id = $receivedData[11];
	//if position="Student", create student table with user_id=[received user_id] (postponed, not now :P)
	//$sql = "INSERT INTO Student (user_id) VALUES ('$user_id');";

	$sql = "UPDATE User SET username = '$username', display_name = '$display_name', position = '$position', password = '$password',
	gender = '$gender', nric = '$nric', phone_number = '$phone_number', email = '$email', address = '$address', city_id = '$city_id' WHERE user_id = 'user_id'";
	$result = dbResult($sql);
		if(mysqli_affected_rows($result) > 0){
		echo "\nUpdated user: $user_id\n";
	}else{
		echo "\nFailed to user: $user_id, $status\n";
		echo mysqli_error($result)."\n";
	}
}

function GET_USER_PROFILE($msg){
	$temp = func_get_arg(0);
	$ack_message = "GET_USER_PROFILE_REPLY, ";

	$temp = explode(',', $temp); //user_id
	$user_id = $temp[1];
	$sql = "SELECT display_name, student.student_id, student.faculty, student.course, student.tutorial_group, student.intake, student.academic_year
			FROM User INNER JOIN Student ON User.user_id = Student.user_id
			WHERE user.user_id = '$user_id'";
	$result = dbResult($sql);
	if(mysqli_num_rows($result) > 0){
		$temp = array();
		while($row = mysqli_fetch_array($result)){
			$temp[] = $row;
		}
		echo "\nUser profile found: ".$user_id."\n";
		$ack_message .= json_encode($temp);
	}else{
		echo "\nUser profile not found: ".$user_id."\n";
		$ack_message .= "NO_RESULT";
	}
	echo "\n".$ack_message;
	return $ack_message;
}

function SEARCH_USER($msg){
	echo "\nSearching user...\n";
	$ack_message = "SEARCH_USER_REPLY,";
	
	$receivedData = explode(',', $msg);
	$user_id = $receivedData[1];
	$target_username = $receivedData[2];
	
	$sql = "SELECT user.user_id, display_name, student.course, student.tutorial_group
			FROM User INNER JOIN Student ON User.user_id = Student.user_id 
			WHERE display_name LIKE '%$target_username%' OR username LIKE '$target_username'";
	$result = dbResult($sql);
	if(mysqli_num_rows($result) > 0){
		$temp = array();
		while($row = mysqli_fetch_array($result)){
			$temp[] = $row;
		}
		echo "\nFound some users\n";
		$ack_message .= json_encode($temp);
	}else{
		echo "\nNo result\n";
		$ack_message .= "NO_RESULT";
	}
	echo "\n".$ack_message;
	return $ack_message;
}

function UPDATE_PUBLIC_KEY(){
	$temp = func_get_arg(0);
	$ack_message = "UPDATE_PUBLIC_KEY_REPLY, ";

	$temp = explode(',', $temp, 3); //user_id, public_key
	$user_id = $temp[1];
	$public_key = $temp[2];

	$sql = "UPDATE User SET public_key = ? WHERE user_id = ?";
	$types = "si";
	$params = array($public_key, $user_id);
	$param_count = count($params);
	$result = dbResult_stmt($sql, $types, $params, $param_count);
	if($result){
		echo "\nUpdated user public_key: $user_id\n";
		$ack_message .= "SUCCESS";
	}else{
		echo "\nFailed to update user public_key: $user_id\n";
		echo mysqli_error($result)."\n";
		$ack_message .= "FAILED";
	}
	return $ack_message;
}

function GET_PUBLIC_KEY(){
	$temp = func_get_arg(0);
	echo "\nGetting public key...\n";
	$ack_message = "GET_PUBLIC_KEY_REPLY, ";

	$temp = explode(',', $temp, 2); //user_id
	$user_id = $temp[1];

	$sql = "SELECT user.public_key
			FROM User
			WHERE user.user_id = '$user_id'";
	$result = dbResult($sql);
	if(mysqli_num_rows($result) > 0){
		$temp = array();
		while($row = mysqli_fetch_array($result)){
			$temp[] = $row;
		}
		echo "\nPublic key found\n";
		$ack_message .= json_encode($temp);
	}else{
		echo "\nNo result\n";
		$ack_message .= "NO_RESULT";
	}
	echo "\n".$ack_message;
	return $ack_message;
}

//The following functions are
//Done by 1st generation seniors
//Will leave it here for future reference
//If you can find any use of it

//from client request contact list
// function fn003810($msg){
	// //clean $ack_message
	// $ack_message = "";
	// $ack_message.="003811";
	// $sql =
		// "SELECT 
		// `friendship`.`fid` AS 'fid' , 

		// CHAR_LENGTH(`user`.`nickname`) AS 'nickname_length',
		// `user`.`nickname` AS 'nickname', 

		// CHAR_LENGTH(`user`.`status`) AS 'status_length',
		// `user`.`status` AS 'status'

		// FROM `user`,`friendship`
		// WHERE `user`.`uid` = `friendship`.`fid` 
		// AND `friendship`.`uid` = '$commandData' 
		// AND `friendship`.`status` = '1'";
		
	// $result = dbResult($sql);
		// if(mysqli_num_rows($result) > 0){
			// while($row = mysqli_fetch_array($result)){
					// $ack_message .= $row['fid'];

					// $ack_message .= str_pad($row['nickname_length'], 3, '0', STR_PAD_LEFT);
					// $ack_message .= $row['nickname'];
					
					// $ack_message .= str_pad($row['status_length'], 3, '0', STR_PAD_LEFT);
					// $ack_message .= $row['status'];
					
			// }
		// } else{
			// echo "\nNo records matching your query were found.";
		// }
	// return $ack_message;
// }		

// //from client request contact details
// function fn003812($msg){
	// $ack_message = "";
	// $reservedString = "000000000000000000000000";
	
	// $commandData = mb_substr($msg,30); 
	// $ack_message.="003813".$reservedString;
	// $sql =
		// "SELECT 
		// `uid`, `gender`, `last_online`, `nickname`, `username`, `status`
		// FROM `user`
		// WHERE `user`.`uid` = $commandData";
		
	// $result = dbResult($sql);
		// if(mysqli_num_rows($result) > 0){
			// while($row = mysqli_fetch_array($result)){
					// $ack_message .= $row['uid'];
					// $ack_message .= $row['gender'];
					// $ack_message .= $row['last_online'];
					
					// $ack_message .= str_pad(mb_strlen($row['username'],'utf8'), 3, '0', STR_PAD_LEFT);
					// $ack_message .= $row['username'];
										
					// $ack_message .= str_pad(mb_strlen($row['nickname'],'utf8'), 3, '0', STR_PAD_LEFT);
					// $ack_message .= $row['nickname'];
					
					// $ack_message .= str_pad(mb_strlen($row['status'],'utf8'), 3, '0', STR_PAD_LEFT);
					// $ack_message .= $row['status'];
					
			// }
		// } 
	// return $ack_message;
// }

// //from client request nearby friends
// function fn003814($msg){
	// $ack_message = "";
	// $reservedString = "000000000000000000000000";
	// $commandData = mb_substr($msg,30); 
	// $ack_message .="003815".$reservedString;
	
	// $uid = $commandData;
	
	// $sql = "SELECT `latitude`, `longitude` FROM `user` WHERE `uid` = $uid";
	// $result = dbResult($sql);
		// if($result)
			// if(mysqli_num_rows($result)==1)
				// $row = mysqli_fetch_array($result);
					// $source_latitude = $row['latitude'];
					// $source_longitude = $row['longitude'];
	
	// $search_radius_km = 3;
	// $date_range = strtotime("-1 week");
	// $result_limit = 15;
	
	// $sql = "SELECT 	u.`uid`, u.`nickname`, (p.distance_unit
													 // * DEGREES(ACOS(COS(RADIANS(p.latpoint))
													 // * COS(RADIANS(u.latitude))
													 // * COS(RADIANS(p.longpoint) - RADIANS(u.longitude))
													 // + SIN(RADIANS(p.latpoint))
													 // * SIN(RADIANS(u.latitude))))) * 1000 AS distance_in_metre
			// FROM 	`user` AS u
			// JOIN	( SELECT  
					// '$source_latitude' AS latpoint, 
					// '$source_longitude' AS longpoint,
					// '$search_radius_km' AS radius, 
					// 111.045 AS distance_unit
					// ) AS p ON 1=1
			// WHERE 	u.latitude
					// BETWEEN p.latpoint  - (p.radius / p.distance_unit)
					// AND p.latpoint  + (p.radius / p.distance_unit)
					// AND u.longitude
					// BETWEEN p.longpoint - (p.radius / (p.distance_unit * COS(RADIANS(p.latpoint))))
					// AND p.longpoint + (p.radius / (p.distance_unit * COS(RADIANS(p.latpoint))))
					// AND u.last_online > $date_range
					// AND u.uid NOT IN
					// ( SELECT `uid` FROM `user` WHERE `user`.`uid` = $uid )
			// ORDER BY distance_in_metre
			// LIMIT $result_limit";

		// $result = dbResult($sql);
		// if($result)
		// if(mysqli_num_rows($result) > 0){
			// while($row = mysqli_fetch_array($result)){
				// $ack_message .= $row['uid'];
				// $ack_message .= str_pad(mb_strlen($row['nickname'], "UTF8"), 3, '0', STR_PAD_LEFT);
				// $ack_message .= $row['nickname'];
				
				// $temp = round($row['distance_in_metre']);
				// $ack_message .= mb_strlen($temp, "UTF8");
				// $ack_message .= $temp;
			// }
		// }
	// return $ack_message;
// }

// //from client request search user
// function fn003816($msg){
	// $ack_message = "";
	// $reservedString = "000000000000000000000000";
	
	// $commandData = mb_substr($msg,30); 
	// $commandData = $commandData."%";
	// $ack_message.="003817".$reservedString;
	// $sql = "
			// SELECT `user`.`uid`, `user`.`username`, `user`.`nickname`, `user`.`gender`, `user`.`status`,
			// `user`.`last_online`, `user`.`latitude`, `user`.`longitude`
			// FROM `user`
			// WHERE `username` LIKE '$commandData'
			// ORDER BY `last_online` DESC
			// LIMIT 5
			// ";
		// $result = dbResult($sql);
		// if(mysqli_num_rows($result) > 0){
			// while($row = mysqli_fetch_array($result)){
					// $ack_message .= $row['uid'];
					// $ack_message .= $row['gender'];
					// $ack_message .= $row['last_online'];
		
					// $ack_message .= str_pad(mb_strlen($row['username'], "utf8"), 3, '0', STR_PAD_LEFT);
					// $ack_message .= $row['username'];
					
					// $ack_message .= str_pad(mb_strlen($row['nickname'], "utf8"), 3, '0', STR_PAD_LEFT);
					// $ack_message .= $row['nickname'];
					
					// $ack_message .= str_pad(mb_strlen($row['status'], "utf8"), 3, '0', STR_PAD_LEFT);
					// $ack_message .= $row['status'];
			// }
		// }
	// return $ack_message;
// }

// //from client request recommended friends
// function fn003818($msg){
// $ack_message = "";
// $reservedString = "000000000000000000000000";

// $commandData = mb_substr($msg,30); 
// $ack_message .="003819".$reservedString;

// $graph = array();
// $edge = 2; //Depth of Graph network. **Do not modify. 
// $sql = "SELECT `uid`,`fid` FROM `friendship` WHERE `uid` = '$commandData' AND status = 1";

// $result = dbResult($sql);
	// if(mysqli_num_rows($result) > 0){
		
		// //Push user's UID into array
		// $graph = array_push_assoc($graph, $commandData, array());
		
		// while($row = mysqli_fetch_array($result)){
			
			// //Push user's friends into array
			// $graph = array_push_assoc($graph, $row['fid'], array());
		// }
		
		// //for each friend in the array 
		// foreach($graph as $source => $destination){
			// $sql = "SELECT `fid` FROM `friendship` WHERE `uid` = '$source' AND status = 1";
			// $result = dbResult($sql);
			// if(mysqli_num_rows($result) > 0){
				// while($row = mysqli_fetch_array($result)){
					
					// //Push user's friends of friends into the array
					// $graph[$source] = array_push_assoc($graph[$source], $row['fid'] , $row['fid']);
					
				// }
			// }
		// }
	// }
	// $res = array();
	
	// //this is used for changing the array format to adapt the Graph library function
	// foreach($graph as $source => $destination){
		// if($source != $commandData){
			// $res = array_merge($res,$graph[$source]);
		// }
	// }
	
	// //Clear user id and duplicated id
	// $res = array_diff($res, [$commandData]);
	// $res = array_unique($res);
	
	// //Construct graph network with 2d array/matrix using the Graph Library function
	// $g = new Graph($graph);
	
	// $sortedList=array();
	// foreach($res as $id){
		
		// //countpath() function is used to count 
		// //number of indirect relationship between User and friend of friend
		// //also known as mutual friend relationship
		// $sortedList = array_push_assoc($sortedList, $id , $g->countpath($commandData, $id, $edge));
	// }
	// arsort($sortedList);
	
	// //construct response message to client
	// foreach($sortedList as $id => $walks){
		// $sql = "SELECT `uid`, `nickname` 
				// FROM `user`
				// WHERE `uid` = '$id'";
		// $result = dbResult($sql);
		// $row = mysqli_fetch_assoc($result);
		// $ack_message .= $row['uid'];
		// $ack_message .= str_pad(mb_strlen($row['nickname'], "utf8"),3,'0',STR_PAD_LEFT);
		// $ack_message .= $row['nickname'];
		// $ack_message .= mb_strlen($walks, "utf8");
		// $ack_message .= $walks;
	// }
	// return $ack_message;
// }

// //from client send friend request
// function fn003820($msg){
										
// $commandData = mb_substr($msg,30);
// $uid = mb_substr($commandData,0,10);
// $commandData = mb_substr($commandData,10);

// $fid = mb_substr($commandData,0,10);
// $commandData = mb_substr($commandData,10);

// $time = mb_substr($commandData,0,10);

	// $sql = "SELECT * 
			// FROM `friendship` 
			// WHERE `uid` = '$uid'
			// AND	`fid` = '$fid'";
		
	// $result = dbResult($sql);
	// if(mysqli_num_rows($result)>0){
		// $sql = "UPDATE `friendship` 
				// SET `date_action` = '$time',
					// `status` = 2
				// WHERE `uid` = '$uid'
				// AND `fid` = '$fid'";
	// } else{
		// $sql = "INSERT INTO `friendship` (`uid`, `fid`, `status`, `date_action`)
				// VALUES ('$uid', '$fid', 2, '$time')";
	// }
	
	// $result= dbResult($sql);
// }

// //from client request friend request list
// function fn003822($msg){
// $status = 2;
// $ack_message = "";
// $reservedString = "000000000000000000000000"; 
// $ack_message .="003823".$reservedString;
// $uid = mb_substr($msg,30);

	// $sql = "SELECT `friendship`.`uid` AS uid, `user`.`nickname` AS nickname, `user`.`status` AS status
			// FROM `user`, `friendship`
			// WHERE `friendship`.`uid` = `user`.`uid` 
				// AND `friendship`.`fid` = '$uid'
				// AND `friendship`.`status` = $status
			// ORDER BY `friendship`.`date_action` DESC
			// ";
			
	// $result = dbResult($sql);
		// if(mysqli_num_rows($result)>0){
			// while($row = mysqli_fetch_array($result)){
				// $ack_message .= $row['uid'];
				
				// $ack_message .= str_pad(mb_strlen($row['nickname'],"utf8"), 3, '0', STR_PAD_LEFT);
				// $ack_message .= $row['nickname'];
				
				// $ack_message .= str_pad(mb_strlen($row['status'],"utf8"), 3, '0', STR_PAD_LEFT);
				// $ack_message .= $row['status'];			}
		// }
	// return $ack_message;
// }

// //from client response friend request
// function fn003824($msg){
// $ack_message = "";
// $reservedString = "000000000000000000000000";
// $ack_message .="003825".$reservedString; 		
// $commandData = mb_substr($msg,30);
		
	// $uid = mb_substr($commandData, 0 , 10);
	// $fid = mb_substr($commandData, 10, 20);
	// $date = strtotime("now");
	
	// $sql = "UPDATE `friendship`
			// SET `status` = 1,
				// `date_action` = $date
			// WHERE `uid` = $uid AND `fid` = $fid;
			// ";
	// $result = dbResult($sql);
	// if($result){
		// $sql = "INSERT INTO `friendship` (`uid`,`fid`,`status`,`date_action`) values ('$fid','$uid','1','$date')";
		// $result = dbResult($sql);
		
		// if($result){
			// $ack_message .= 1;
		// }
		// else
			// $ack_message .= 0;
	// }
	// else
		// $ack_message .= 0;

	// return $ack_message;
// }

// //from client request faculty list
// function fn003826($msg){
// $ack_message = "";
// $reservedString = "000000000000000000000000";
// $ack_message .="003827".$reservedString; 		
// $commandData = mb_substr($msg,30);
	
	// $sql = "SELECT `faculty` FROM `student` GROUP BY `faculty`";
			
	// $result = dbResult($sql);
		// if(mysqli_num_rows($result)>0){
			// while($row = mysqli_fetch_array($result)){
				// $ack_message .= $row['faculty'];		
			// }
		// }
		
	// return $ack_message;
// }

// //from client request academic year list based on faculty
// function fn003828($msg){
// $ack_message = "";
// $reservedString = "000000000000000000000000";
// $ack_message .="003829".$reservedString; 		
	
	// $faculty = mb_substr($msg,30);
	
	// $sql = "SELECT `academic_year` 
			// FROM `student` 
			// WHERE `student`.`faculty` = '$faculty' 
			// GROUP BY `academic_year`
			// ORDER BY `academic_year` DESC";
			
	// $result = dbResult($sql);
		// if(mysqli_num_rows($result)>0){
			// while($row = mysqli_fetch_array($result)){
				// $ack_message .= $row['academic_year'];		
			// }
		// }
		
	// return $ack_message;
// }

// //from client request intake list based on faculty,year
// function fn003830($msg){
// $ack_message = "";
// $reservedString = "000000000000000000000000";
// $ack_message .="003831".$reservedString; 		
	
	// $commandData = mb_substr($msg, 30);
	// $faculty = mb_substr($commandData,0,4);
	// $commandData = mb_substr($commandData, 4);
	// $year = mb_substr($commandData,0,4);

	// $sql = "SELECT `intake` 
			// FROM `student` 
			// WHERE `student`.`faculty` = '$faculty' 
			// AND	`student`.`academic_year` = '$year'
			// GROUP BY `intake`
			// ORDER BY `intake` DESC";
			
	// $result = dbResult($sql);
		// if(mysqli_num_rows($result)>0){
			// while($row = mysqli_fetch_array($result)){
				// $ack_message .= $row['intake'];		
			// }
		// }
		
	// return $ack_message;
// }

// //from client request courses list based on faculty,year,session
// function fn003832($msg){
// $ack_message = "";
// $reservedString = "000000000000000000000000";
// $ack_message .="003833".$reservedString; 			

	// $commandData = mb_substr($msg, 30);
	// $faculty = mb_substr($commandData,0,4);
	// $commandData = mb_substr($commandData, 4);
	// $year = mb_substr($commandData,0,4);
	// $commandData = mb_substr($commandData, 4);
	// $session = mb_substr($commandData,0,6);

	// $sql = "SELECT `course` 
			// FROM `student` 
			// WHERE `student`.`faculty` = '$faculty' 
			// AND	`student`.`academic_year` = '$year'
			// AND `student`.`intake` = '$session'
			// GROUP BY `course`
			// ORDER BY `course`";
			
	// $result = dbResult($sql);
		// if(mysqli_num_rows($result)>0){
			// while($row = mysqli_fetch_array($result)){
				// $ack_message .= $row['course'];		
			// }
		// }
		
	// return $ack_message;
// }

// //From client request group list based on faculty,year,session,course
// function fn003834($msg){
// $ack_message = "";
// $reservedString = "000000000000000000000000";
// $ack_message .="003835".$reservedString; 			

	// $commandData = mb_substr($msg, 30);
	// $faculty = mb_substr($commandData,0,4);
	// $commandData = mb_substr($commandData, 4);
	// $year = mb_substr($commandData,0,4);
	// $commandData = mb_substr($commandData, 4);
	// $session = mb_substr($commandData,0,6);
	// $commandData = mb_substr($commandData, 6);
	// $course = mb_substr($commandData,0,3);

	// $sql = "SELECT `tutorial_group` 
			// FROM `student` 
			// WHERE `student`.`faculty` = '$faculty' 
			// AND	`student`.`academic_year` = '$year'
			// AND `student`.`intake` = '$session'
			// AND `student`.`course` = '$course'
			// GROUP BY `tutorial_group`
			// ORDER BY `tutorial_group`";
			
	// $result = dbResult($sql);
		// if(mysqli_num_rows($result)>0){
			// while($row = mysqli_fetch_array($result)){
				// $ack_message .= str_pad($row['tutorial_group'],2,'0',STR_PAD_LEFT);		
			// }
		// }
		
	// return $ack_message;
// }

// //From client request user list based on faculty,year,session,course,group	
// function fn003836($msg){
// $ack_message = "";
// $reservedString = "000000000000000000000000";
// $ack_message .="003837".$reservedString; 		

	// $commandData = mb_substr($msg, 30);
	// $faculty = mb_substr($commandData,0,4);
	// $commandData = mb_substr($commandData, 4);
	// $year = mb_substr($commandData,0,4);
	// $commandData = mb_substr($commandData, 4);
	// $session = mb_substr($commandData,0,6);
	// $commandData = mb_substr($commandData, 6);
	// $course = mb_substr($commandData,0,3);
	// $commandData = mb_substr($commandData, 3);
	// $group = mb_substr($commandData,0,2);

	// $sql = "SELECT `user`.`uid`,`user`.`nickname`,`user`.`student_id`
			// FROM `student`,`user`
			// WHERE `student`.`student_id` = `user`.`student_id`
			// AND `student`.`faculty` = '$faculty' 
			// AND	`student`.`academic_year` = '$year'
			// AND `student`.`intake` = '$session'
			// AND `student`.`course` = '$course'
			// AND `student`.`tutorial_group` = $group
			// ORDER BY `user`.`student_id`";
			
	// $result = dbResult($sql);
		// if(mysqli_num_rows($result)>0){
			// while($row = mysqli_fetch_array($result)){
				// $ack_message .= $row['uid'];	
				
				// $ack_message .= str_pad(mb_strlen($row['nickname'], "utf8"), 3, '0', STR_PAD_LEFT);
				// $ack_message .= $row['nickname'];					
			// }
		// }
	
	// return $ack_message;
// }

// //From client KeepAlive
// function fn003999($msg){
	// $commandData = mb_substr($msg,30);
	
	// $uid = mb_substr($commandData,0,10);
	// $commandData = mb_substr($commandData,10);
	
	// $time = mb_substr($commandData,0,10);
	// $commandData = mb_substr($commandData,10);
	
	// $temp = mb_substr($commandData, 0, 2);
	// $commandData = mb_substr($commandData, 2);
	// $latitude = mb_substr($commandData, 0, (int)$temp);
	// $commandData = mb_substr($commandData, (int)$temp);
	
	// $temp = mb_substr($commandData, 0, 2);
	// $commandData = mb_substr($commandData, 2);
	// $longitude = mb_substr($commandData, 0, (int)$temp);
	// $commandData = mb_substr($commandData, (int)$temp);
	
	// $sql = "UPDATE `user` 
	// SET `last_online` = '$time',
		// `latitude` = $latitude,
		// `longitude` = $longitude
	// WHERE `user`.`uid` = '$uid'";
	
	// $result = dbResult($sql);
	// if($result)
		// echo "\n[Success] Updated UID:$uid @ lat $latitude long $longitude";
// }


?>
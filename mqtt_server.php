<?php
require("phpMQTT.php");
require("graph.php");

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
if you never lean Php before, don't panic, its more easier compare to Java, C++
Go to w3school and learn yourself
follow the steps below
Before that, I recommended you to use NotePad++ to edit this file

To setup your MQTT broker:
	1. download any free broker, recommended HiveMQ
	2. Run the broker, if you use HiveMQ -> Go to HiveMQ/bin/run.bat, run as administrator, then wait
	3. open a web browser, type 127.0.0.1:8080 or refer to the terminal screen
	4. now your mqtt broker is up
	5. you can observe the client conection via the browser

To setup your server locally, you need to do this:
	1. use hotkey Windows Key + R, type cmd
	2. in cmd, type ipconfig to check your IPv4 address
	3. type your ip address to $server at below and in publishMessage()
	4. Open XAMPP, start Apache and MySQL, make sure both are green color, disable the rest
	5. click admin button next to MySQL
	6. you should now see a localhost opened in your web browser
	7. create a new database name it "ccs_master"
		create a new user with username: ccs_main and password: "123456"
		select import, import the ccs.sql file, and click go
	8. now your database is setup
	9. now, this one very important, open your XAMPP folder, go to htdocs
	10. htdocs is the default directory of your local server
	11. put all the php files you got in htdocs
	12. now open cmd again and type START C:\xampp\php\php.exe C:\xampp\htdocs\mqtt_server.php
	13. now you should see a php terminal open
	14. make sure the php terminal says something like "Connected to MQTT Broker @ x.x.x.x:xxxx"
		*if it does not or the php terminal shutdown, then you are having errors in this file
		go to below session and see how to debug
	15. and tada your server is up
	16. everytime client sent something to the server, 
	17. the php terminal should show some text regarding message received from client
	
Now make your android project connect to this local server
	1. Open android studio
	2. Go to MQTTHelper and change the ip address like above
	3. now it should connect

Now, to debug this entire server script
	1. activate XAMPP
	2. open a web browser
	3. type localhost/mqtt_server.php
	4. if u use Chrome, it should tell you where is the error
	5. if you get message like maximum 30 seconds, then you are most likely no error
	6. that 30 seconds message is just a restriction on Chrome
	7. now go back to cmd and start the server
	
Again, make sure to change the server ip address to your ip everytime before u start
Good luck!
find me at https://www.facebook.com/leo477831
if you need any more help
*/

$server = "192.168.0.106";     		// change to your broker's ip
$port = 1883;                     		// change if necessary, default is 1883
$username = "root";                 // set your username
$password = "";             // set your password
$client_id = "CCS_SERVER"; 				// make sure this is unique for connecting to sever - you could use uniqid()

$QOS = 1;

//Note: Very important, this topic prefix must be same as the prefix in your android project
//and the # indicates that this server will subscribe to any new topic in this directory
$subscribeTopic = "MY/TARUC/CCS/000000001/PUB/#";

//-<<<<<<<<<<<<<<<<<<<<<<<--Do not modify---
$mqtt = new phpMQTT($server, $port, $client_id);
if(!$mqtt->connect(true, NULL, $username, $password)) {
	echo "Couldn't connect to MQTT server.";
	exit(1);
}
echo "Connected to MQTT Broker @ ".$server.":".$port."\n";
echo "\n===============================\n";
echo "Hello world!";
echo "\n===============================\n";
$topics[$subscribeTopic] = array("qos" => '$QOS', "function" => "procmsg");
$mqtt->subscribe($topics, $QOS);

while($mqtt->proc()){
}
$mqtt->close();
//->>>>>>>>>>>>>>>>>>>>>>--Do not modify--- END

//Server Responses
function procmsg($topic, $msg){		
		$ack_message = "";
		echo "\nReceived message from topic ".$topic."\n";
		echo "Received Message: ".$msg."\n";
		
		
		if(!empty($msg)){
			$commandmsg = explode(",", $msg);				
			switch($commandmsg[0]){
				case "LOGIN":	{
					$ack_message = LOGIN($msg);
					publishMessage($topic, $ack_message); break;}
				case "REGISTER_USER":	{
					$ack_message = REGISTER_USER($msg);
					publishMessage($topic, $ack_message); break;}
				case "GET_FRIEND_LIST":	{
					$ack_message = GET_FRIEND_LIST($msg);
					publishMessage($topic, $ack_message); break;}
				case "FIND_BY_ADDRESS":	{
					$ack_message = FIND_BY_ADDRESS($msg);
					publishMessage($topic, $ack_message); break;}
				case "FIND_BY_PROGRAMME":	{
					$ack_message = FIND_BY_PROGRAMME($msg);
					publishMessage($topic, $ack_message); break;}
				case "FIND_BY_TUTORIAL_GROUP":	{
					$ack_message = FIND_BY_TUTORIAL_GROUP($msg);
					publishMessage($topic, $ack_message); break;}
				case "FIND_BY_TUTORIAL_GROUP":	{
					$ack_message = FIND_BY_TUTORIAL_GROUP($msg);
					publishMessage($topic, $ack_message); break;}
				case "FIND_BY_AGE":	{
					$ack_message = FIND_BY_AGE($msg);
					publishMessage($topic, $ack_message); break;}
				case "GET_FRIEND_REQUEST":{
					$ack_message = GET_FRIEND_REQUEST($msg);
					publishMessage($topic, $ack_message); break;
					}
				case "ADD_FRIEND":	{
					$ack_message = ADD_FRIEND($msg);
					publishMessage($topic, $ack_message); break;}
				case "REQ_ADD_FRIEND":	{
					$ack_message = REQ_ADD_FRIEND($msg);
					publishMessage($topic, $ack_message); break;}
				case "DELETE_FRIEND":	{
					$ack_message = DELETE_FRIEND($msg);
					publishMessage($topic, $ack_message); break;}
				case "SEARCH_USER":	{
					$ack_message = SEARCH_USER($msg);
					publishMessage($topic, $ack_message); break;}
				//case "003810":	{$ack_message = fn003810($msg);publishMessage($topic, $ack_message); break;}  				
				// case "003812":	{$ack_message = fn003812($msg);publishMessage($topic, $ack_message); break;}
				// case "003814":	{$ack_message = fn003814($msg);publishMessage($topic, $ack_message); break;}
				// case "003816":	{$ack_message = fn003816($msg);publishMessage($topic, $ack_message); break;}			
				// case "003818":	{$ack_message = fn003818($msg);publishMessage($topic, $ack_message); break;}
				// case "003820":	{fn003820($msg); break;}
				// case "003822": 	{$ack_message = fn003822($msg);publishMessage($topic, $ack_message); break;}
				// case "003824":	{$ack_message = fn003824($msg);publishMessage($topic, $ack_message); break;}
				// case "003826":	{$ack_message = fn003826($msg);publishMessage($topic, $ack_message); break;}
				// case "003828":	{$ack_message = fn003828($msg);publishMessage($topic, $ack_message); break;}
				// case "003830":	{$ack_message = fn003830($msg);publishMessage($topic, $ack_message); break;}
				// case "003832":	{$ack_message = fn003832($msg);publishMessage($topic, $ack_message); break;}
				// case "003834":	{$ack_message = fn003834($msg);publishMessage($topic, $ack_message); break;}
				// case "003836": 	{$ack_message = fn003836($msg);publishMessage($topic, $ack_message); break;}
				// case "003999":	{fn003999($msg); break;}
			}
			echo "\nReturning to Topic :".$topic."\nAckMessage: \"".$ack_message."\"" ." \n";
		}
}

//***-----internal functions---------

//push array function
function array_push_assoc($array, $key, $value){
	$array[$key] = $value;
	return $array;
}

//Database connector : input SQL and return result;
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
				return $result;
			// Close connection
			mysqli_close($link);
			usleep(200000);//sleep for 0.2 second
			}
}	

//MQTT publish message
//DO NOT MODIFY, except ip address
function publishMessage($topic, $ack_message){
$server = "192.168.0.106";     		// change if necessary
$port = 1883;                     		// change if necessary
$username = "";                 // set your username
$password = "";             // set your password
$client_id = "CCS_SERVER"; 				// make sure this is unique for connecting to sever - you could use uniqid()
/*
$server = "m14.cloudmqtt.com";     		// change if necessary
$port = 16672;                     		// change if necessary
$username = "vwkohpay";                 // set your username
$password = "JPG3F4XUHjRv";             // set your password
$client_id = "SERVER_1"; 				// make sure this is unique for connecting to sever - you could use uniqid()
*/
$QOS = 1;
	$mqtt = new phpMQTT($server, $port, $client_id);
	if(!$mqtt->connect(true, NULL, $username, $password)) {
		exit(1);
	}
	$mqtt->publish($topic, $ack_message , $QOS);
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

	$ack_message.="LOGIN_REPLY,";
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
			UPDATE_USER_STATUS($row['user_id'], 'Online');
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
	if(mysqli_num_rows($result) = 0){
		echo "Registering user:".$username;
		
		$sql = "INSERT INTO User (username, password, display_name) VALUES ('$username', '$password', '$username');";
		$result = dbResult($sql);
		if(mysqli_affected_rows($result) > 0){
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
	echo $ack_message;
}

//private use
function UPDATE_USER_STATUS($user_id, $status){
	$sql = "UPDATE User SET status = '$status' WHERE user_id = 'user_id'";
	$result = dbResult($sql);
	if(mysqli_affected_rows($result) > 0){
		echo "\nUpdated user status: $user_id, $status\n";
	}else{
		echo "\nFailed to update user status: $user_id, $status\n";
		echo mysqli_error($result)."\n";
	}
}

function UPDATE_STUDENT($msg){
	//TODO: GAN DO this
}

function UPDATE_USER($msg){
	//TODO: GAN DO this
}

//since this one will return many rows
//so the data has to be converted to JSON object array
//please google it how to do
function GET_FRIEND_LIST($msg){
	echo "\ngetting friend list...\n";
	$ack_message = "GET_FRIEND_LIST_REPLY,";
	
	$receivedData = explode(',', $msg);		 
	$user_id = $receivedData[1];
	$sql = "SELECT friend_id, display_name, User.status, last_online 
			FROM Friendship INNER JOIN User 
			ON Friendship.user_id = User.user_id
			WHERE Friendship.user_id = $user_id AND Friendship.Status = 'Friend'
			ORDER BY 
				CASE 
					WHEN User.status = 'Online' THEN 1
					ELSE 2
				END, 
				User.last_online DESC,
				User.display_name";
	$result = dbResult($sql);
	if(mysqli_num_rows($result) > 0){
		$temp = array();
		while($row = mysqli_fetch_assoc($result)){
			$temp[] = $row;
		}
		echo "\ngot friend list for user:".$user_id."\n";
		$ack_message .= json_encode($temp);
	}else{
		echo "\nNo friends, feelsbadman\n";
		$ack_message .= "NO_RESULT";
	}
	return $ack_message;
} 

function FIND_BY_ADDRESS($msg){
	echo "\nFinding friend by address, city and state...\n";
	$ack_message = "FIND_BY_ADDRESS_REPLY,";
	
	$receivedData = explode(',', $msg);	
	$user_id = $receivedData[1];
	$city_id = $receivedData[2];
	$sql = "SELECT
				User.user_id, User.display_name, User.status, User.last_online, 
				Student.course, Student.academic_year, Student.tutorial_group
				FROM User 
					INNER JOIN City ON User.city_id = City.city_id 
					INNER JOIN State ON City.state_id = State.state_id 
					INNER JOIN Student ON User.user_id = Student.user_id 
				WHERE 
					State.state_id = (SELECT DISTINCT state_id 
							FROM City 
							WHERE city_id = '$city_id') AND 
					User.user_id NOT IN 
						(
							SELECT Friendship.friend_id 
							FROM Friendship INNER JOIN User ON User.user_id = Friendship.user_id 
							WHERE Friendship.user_id = $user_id
						) AND
					User.user_id <> $user_id
				ORDER BY 
					CASE 
						WHEN User.status = 'Online' THEN 1
						ELSE 2
					END, 
					CASE 
						WHEN User.city_id = '$city_id' THEN 1 
						ELSE 2
					END, 
					User.city_id, 
					User.display_name";
				
	$result = dbResult($sql);
	if(mysqli_num_rows($result) > 0){
		$temp = array();
		while($row = mysqli_fetch_assoc($result)){
			$temp[] = $row;
		}
		echo "\nFriends live in similar place found!\n";
		$ack_message .= json_encode($temp);
	}else{
		echo "\nNo one live in same place with this user, feelsbadman\n";
		$ack_message .= "NO_RESULT";
	}
	echo "\n".$ack_message;
	return $ack_message;
}

function FIND_BY_PROGRAMME($msg){
	echo "\nFinding friend by programme, faculty...\n";
	$ack_message = "FIND_BY_PROGRAMME_REPLY,";
	
	$receivedData = explode(',', $msg);	
	$user_id = $receivedData[1];
	$faculty = $receivedData[2];
	$course = $receivedData[3];
	$tutorial_group = $receivedData[4];
	$sql = "SELECT
				User.user_id, User.display_name, User.status, User.last_online, 
				Student.course, Student.academic_year, Student.tutorial_group
			FROM Student INNER JOIN User ON User.user_id = Student.user_id 
			WHERE 
				faculty = '$faculty' AND 
				course = '$course' AND
				User.user_id NOT IN 
					(
						SELECT Friendship.friend_id AS 'user_id'
						FROM Friendship INNER JOIN User ON User.user_id = Friendship.user_id 
						WHERE Friendship.user_id = $user_id
					) AND
				User.user_id <> $user_id
			ORDER BY 
				CASE 
					WHEN User.status = 'Online' THEN 1
					ELSE 2
				END,  
				CASE 
					WHEN faculty = '$faculty' THEN 1
					ELSE 2
				END, 
				CASE 
					WHEN course = '$course' THEN 1
					ELSE 2
				END, 
				CASE 
					WHEN tutorial_group = '$tutorial_group' THEN 1
					ELSE 2
				END,
				User.last_online DESC,
				User.display_name";
				
	$result = dbResult($sql);
	if(mysqli_num_rows($result) > 0){
		$temp = array();
		while($row = mysqli_fetch_assoc($result)){
			$temp[] = $row;
		}
		echo "\nFriends live in similar place found !\n";
		$ack_message .= json_encode($temp);
	}else{
		echo "\nNo one live in same place with this user, feelsbadman\n";
		$ack_message .= "NO_RESULT";
	}
	echo "\n".$ack_message;
	return $ack_message;
}

function FIND_BY_TUTORIAL_GROUP($msg){
	echo "\nFinding friend by tutorial group...\n";
	$ack_message = "FIND_BY_TUTORIAL_GROUP_REPLY,";
	
	$receivedData = explode(',', $msg);
	$user_id = $receivedData[1];
	$faculty = $receivedData[2];
	$course = $receivedData[3];
	$tutorial_group = $receivedData[4];
	$intake = $receivedData[5];
	$academic_year = $receivedData[6];
	$sql = "SELECT
				User.user_id, User.display_name, User.status, User.last_online, 
				Student.course, Student.academic_year, Student.tutorial_group
			FROM Student INNER JOIN User ON User.user_id = Student.user_id 
			WHERE 
				faculty = '$faculty' AND 
				course = '$course' AND 
				intake = '$intake' AND 
				academic_year = '$academic_year' AND 
				tutorial_group = '$tutorial_group' AND
				User.user_id NOT IN 
					(
						SELECT Friendship.friend_id AS 'user_id'
						FROM Friendship INNER JOIN User ON User.user_id = Friendship.user_id 
						WHERE Friendship.user_id = $user_id
					) AND
				User.user_id <> $user_id
			ORDER BY 
				CASE 
					WHEN User.status = 'Online' THEN 1
					ELSE 2
				END,
				last_online DESC,
				User.display_name";
				
	$result = dbResult($sql);
	if(mysqli_num_rows($result) > 0){
		$temp = array();
		while($row = mysqli_fetch_assoc($result)){
			$temp[] = $row;
		}
		echo "\nFriends live in similar place found!\n";
		$ack_message .= json_encode($temp);
	}else{
		echo "\nNo one live in same place with this user, feelsbadman\n";
		$ack_message .= "NO_RESULT";
	}
	echo "\n".$ack_message;
	return $ack_message;
}

function FIND_BY_AGE($msg){
	echo "\nFinding friend by age...\n";
	$ack_message = "FIND_BY_AGE_REPLY,";
	
	$receivedData = explode(',', $msg);	
	$user_id = $receivedData[1];
	$year = $receivedData[2];
	$faculty = $receivedData[3];
	$course = $receivedData[4];
	
	$sql = "SELECT
				User.user_id, User.display_name, User.status, User.last_online, 
				Student.course, Student.academic_year, Student.tutorial_group
			FROM Student INNER JOIN User ON User.user_id = Student.user_id 
			WHERE 
				CAST(SUBSTRING(nric, 1, 2) AS INTEGER) = '$year' AND 
				User.user_id NOT IN 
					(
						SELECT Friendship.friend_id AS 'user_id'
						FROM Friendship INNER JOIN User ON User.user_id = Friendship.user_id 
						WHERE Friendship.user_id = $user_id
					) AND
				User.user_id <> $user_id
			ORDER BY 
				CASE 
					WHEN User.status = 'Online' THEN 1
					ELSE 2
				END,  
				CASE 
					WHEN faculty = '$faculty' THEN 1
					ELSE 2
				END, 
				CASE 
					WHEN course = '$course' THEN 1
					ELSE 2
				END,
				User.last_online DESC,
				User.user_id";
				
	$result = dbResult($sql);
	if(mysqli_num_rows($result) > 0){
		$temp = array();
		while($row = mysqli_fetch_assoc($result)){
			$temp[] = $row;
		}
		echo "\nFriends with same age found! feelsgoodman\n";
		$ack_message .= json_encode($temp);
	}else{
		echo "\nNo one is same age with this user, feelsbadman\n";
		$ack_message .= "NO_RESULT";
	}
	echo "\n".$ack_message;
	return $ack_message;
}

function COUNT_FRIEND_REQUEST($msg){
	echo "\nCounting friend requests...\n";
	$ack_message = "COUNT_FRIEND_REQUEST_REPLY,";
	
	$receivedData = explode(',', $msg);	
	$user_id = $receivedData[1];
	
	$sql = "SELECT COUNT(*) AS count_result FROM Friendship WHERE user_id = '$user_id'";
				
	$result = dbResult($sql);
	if(mysqli_num_rows($result) > 0){
		$row = mysqli_fetch_row($result);
		echo "\nFound some friend request for user $user_id. feelsgoodman\n";
		$ack_message .= "$row[count_result]";
	}else{
		echo "\nUser $user_id has no friend request\n";
		$ack_message .= "NO_RESULT";
	}
	echo "\n".$ack_message;
	return $ack_message;
}

function REQ_ADD_FRIEND($msg){
	echo "\nRequesting to add friends...\n";
	$ack_message = "REQ_ADD_FRIEND_REPLY,";
	
	$receivedData = explode(',', $msg);	
	$user_id = $receivedData[1];
	$friend_id = $receivedData[2];
	
	$sql = "INSERT INTO Friendship (user_id, friend_id) VALUES ($user_id, $friend_id),($friend_id, $user_id)";
				
	$result = dbResult($sql);
	if(mysqli_affected_rows($result) > 0){
		echo "\n$user_id sent friend request to $friend_id. feelsgoodman\n";
		$ack_message .= "SUCCESS";
	}else{
		echo "\n$user_id cannot request $friend_id, feelsbadman\n";
		$ack_message .= "NO_RESULT";
	}
	echo "\n".$ack_message;
	return $ack_message;
}

function GET_FRIEND_REQUEST($msg){
	echo "\ngetting friend list...\n";
	$ack_message = "GET_FRIEND_LIST_REPLY,";
	
	$receivedData = explode(',', $msg);		 
	$user_id = $receivedData[1];
	$sql = "SELECT friend_id AS 'user_id', display_name, User.status, last_online, 
				course, academic_year, tutorial_group 
			FROM Friendship INNER JOIN User 
			ON Friendship.user_id = User.user_id
			WHERE Friendship.user_id = $user_id AND Friendship.Status = 'Pending'";
	$result = dbResult($sql);
	if(mysqli_num_rows($result) > 0){
		$temp = array();
		while($row = mysqli_fetch_assoc($result)){
			$temp[] = $row;
		}
		echo "\nFound friend request for user:".$user_id."\n";
		$ack_message .= json_encode($temp);
	}else{
		echo "\nNo friends, feelsbadman\n";
		$ack_message .= "NO_RESULT";
	}
	return $ack_message;
}

function ADD_FRIEND($msg){
	echo "\nAdding friends...\n";
	$ack_message = "ADD_FRIEND_REPLY,";
	
	$receivedData = explode(',', $msg);	
	$user_id = $receivedData[1];
	$friend_id = $receivedData[2];
	
	$sql = "UPDATE Friendship 
			SET status = 'Friend' 
			WHERE 
				(user_id = '$user_id' AND friend_id = '$friend_id') OR 
				(user_id = '$friend_id' AND friend_id = '$user_id')";
				
	$result = dbResult($sql);
	if(mysqli_affected_rows($result) > 0){
		echo "\n$user_id sent friend request to $friend_id. feelsgoodman\n";
		$ack_message .= "SUCCESS";
	}else{
		echo "\n$user_id cannot request $friend_id, feelsbadman\n";
		$ack_message .= "NO_RESULT";
	}
	echo "\n".$ack_message;
	return $ack_message;
}

function DELETE_FRIEND($msg){
	echo "\nDeleting friends...\n";
	$ack_message = "DELETE_FRIEND_REPLY,";
	
	$receivedData = explode(',', $msg);	
	$user_id = $receivedData[1];
	$friend_id = $receivedData[2];
	
	$sql = "DELETE FROM Friendship 
			WHERE 
				(user_id = '$user_id' AND friend_id = '$friend_id') OR 
				(user_id = '$friend_id' AND friend_id = '$user_id')";
				
	$result = dbResult($sql);
	if(mysqli_affected_rows($result) > 0){
		echo "\n$user_id and $friend_id are no more friends now. feelssadman\n";
		$ack_message .= "SUCCESS";
	}else{
		echo "\n$user_id cannot delete $friend_id, feelsokayman\n";
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

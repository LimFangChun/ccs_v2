<?php
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
					DATE(last_online) DESC, 
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
				DATE(last_online) DESC,
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
				DATE(last_online) DESC,
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
				DATE(last_online) DESC,
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

?>
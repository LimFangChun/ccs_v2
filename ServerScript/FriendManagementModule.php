<?php

/**
 * @Deprecated
 */
function GET_FRIEND_LIST($msg){
	echo "\ngetting friend list...\n";
	$ack_message = "GET_FRIEND_LIST_REPLY,";
	
	$receivedData = explode(',', $msg);		 
	$user_id = $receivedData[1];
	$sql = "SELECT User.user_id, display_name, User.status, last_online,
				course, academic_year, tutorial_group
			FROM User INNER JOIN Student ON Student.user_id = User.user_id 
			WHERE User.user_id IN (
				SELECT friend_id as 'user_id'
				FROM Friendship
				WHERE Friendship.user_id = $user_id AND Friendship.Status = 'Friend')
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
		echo "\nGot friend list for user:".$user_id."\n";
		$ack_message .= json_encode($temp);
	}else{
		echo "\nNo friends, feelsbadman\n";
		$ack_message .= "NO_RESULT";
	}
	return $ack_message;
} 

/**
 * @Deprecated
 */
function COUNT_FRIEND_REQUEST($msg){
	echo "\nCounting friend requests...\n";
	$ack_message = "COUNT_FRIEND_REQUEST_REPLY,";
	
	$receivedData = explode(',', $msg);	
	$user_id = $receivedData[1];
	
	$sql = "SELECT COUNT(*) AS count_result FROM Friendship WHERE user_id = '$user_id' AND status = 'Pending' AND sender_id <> $user_id";
				
	$result = dbResult($sql);
	if(mysqli_num_rows($result) > 0){
		$row = mysqli_fetch_row($result);
		echo "\nFound some friend request for user $user_id. feelsgoodman\n";
		$ack_message .= "$row[0]";
	}else{
		echo "\nUser $user_id has no friend request\n";
		$ack_message .= "NO_RESULT";
	}
	echo "\n".$ack_message;
	return $ack_message;
}

/**
 * @Deprecated
 */
function REQ_ADD_FRIEND($msg){
	echo "\nRequesting to add friends...\n";
	$ack_message = "REQ_ADD_FRIEND_REPLY,";
	
	$receivedData = explode(',', $msg);	
	$user_id = $receivedData[1];
	$friend_id = $receivedData[2];
	
	$sql = "INSERT INTO Friendship (user_id, friend_id, sender_id) VALUES ($user_id, $friend_id, $user_id),($friend_id, $user_id, $user_id)";
				
	$result = dbResult($sql);
	if($result){
		echo "\n$user_id sent friend request to $friend_id. feelsgoodman\n";
		$ack_message .= "SUCCESS";
	}else{
		echo "\n$user_id cannot request $friend_id, feelsbadman\n";
		$ack_message .= "NO_RESULT";
	}
	return $ack_message;
}

/**
 * @Deprecated
 */
function GET_FRIEND_REQUEST($msg){
	echo "\ngetting friend request...\n";
	$ack_message = "GET_FRIEND_REQUEST_REPLY,";
	
	$receivedData = explode(',', $msg);		 
	$user_id = $receivedData[1];
	$sql = "SELECT User.user_id, display_name, User.status, last_online, 
				course, academic_year, tutorial_group 
			FROM Student INNER JOIN User ON User.user_id = Student.user_id 
			WHERE User.user_id IN (
				SELECT friend_id
				FROM Friendship INNER JOIN User ON Friendship.user_id = User.user_id 
				INNER JOIN Student ON Student.user_id = User.user_id 
				WHERE Friendship.user_id = $user_id AND Friendship.Status = 'Pending' AND Friendship.sender_id <> $user_id
			)
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
		echo "\nFound friend request for user:".$user_id."\n";
		$ack_message .= json_encode($temp);
	}else{
		echo "\nNo friends, feelsbadman\n";
		$ack_message .= "NO_RESULT";
	}
	return $ack_message;
}

/**
 * @Deprecated
 */
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
	if($result){
		echo "\n$user_id sent friend request to $friend_id. feelsgoodman\n";
		$ack_message .= "SUCCESS";
	}else{
		echo "\n$user_id cannot request $friend_id, feelsbadman\n";
		$ack_message .= "NO_RESULT";
	}
	return $ack_message;
}

/**
 * @Deprecated
 */
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
	if($result){
		echo "\n$user_id and $friend_id are no more friends now. feelssadman\n";
		$ack_message .= "SUCCESS";
	}else{
		echo "\n$user_id cannot delete $friend_id, feelsokayman\n";
		$ack_message .= "NO_RESULT";
	}
	return $ack_message;
}

?>
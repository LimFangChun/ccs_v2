<?php 

//depecrated -> see ChatModule.js
function GET_CHAT_ROOM($msg){
	echo "\nGetting chat room...\n";
	$ack_message = "GET_CHAT_ROOM_REPLY,";
	
	$receivedData = explode(',', $msg);	
	$user_id = $receivedData[1];
	
	$sql = "SELECT * 
			FROM Chat_Room INNER JOIN Participant ON Chat_Room.room_id = Participant.room_id 
			WHERE Participant.user_id = $user_id
			ORDER BY last_update DESC";
				
	$result = dbResult($sql);
	if(mysqli_num_rows($result) > 0){
		echo "\nFound chat room for user $user_id. FeelsGoodMan\n";
		$temp = array();
		while($row = mysqli_fetch_assoc($result)){
			$temp[] = $row;
		}

		//check for empty room name
		//if is empty, put room name with opponents display name
		//example: Bob and Alice are in same room, but the room has no name
		//if request was sent by Bob, the room name will be replaced by 'Alice'
		for ($x = 0; $x < sizeof($temp); $x++){
			if($temp[$x]['room_name'] == ""){
				$room_id = $temp[$x]['room_id'];
				$sql = "SELECT display_name 
						FROM User INNER JOIN Participant ON Participant.user_id = User.user_id
						WHERE Participant.room_id = $room_id AND User.user_id <> $user_id";
				$result = dbResult($sql);

				if($result){
					$row = mysqli_fetch_assoc($result);
					$temp[$x]['room_name'] = $row['display_name'];
				}
			}
		}

		$ack_message .= json_encode($temp);
	}else{
		echo "\nUser $user_id has no chat history\n";
		$ack_message .= "NO_RESULT";
	}
	return $ack_message;
}

//depecrated -> see ChatModule.js
function GET_ROOM_MESSAGE($msg){
	echo "\nGetting room messages...\n";
	$ack_message = "GET_ROOM_MESSAGE_REPLY,";
	
	$receivedData = explode(',', $msg);	
	$room_id = $receivedData[1];
	
	$sql = "SELECT Message.*, User.display_name 
			FROM Message 
				INNER JOIN User ON Message.sender_id = User.user_id 
			WHERE room_id = $room_id AND Message.status <> 'Deleted'
			ORDER BY date_created";
				
	$result = dbResult($sql);
	if(mysqli_num_rows($result) > 0){
		echo "\nFound messages for chat room $room_id. FeelsGoodMan\n";
		$temp = array();
		while($row = mysqli_fetch_assoc($result)){
			$temp[] = $row;
		}
		$ack_message .= json_encode($temp);
	}else{
		echo "\nRoom $room_id has no messages\n";
		$ack_message .= "NO_RESULT";
	}
	return $ack_message;
}

//depecrated -> see ChatModule.js
function DELETE_CHAT_ROOM($msg){
	echo "\n Deleting chat room, user exiting group...\n";
	$ack_message = "DELETE_CHAT_ROOM_REPLY,";
	
	$receivedData = explode(',', $msg);
	$room_id = $receivedData[1];
	$user_id = $receivedData[2];
	
	$sql = "UPDATE Participant SET status = 'Left'
			WHERE room_id = $room_id AND user_id = $user_id";
	
	$result = dbResult($sql);
	if($result){
		echo "\nUser $user_id has exited from chat room $room_id\n";
		$ack_message .= "SUCCESS";
	}else{
		echo "\nUser $user_id failed to exit from chat room $room_id\n";
		$ack_message .= "NO_RESULT";
	}
	return $ack_message;
}

//depecrated -> see ChatModule.js
function GET_ROOM_INFO($msg){
	echo "\n Getting room info...\n";
	$ack_message = "GET_ROOM_INFO_REPLY,";
	
	$receivedData = explode(',', $msg);	
	$room_id = $receivedData[1];
	
	$sql = "SELECT Chat_Room.*, Participant.role, Participant.user_id, User.display_name, User.last_online, User.status
			FROM Chat_Room 
					INNER JOIN Participant ON Participant.room_id = Chat_Room.room_id 
					INNER JOIN User ON User.user_id = Participant.user_id
			WHERE Chat_Room.room_id = $room_id
			ORDER BY 
				CASE 
					WHEN Participant.role = 'Admin' THEN 1
					ELSE 2
				END, 
				CASE 
					WHEN User.status = 'Online' THEN 1
					ELSE 2
				END, 
				User.last_online DESC,
				User.display_name";
				
	$result = dbResult($sql);
	if(mysqli_num_rows($result) > 0){
		echo "\nFound info for chat room $room_id. FeelsGoodMan\n";
		$temp = array();
		while($row = mysqli_fetch_assoc($result)){
			$temp[] = $row;
		}
		$ack_message .= json_encode($temp);
	}else{
		echo "\nRoom $room_id has no messages\n";
		$ack_message .= "NO_RESULT";
	}
	return $ack_message;
}

//depecrated -> see ChatModule.js
function ADD_PEOPLE_TO_GROUP($msg){
	echo "\n Adding people to existing chat room...\n";
	$ack_message = "ADD_PEOPLE_TO_GROUP_REPLY,";
	
	$receivedData = explode(',', $msg);
	$room_id = $receivedData[1];
	$user_id = $receivedData[2];
	
	
	$sql = "insert into Participant (room_id, user_id) values ($room_id, $user_id);";
	$result = dbResult($sql);
	
	if($result){
		echo "\nUser $user_id has been added to chat room $room_id\n";
		$ack_message .= "SUCCESS";
	}else{
		echo "\nFailed to add new participant $user_id into chat room $room_id\n";
		$ack_message .= "NO_RESULT";
	}
	
	return $ack_message;
}

//depecrated -> see ChatModule.js
function REMOVE_PEOPLE_FROM_GROUP($msg){
	echo "\n Removing user from group chat, by group admin...\n";
	$ack_message = "REMOVE_PEOPLE_FROM_GROUP_REPLY,";
	
	$receivedData = explode(',', $msg);
	$room_id = $receivedData[1];
	$user_id = $receivedData[2];
	
		$sql = "UPDATE Participant SET status = 'Removed' 
		WHERE room_id = $room_id AND user_id = $user_id";
		$result = dbResult($sql);
		if($result){
			echo "\nUser $user_id has been removed from chat room $room_id\n";
			$ack_message .= "SUCCESS";
		}else{
			echo "\nFailed to remove user $user_id from chat room $room_id\n";
			echo "$result";
			$ack_message .= "NO_RESULT";
		}
	return $ack_message;
}

//depecrated -> see ChatModule.js
function GET_FRIEND_LIST_FOR_PARTICIPANT_ADD($msg){
	echo "\n Getting available friends to add into group chat as participant...\n";
	$ack_message = "GET_FRIEND_LIST_FOR_PARTICIPANT_ADD_REPLY,";
	
	$receivedData = explode(',', $msg);	
	$room_id = $receivedData[1];
	$user_id = $receivedData[2];
	
	$sql = "SELECT *
			FROM User 
			WHERE 
				User.user_id <> $user_id AND 
				User.user_id IN (
					SELECT friend_id
					FROM Friendship
					WHERE Friendship.user_id = $user_id AND Friendship.Status = 'Friend') AND 
				User.user_id NOT IN (
					SELECT user_id
					FROM Participant
					WHERE Participant.room_id = $room_id AND status = 'Active')
			ORDER BY 
				CASE 
					WHEN User.status = 'Online' THEN 1
					ELSE 2
				END, 
				User.last_online DESC,
				User.display_name";
				
	$result = dbResult($sql);
	if(mysqli_num_rows($result) > 0){
		echo "\nFound some available users to add in group chat $room_id as participant. FeelsGoodMan\n";
		$temp = array();
		while($row = mysqli_fetch_assoc($result)){
			$temp[] = $row;
		}
		$ack_message .= json_encode($temp);
	}else{
		echo "\n Did not find any available users to add into group chat $room_id as participant\n";
		$ack_message .= "NO_RESULT";
	}
	return $ack_message;
}

//depecrated -> see ChatModule.js
function GET_PARTICIPANT_LIST_REMOVE($msg){
	echo "\n Getting available participant in group chat to be removed, by admin...\n";
	$ack_message = "GET_PARTICIPANT_LIST_REMOVE_REPLY,";
	
	$receivedData = explode(',', $msg);	
	$room_id = $receivedData[1];
	$user_id = $receivedData[2];
	
	$sql = "SELECT *
			FROM User 
			WHERE 
				User.user_id <> $user_id AND 
				User.user_id IN (
					SELECT user_id
					FROM Participant
					WHERE Participant.room_id = $room_id AND status = 'Active')
			ORDER BY 
				CASE 
					WHEN User.status = 'Online' THEN 1
					ELSE 2
				END, 
				User.last_online DESC,
				User.display_name";
				
	$result = dbResult($sql);
	if(mysqli_num_rows($result) > 0){
		echo "\nFound some available participant in chat room $room_id to be removed. FeelsGoodMan\n";
		$temp = array();
		while($row = mysqli_fetch_assoc($result)){
			$temp[] = $row;
		}
		$ack_message .= json_encode($temp);
	}else{
		echo "\n Did not find any available participant in group chat $room_id to be removed\n";
		$ack_message .= "NO_RESULT";
	}
	return $ack_message;
}

function CREATE_CHAT_ROOM($msg){
	echo "\n Adding people to existing chat room...\n";
	$ack_message = "CREATE_CHAT_ROOM_REPLY,";
	
	//split received data
	$receivedData = explode(',', $msg, 4);
	$owner_id = $receivedData[1];
	$friend_id = $receivedData[2];

	$sql = "SELECT DISTINCT ChatList.room_id from (
				SELECT participant.room_id, chat_room.room_type, participant.user_id 
				from participant inner join chat_room on chat_room.room_id = participant.room_id
				where participant.room_id in (
	  				SELECT chat_room.room_id
	  				FROM Chat_Room 
	  				INNER JOIN Participant ON Chat_Room.room_id = Participant.room_id 
	  				WHERE Participant.user_id = $user_id AND Participant.status = 'Active' and room_type = 'Private'
	  				) 
  				) AS ChatList
  			WHERE (ChatList.user_id = $friend_id)";

	$result = dbResult($sql);

	if(mysqli_num_rows($result) >= 1){
		$row = mysqli_fetch_array($result);
		$chat_room_id = $row['room_id'];
		echo "\n User $owner_id and $friend_id already has chat room setup before\n";
		echo "\n Returning chat room id $chat_room_id to client \n";
		$ack_message .= $chat_room_id;
	}else{
		$sql = "INSERT INTO Chat_Room (owner_id, room_name) values ($owner_id, '');";

		$hostname_localhost = "localhost";
		$database_localhost = "ccs_master";
		$username_localhost = "ccs_main";
		$password_localhost = "123456";
		$link = mysqli_connect($hostname_localhost, $username_localhost, $password_localhost, $database_localhost);
		// Check connection
		if($link === false){
			echo("ERROR: Could not connect. " . mysqli_connect_error());
		}
		else{
			mysqli_set_charset($link, "UTF8");	
			$result = mysqli_query($link, $sql);
			
			if($result){
				$new_room_id = mysqli_insert_id($link);
				$sql = "INSERT into participant(room_id, user_id, role) values ($new_room_id, $owner_id, 'Admin')";
				if(mysqli_query($link, $sql)){
					echo "\n$owner_id has been added into chat room $new_room_id\n";
				}
				$sql = "INSERT into participant(room_id, user_id, role) values ($new_room_id, $friend_id, 'Admin')";
				if(mysqli_query($link, $sql)){
					echo "\n$friend_id has been added into chat room $new_room_id\n";
				}
				$sql = "SELECT display_name FROM User WHERE user_id = $owner_id";
				$result = mysqli_query($link, $sql);
				if($result){
					$row = mysqli_fetch_assoc($result);
					$creator_name = $row['display_name'];
					date_default_timezone_set("Asia/Kuala_Lumpur");
					$date = date('d M Y, h:i A');
					$message = "$creator_name created the chat room on $date";
					echo "\n$message\n";
					$sql = "INSERT INTO Message (message, sender_id, room_id, message_type) 
							VALUES ('$message', $owner_id, $new_room_id, 'Action')";
					if(mysqli_query($link, $sql)){
						echo "Success\n";
					}
				}
				echo "\n New chat room, ID ($new_room_id) has been created \n";
				$ack_message .= $new_room_id;
			}else{
				echo "\n Failed to create new chat room $room_name\n";
				$ack_message .= "NO_RESULT";
			}
		}
		mysqli_close($link);
	}
	
	return $ack_message;
}

function CREATE_PUBLIC_CHAT_ROOM($msg){
	echo "\n Creating public chat room...\n";
	$ack_message = "CREATE_PUBLIC_CHAT_ROOM_REPLY,";
	
	$hostname_localhost = "localhost";
	$database_localhost = "ccs_master";
	$username_localhost = "ccs_main";
	$password_localhost = "123456";
	$link = mysqli_connect($hostname_localhost, $username_localhost, $password_localhost, $database_localhost);
	// Check connection
	if($link === false){
		die ("ERROR: Could not connect. " . mysqli_connect_error());
	}

	//todo
	$receivedData = explode(',', $msg, 4);
	$user_id = $receivedData[1];
	$room_name = $receivedData[2];
	$targetUserJson = json_decode($receivedData[3], true);
	
	//step 1 create room and get new room id
	$sql = "INSERT INTO Chat_Room (owner_id, room_name, room_type) values ($user_id, '$room_name', 'Public');";
	mysqli_set_charset($link, "UTF8");
	$result = mysqli_query($link, $sql);

	//step 2 insert all selected users into the chat room as participant
	if($result){
		$new_room_id = mysqli_insert_id($link);

		//insert owner into participant
		$sql = "INSERT into participant(room_id, user_id, role) values ($new_room_id, $user_id, 'Admin')";
		if(mysqli_query($link, $sql)){
			echo "\nOwner $user_id has join the chat room";
		}
		
		for ($x = 0; $x < sizeof($targetUserJson); $x++){
			$paticipant_id = $targetUserJson[$x]['user_id'];
			$sql = "INSERT into participant(room_id, user_id, role) values ($new_room_id, $paticipant_id, 'Member')";
			
			if(!mysqli_query($link, $sql)){
				echo "\nError occured while inserting user $paticipant_id\n";
				$ack_message .= "NO_RESULT";
				break;
			}
		}

		$sql = "SELECT display_name FROM User WHERE user_id = $user_id";
		$result = mysqli_query($link, $sql);
		if($result){
			$row = mysqli_fetch_assoc($result);
			$creator_name = $row['display_name'];
			date_default_timezone_set("Asia/Kuala_Lumpur");
			$date = date('d M Y, h:i A');
			$message = "$creator_name created the group chat on $date";
			$sql = "INSERT INTO Message (message, sender_id, room_id, message_type) 
					VALUES ('$message', $user_id, $new_room_id, 'Action')";
			if(mysqli_query($link, $sql)){
				echo "Success";
			}
		}
		$ack_message .= "$new_room_id";
	}else{
		echo "\nFailed to create new public chat room\n";
		echo "$result\n";
		$ack_message .= "NO_RESULT";
	}

	mysqli_close($link);
	return $ack_message;
}
?>
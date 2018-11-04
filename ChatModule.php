<?php 
function GET_CHAT_ROOM($msg){
	echo "\nGetting chat room...\n";
	$ack_message = "GET_CHAT_ROOM_REPLY,";
	
	$receivedData = explode(',', $msg);	
	$user_id = $receivedData[1];
	
	$sql = "SELECT * 
			FROM Chat_Room INNER JOIN Participant ON Chat_Room.room_id = Participant.room_id 
			WHERE Participant.user_id = $user_id";
				
	$result = dbResult($sql);
	if(mysqli_num_rows($result) > 0){
		echo "\nFound chat room for user $user_id. FeelsGoodMan\n";
		$temp = array();
		while($row = mysqli_fetch_assoc($result)){
			$temp[] = $row;
		}
		$ack_message .= json_encode($temp);
	}else{
		echo "\nUser $user_id has no chat history\n";
		$ack_message .= "NO_RESULT";
	}
	return $ack_message;
}

function GET_ROOM_MESSAGE($msg){
	echo "\nGetting room messages...\n";
	$ack_message = "GET_ROOM_MESSAGE_REPLY,";
	
	$receivedData = explode(',', $msg);	
	$room_id = $receivedData[1];
	
	$sql = "SELECT Message.*, User.display_name 
			FROM Message INNER JOIN User ON Message.sender_id = User.user_id 
			WHERE room_id = $room_id
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

//receive room messages from client
//Note: the chat room last update column will be updated by trigger
//see ccs.sql > Trg_Insert_New_Message
function SEND_ROOM_MESSAGE($msg){
	echo "\nStoring received room message...\n";
	$ack_message = "NO_PUB,";
	
	$receivedData = explode(',', $msg, 2);	
	$messageJSON = json_decode($receivedData[1], true);
	
	$message = $messageJSON['message']; 
	$sender_id = $messageJSON['sender_id'];
	$room_id = $messageJSON['room_id'];
	
	$sql = "INSERT INTO Message (message, sender_id, room_id) 
						VALUES ('$message', $sender_id, $room_id)";
				
	$result = dbResult($sql);
	if($result){
		echo "\nMessage has been stored\n";
		$ack_message .= "SUCCESS";
	}else{
		echo "\nFailed to store message\n";
		$ack_message .= "NO_RESULT";
	}
	return $ack_message;
}

function DELETE_CHAT_ROOM($msg){
	echo "\n Deleting chat room, user exiting group...\n";
	$ack_message = "DELETE_CHAT_ROOM_REPLY,";
	
	$receivedData = explode(',', $msg);
	$room_id = $receivedData[1];
	$user_id = $receivedData[2];
	
	$sql = "DELETE FROM Participant 
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

function ADD_PEOPLE_TO_GROUP($msg){
	echo "\n Adding people to existing chat room...\n";
	$ack_message = "ADD_PEOPLE_TO_GROUP_REPLY,";
	
	$receivedData = explode(',', $msg, 2);
	$userJSON = json_decode($receivedData[1], true);
	
	//loop through the json array
	foreach($userJSON as $newParticipant){
		$user_id = $newParticipant['user_id'];
		$room_id = $newParticipant['room_id'];
		$role = $newParticipant['role'];
		
		$sql = "insert into Participant (room_id, user_id, role) values ($room_id, $user_id, $role);";
		$result = dbResult($sql);
		
		if($result){
			echo "\nUser $user_id has been added to chat room $room_id\n";
			$ack_message .= "SUCCESS";
		}else{
			//error occured, stop the function immediately
			echo "\nFailed to add new participant $user_id into chat room $room_id\n";
			return "NO_PUB, FAILED";
		}
	}
	
	return $ack_message;
}

function REMOVE_PEOPLE_FROM_GROUP($msg){
	echo "\n Removing user from group chat, by group admin...\n";
	$ack_message = "REMOVE_PEOPLE_FROM_GROUP_REPLY,";
	
	$receivedData = explode(',', $msg);
	$room_id = $receivedData[1];
	$user_id = $receivedData[2];
	
	$sql = "DELETE FROM Participant 
			WHERE room_id = $room_id AND $user_id = $user_id";
	
	$result = dbResult($sql);
	if($result){
		echo "\nUser $user_id has been removed from chat room $room_id\n";
		$ack_message .= "SUCCESS";
	}else{
		echo "\nFailed to remove user $user_id from chat room $room_id\n";
		$ack_message .= "NO_RESULT";
	}
	return $ack_message;
}
?>
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
					WHERE Participant.room_id = $room_id)
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
					WHERE Participant.room_id = $room_id)
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
	$room_name = $receivedData[2];

	$sql = "insert into Chat_Room (owner_id, room_name) values ($owner_id, $room_name);";

	$result = dbResult($sql);
	if($result){
		echo "\n New chat room $room_name has been created \n";
		$ack_message .= "SUCCESS";
	}else{
		echo "\n Failed to create new chat room $room_name\n";
		$ack_message .= "NO_RESULT";
	}

	return $ack_message;
}
?>

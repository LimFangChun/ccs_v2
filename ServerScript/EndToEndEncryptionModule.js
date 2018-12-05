var connector = require("./NodeJS_Server.js");

var UPDATE_PUBLIC_KEY = function (topic, message) {
	console.log('Updating user public_key...');
	
	var output = "UPDATE_PUBLIC_KEY_REPLY,";

    var receivedData = message.toString().split(",",3); //user_id, public_key
    var user_id = receivedData[1];
    var public_key = receivedData[2];
	
	var sql = `UPDATE User SET public_key = ? WHERE user_id = ?`;
    var inserts = [public_key, user_id];
    executeQuery(sql, inserts, topic, output);
}

var GET_PUBLIC_KEY = function (topic, message) {
	console.log('Getting public key...');
	
	var output = "GET_PUBLIC_KEY_REPLY,";

    var receivedData = message.toString().split(",",2); //user_id
    var user_id = receivedData[1];
	
	var sql = `SELECT DISTINCT user.user_id, user.public_key			
			FROM User 
			WHERE user.user_id = ? AND user.public_key IS NOT NULL`;
    var inserts = [user_id];
    executeQuery(sql, inserts, topic, output);
}

var GET_PUBLIC_KEY_ROOM = function (topic, message) {
	console.log('Getting public key...');
	
	var output = "GET_PUBLIC_KEY_REPLY,";

    var receivedData = message.toString().split(",",2); //room_id
    var room_id = receivedData[1];
	
	var sql = `SELECT user.user_id, user.public_key
			FROM User 
				INNER JOIN participant ON user.user_id=participant.user_id 
				INNER JOIN chat_room ON participant.room_id = chat_room.room_id
			WHERE chat_room.room_id = ? AND user.public_key IS NOT NULL`;
    var inserts = [room_id];
    executeQuery(sql, inserts, topic, output);
}


//currently not used, might have a chance to use in future
var GET_CHATROOM_SECRET = function (topic, message) {
	console.log('Getting chat room secret key...');
	
	var output = "GET_CHATROOM_SECRET_REPLY,";

    var receivedData = message.toString().split(",",3); //user_id, room_id
    var user_id = receivedData[1];
    var room_id = receivedData[2];
	
	var sql = `SELECT RoomSecret.secret_key
			FROM RoomSecret
			WHERE RoomSecret.user_id = ? AND RoomSecret.room_id = ? AND RoomSecret.status != 'Forbidden'`;
    var inserts = [user_id, room_id];
    executeQuery(sql, inserts, topic, output);
}

var GET_CHATROOM_SECRET_ALL = function (topic, message) {
	console.log('Getting chat room secret keys...');
	
	var output = "GET_CHATROOM_SECRET_ALL_REPLY,";

    var receivedData = message.toString().split(",",2); //user_id
    var user_id = receivedData[1];
	
	var sql = `SELECT RoomSecret.room_id, RoomSecret.secret_key
			FROM User INNER JOIN RoomSecret ON User.user_id = RoomSecret.user_id
			WHERE RoomSecret.user_id = ? AND RoomSecret.status != 'Forbidden'`;
    var inserts = [user_id];
    executeQuery(sql, inserts, topic, output);
}

var SET_CHATROOM_SECRET = function (topic, message) {
	console.log('Inserting chat room secret keys...');
	
	var output = "SET_CHATROOM_SECRET_REPLY,";

    var receivedData = message.toString().split(",",4); //room_id, user_id, secret_key
    var room_id = receivedData[1];
    var user_id = receivedData[2];
	var secret_key = receivedData[3];
	
	var sql = `INSERT INTO RoomSecret(room_id, user_id, secret_key, status) values(?, ?, ?, 'Available') ON DUPLICATE KEY UPDATE secret_key = ?, status = 'Available'`;
    var inserts = [room_id, user_id, secret_key, secret_key];
    executeQuery(sql, inserts, topic, output);
}

var GET_FORBIDDEN_SECRETS = function (topic, message) {
	console.log('Getting users with forbidden secret keys...');
	
	var output = "GET_FORBIDDEN_SECRETS_REPLY,";

    var receivedData = message.toString().split(",",2); //user_id
    var user_id = receivedData[1];
	
	var sql = `SELECT Chat_Room.room_id, user.user_id, user.public_key
			FROM User, Participant, Chat_Room, RoomSecret
			WHERE user.user_id = participant.user_id AND user.user_id != ? AND participant.user_id = RoomSecret.user_id AND participant.room_id = chat_room.room_id AND RoomSecret.status = 'Forbidden'
				AND chat_room.room_id IN(
					SELECT chat_room.room_id 
					FROM Participant, Chat_Room
					WHERE participant.room_id = chat_room.room_id AND participant.user_id = ? AND participant.role = 'Admin'
					)`;
    var inserts = [user_id,user_id];
    executeQuery(sql, inserts, topic, output);
}

function executeQuery(sql, inserts, topic, output) {
    connector.DB_CONNECTION.query(sql, inserts, function (err, result) {
        if (err) {
            console.log(err);
            output += "NO_RESULT";
        } else if (result || result.length > 0) {
            output += JSON.stringify(result);
            console.log(output.substring(0, 200) + "...");
        } else {
            output += "NO_RESULT";
        }
        connector.mqttClient.publish(topic, output);
    });
}

//export all the functions to be called in main script, NodeJS_Server.js
module.exports = {
    UPDATE_PUBLIC_KEY,
    GET_PUBLIC_KEY,
    GET_PUBLIC_KEY_ROOM,
    GET_CHATROOM_SECRET,
    GET_CHATROOM_SECRET_ALL,
    SET_CHATROOM_SECRET,
	GET_FORBIDDEN_SECRETS
}
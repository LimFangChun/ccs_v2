var connector = require("./NodeJS_Server.js");

var SEND_ROOM_MESSAGE = function (topic, message) {
    console.log('Storing received room message...');
    var output = "NO_PUB,";

    var receivedData = message.toString().substring(message.toString().indexOf(',') + 1);
    var messageJSON = JSON.parse(receivedData);

    var sql = `INSERT INTO Message (message, sender_id, room_id) 
                        VALUES (?, ?, ?)`;
    var input = [messageJSON['message'], messageJSON['sender_id'], messageJSON['room_id']];

    connector.DB_CONNECTION.query(sql, input, function (err, result) {
        if (err) {
            output += "NO_RESULT";
            console.log(err);
        } else if (result || result.length > 0) {
            output += "SUCCESS";
            console.log("Message has been stored");
        } else {
            output += "NO_RESULT";
        }

        console.log('Output: ' + output);
    });
}

var SEND_ROOM_IMAGE = function (topic, message) {
    console.log('Storing received room image message...');
    var output = "NO_PUB,";

    var receivedData = message.toString().substring(message.toString().indexOf(',') + 1);
    var messageJSON = JSON.parse(receivedData);

    var sql = `INSERT INTO Message (message, sender_id, room_id, message_type, media_path) VALUES (?, ?, ?, ?, ?)`;
    var input = ["IMAGE", messageJSON['sender_id'], messageJSON['room_id'], messageJSON['message_type'], messageJSON['mediaPath']];

    connector.DB_CONNECTION.query(sql, input, function (err, result) {
        if (err) {
            output += "NO_RESULT";
            console.log(err);
        } else if (result || result.length > 0) {
            //when success
            var sql = `INSERT INTO Message_Image (message_id, image) 
                        VALUES (?, ?)`;
            var input = [result.insertId, messageJSON['media']];

            connector.DB_CONNECTION.query(sql, input, function (err, result) {
                if (err) {
                    output += "NO_RESULT";
                    console.log(err);
                } else if (result || result.length > 0) {
                    output += "SUCCESS";
                    console.log("Image has been stored");
                } else {
                    output += "NO_RESULT";
                }

                console.log('Output: ' + output);
            });
        } else {
            output += "NO_RESULT";
        }
    });
}

var DOWNLOAD_IMAGE = function (topic, message) {
    console.log("Downloading image to " + topic);
    var output = "DOWNLOAD_IMAGE_REPLY,"

    var receivedData = message.toString().split(",");
    var messageID = receivedData[1];
    var sql = "SELECT * FROM Message_Image WHERE message_id = ?";
    var input = [messageID];

    connector.DB_CONNECTION.query(sql, input, function (err, result) {
        if (err) {
            output += "NO_RESULT";
            console.log(err);
        } else if (result && result.length > 0) {
            output += JSON.stringify(result);
            console.log("Image has been retrieved. Returning back to client");
        } else {
            output += "NO_RESULT";
            console.log(err);
        }

        connector.mqttClient.publish(topic, output);
        console.log('Output: ' + output.substr(0, 200));
    });
}

var PIN_MESSAGE = function (topic, message) {
    console.log("Pinning message...");
    var output = "PIN_MESSAGE_REPLY,";

    var receivedData = message.toString().split(",");
    var messageID = receivedData[1];
    var sql = "UPDATE Message SET status = 'Pinned' WHERE message_id = ?";
    var input = [messageID];

    connector.DB_CONNECTION.query(sql, input, function (err, result) {
        if (err) {
            output += "NO_RESULT";
            console.log(err);
        } else if (result && result.affectedRows == 1) {
            output += "SUCCESS";
            console.log(`Message ${messageID} has been pinned`);
        } else {
            output += "NO_RESULT";
            console.log(err);
        }
        connector.mqttClient.publish(topic, output);
        console.log('Output: ' + output);
    });
}

var UNPIN_MESSAGE = function (topic, message) {
    console.log("Unpinning message...");
    var output = "UNPIN_MESSAGE_REPLY,";

    var receivedData = message.toString().split(",");
    var messageID = receivedData[1];
    var sql = "UPDATE Message SET status = 'Unpinned' WHERE message_id = ?";
    var input = [messageID];

    connector.DB_CONNECTION.query(sql, input, function (err, result) {
        if (err) {
            output += "NO_RESULT";
            console.log(err);
        } else if (result && result.affectedRows == 1) {
            output += "SUCCESS";
            console.log(`Message ${messageID} has been unpinned`);
        } else {
            output += "NO_RESULT";
            console.log(err);
        }
        connector.mqttClient.publish(topic, output);
        console.log('Output: ' + output);
    });
}

var GET_PINNED_MESSAGE = function (topic, message) {
    console.log("Getting all pinned messages...");
    var output = "GET_PINNED_MESSAGE_REPLY,";

    var receivedData = message.toString().split(",");
    var roomID = receivedData[1];

    var sql = `SELECT Message.*, User.display_name 
                FROM Message 
                    INNER JOIN User ON Message.sender_id = User.user_id 
                WHERE room_id = ? AND Message.status = 'Pinned'
                ORDER BY date_created`;
    var input = [roomID];

    connector.DB_CONNECTION.query(sql, input, function (err, result) {
        if (err) {
            output += "NO_RESULT";
            console.log(err);
        } else if (result && result.length >= 1) {
            console.log(`Successfully getting all pinned messages for room ${roomID}`);
            output += JSON.stringify(result);
        } else {
            output += "NO_RESULT";
            console.log(err);
        }
        connector.mqttClient.publish(topic, output);
        console.log('Output: ' + output.substr(0, 200));
    });
}

var DELETE_MESSAGE = function (topic, message) {
    console.log("Deleting message...");
    var output = "DELETE_MESSAGE_REPLY,";
    var receivedData = message.toString().split(",");
    var messageID = receivedData[1];
    var sql = "UPDATE Message SET status = 'Deleted' WHERE message_id = ?";
    var input = [messageID];

    connector.DB_CONNECTION.query(sql, input, function (err, result) {
        if (err) {
            output += "NO_RESULT";
            console.log(err);
        } else if (result && result.affectedRows == 1) {
            output += "SUCCESS";
            console.log(`Message ${messageID} has been deleted`);
        } else {
            output += "NO_RESULT";
            console.log(err);
        }
        connector.mqttClient.publish(topic, output);
        console.log('Output: ' + output);
    });
}

var MODIFY_MESSAGE = function (topic, message) {
    console.log("Modifiying message...");
    var output = "MODIFY_MESSAGE_REPLY,";
    var receivedData = message.toString().substring(message.toString().indexOf(',') + 1);
    var messageJSON = JSON.parse(receivedData);
    var messageID = messageJSON['message_id'];
    var newMessage = messageJSON['message'];

    var sql = "UPDATE Message set message = ? where message_id = ?";
    var input = [newMessage, messageID];

    connector.DB_CONNECTION.query(sql, input, function (err, result) {
        if (err) {
            output += "NO_RESULT";
            console.log(err);
        } else if (result && result.affectedRows == 1) {
            output += "SUCCESS";
            console.log(`Message ${messageID} has been modified`);
            console.log(`New message: ${message}`);
        } else {
            output += "NO_RESULT";
            console.log(err);
        }
        connector.mqttClient.publish(topic, output);
        console.log('Output: ' + output);
    });
}

var CHAT_BOT = async function (topic, message) {
    try {
        var df = require('./DialogFlow');
        var output = "CHAT_BOT_REPLY,";
        var receivedData = message.toString().substring(message.toString().indexOf(',') + 1)

        var respond = await df.DialogFlow_Respond(receivedData);

        connector.mqttClient.publish(topic, output + respond);
    } catch (e) {
        console.log(e);
    }
}

var GET_CHAT_ROOM = function (topic, message) {
    console.log("Getting chat room");
    var output = "GET_CHAT_ROOM_REPLY,";
    var receivedData = message.toString().split(",");
    var user_id = receivedData[1];

    var sql = `SELECT Chat_Room.room_id, Chat_Room.owner_id, Chat_Room.room_type, 
                        Chat_Room.date_created, Chat_Room.last_update, Chat_Room.topic_address,
                        Participant.*, 
                CASE
                    WHEN Chat_Room.room_name IS NULL OR Chat_Room.room_name = '' 
                        THEN (SELECT display_name 
                            FROM User INNER JOIN Participant ON Participant.user_id = User.user_id
                            WHERE Participant.room_id = Chat_Room.room_id AND User.user_id <> ?)
                    ELSE room_name
                END AS 'room_name'
            FROM Chat_Room 
                INNER JOIN Participant ON Chat_Room.room_id = Participant.room_id 
            WHERE Participant.user_id = ? AND Participant.status = 'Active'
            ORDER BY last_update DESC`;
    var input = [user_id, user_id];

    connector.DB_CONNECTION.query(sql, input, function (err, result) {
        if (err) {
            console.log("Error: " + err)
            output += "NO_RESULT";
        } else if (result && result.length > 0) {
            console.log(`Found chat room for user ${user_id}. FeelsGoodMan`)
            output += JSON.stringify(result);
        } else {
            console.log("Error: " + err)
            output += "NO_RESULT";
        }

        connector.mqttClient.publish(topic, output);
        console.log("Output: " + output.substring(0, 100));
    });
}

var GET_ROOM_MESSAGE = function (topic, message) {
    console.log("Getting room message...");
    var output = "GET_ROOM_MESSAGE_REPLY,";
    var receivedData = message.toString().split(",");
    var room_id = receivedData[1];

    var sql = `SELECT Message.*, User.display_name 
                FROM Message 
                    INNER JOIN User ON Message.sender_id = User.user_id 
                WHERE room_id = ? AND Message.status <> 'Deleted'
                ORDER BY date_created`;
    var input = [room_id];

    connector.DB_CONNECTION.query(sql, input, function (err, result) {
        if (err) {
            console.log("Error: " + err)
            output += "NO_RESULT";
        } else if (result && result.length > 0) {
            console.log(`Found messages for chat room ${room_id}. FeelsGoodMan`);
            output += JSON.stringify(result);
        } else {
            console.log("Error: " + err)
            output += "NO_RESULT";
        }

        connector.mqttClient.publish(topic, output);
        console.log("Output: " + output.substring(0, 100));
    });
}

var DELETE_CHAT_ROOM = function (topic, message) {
    console.log("Deleting chat room, user exiting group...");
    var output = "DELETE_CHAT_ROOM_REPLY,";
    var receivedData = message.toString().split(",");
    var room_id = receivedData[1];
    var user_id = receivedData[2];

    var sql = `UPDATE Participant SET status = 'Left'
                WHERE room_id = ? AND user_id = ?`;
    var input = [room_id, user_id];

    connector.DB_CONNECTION.query(sql, input, function (err, result) {
        if (err) {
            console.log("Error: " + err)
            output += "NO_RESULT";
        } else if (result && result.affectedRows > 0) {
            console.log(`User ${user_id} has exited from chat room ${room_id}`);
            output += "SUCCESS";
        } else {
            console.log("Error: " + err)
            output += "NO_RESULT";
        }

        connector.mqttClient.publish(topic, output);
        console.log("Output: " + output.substring(0, 100));
    });
}

var GET_ROOM_INFO = function (topic, message) {
    console.log("Getting room info...");
    var output = "GET_ROOM_INFO_REPLY,";
    var receivedData = message.toString().split(",");
    var room_id = receivedData[1];

    var sql = `SELECT Chat_Room.*, Participant.role, Participant.user_id, User.display_name, User.last_online, User.status
    FROM Chat_Room 
            INNER JOIN Participant ON Participant.room_id = Chat_Room.room_id 
            INNER JOIN User ON User.user_id = Participant.user_id
    WHERE Chat_Room.room_id = ?
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
        User.display_name`;
    var input = [room_id];

    connector.DB_CONNECTION.query(sql, input, function (err, result) {
        if (err) {
            console.log("Error: " + err)
            output += "NO_RESULT";
        } else if (result && result.length > 0) {
            console.log(`Found info for chat room ${room_id}. FeelsGoodMan`);
            output += JSON.stringify(result);
        } else {
            console.log("Error: " + err)
            output += "NO_RESULT";
        }

        connector.mqttClient.publish(topic, output);
        console.log("Output: " + output.substring(0, 100));
    });
}

var ADD_PEOPLE_TO_GROUP = function (topic, message) {
    console.log("Adding people to existing chat room...");
    var output = "ADD_PEOPLE_TO_GROUP_REPLY,";
    var receivedData = message.toString().split(",");
    var room_id = receivedData[1];
    var user_id = receivedData[2];

    var sql = `insert into Participant (room_id, user_id) values (?, ?);`;
    var input = [room_id, user_id];

    connector.DB_CONNECTION.query(sql, input, function (err, result) {
        if (err) {
            console.log("Error: " + err)
            output += "NO_RESULT";
        } else if (result && result.affectedRows > 0) {
            console.log(`User ${user_id} has been added to chat room ${room_id}`);
            output += "SUCCESS";
        } else {
            console.log("Error: " + err)
            output += "NO_RESULT";
        }

        connector.mqttClient.publish(topic, output);
        console.log("Output: " + output.substring(0, 100));
    });
}

var REMOVE_PEOPLE_FROM_GROUP = function (topic, message) {
    console.log("Removing user from group chat, by group admin...");
    var output = "REMOVE_PEOPLE_FROM_GROUP_REPLY,";
    var receivedData = message.toString().split(",");
    var room_id = receivedData[1];
    var user_id = receivedData[2];

    var sql = `UPDATE Participant SET status = 'Removed' 
                WHERE room_id = ? AND user_id = ?`;
    var input = [room_id, user_id];

    connector.DB_CONNECTION.query(sql, input, function (err, result) {
        if (err) {
            console.log("Error: " + err);
            output += "NO_RESULT";
        } else if (result && result.affectedRows > 0) {
            console.log(`User ${user_id} has been removed from chat room ${room_id}`);
            output += "SUCCESS";
        } else {
            console.log("Error: " + err);
            output += "NO_RESULT";
        }

        connector.mqttClient.publish(topic, output);
        console.log("Output: " + output.substring(0, 100));
    });
}

var GET_FRIEND_LIST_FOR_PARTICIPANT_ADD = function(topic, message){
    console.log("Getting available friends to add into group chat as participant...");
    var output = "GET_FRIEND_LIST_FOR_PARTICIPANT_ADD_REPLY,";
    var receivedData = message.toString().split(",");
    var room_id = receivedData[1];
    var user_id = receivedData[2];

    var sql = `SELECT *
    FROM User 
    WHERE 
        User.user_id <> ? AND 
        User.user_id IN (
            SELECT friend_id
            FROM Friendship
            WHERE Friendship.user_id = ? AND Friendship.Status = 'Friend') AND 
        User.user_id NOT IN (
            SELECT user_id
            FROM Participant
            WHERE Participant.room_id = ? AND status = 'Active')
    ORDER BY 
        CASE 
            WHEN User.status = 'Online' THEN 1
            ELSE 2
        END, 
        User.last_online DESC,
        User.display_name`;
    var input = [user_id, user_id, room_id];

    connector.DB_CONNECTION.query(sql, input, function (err, result) {
        if (err) {
            console.log("Error: " + err);
            output += "NO_RESULT";
        } else if (result && result.length > 0) {
            console.log(`Found some available users to add in group chat ${room_id} as participant. FeelsGoodMan`);
            output += JSON.stringify(result);
        } else {
            console.log("Error: " + err);
            output += "NO_RESULT";
        }

        connector.mqttClient.publish(topic, output);
        console.log("Output: " + output.substring(0, 100));
    });
}

var GET_PARTICIPANT_LIST_REMOVE = function(topic, message){
    console.log("Getting available participant in group chat to be removed, by admin...");
    var output = "GET_PARTICIPANT_LIST_REMOVE_REPLY,";
    var receivedData = message.toString().split(",");
    var room_id = receivedData[1];
    var user_id = receivedData[2];

    var sql = `SELECT *
    FROM User 
    WHERE 
        User.user_id <> ? AND 
        User.user_id IN (
            SELECT user_id
            FROM Participant
            WHERE Participant.room_id = ? AND status = 'Active')
    ORDER BY 
        CASE 
            WHEN User.status = 'Online' THEN 1
            ELSE 2
        END, 
        User.last_online DESC,
        User.display_name`;
    var input = [user_id, room_id];

    connector.DB_CONNECTION.query(sql, input, function (err, result) {
        if (err) {
            console.log("Error: " + err)
            output += "NO_RESULT";
        } else if (result && result.length > 0) {
            console.log(`Found some available participant in chat room ${room_id} to be removed. FeelsGoodMan`);
            output += JSON.stringify(result);
        } else {
            console.log("Error: " + err);
            output += "NO_RESULT";
        }

        connector.mqttClient.publish(topic, output);
        console.log("Output: " + output.substring(0, 100));
    });
}

module.exports = {
    SEND_ROOM_MESSAGE,
    SEND_ROOM_IMAGE,
    DOWNLOAD_IMAGE,
    PIN_MESSAGE,
    UNPIN_MESSAGE,
    GET_PINNED_MESSAGE,
    DELETE_MESSAGE,
    MODIFY_MESSAGE,
    CHAT_BOT,
    GET_CHAT_ROOM,
    GET_ROOM_MESSAGE,
    DELETE_CHAT_ROOM,
    GET_ROOM_INFO,
    ADD_PEOPLE_TO_GROUP,
    REMOVE_PEOPLE_FROM_GROUP,
    GET_FRIEND_LIST_FOR_PARTICIPANT_ADD,
    GET_PARTICIPANT_LIST_REMOVE
}
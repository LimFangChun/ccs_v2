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
        
        connector.mqttClient.publish(topic, output+respond);
    } catch (e) {
        console.log(e);
    }
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
    CHAT_BOT
}
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
        }

        connector.mqttClient.publish(topic, output);
        console.log('Output: ' + output.substr(0, 200));
    });
}

module.exports = {
    SEND_ROOM_MESSAGE,
    SEND_ROOM_IMAGE,
    DOWNLOAD_IMAGE
}
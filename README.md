Hello there and welcome
This is my university final year project
This project is to develop a commune chat system like Facebook Messenger, WhatsApp etc
This project is mainly developed on Android platform only
This project also adopted MQTT protocol to control traffic and conserve bandwidth
The server side is using PHP as backend language
Currently, we are using XAMPP to simulate a local server
To setup this project in your device, follow these steps
  1. Download XAMPP and make sure Apache and MySQL is installed
  2. Download a free MQTT broker, we are using HiveMQ currently
  3. Activate XAMPP, turn on Apache and MySQL, turn off the rest in case they cause troubles
  4. Open the localhost web UI
      Create a new database give it any name
      Create a new user with password or use the default user (Username: "root", password: "")
      Import the database script "ccs.sql"
      
  5. Activate your MQTT broker, if you use HiveMQ, you should be able to open a web UI as well
  6. Change the ip address in server script, mqtt_server.php, 
      and in the android project -> Go to MqttHelper and change the server ip address
      Note: to check your ip address, use hotkey Windows key+R, open cmd, type ipconfig
     Also change the database name, username and password in mqtt_server.php as well
  7. Now open a cmd terminal and type something like "START C:\xampp\php\php.exe C:\xampp\htdocs\mtqq_server.php"
      I recommed to put the server script in htdocs because thats the default directory of localhost
  8. Install the android project in your phone
  9. Now it should be able to run

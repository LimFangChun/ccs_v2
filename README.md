Commune Chat System For Tunku Abdul Rahman University College, KL Main Campus

Supervisor: Miss Pua Bee Lian
Moderator: Miss Gan Lay Kee

Minimum API level: 19
Target API level: 26

<h1>Introduction</h1>
Hello there and welcome.
This is my university final year project.
This project is to develop a commune chat system like Facebook Messenger, WhatsApp etc.
This project is mainly developed on Android platform only.
This project also adopted MQTT protocol to control traffic and conserve bandwidth.
The server side is using PHP as backend language.
Currently, we are using XAMPP to simulate a local server.

<h1>Getting started</h1>
To setup this project in your device, follow these steps:<br>
  1. Download the required tools
    - XAMPP, make sure Apache and MySQL is installed
    - NodeJS
    - Android Studio
    - A text editor such as Visual Studio Code is recommended
    - A local MQTT broker is recommended (You may use a public broker such as hivemq.broker.com, but it is not secured and very slow)
    
  2. Turn on server and MQTT broker
  3. Activate XAMPP, turn on Apache and MySQL, turn off the rest in case they cause troubles
  4. Open the localhost web UI
      Create a new database name it "ccs_master"
      Create a new user with password or use the default user (Username: "root", password: "")
      Import the database script "ccs.sql"
      
  5. Activate your MQTT broker, if you use HiveMQ, you should be able to open a web UI as well
  6. (If you use a local MQTT broker) <br>
      Change the ip address in server script, mqtt_server.php and NodeJS_Server.js, 
      and in the android project -> Go to MqttHelper and change the server ip address
      Note: to check your ip address, use hotkey Windows key+R, open cmd, type ipconfig
     Also change the database name, username and password in mqtt_server.php as well
  7. Bring the Php online <br>
      Open a cmd terminal and type something like "C:\xampp\php\php.exe -f C:\xampp\htdocs\mtqq_server.php"
      I recommend to put the server script in htdocs because thats the default directory of localhost
  8. Bring Node JS online<br>
      Open a cmd terminal and type "node C:\xampp\htdocs\NodeJS_Server.js"
  9. Install the android project in your phone
  10. Now it should be able to run
  11. Login with user1, password 12345

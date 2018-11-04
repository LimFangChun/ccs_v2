package my.edu.tarc.communechat_v2.chatEngine.testData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import my.edu.tarc.communechat_v2.chatEngine.database.Chat;
import my.edu.tarc.communechat_v2.chatEngine.database.ChatRoom;

public class TestData {

    private Calendar calendar;

    public TestData () {
        calendar = Calendar.getInstance();
    }

    public List<Chat> chatTestData() {
        Chat chat = new Chat();
        List<Chat> chatList = new ArrayList<>();
        chat.setRoomId(2);
        chat.setMessage("Hi my name is jane doe, i need to meet up with you");
        chat.setSenderId("012-3456789");
        chat.setChatRoomUniqueTopic("chat.setChatRoomUniqueTopic");
        chat.setDate(calendar.get(Calendar.DAY_OF_MONTH) + " - " + calendar.get(Calendar.MONTH) + " - " + calendar.get(Calendar.YEAR));
        chatList.add(chat);

        chat = new Chat();
        chat.setRoomId(1);
        chat.setMessage("Hello, where are we going to meet");
        chat.setSenderId("John Doe");
        chat.setDate(calendar.get(Calendar.DAY_OF_MONTH) + " - " + calendar.get(Calendar.MONTH) + " - " + calendar.get(Calendar.YEAR));
        chat.setChatRoomUniqueTopic("chat.setChatRoomUniqueTopic");
        chatList.add(chat);

        return chatList;
    }

    public List<ChatRoom> chatRoomTestData() {
        ChatRoom chatRoom = new ChatRoom();

        List<ChatRoom> chatRoomList = new ArrayList<>();

        chatRoom.setName("John Doe");
        chatRoom.setLatestMessage("Hello");
        chatRoom.setDateTimeMessageReceived(calendar.get(Calendar.DAY_OF_MONTH) + " - " + calendar.get(Calendar.MONTH) + " - " + calendar.get(Calendar.YEAR));
        chatRoom.setId(1);
        chatRoom.setChatRoomUniqueTopic("Group Creator Id + Date & Time Unique Value");
        chatRoom.setChatRoomType(ChatRoom.PRIVATE_CHAT_ROOM);
        //chatRoom.setStatus(ChatRoom.ACTIVE_STATUS);
        chatRoomList.add(chatRoom);

        chatRoom = new ChatRoom();
        chatRoom.setName("Jane Doe");
        chatRoom.setLatestMessage("Bye");
        chatRoom.setDateTimeMessageReceived(calendar.get(Calendar.DAY_OF_MONTH) + " - " + calendar.get(Calendar.MONTH) + " - " + calendar.get(Calendar.YEAR));
        chatRoom.setId(2);
        chatRoom.setChatRoomUniqueTopic("Group Creator Id + Date & Time Unique Value2");
        //chatRoom.setStatus(ChatRoom.ACTIVE_STATUS);
        chatRoomList.add(chatRoom);

        return chatRoomList;
    }


}

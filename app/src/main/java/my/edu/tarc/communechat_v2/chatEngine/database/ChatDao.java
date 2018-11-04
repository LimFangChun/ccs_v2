package my.edu.tarc.communechat_v2.chatEngine.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ChatDao {

    @Query("SELECT * FROM Chat WHERE chatRoomUniqueTopic =:uniqueTopic AND message =:chatRoomMessage AND senderId =:chatRoomSenderId AND date=:dateTime")
    boolean checkRepetition(String uniqueTopic, String chatRoomMessage, String dateTime, String chatRoomSenderId);

    @Insert
    void insertAll(Chat[] chatArray);

    @Insert
    void insertAll(List<Chat> chatList);

    @Insert
    void insert(Chat chat);

    @Query("SELECT * FROM Chat WHERE roomId = :chatRoomId ORDER BY comparingDateTime DESC")
    List<Chat> getChatFromChatRoom(long chatRoomId);

    @Query("DELETE FROM Chat WHERE id = :id")
    void deleteChat(long id);

    @Query("DELETE FROM Chat WHERE roomId = :roomId")
    void deleteAllChatInChatRoom(long roomId);

    @Query("DELETE FROM Chat WHERE roomId IN (:roomId)")
    void deleteAllChatInChatRoom(List<Long> roomId);

    @Delete
    void deleteAllSelectedChat(List<Chat> chatList);

}

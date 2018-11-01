package my.edu.tarc.communechat_v2.chatEngine.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ChatRoomDao {

    @Insert
    void insertAll(ChatRoom[] chatRoomArray);

    @Insert
    void insertAll(List<ChatRoom> chatRoomList);

    @Insert
    long insert(ChatRoom chatRoom);

    @Update
    void updateChatRoom(ChatRoom chatRoom);

    @Query("SELECT * FROM ChatRoom WHERE chatRoomUniqueTopic = :topic")
    ChatRoom get(String topic);

    @Query("SELECT * FROM ChatRoom WHERE chatRoomUniqueTopic = :chatRoomTopic AND chatRoomType = :chatRoomType")
    ChatRoom get(String chatRoomTopic, String chatRoomType);

    @Query("SELECT * FROM ChatRoom WHERE id = :chatRoomId")
    ChatRoom get(long chatRoomId);

    @Query("SELECT * FROM ChatRoom WHERE latestMessage != '' OR chatRoomType =:groupTypeFilter ORDER BY comparingDateTime DESC")
    List<ChatRoom> getAll(String groupTypeFilter);

    @Query("SELECT * FROM ChatRoom WHERE chatRoomType =:type AND chatRoomUniqueTopic =:topic")
    ChatRoom getGroupChatRoom(String type, String topic);

    @Query("SELECT * FROM ChatRoom WHERE chatRoomType =:chatRoomType AND status =:chatRoomStatus")
    List<ChatRoom> getSubscriptionChatRoom(String chatRoomType, String chatRoomStatus);

    @Query("SELECT * FROM ChatRoom WHERE chatRoomType != :chatRoomType AND status !=:chatRoomStatus")
    List<ChatRoom> getUnsubscriptionChatRoom(String chatRoomType, String chatRoomStatus);

    @Query("DELETE FROM ChatRoom WHERE id = :id")
    void delete(long id);

    @Delete
    void delete(ChatRoom chatRoom);

    @Delete
    void deleteManyChatRoom(List<ChatRoom> chatRoomList);

}

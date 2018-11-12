package my.edu.tarc.communechat_v2.LocalDatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import my.edu.tarc.communechat_v2.model.Chat_Room;

@Dao
public interface ChatRoomDao {
    @Query("SELECT * FROM Chat_Room ORDER BY last_update DESC")
    List<Chat_Room> getAllChatRoom();

    @Insert
    void insertNewChatRoom(Chat_Room chatRoom);

    @Insert
    void insertChatRooms(Chat_Room... chatRooms);

    @Delete
    void deleteMultipleChatRoom(Chat_Room... chatRooms);

    @Delete
    void deleteChatRoom(Chat_Room chatRoom);

    @Query("DELETE FROM Chat_Room")
    void deleteAllChatRoom();
}

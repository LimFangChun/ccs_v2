package my.edu.tarc.communechat_v2.LocalDatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import my.edu.tarc.communechat_v2.model.Message;

@Dao
public interface MessageDao {
    @Query("SELECT * FROM Message WHERE room_id = :room_id ORDER BY date_created DESC")
    List<Message> getMessageByRoomID(int room_id);

    @Insert
    void insertNewMessage(Message message);

    @Insert
    void insertMessages(Message... messages);

    @Query("DELETE FROM Message")
    void deleteAllMessage();
}

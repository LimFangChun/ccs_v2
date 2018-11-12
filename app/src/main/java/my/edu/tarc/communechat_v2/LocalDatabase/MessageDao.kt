package my.edu.tarc.communechat_v2.LocalDatabase

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

import my.edu.tarc.communechat_v2.model.Message

@Dao
interface MessageDao {
    @Query("SELECT * FROM Message WHERE room_id = :room_id ORDER BY date_created DESC")
    fun getMessageByRoomID(room_id: Int): List<Message>

    @Insert
    fun insertNewMessage(message: Message)

    @Insert
    fun insertMessages(vararg messages: Message)

    @Query("DELETE FROM Message")
    fun deleteAllMessage()
}

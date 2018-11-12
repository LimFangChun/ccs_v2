package my.edu.tarc.communechat_v2.LocalDatabase

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

import my.edu.tarc.communechat_v2.model.Chat_Room

@Dao
interface ChatRoomDao {
    @get:Query("SELECT * FROM Chat_Room ORDER BY last_update DESC")
    val allChatRoom: List<Chat_Room>

    @Insert
    fun insertNewChatRoom(chatRoom: Chat_Room)

    @Insert
    fun insertChatRooms(vararg chatRooms: Chat_Room)

    @Delete
    fun deleteMultipleChatRoom(vararg chatRooms: Chat_Room)

    @Delete
    fun deleteChatRoom(chatRoom: Chat_Room)

    @Query("DELETE FROM Chat_Room")
    fun deleteAllChatRoom()
}

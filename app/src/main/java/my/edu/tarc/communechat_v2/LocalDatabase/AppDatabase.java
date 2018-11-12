package my.edu.tarc.communechat_v2.LocalDatabase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import my.edu.tarc.communechat_v2.model.Chat_Room;
import my.edu.tarc.communechat_v2.model.Message;

@Database(entities = {Message.class, Chat_Room.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract ChatRoomDao chatRoomDao();

    public abstract MessageDao messageDao();
}

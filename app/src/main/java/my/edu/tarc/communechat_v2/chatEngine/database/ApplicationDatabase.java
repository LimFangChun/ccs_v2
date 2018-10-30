package my.edu.tarc.communechat_v2.chatEngine.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

@Database(entities = {Chat.class, ChatRoom.class}, version = 1)
public abstract class ApplicationDatabase extends RoomDatabase {

    private static ApplicationDatabase sSavedInstanceApplicationDatabase;

    private static final String APPLICATION_DATABASE_NAME = "Yumenai Social Network";

    public static ApplicationDatabase build(Context context) {

        if (sSavedInstanceApplicationDatabase == null) {
            sSavedInstanceApplicationDatabase = Room.databaseBuilder(
                    context, ApplicationDatabase.class, APPLICATION_DATABASE_NAME
            ).build();
        }

        return sSavedInstanceApplicationDatabase;
    }

    public abstract ChatDao chatDao();
    public abstract ChatRoomDao chatRoomDao();

    // Use for upgrading database from version 1 to 2
    public static Migration migration_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
        }
    };

}


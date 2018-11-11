package my.edu.tarc.communechat_v2.LocalDatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import my.edu.tarc.communechat_v2.model.User;

@Dao
public interface UserDao {
    @Query("SELECT * FROM User WHERE user_id = :user_id")
    User getUserByID(int user_id);

    @Insert
    void insertUser(User user);

    @Insert
    void insertUsers(User... users);

    @Delete
    void deleteUser(User user);

    @Update
    void updateUser(User user);
}

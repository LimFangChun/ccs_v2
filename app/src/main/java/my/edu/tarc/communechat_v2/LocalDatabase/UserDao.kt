package my.edu.tarc.communechat_v2.LocalDatabase

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update

import my.edu.tarc.communechat_v2.model.User

@Dao
interface UserDao {
    @Query("SELECT * FROM User WHERE user_id = :user_id")
    fun getUserByID(user_id: Int): User

    @Insert
    fun insertUser(user: User)

    @Insert
    fun insertUsers(vararg users: User)

    @Delete
    fun deleteUser(user: User)

    @Update
    fun updateUser(user: User)
}

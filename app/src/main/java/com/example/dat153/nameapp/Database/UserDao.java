package com.example.dat153.nameapp.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by mrreitet on 01.02.2018.
 */

@Dao
public interface UserDao {

    @Query("SELECT * FROM User")
    List<User> getAll();

    @Query("SELECT name FROM USER")
    List<String> loadAllUserNames();

    @Query("select * from User where name = :name")
    User loadUserByName(String name);

    @Insert(onConflict = REPLACE)
    void insertUser(User users);

    @Delete
    void deleteUser(User user);
}

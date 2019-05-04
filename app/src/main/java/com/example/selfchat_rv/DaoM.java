package com.example.selfchat_rv;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface DaoM {
    @Insert
    void insert(Messege msg);

    @Query("DELETE FROM messages")
    void deleteAll();

    @Query("SELECT * from messages")
    List<Messege> getAllMsgs();

    @Delete
    void delete(Messege msg);
}

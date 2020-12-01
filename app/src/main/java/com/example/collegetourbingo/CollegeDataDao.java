package com.example.collegetourbingo;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CollegeDataDao {
    @Query("INSERT INTO collegeData (name, gameID, bingos, firstBingoTime, termsMarked) VALUES (:name, (SELECT COUNT(*) FROM collegeData WHERE name = :name), :bingos, :firstBingoTime, :termsMarked)")
    void add(String name, int bingos, long firstBingoTime, String termsMarked);

    @Query("SELECT * FROM collegeData")
    List<CollegeData> getAll();

    @Query("SELECT * FROM collegeData WHERE gameID = 0")
    List<CollegeData> getOnePerCollege();

    @Query("SELECT * FROM collegeData WHERE name = :name")
    List<CollegeData> getFromCollege(String name);
}

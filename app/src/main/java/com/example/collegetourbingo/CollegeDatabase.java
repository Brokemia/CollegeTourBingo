package com.example.collegetourbingo;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {CollegeData.class}, version = 1, exportSchema = false)
public abstract class CollegeDatabase extends RoomDatabase {
    public abstract CollegeDataDao collegeDataDao();
}

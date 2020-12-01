package com.example.collegetourbingo;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "collegeData")
public class CollegeData implements Comparable<CollegeData> {
    // It got annoyed at me for not having a PrimaryKey so I added this
    // Entries can be uniquely identified by name and gameID anyway, so a PrimaryKey is unnecessary
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "gameID")
    public int gameID;

    @ColumnInfo(name = "bingos")
    public int bingos;

    @ColumnInfo(name = "firstBingoTime")
    public long firstBingoTime;

    @ColumnInfo(name = "termsMarked")
    public String termsMarked;

    @Override
    public int compareTo(CollegeData collegeData) {
        return name.compareTo(collegeData.name);
    }
}

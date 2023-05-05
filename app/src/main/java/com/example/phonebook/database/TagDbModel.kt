package com.example.phonebook.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TagDbModel(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "hex") val hex: String,
    @ColumnInfo(name = "name") val name: String

){
    companion object {
        val DEFAULT_TAGS = listOf(
            TagDbModel(1,"#BF5B4B","Mobile"),
            TagDbModel(2,"#D7897E","Home"),
            TagDbModel(3,"#F0D0C1","Work"),
            TagDbModel(4,"#EEB182","School"),
            TagDbModel(5,"#CA8462","Pager"),
            TagDbModel(6,"#CA8462","Home Fax"),
            TagDbModel(7,"#925946","Work Fax"),
            TagDbModel(8,"#C55F4E","Other")

        )
        val DEFAULT_TAG = DEFAULT_TAGS[0]
    }
}

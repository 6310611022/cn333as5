package com.example.phonebook.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PhoneBookDbModel(
    @PrimaryKey(autoGenerate = true) val id: Long =0 ,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "phone") val phone: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "tag_id") val tagId: Long,
    @ColumnInfo(name = "in_trash") val isInTrash: Boolean
){
    companion object{
        val DEFAULT_PHONES = listOf(
            PhoneBookDbModel(1,"p1","0123456789","detail",1,false),
            PhoneBookDbModel(2,"p2","1234567890","detail",2,false),
            PhoneBookDbModel(3,"p3","2345678901","detail",3,false),
            PhoneBookDbModel(4,"p4","3456789012","detail",4,false),
            PhoneBookDbModel(5,"p5","4567890123","detail",5,false),
            PhoneBookDbModel(6,"p6","5678901234","detail",6,false),
            PhoneBookDbModel(7,"p7","6789012345","detail",7,false),
            PhoneBookDbModel(8,"p8","7890123456","detail",8,false),

        )
    }
}

package com.drag0n.ideaplatform.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.temporal.TemporalAmount

@Entity
data class Item(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo val name: String,
    @ColumnInfo val time: Int,
    @ColumnInfo val tags: String,
    @ColumnInfo var amount: Int,

    )

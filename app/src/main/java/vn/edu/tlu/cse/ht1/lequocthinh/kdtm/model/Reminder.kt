// vn/edu/tlu/cse/ht1/lequocthinh/kdtm/model/Reminder.kt

package vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminder")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val hour: Int,
    val minute: Int,
    val title: String = "Giờ học tới rồi!"
)
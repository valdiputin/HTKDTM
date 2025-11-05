// vn/edu/tlu/cse/ht1/lequocthinh/kdtm/data/ReminderDatabase.kt

package vn.edu.tlu.cse.ht1.lequocthinh.kdtm.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model.Reminder

@Database(entities = [Reminder::class], version = 1, exportSchema = false)
abstract class ReminderDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: ReminderDatabase? = null

        fun getDatabase(context: Context): ReminderDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReminderDatabase::class.java,
                    "reminder_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
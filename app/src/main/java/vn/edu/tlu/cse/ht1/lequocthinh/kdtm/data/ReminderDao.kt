// vn/edu/tlu/cse/ht1/lequocthinh/kdtm/data/ReminderDao.kt

package vn.edu.tlu.cse.ht1.lequocthinh.kdtm.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model.Reminder

@Dao
interface ReminderDao {
    @Insert
    suspend fun insert(reminder: Reminder): Long // Trả về ID

    @Query("SELECT * FROM reminder ORDER BY hour, minute")
    suspend fun getAll(): List<Reminder>

    @Delete
    suspend fun delete(reminder: Reminder) // Xóa 1 cái

    @Query("DELETE FROM reminder")
    suspend fun deleteAll() // Xóa tất cả
}
// vn/edu/tlu/cse/ht1/lequocthinh/kdtm/service/ReminderActivity.kt

package vn.edu.tlu.cse.ht1.lequocthinh.kdtm.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.R
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.data.ReminderDao
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.data.ReminderDatabase
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model.Reminder
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.receiver.ReminderReceiver
import java.util.*

class ReminderActivity : AppCompatActivity() {

    // Thay đổi từ TextView sang RecyclerView
    private lateinit var reminderRecyclerView: RecyclerView
    private lateinit var reminderAdapter: ReminderAdapter

    private val dao: ReminderDao by lazy { ReminderDatabase.getDatabase(this).reminderDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        val btnAdd = findViewById<Button>(R.id.btnAddReminder)

        // Cài đặt Adapter và RecyclerView
        setupRecyclerView()

        loadData()
        btnAdd.setOnClickListener { showTimePicker() }

        // Yêu cầu quyền POST_NOTIFICATIONS cho Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101)
        }
    }

    private fun setupRecyclerView() {
        // Khởi tạo adapter và truyền hàm xử lý xóa
        reminderAdapter = ReminderAdapter { reminder ->
            // Đây là nơi xử lý khi người dùng nhấn nút "Xóa" trên 1 hàng
            deleteSingleReminder(reminder)
        }

        // Tìm RecyclerView và cài đặt
        reminderRecyclerView = findViewById(R.id.reminderRecyclerView)
        reminderRecyclerView.layoutManager = LinearLayoutManager(this)
        reminderRecyclerView.adapter = reminderAdapter
    }

    private fun showTimePicker() {
        val cal = Calendar.getInstance()
        TimePickerDialog(
            this,
            { _, hour, minute ->
                val reminder = Reminder(hour = hour, minute = minute)
                lifecycleScope.launch {
                    val newId = dao.insert(reminder)
                    val scheduledReminder = reminder.copy(id = newId.toInt())
                    schedule(scheduledReminder)
                    loadData() // Tải lại danh sách
                }
            },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun schedule(reminder: Reminder) {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, reminder.hour)
            set(Calendar.MINUTE, reminder.minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) add(Calendar.DAY_OF_MONTH, 1)
        }

        val intent = Intent(this, ReminderReceiver::class.java).apply {
            putExtra("title", reminder.title)
            // SỬA LỖI: Truyền ID cho Receiver
            putExtra("notification_id", reminder.id)
        }

        val pending = PendingIntent.getBroadcast(
            this, reminder.id, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Xử lý quyền SCHEDULE_EXACT_ALARM cho Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarm.canScheduleExactAlarms()) {
                alarm.setExact(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pending)
            } else {
                Toast.makeText(this, "Cần cấp quyền để đặt báo thức chính xác", Toast.LENGTH_LONG).show()
                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).also {
                    startActivity(it)
                }
            }
        } else {
            alarm.setExact(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pending)
        }
    }

    // Hàm xóa 1 cái
    private fun deleteSingleReminder(reminder: Reminder) {
        lifecycleScope.launch {
            dao.delete(reminder) // Xóa khỏi CSDL
            cancelAlarm(reminder) // Hủy báo thức
            loadData() // Tải lại danh sách
            Toast.makeText(this@ReminderActivity, "Đã xóa giờ học", Toast.LENGTH_SHORT).show()
        }
    }

    // Hàm hủy báo thức
    private fun cancelAlarm(reminder: Reminder) {
        val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ReminderReceiver::class.java)
        val pending = PendingIntent.getBroadcast(
            this,
            reminder.id, // Dùng ID gốc
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarm.cancel(pending)
    }

    // Cập nhật loadData để dùng Adapter
    private fun loadData() {
        lifecycleScope.launch {
            val reminders = dao.getAll()
            // Nộp danh sách cho adapter, nó sẽ tự cập nhật
            reminderAdapter.submitList(reminders)
        }
    }
}
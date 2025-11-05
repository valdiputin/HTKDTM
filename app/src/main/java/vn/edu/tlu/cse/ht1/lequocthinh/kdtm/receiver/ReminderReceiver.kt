// vn/edu/tlu/cse/ht1/lequocthinh/kdtm/receiver/ReminderReceiver.kt

package vn.edu.tlu.cse.ht1.lequocthinh.kdtm.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.R
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.service.ReminderActivity

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val title = intent?.getStringExtra("title") ?: "Đến giờ học rồi!"

        // SỬA LỖI: Lấy ID từ Intent thay vì dùng thời gian
        val notificationId = intent?.getIntExtra("notification_id", 0) ?: 0

        val channelId = "reminder_channel"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Reminders",
            NotificationManager.IMPORTANCE_HIGH
        )
        manager.createNotificationChannel(channel)

        val activityIntent = Intent(context, ReminderActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, activityIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Nhắc học")
            .setContentText(title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Tự xóa khi nhấn vào
            .build()

        // SỬA LỖI: Dùng ID duy nhất
        manager.notify(notificationId, notification)
    }
}
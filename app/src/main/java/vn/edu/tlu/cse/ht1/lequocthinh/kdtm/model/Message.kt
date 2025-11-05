// vn/edu/tlu/cse/ht1/lequocthinh/kdtm/model/Message.kt
package vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model

import android.graphics.Bitmap // 👈 1. Phải có dòng import này

data class Message(
    val text: String,
    val isUser: Boolean,
    val image: Bitmap? = null, // 👈 2. Phải có trường này
    val timestamp: Long = System.currentTimeMillis()
)
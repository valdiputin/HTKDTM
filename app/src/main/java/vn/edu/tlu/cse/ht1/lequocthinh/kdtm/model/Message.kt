// Message.kt
package vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model

data class Message(
    val text: String,
    val isUser: Boolean, // True nếu là tin nhắn người dùng, False nếu là AI
    val timestamp: Long = System.currentTimeMillis()
)
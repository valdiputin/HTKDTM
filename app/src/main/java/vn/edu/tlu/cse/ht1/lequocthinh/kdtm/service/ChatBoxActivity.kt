
package vn.edu.tlu.cse.ht1.lequocthinh.kdtm

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.Chat
import kotlinx.coroutines.*
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.adapter.MessageAdapter
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model.Message

class ChatBoxActivity : BaseActivity() {

    private lateinit var etMessageInput: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var recyclerViewChat: RecyclerView
    private lateinit var chatLoadingBar: ProgressBar

    private lateinit var chatAdapter: MessageAdapter
    private val messages = mutableListOf<Message>()

    // Đối tượng Chat để duy trì bối cảnh hội thoại
    private lateinit var chatSession: Chat

    companion object {
        const val GEMINI_API_KEY = "AIzaSyDWNQVAX2PwvFe7b0yY1Ce2QobrTJQRk2Y" // Khóa API của bạn
        const val GEMINI_MODEL = "gemini-2.5-flash"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_box)

        etMessageInput = findViewById(R.id.etMessageInput)
        btnSend = findViewById(R.id.btnSend)
        recyclerViewChat = findViewById(R.id.recyclerViewChat)
        chatLoadingBar = findViewById(R.id.chatLoadingBar)

        setupChatAdapter()
        initializeGeminiChat()

        btnSend.setOnClickListener {
            sendMessage()
        }
    }

    private fun setupChatAdapter() {
        chatAdapter = MessageAdapter(messages)
        recyclerViewChat.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true // Hiển thị tin nhắn mới nhất ở dưới cùng
        }
        recyclerViewChat.adapter = chatAdapter
    }

    private fun initializeGeminiChat() {
        // Cần có prompt hệ thống để định hình vai trò của AI
        val systemInstruction = "Bạn là Gia sư AI thân thiện và kiên nhẫn, chuyên về các khóa học lập trình và công nghệ. Hãy trả lời ngắn gọn, tập trung vào kiến thức công nghệ và luôn giữ thái độ tích cực."

        // Tạo đối tượng GenerativeModel và Chat session
        val model = GenerativeModel(
            GEMINI_MODEL,
            GEMINI_API_KEY,
            // Thêm cấu hình nếu cần
            // config = generateModelConfiguration { 
            //     systemInstruction = systemInstruction
            // }
        )

        // Bắt đầu một Chat session mới (sẽ ghi nhớ lịch sử)
        chatSession = model.startChat()

        // Tin nhắn chào mừng ban đầu
        addMessage(Message("Chào bạn! Tôi là Gia sư AI. Bạn có thắc mắc gì về các lớp học hoặc công nghệ không?", false))
    }

    private fun sendMessage() {
        val userMessage = etMessageInput.text.toString().trim()
        if (userMessage.isBlank()) return

        // 1. Thêm tin nhắn người dùng và làm sạch input
        addMessage(Message(userMessage, true))
        etMessageInput.setText("")
        setLoading(true)

        // 2. Gọi AI trong Coroutine
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Thêm tin nhắn chờ phản hồi (AI)
                addMessage(Message("...", false))

                // Gửi tin nhắn qua Chat Session để duy trì bối cảnh
                val response = withContext(Dispatchers.IO) {
                    chatSession.sendMessage(userMessage)
                }

                // 3. Cập nhật tin nhắn AI cuối cùng
                updateLastMessage(response.text ?: "Lỗi: Không nhận được phản hồi hợp lệ.")

            } catch (e: Exception) {
                updateLastMessage("Xin lỗi, đã xảy ra lỗi kết nối: ${e.message}")
            } finally {
                setLoading(false)
            }
        }
    }

    // --- Các hàm tiện ích để cập nhật UI ---

    private fun addMessage(message: Message) {
        messages.add(message)
        chatAdapter.notifyItemInserted(messages.size - 1)
        recyclerViewChat.scrollToPosition(messages.size - 1)
    }

    private fun updateLastMessage(text: String) {
        if (messages.isNotEmpty()) {
            val lastIndex = messages.size - 1
            messages[lastIndex] = messages[lastIndex].copy(text = text)
            chatAdapter.notifyItemChanged(lastIndex)
            recyclerViewChat.scrollToPosition(lastIndex)
        }
    }

    private fun setLoading(isLoading: Boolean) {
        chatLoadingBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        btnSend.isEnabled = !isLoading
    }
}
package vn.edu.tlu.cse.ht1.lequocthinh.kdtm

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.adapter.MessageAdapter
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model.Message

class ChatBoxActivity : BaseActivity() {

    private lateinit var etMessageInput: EditText
    private lateinit var btnSend: ImageButton
    private lateinit var btnUploadImage: ImageButton
    private lateinit var recyclerViewChat: RecyclerView
    private lateinit var chatLoadingBar: ProgressBar

    private lateinit var chatAdapter: MessageAdapter
    private val messages = mutableListOf<Message>()

    private lateinit var generativeModel: GenerativeModel
    private var selectedBitmap: Bitmap? = null

    companion object {
        const val GEMINI_API_KEY = "AIzaSyDWNQVAX2PwvFe7b0yY1Ce2QobrTJQRk2Y"
        const val GEMINI_MODEL = "gemini-2.5-flash"

    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = contentResolver.openInputStream(it)
                selectedBitmap = BitmapFactory.decodeStream(inputStream)
                btnUploadImage.setImageBitmap(selectedBitmap)
            } catch (e: Exception) {
                Log.e("ChatBoxActivity", "Không thể load ảnh", e)
                Toast.makeText(this, "Không thể load ảnh", Toast.LENGTH_SHORT).show()
                resetUploadButtonIcon()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_box)

        etMessageInput = findViewById(R.id.etMessageInput)
        btnSend = findViewById(R.id.btnSend)
        btnUploadImage = findViewById(R.id.btnUploadImage)
        recyclerViewChat = findViewById(R.id.recyclerViewChat)
        chatLoadingBar = findViewById(R.id.chatLoadingBar)

        setupChatAdapter()
        initializeGemini()

        btnSend.setOnClickListener { sendMessage() }
        btnUploadImage.setOnClickListener { pickImageLauncher.launch("image/*") }
    }

    private fun setupChatAdapter() {
        chatAdapter = MessageAdapter(messages)
        recyclerViewChat.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        recyclerViewChat.adapter = chatAdapter
    }

    private fun initializeGemini() {
        generativeModel = GenerativeModel(
            modelName = GEMINI_MODEL,
            apiKey = GEMINI_API_KEY
        )
        addMessage(Message("Chào bạn! Tôi là Gia sư AI. Bạn có thể hỏi tôi hoặc gửi ảnh để tôi phân tích nhé!", false))
    }

    private fun sendMessage() {
        val userMessageText = etMessageInput.text.toString().trim()
        val imageToSend = selectedBitmap

        if (userMessageText.isBlank() && imageToSend == null) {
            Toast.makeText(this, "Vui lòng nhập câu hỏi hoặc chọn ảnh", Toast.LENGTH_SHORT).show()
            return
        }

        val userMessage = Message(userMessageText, true, imageToSend)
        addMessage(userMessage)
        etMessageInput.setText("")
        selectedBitmap = null
        resetUploadButtonIcon()
        setLoading(true)

        lifecycleScope.launch {
            try {
                addMessage(Message("...", false, null))

                val history = messages
                    .filter { it.text != "..." }
                    .map { msg ->
                        content(role = if (msg.isUser) "user" else "model") {
                            msg.image?.let { image(it) }
                            text(msg.text)
                        }
                    }

                val response = withContext(Dispatchers.IO) {
                    generativeModel.generateContent(*history.toTypedArray())
                }

                updateLastMessage(response.text ?: "Không có phản hồi từ Gemini.")

            } catch (e: Exception) {
                Log.e("ChatBoxActivity", "Lỗi API Gemini", e)
                updateLastMessage("Xin lỗi, đã xảy ra lỗi: ${e.message}")
            } finally {
                setLoading(false)
            }
        }
    }

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
        btnUploadImage.isEnabled = !isLoading
    }

    private fun resetUploadButtonIcon() {
        btnUploadImage.setImageResource(android.R.drawable.ic_menu_gallery)
    }
}

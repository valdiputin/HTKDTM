package vn.edu.tlu.cse.ht1.lequocthinh.kdtm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.R
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model.Message

class MessageAdapter(private val messages: List<Message>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_AI = 2
    }

    override fun getItemViewType(position: Int): Int {
        // Model 'Message' cần có trường 'isUser: Boolean'
        return if (messages[position].isUser) VIEW_TYPE_USER else VIEW_TYPE_AI
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layout = if (viewType == VIEW_TYPE_USER) {
            R.layout.item_chat_user // Layout cho tin nhắn người dùng (có ImageView)
        } else {
            R.layout.item_chat_ai // Layout cho tin nhắn AI (chỉ có TextView)
        }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int = messages.size

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)

        // Dòng này sẽ hết lỗi sau khi bạn sửa file item_chat_user.xml
        private val ivMessageImage: ImageView? = itemView.findViewById(R.id.ivMessageImage)

        fun bind(message: Message) {

            // 1. Xử lý Văn bản (Text)
            if (message.text.isNotEmpty()) {
                tvMessage.text = message.text
                tvMessage.visibility = View.VISIBLE
            } else {
                tvMessage.visibility = View.GONE
            }

            // 2. Xử lý Ảnh (Image)
            ivMessageImage?.let { imageView ->
                // Các dòng này sẽ hết lỗi sau khi bạn sửa file Message.kt
                if (message.image != null) {
                    imageView.setImageBitmap(message.image)
                    imageView.visibility = View.VISIBLE
                } else {
                    imageView.visibility = View.GONE
                }
            }
        }
    }
}
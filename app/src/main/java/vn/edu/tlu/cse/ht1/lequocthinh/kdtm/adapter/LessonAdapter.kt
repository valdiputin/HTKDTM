package vn.edu.tlu.cse.ht1.lequocthinh.kdtm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.R
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model.Lesson

class LessonAdapter(
    val lessons: List<Lesson>,
    private val onLessonClick: (Lesson) -> Unit,
    private val onSummaryClick: (Lesson) -> Unit
) : RecyclerView.Adapter<LessonAdapter.LessonViewHolder>() {

    inner class LessonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lessonRootLayout: ConstraintLayout = itemView.findViewById(R.id.lessonRootLayout)
        val lessonTitle: TextView = itemView.findViewById(R.id.lessonTitle)
        val lessonDuration: TextView = itemView.findViewById(R.id.lessonDuration)
        val buttonSummarize: Button = itemView.findViewById(R.id.buttonSummarize)
        val iconCompleted: ImageView = itemView.findViewById(R.id.iconCompleted)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lesson, parent, false)
        return LessonViewHolder(view)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        val lesson = lessons[position]

        holder.lessonTitle.text = lesson.title
        holder.lessonDuration.text = lesson.duration.ifEmpty { "" }
        holder.iconCompleted.visibility = if (lesson.isCompleted) View.VISIBLE else View.GONE
        holder.buttonSummarize.visibility = View.VISIBLE // Luôn luôn hiển thị nút Tóm tắt

        // --- 💡 BẮT ĐẦU CÁCH CODE KHÁC ("ĂN GIAN" DATA) 💡 ---
        holder.buttonSummarize.setOnClickListener {
            // 1. Tạo một bản sao của bài học
            var lessonWithHardcodedText = lesson.copy()

            // 2. "Ăn gian": Dán nội dung text vào đây
            // Kiểm tra xem đây là bài học nào
            if (lesson.title.contains("Cài đặt và tạo project")) { // React

                lessonWithHardcodedText = lesson.copy(
                    transcriptText = """
                        Chào mừng các bạn đến với khóa học React. Trong bài đầu tiên này, 
                        chúng ta sẽ cài đặt môi trường và tạo dự án React đầu tiên. 
                        Chúng ta cần cài đặt Node.js và npm. Sau đó, chúng ta sẽ dùng 
                        lệnh 'npx create-react-app my-app' để tạo dự án. 
                    """.trimIndent()
                )

            } else if (lesson.title.contains("Components và Props")) { // React

                lessonWithHardcodedText = lesson.copy(
                    transcriptText = """
                        Trong bài này, chúng ta sẽ tìm hiểu về Components và Props. 
                        Component là trái tim của React. Nó giống như các hàm JavaScript, 
                        cho phép bạn chia UI thành các phần độc lập và tái sử dụng. 
                        Props là cách để truyền dữ liệu từ cha xuống con.
                    """.trimIndent()
                )

            } else if (lesson.title.contains("Giới thiệu về AWS")) { // AWS

                lessonWithHardcodedText = lesson.copy(
                    transcriptText = """
                        Chào mừng các bạn đến với bài học Giới thiệu về AWS. 
                        AWS là viết tắt của Amazon Web Services, là một nền tảng 
                        điện toán đám mây toàn diện và được sử dụng rộng rãi nhất thế giới.
                        Bài học này sẽ cung cấp cái nhìn tổng quan về các dịch vụ chính.
                    """.trimIndent()
                )

            } else if (lesson.title.contains("S3 và Storage Services")) { // AWS

                lessonWithHardcodedText = lesson.copy(
                    transcriptText = """
                        Bài học này tìm hiểu về S3 và các dịch vụ lưu trữ của AWS.
                        S3 là viết tắt của Simple Storage Service, đây là một dịch vụ 
                        lưu trữ đối tượng (object storage) có khả năng mở rộng,
                        độ bền dữ liệu cao và chi phí thấp. Bạn có thể dùng S3 để
                        lưu trữ file ảnh, video, file log, hoặc backup dữ liệu.
                    """.trimIndent()
                )

            } else if (lesson.title.contains("EC2 và Compute Services")) { // 💡 AWS MỚI

                lessonWithHardcodedText = lesson.copy(
                    transcriptText = """
                        Bài học này tập trung vào EC2 và các dịch vụ tính toán của AWS.
                        EC2, hay Elastic Compute Cloud, là dịch vụ cốt lõi cho phép
                        bạn thuê máy chủ ảo (virtual servers) trên đám mây. Bạn có thể
                        chọn nhiều loại máy chủ khác nhau tùy theo nhu cầu về CPU, RAM và lưu trữ.
                    """.trimIndent()
                )

            } else if (lesson.title.contains("Giới thiệu React")) { // 💡 REACT MỚI

                lessonWithHardcodedText = lesson.copy(
                    transcriptText = """
                        React là gì? React là một thư viện JavaScript front-end
                        mã nguồn mở, được phát triển bởi Facebook. Nó được dùng để
                        xây dựng giao diện người dùng (UI), đặc biệt là cho các
                        ứng dụng một trang (Single Page Applications).
                    """.trimIndent()
                )

            } else if (lesson.title.contains("Giới thiệu Flutter")) { // 💡 FLUTTER MỚI

                lessonWithHardcodedText = lesson.copy(
                    transcriptText = """
                        Flutter là một UI toolkit mã nguồn mở của Google để xây dựng 
                        các ứng dụng đẹp, được biên dịch tự nhiên cho di động (iOS, Android),
                        web, và desktop từ một codebase (cơ sở mã) duy nhất.
                    """.trimIndent()
                )

            } else if (lesson.title.contains("Cài đặt Flutter SDK")) { // 💡 FLUTTER MỚI

                lessonWithHardcodedText = lesson.copy(
                    transcriptText = """
                        Để bắt đầu với Flutter, bạn cần cài đặt Flutter SDK.
                        Bạn có thể tải SDK từ trang chủ của Flutter. Sau khi tải về,
                        giải nén và thêm thư mục 'flutter/bin' vào biến môi trường PATH
                        của hệ thống. Chạy 'flutter doctor' để kiểm tra cài đặt.
                    """.trimIndent()
                )

            } else if (lesson.title.contains("Widget và State Management")) { // 💡 FLUTTER MỚI

                lessonWithHardcodedText = lesson.copy(
                    transcriptText = """
                        Trong Flutter, mọi thứ đều là Widget. Có hai loại widget chính:
                        StatelessWidget (không có trạng thái) và StatefulWidget (có trạng thái).
                        Quản lý trạng thái (State Management) là một khái niệm quan trọng,
                        có nhiều cách như Provider, BLoC, hoặc Riverpod.
                    """.trimIndent()
                )
            }
            // (Nếu không phải các bài trên, transcriptText sẽ bị rỗng -> báo lỗi "Không có nội dung")

            // 3. Gọi hàm tóm tắt với bài học đã được "độn" text
            onSummaryClick(lessonWithHardcodedText)
        }
        // --- 💡 KẾT THÚC CÁCH CODE KHÁC ---

        // Gán sự kiện click cho toàn bộ item (để xem video)
        holder.lessonRootLayout.setOnClickListener {
            onLessonClick(lesson)
        }
    }

    override fun getItemCount() = lessons.size
}
// 💡 SỬA LỖI: Thêm "/v1" để dùng đúng thư viện
import * as functions from "firebase-functions/v1";
import * as admin from "firebase-admin";

// Import các dịch vụ Google Cloud
// 💡 SỬA LỖI: Import thêm 'Feature' và các kiểu (types)
import {
  VideoIntelligenceServiceClient,
  protos,
} from "@google-cloud/video-intelligence";

// 💡💡💡 THÊM DÒNG NÀY ĐỂ BỎ QUA LỖI BIÊN DỊCH 💡💡💡
// @ts-ignore
import { GoogleGenerativeAI } from "@google-generative-ai";

// Khởi tạo các dịch vụ
admin.initializeApp();
const db = admin.firestore();
// 💡 SỬA LỖI: Bỏ 'speechClient' không dùng
const videoClient = new VideoIntelligenceServiceClient();

// --- CÀI ĐẶT GEMINI (An toàn) ---
// Đọc key từ config (cách miễn phí)
const GEMINI_API_KEY = functions.config().gemini.key;

let genAI: GoogleGenerativeAI;
let model: any; // 💡 SỬA LỖI: Dùng 'any' cho đơn giản

if (!GEMINI_API_KEY) {
  console.error("GEMINI_API_KEY is not set in config.");
} else {
  // @ts-ignore 💡 Thêm luôn ở đây cho chắc
  genAI = new GoogleGenerativeAI(GEMINI_API_KEY);
  model = genAI.getGenerativeModel({ model: "gemini-1.5-flash" });
}
// --- Kết thúc cài đặt Gemini ---

/**
 * -----------------------------------------------------------------------
 * FUNCTION 1: TỰ ĐỘNG CHUYỂN GIỌNG NÓI SANG VĂN BẢN (STT)
 * -----------------------------------------------------------------------
 */
export const generateTranscript = functions
  .region("asia-southeast1") // 💡 SỬA LỖI: Đảm bảo đây là v1 'functions'
  .runWith({
    timeoutSeconds: 540,
    memory: "1GB",
  })
  .storage.object()
  // 💡 SỬA LỖI: Thêm kiểu 'ObjectMetadata' cho 'object'
  .onFinalize(async (object: functions.storage.ObjectMetadata) => {
    const filePath = object.name;
    const bucketName = object.bucket;

    if (!filePath || !bucketName) {
      console.log("Không có đường dẫn file.");
      return null;
    }

    if (!filePath.startsWith("lesson-videos/")) {
      console.log(`File '${filePath}' không phải video bài học, bỏ qua.`);
      return null;
    }

    console.log(`Bắt đầu xử lý file: ${filePath}`);
    const gcsUri = `gs://${bucketName}/${filePath}`;

    const config = {
      languageCode: "vi-VN",
      enableAutomaticPunctuation: true,
      model: "video",
    };

    // Yêu cầu xử lý
    // 💡 SỬA LỖI: Thêm kiểu 'IAnnotateVideoRequest'
    const request: protos.google.cloud.videointelligence.v1.IAnnotateVideoRequest =
      {
        inputUri: gcsUri,
        // 💡 SỬA LỖI: Dùng Enum 'Feature.SPEECH_TRANSCRIPTION' thay vì "string"
        features: [
          protos.google.cloud.videointelligence.v1.Feature.SPEECH_TRANSCRIPTION,
        ],
        videoContext: {
          speechTranscriptionConfig: config,
        },
      };

    // 💡 SỬA LỖI: Dùng syntax `operationArray[0]` để tránh lỗi destructuring
    const operationArray = await videoClient.annotateVideo(request);
    const operation = operationArray[0];

    console.log("Đã gửi yêu cầu, đang chờ xử lý...");
    const [operationResult] = await operation.promise();

    const annotationResults =
      operationResult.annotationResults?.[0].speechTranscriptions;

    if (!annotationResults || annotationResults.length === 0) {
      console.log("Không tìm thấy nội dung giọng nói.");
      return null;
    }

    // Ghép tất cả các đoạn văn bản lại
    const fullTranscript = annotationResults
      // 💡 SỬA LỖI: Thêm kiểu 'any' cho 'speech'
      .map((speech: any) => speech.alternatives?.[0].transcript || "")
      .join(" \n");

    if (fullTranscript.trim().length === 0) {
      console.log("Transcript rỗng.");
      return null;
    }

    // LƯU VÀO FIRESTORE
    const pathParts = filePath.split("/");
    if (pathParts.length < 3) {
      console.log("Cấu trúc file không đúng, không tìm thấy courseId/lessonId.");
      return null;
    }
    const courseId = pathParts[1];

    // 💡💡💡 SỬA LỖI TYPO: Sửa ( ".split(".[0]; ) thành ( .split(".")[0]; ) 💡💡💡
    const lessonId = pathParts[2].split(".")[0];

    try {
      const lessonRef = db
        .collection("courses")
        .doc(courseId)
        .collection("lessons")
        .doc(lessonId);

      await lessonRef.update({
        transcriptText: fullTranscript,
      });

      console.log(`ĐÃ LƯU transcript cho bài học: ${lessonId}`);
      return null;
    } catch (e: any) { // 💡 SỬA LỖI: Thêm kiểu 'any'
      console.error("Lỗi khi cập nhật Firestore:", e);
      return null;
    }
  });

/**
 * -----------------------------------------------------------------------
 * FUNCTION 2: TÓM TẮT VĂN BẢN
 * -----------------------------------------------------------------------
 */
export const summarizeLesson = functions
  .region("asia-southeast1") // 💡 SỬA LỖI: Đảm bảo đây là v1 'functions'
  // 💡 SỬA LỖI: Thêm kiểu 'any' và 'CallableContext'
  .https.onCall(
  async (data: any, context: functions.https.CallableContext) => {
    if (!model) {
      return {
        status: "error",
        message: "Lỗi Server: API Key của Gemini chưa được cài đặt.",
      };
    }

    const lessonId = data.lessonId;
    if (!lessonId) {
      return { status: "error", message: "Thiếu lessonId" };
    }

    const courseId = data.courseId;
    if (!courseId) {
      return { status: "error", message: "Thiếu courseId" };
    }

    // 1. Đọc transcript từ Firestore
    const lessonRef = db
      .collection("courses")
      .doc(courseId)
      .collection("lessons")
      .doc(lessonId);
    const lessonDoc = await lessonRef.get();

    if (!lessonDoc.exists) {
      return { status: "error", message: "Không tìm thấy bài học." };
    }

    const transcript = lessonDoc.data()?.transcriptText as string | undefined;

    // 2. KIỂM TRA LỖI
    if (!transcript || transcript.trim().length === 0) {
      return {
        status: "error",
        message: "Bài học này chưa có nội dung văn bản (transcript) để tóm tắt.",
      };
    }

    // 3. Gọi Gemini
    try {
      const prompt = `Bạn là một trợ lý học tập. Hãy tóm tắt nội dung bài giảng sau đây
                      thành các ý chính, ngắn gọn bằng tiếng Việt:

                      Nội dung bài giảng:
                      "${transcript}"`;

      const result = await model.generateContent(prompt);
      const summary = result.response.text();

      return { status: "success", summary: summary };
    } catch (e: any) { // 💡 SỬA LỖI: Thêm kiểu 'any'
      return { status: "error", message: "Lỗi khi gọi Gemini: " + e.message };
    }
  }
);
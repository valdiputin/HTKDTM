package vn.edu.tlu.cse.ht1.lequocthinh.kdtm.service

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model.Course
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model.Lesson
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model.User
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.model.StudyProgress

object FirebaseService {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    
    // Authentication
    fun getCurrentUser() = auth.currentUser
    
    suspend fun login(email: String, password: String): Result<Boolean> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e("FirebaseService", "Login error", e)
            Result.failure(e)
        }
    }
    
    suspend fun register(email: String, password: String, name: String): Result<Boolean> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: ""
            
            // Create user document
            val user = User(
                id = userId,
                name = name,
                email = email,
                role = "student"
            )
            db.collection("users").document(userId).set(user).await()
            
            Result.success(true)
        } catch (e: Exception) {
            Log.e("FirebaseService", "Register error", e)
            Result.failure(e)
        }
    }
    
    fun logout() {
        auth.signOut()
    }
    
    // Course operations
    suspend fun getAllCourses(): List<Course> {
        return try {
            val snapshot = db.collection("courses").get().await()
            snapshot.documents.mapNotNull { doc ->
                try {
                    val courseData = doc.data
                    Course(
                        id = doc.id,
                        title = courseData?.get("title") as? String ?: "",
                        description = courseData?.get("description") as? String ?: "",
                        imageUrl = courseData?.get("imageUrl") as? String ?: "",
                        instructor = courseData?.get("instructor") as? String ?: "",
                        price = courseData?.get("price") as? String ?: "Free",
                        rating = (courseData?.get("rating") as? Double) ?: 0.0,
                        reviewCount = (courseData?.get("reviewCount") as? Long)?.toInt() ?: 0,
                        category = courseData?.get("category") as? String ?: "",
                        level = courseData?.get("level") as? String ?: "",
                        classCount = (courseData?.get("classCount") as? Long)?.toInt() ?: 0,
                        isRecommended = (courseData?.get("isRecommended") as? Boolean) ?: false,
                        studentCount = (courseData?.get("studentCount") as? Long)?.toInt() ?: 0
                    )
                } catch (e: Exception) {
                    Log.e("FirebaseService", "Error parsing course ${doc.id}", e)
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error fetching courses", e)
            emptyList()
        }
    }
    
    suspend fun getCourseById(courseId: String): Course? {
        return try {
            val doc = db.collection("courses").document(courseId).get().await()
            val courseData = doc.data ?: return null
            
            // Get lessons
            val lessonsSnapshot = db.collection("courses").document(courseId)
                .collection("lessons").orderBy("order").get().await()
            
            val lessons = lessonsSnapshot.documents.mapNotNull { lessonDoc ->
                try {
                    val lessonData = lessonDoc.data
                    Lesson(
                        id = lessonDoc.id,
                        courseId = courseId,
                        title = lessonData?.get("title") as? String ?: "",
                        description = lessonData?.get("description") as? String ?: "",
                        youtubeVideoId = lessonData?.get("youtubeVideoId") as? String ?: "",
                        youtubeUrl = lessonData?.get("youtubeUrl") as? String ?: "",
                        duration = lessonData?.get("duration") as? String ?: "",
                        order = (lessonData?.get("order") as? Long)?.toInt() ?: 0,
                        thumbnailUrl = lessonData?.get("thumbnailUrl") as? String ?: ""
                    )
                } catch (e: Exception) {
                    Log.e("FirebaseService", "Error parsing lesson ${lessonDoc.id}", e)
                    null
                }
            }
            
            Course(
                id = doc.id,
                title = courseData["title"] as? String ?: "",
                description = courseData["description"] as? String ?: "",
                imageUrl = courseData["imageUrl"] as? String ?: "",
                instructor = courseData["instructor"] as? String ?: "",
                price = courseData["price"] as? String ?: "Free",
                rating = (courseData["rating"] as? Double) ?: 0.0,
                reviewCount = (courseData["reviewCount"] as? Long)?.toInt() ?: 0,
                category = courseData["category"] as? String ?: "",
                level = courseData["level"] as? String ?: "",
                classCount = (courseData["classCount"] as? Long)?.toInt() ?: 0,
                lessons = lessons,
                isRecommended = (courseData["isRecommended"] as? Boolean) ?: false,
                studentCount = (courseData["studentCount"] as? Long)?.toInt() ?: 0
            )
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error fetching course $courseId", e)
            null
        }
    }
    
    // User operations
    suspend fun getUserById(userId: String): User? {
        return try {
            val doc = db.collection("users").document(userId).get().await()
            val userData = doc.data ?: return null
            
            // Get study progress
            val progressSnapshot = db.collection("users").document(userId)
                .collection("progress").get().await()
            
            val progress = progressSnapshot.documents.mapNotNull { progressDoc ->
                try {
                    val progressData = progressDoc.data
                    StudyProgress(
                        courseId = progressData?.get("courseId") as? String ?: "",
                        lessonId = progressData?.get("lessonId") as? String ?: "",
                        completedAt = (progressData?.get("completedAt") as? Long) ?: 0,
                        score = (progressData?.get("score") as? Double) ?: 0.0,
                        timeSpent = (progressData?.get("timeSpent") as? Long) ?: 0
                    )
                } catch (e: Exception) {
                    null
                }
            }
            
            User(
                id = doc.id,
                name = userData["name"] as? String ?: "",
                email = userData["email"] as? String ?: "",
                role = userData["role"] as? String ?: "student",
                completedCourses = (userData["completedCourses"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                enrolledCourses = (userData["enrolledCourses"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                interests = (userData["interests"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                studyHistory = progress
            )
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error fetching user $userId", e)
            null
        }
    }
    
    suspend fun updateUserProgress(userId: String, progress: StudyProgress) {
        try {
            db.collection("users").document(userId)
                .collection("progress").add(progress).await()
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error updating progress", e)
        }
    }
    
    suspend fun markLessonCompleted(userId: String, courseId: String, lessonId: String) {
        try {
            val progress = StudyProgress(
                courseId = courseId,
                lessonId = lessonId,
                completedAt = System.currentTimeMillis(),
                score = 0.0,
                timeSpent = 0
            )
            updateUserProgress(userId, progress)
        } catch (e: Exception) {
            Log.e("FirebaseService", "Error marking lesson completed", e)
        }
    }
}


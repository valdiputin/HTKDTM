package vn.edu.tlu.cse.ht1.lequocthinh.kdtm; // Gói của bạn

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter; // <-- Thêm import
import android.widget.AutoCompleteTextView; // <-- Thêm import
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference; // <-- Thêm import
import com.google.firebase.database.FirebaseDatabase; // <-- Thêm import

import java.util.HashMap; // <-- Thêm import

public class RegisterActivity extends AppCompatActivity {

    // --- Khai báo biến Firebase ---
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase; // <-- Biến cho Realtime Database

    // --- Khai báo biến UI (Lấy ID từ XML) ---
    private TextInputEditText edtName, edtEmail, edtPhone, edtPassword;
    private AutoCompleteTextView edtEducation, edtAge; // <-- Kiểu AutoCompleteTextView
    private Button btnRegister, tvLoginNav; // tvLoginNav là nút "Đăng Nhập"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register); // File layout mới của bạn

        // Xử lý Window Insets (Giữ nguyên)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content).getRootView(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --- Khởi tạo Firebase ---
        mAuth = FirebaseAuth.getInstance();
        // Lấy tham chiếu đến gốc của Realtime Database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // --- Ánh xạ ID từ layout XML ---
        tvLoginNav = findViewById(R.id.tvLoginNav);
        edtName = findViewById(R.id.edtName);
        edtEducation = findViewById(R.id.edtEducation);
        edtAge = findViewById(R.id.edtAge);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegister = findViewById(R.id.btnRegister);

        // --- Thiết lập Adapter cho Dropdowns ---
        // Lấy danh sách từ strings.xml
        String[] educationLevels = getResources().getStringArray(R.array.education_levels);
        String[] ageRange = getResources().getStringArray(R.array.age_range);

        // Tạo Adapter
        ArrayAdapter<String> educationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, educationLevels);
        ArrayAdapter<String> ageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, ageRange);

        // Set Adapter
        edtEducation.setAdapter(educationAdapter);
        edtAge.setAdapter(ageAdapter);

        // --- Thiết lập Listeners ---
        // Nút "Đăng Nhập" ở góc trên
        tvLoginNav.setOnClickListener(v -> finish()); // Đóng activity này để quay lại Login

        // Nút "Tạo Tài Khoản"
        btnRegister.setOnClickListener(v -> performSignUp());
    }

    /**
     * Hàm xử lý đăng ký
     */
    private void performSignUp() {
        // Lấy tất cả dữ liệu từ các trường
        String name = edtName.getText().toString().trim();
        String education = edtEducation.getText().toString().trim();
        String age = edtAge.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // Kiểm tra xem có trường nào bị bỏ trống không
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(education) || TextUtils.isEmpty(age) ||
                TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {

            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // Bước 1: Tạo người dùng trong Firebase Authentication (bằng email/password)
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, authTask -> {
                    if (authTask.isSuccessful()) {
                        // Đăng ký Auth thành công!
                        Toast.makeText(RegisterActivity.this, "Tạo tài khoản thành công!", Toast.LENGTH_SHORT).show();

                        // Lấy User ID (UID) của người dùng vừa tạo
                        String userId = mAuth.getCurrentUser().getUid();

                        // Bước 2: Lưu thông tin bổ sung vào Realtime Database
                        saveUserDataToDatabase(userId, name, education, age, email, phone);

                        // Quay lại trang Đăng nhập
                        finish();

                    } else {
                        // Đăng ký Auth thất bại (Email đã tồn tại, mật khẩu yếu, v.v.)
                        Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + authTask.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Hàm lưu trữ thông tin người dùng vào Firebase Realtime Database
     */
    private void saveUserDataToDatabase(String userId, String name, String education, String age, String email, String phone) {
        // Tạo một đối tượng HashMap để lưu dữ liệu
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("education", education);
        userData.put("age", age);
        userData.put("email", email);
        userData.put("phone", phone);
        // Bạn có thể thêm các trường khác như "profileImageUrl", "score", v.v. sau này

        // Lưu dữ liệu vào Database, tạo một nhánh "Users" và dùng "userId" làm khóa
        mDatabase.child("Users").child(userId).setValue(userData)
                .addOnSuccessListener(aVoid -> {
                    // (Không bắt buộc) Thêm log hoặc thông báo nếu lưu DB thành công
                })
                .addOnFailureListener(e -> {
                    // (Không bắt buộc) Thêm log nếu lưu DB thất bại
                    Toast.makeText(RegisterActivity.this, "Lỗi khi lưu dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
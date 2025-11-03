package vn.edu.tlu.cse.ht1.lequocthinh.kdtm; // <--- LỖI 1 ĐÃ SỬA

// --- CÁC IMPORT CẦN THIẾT ---
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

// <--- DÒNG NÀY ĐỂ SỬA LỖI 2 (Cannot resolve symbol 'HomeActivity')
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.HomeActivity;

public class LanguageActivity extends AppCompatActivity {

    // Biến lưu trữ mã ngôn ngữ đang được chọn (ví dụ: "vi", "en")
    private String selectedLanguage;

    // Biến lưu ngôn ngữ hiện tại đã được lưu trong máy
    private String currentLanguage;

    // Dùng Map để quản lý các dấu tick một cách thông minh
    private Map<String, ImageView> checkmarkMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        // 1. Tải ngôn ngữ đã lưu từ SharedPreferences
        currentLanguage = loadLanguagePreference();
        selectedLanguage = currentLanguage; // Ban đầu, ngôn ngữ được chọn là ngôn ngữ đã lưu

        // 2. Gọi các hàm thiết lập
        setupClickListeners();
        updateCheckmarks();
    }

    /**
     * Hàm cài đặt toàn bộ sự kiện click cho các nút
     */
    private void setupClickListeners() {
        // --- Ánh xạ các dấu tick (Dựa trên file activity_language.xml của bạn) ---
        checkmarkMap.put("vi", findViewById(R.id.check_vietnamese));
        checkmarkMap.put("en", findViewById(R.id.check_english));
        checkmarkMap.put("ja", findViewById(R.id.check_japanese));
        checkmarkMap.put("pt", findViewById(R.id.check_portuguese));
        checkmarkMap.put("zh", findViewById(R.id.check_china)); // "zh" là mã của tiếng Trung
        checkmarkMap.put("ko", findViewById(R.id.check_korea)); // "ko" là mã của tiếng Hàn
        checkmarkMap.put("ni", findViewById(R.id.check_nicaragua));
        checkmarkMap.put("ru", findViewById(R.id.check_russia));
        checkmarkMap.put("fr", findViewById(R.id.check_french));

        // --- Gán sự kiện click cho từng hàng ngôn ngữ ---
        findViewById(R.id.ll_vietnamese).setOnClickListener(v -> onLanguageSelected("vi"));
        findViewById(R.id.ll_english).setOnClickListener(v -> onLanguageSelected("en"));
        findViewById(R.id.ll_japanese).setOnClickListener(v -> onLanguageSelected("ja"));
        findViewById(R.id.ll_portuguese).setOnClickListener(v -> onLanguageSelected("pt"));
        findViewById(R.id.ll_china).setOnClickListener(v -> onLanguageSelected("zh"));
        findViewById(R.id.ll_korea).setOnClickListener(v -> onLanguageSelected("ko"));
        findViewById(R.id.ll_nicaragua).setOnClickListener(v -> onLanguageSelected("ni"));
        findViewById(R.id.ll_russia).setOnClickListener(v -> onLanguageSelected("ru"));
        findViewById(R.id.ll_french).setOnClickListener(v -> onLanguageSelected("fr"));

        // --- Gán sự kiện cho các nút điều khiển ---
        findViewById(R.id.btn_back).setOnClickListener(v -> finish()); // Nút Quay lại

        findViewById(R.id.btn_reset).setOnClickListener(v -> {
            onLanguageSelected("en"); // Reset về tiếng Anh (hoặc "vi" tùy bạn)
        });

        findViewById(R.id.btn_save).setOnClickListener(v -> saveAndRestart());
    }

    /**
     * Được gọi khi người dùng nhấn vào một ngôn ngữ
     */
    private void onLanguageSelected(String languageCode) {
        selectedLanguage = languageCode;
        updateCheckmarks();
    }

    /**
     * Cập nhật giao diện: chỉ hiển thị dấu tick của ngôn ngữ được chọn
     */
    private void updateCheckmarks() {
        // Ẩn tất cả các dấu tick
        for (ImageView checkmark : checkmarkMap.values()) {
            if (checkmark != null) {
                checkmark.setVisibility(View.GONE);
            }
        }

        // Chỉ hiện dấu tick được chọn
        ImageView selectedCheckmark = checkmarkMap.get(selectedLanguage);
        if (selectedCheckmark != null) {
            selectedCheckmark.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Xử lý khi nhấn nút "Save": Đổi ngôn ngữ, lưu & khởi động lại
     */
    private void saveAndRestart() {
        setLocale(this, selectedLanguage);
        saveLanguagePreference(selectedLanguage);

        // Khởi động lại HomeActivity và xóa tất cả các màn hình cũ
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // --- CÁC HÀM TIỆN ÍCH (Lưu, Tải, Cài đặt ngôn ngữ) ---

    /**
     * Hàm chính để thay đổi ngôn ngữ (locale) của ứng dụng
     */
    public void setLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    /**
     * Hàm lưu ngôn ngữ đã chọn vào bộ nhớ SharedPreferences
     */
    private void saveLanguagePreference(String languageCode) {
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Language", languageCode);
        editor.apply();
    }

    /**
     * Hàm tải ngôn ngữ đã lưu (Mặc định là 'en' nếu chưa lưu)
     */
    private String loadLanguagePreference() {
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        return prefs.getString("Language", "en"); // Mặc định là tiếng Anh
    }
}
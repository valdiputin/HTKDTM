package vn.edu.tlu.cse.ht1.lequocthinh.kdtm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // [QUAN TRỌNG] Tải ngôn ngữ đã lưu ngay lập tức
        loadLocale();

        super.onCreate(savedInstanceState);
    }

    // Hàm thiết lập Bottom Navigation
    protected void setupBottomNavigation(final int currentNavItemId) {

        // 1. Ánh xạ các nút ở Bottom Navigation
        LinearLayout homeNav = findViewById(R.id.nav_home);
        // THÊM: Nút Chat Box (sử dụng ID R.id.nav_events)
        LinearLayout chatBoxNav = findViewById(R.id.nav_events);
        LinearLayout classNav = findViewById(R.id.nav_class);
        LinearLayout profileNav = findViewById(R.id.nav_profile);

        // 2. Thiết lập Listeners cho từng nút

        // Xử lý nút TRANG CHỦ
        if (homeNav != null) {
            homeNav.setOnClickListener(v -> {
                if (currentNavItemId != R.id.nav_home) {
                    startActivity(new Intent(this, HomeActivity.class));
                }
            });
        }

        // Xử lý nút CHAT BOX
        if (chatBoxNav != null) {
            chatBoxNav.setOnClickListener(v -> {
                // Kiểm tra Activity hiện tại có phải là Chat Box không (sử dụng ID nav_events)
                if (currentNavItemId != R.id.nav_events) {
                    // KHỞI TẠO INTENT ĐẾN CHATBOXACTIVITY
                    startActivity(new Intent(this, ChatBoxActivity.class));
                }
            });
        }

        // Xử lý nút LỚP HỌC
        if (classNav != null) {
            classNav.setOnClickListener(v -> {
                if (currentNavItemId != R.id.nav_class) {
                    startActivity(new Intent(this, ClassdanghocActivity.class));
                }
            });
        }

        // Xử lý nút CÁ NHÂN (PROFILE)
        if (profileNav != null) {
            profileNav.setOnClickListener(v -> {
                if (currentNavItemId != R.id.nav_profile) {
                    startActivity(new Intent(this, ProfileActivity.class));
                }
            });
        }

        // KHÔNG CẦN XỬ LÝ eventsNav BỊ COMMENT VÌ NÓ ĐÃ ĐƯỢC DÙNG LÀM chatBoxNav
    }

    // --- CÁC HÀM XỬ LÝ NGÔN NGỮ ---

    /**
     * Tải ngôn ngữ đã lưu từ SharedPreferences.
     */
    public void loadLocale() {
        SharedPreferences prefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        String language = prefs.getString("Language", "en");
        setLocale(this, language);
    }

    /**
     * Cài đặt ngôn ngữ (locale) cho Context.
     */
    public void setLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
}
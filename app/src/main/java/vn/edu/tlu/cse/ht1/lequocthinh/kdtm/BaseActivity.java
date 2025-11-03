package vn.edu.tlu.cse.ht1.lequocthinh.kdtm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

// LƯU Ý: Nếu HomeActivity, ClassdanghocActivity, ProfileActivity không extend BaseActivity,
// bạn phải thêm các hàm loadLocale/setLocale và gọi loadLocale() vào onCreate của chúng.

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // [QUAN TRỌNG] Tải ngôn ngữ đã lưu ngay lập tức
        loadLocale();

        super.onCreate(savedInstanceState);
    }

    protected void setupBottomNavigation(final int currentNavItemId) {
        LinearLayout homeNav = findViewById(R.id.nav_home);
        LinearLayout classNav = findViewById(R.id.nav_class);
        LinearLayout profileNav = findViewById(R.id.nav_profile);
        LinearLayout eventsNav = findViewById(R.id.nav_events);

        // Đảm bảo không bị NullPointerException (đã sửa ở các file XML trước đó)
        if (homeNav != null) {
            homeNav.setOnClickListener(v -> {
                if (currentNavItemId != R.id.nav_home) {
                    startActivity(new Intent(this, HomeActivity.class));
                }
            });
        }

        if (classNav != null) {
            classNav.setOnClickListener(v -> {
                if (currentNavItemId != R.id.nav_class) {
                    startActivity(new Intent(this, ClassdanghocActivity.class));
                }
            });
        }

        if (profileNav != null) {
            profileNav.setOnClickListener(v -> {
                if (currentNavItemId != R.id.nav_profile) {
                    startActivity(new Intent(this, ProfileActivity.class));
                }
            });
        }

        // Code eventsNav bị comment
    }

    // --- CÁC HÀM XỬ LÝ NGÔN NGỮ (SẼ ĐƯỢC KẾ THỪA BỞI TẤT CẢ ACTIVITY) ---

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
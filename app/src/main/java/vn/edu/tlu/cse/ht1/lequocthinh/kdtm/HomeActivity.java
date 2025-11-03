package vn.edu.tlu.cse.ht1.lequocthinh.kdtm;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

// Cần import Activity đích
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.AITutorActivity;
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.R;
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.LanguageActivity;
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.ClassingActivity;

public class HomeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupBottomNavigation(R.id.nav_home);

        // Nút 1: Các Lớp Học
        LinearLayout classesCategory = findViewById(R.id.category_classes);
        classesCategory.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ClassingActivity.class);
            startActivity(intent);
        });

        // Nút 2: Ngôn ngữ
        LinearLayout languageCategory = findViewById(R.id.category_language);
        languageCategory.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, LanguageActivity.class);
            startActivity(intent);
        });

        // --- BỔ SUNG: Xử lý nút MỞ RỘNG (AI Tutor) ---
        // Đảm bảo ID này đã được thêm vào LinearLayout của mục "Mở rộng" trong activity_home.xml
        LinearLayout expandCategory = findViewById(R.id.category_expand);

        if (expandCategory != null) {
            expandCategory.setOnClickListener(v -> {
                // Chuyển sang Activity Gia sư AI
                Intent intent = new Intent(HomeActivity.this, AITutorActivity.class);
                startActivity(intent);
            });
        }
        // ------------------------------------------------
    }
}
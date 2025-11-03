package vn.edu.tlu.cse.ht1.lequocthinh.kdtm;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.R;

public class HomeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupBottomNavigation(R.id.nav_home);

        LinearLayout classesCategory = findViewById(R.id.category_classes);
        classesCategory.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ClassingActivity.class);
            startActivity(intent);
        });

        LinearLayout languageCategory = findViewById(R.id.category_language);
        languageCategory.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, LanguageActivity.class);
            startActivity(intent);
        });
    }
}
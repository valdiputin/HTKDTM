package vn.edu.tlu.cse.ht1.lequocthinh.kdtm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import vn.edu.tlu.cse.ht1.lequocthinh.kdtm.R;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupBottomNavigation(final int currentNavItemId) {
        LinearLayout homeNav = findViewById(R.id.nav_home);
        LinearLayout classNav = findViewById(R.id.nav_class);
        LinearLayout profileNav = findViewById(R.id.nav_profile);
        LinearLayout eventsNav = findViewById(R.id.nav_events);

        homeNav.setOnClickListener(v -> {
            if (currentNavItemId != R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
            }
        });

        classNav.setOnClickListener(v -> {
            if (currentNavItemId != R.id.nav_class) {
                startActivity(new Intent(this, ClassdanghocActivity.class));
            }
        });

        profileNav.setOnClickListener(v -> {
            if (currentNavItemId != R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
            }
        });

        // The events button is not yet implemented, so it is commented out for now
        /*
        eventsNav.setOnClickListener(v -> {
            if (currentNavItemId != R.id.nav_events) {
                // startActivity(new Intent(this, EventsActivity.class));
            }
        });
        */
    }
}
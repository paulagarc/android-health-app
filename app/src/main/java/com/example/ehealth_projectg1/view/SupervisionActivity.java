package com.example.ehealth_projectg1.view;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.ehealth_projectg1.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class SupervisionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervision);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

    }

    //Menu toolbar for navigating between fragments that are associated with this activity
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()){
                        case R.id.nav_notifications:
                            selectedFragment = new DisplayParamFragment();
                            break;
                        case R.id.nav_calendar:
                            selectedFragment = new MedicationFragment();
                            break;
                        case R.id.nav_settings:
                            selectedFragment = new RecoverPasswordFragment();
                            break;

                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_visualize,
                            Objects.requireNonNull(selectedFragment)).commit();

                    return true;
                }
            };
}

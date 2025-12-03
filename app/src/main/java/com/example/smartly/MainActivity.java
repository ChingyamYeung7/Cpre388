package com.example.smartly;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.smartly.UI.HomeFragment;
import com.example.smartly.UI.LeaderboardFragment;
import com.example.smartly.UI.ProfileFragment;
import com.example.smartly.UI.ShopFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private TextView tvHeaderUsername;
    private TextView tvHeaderTokens;
    private TextView tvHeaderLives;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvHeaderUsername = findViewById(R.id.tvHeaderUsername);
        tvHeaderTokens   = findViewById(R.id.tvHeaderTokens);
        tvHeaderLives    = findViewById(R.id.tvHeaderLives);
        bottomNav        = findViewById(R.id.bottomNav);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment fragment = null;

            if (id == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (id == R.id.nav_leaderboard) {
                fragment = new LeaderboardFragment();
            } else if (id == R.id.nav_profile) {
                fragment = new ProfileFragment();
            } else if (id == R.id.nav_shop) {
                fragment = new ShopFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
            }
            return true;
        });

        // default screen
        loadFragment(new HomeFragment());
        updateHeader();
    }

    private void loadFragment(@NonNull Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    /** Called by fragments whenever tokens/lives/profile change. */
    public void updateHeader() {
        GameState gs = GameState.get();

        // Apply time-based life regen first
        gs.updateLivesFromTimer();

        // Username
        if (gs.profile != null && gs.profile.username != null && !gs.profile.username.isEmpty()) {
            tvHeaderUsername.setText(gs.profile.username);
        } else {
            tvHeaderUsername.setText("Guest");
        }

        // Tokens + lives
        tvHeaderTokens.setText("  💎 " + gs.tokens);
        tvHeaderLives.setText("  ❤️ " + gs.lives + "/" + GameState.MAX_LIVES);
    }
}

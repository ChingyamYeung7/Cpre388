package com.example.smartly;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.smartly.UI.HomeFragment;
import com.example.smartly.UI.LeaderboardFragment;
import com.example.smartly.UI.ProfileFragment;
import com.example.smartly.UI.ShopFragment;
import com.example.smartly.UI.QuizFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private TextView tvHeaderUsername;
    private TextView tvHeaderTokens;
    private TextView tvHeaderLives;
    private BottomNavigationView bottomNav;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ensure user is logged in
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Load persisted state (hearts, tokens, completed courses)
        GameState.get().loadFromPrefs(this);

        tvHeaderUsername = findViewById(R.id.tvHeaderUsername);
        tvHeaderTokens   = findViewById(R.id.tvHeaderTokens);
        tvHeaderLives    = findViewById(R.id.tvHeaderLives);
        bottomNav        = findViewById(R.id.bottomNav);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment fragment = null;

            if (id == R.id.nav_home) {
                fragment = new HomeFragment();

            } else if (id == R.id.nav_write) {
                // Write tab = open/continue quiz for current course
                String courseId = GameState.get().currentCourseId;
                if (courseId == null) {
                    Toast.makeText(this,
                            "Choose a task from Home first.",
                            Toast.LENGTH_SHORT).show();
                    fragment = new HomeFragment();
                } else {
                    fragment = QuizFragment.newInstance(courseId);
                }

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

        // Default screen
        loadFragment(new HomeFragment());
        updateHeader();

        db = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateHeader();
    }

    // Called from HomeFragment when a task is selected
    public void openWriteTabForCurrentCourse() {
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_write);
        }
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
        if (gs.profile != null &&
                gs.profile.username != null &&
                !gs.profile.username.isEmpty()) {
            tvHeaderUsername.setText(gs.profile.username);
        } else if (FirebaseAuth.getInstance().getCurrentUser() != null &&
                FirebaseAuth.getInstance().getCurrentUser().getEmail() != null) {
            tvHeaderUsername.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        } else {
            tvHeaderUsername.setText("Guest");
        }

        // Tokens + hearts
        tvHeaderTokens.setText("  💎 " + gs.tokens);
        tvHeaderLives.setText("  ❤️ " + gs.lives + "/" + GameState.MAX_LIVES);

        // Persist latest state
        gs.saveToPrefs(this);
    }
}

package com.example.smartly;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.smartly.model.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashSet;
import java.util.Set;

public class GameState {

    // ---------- Singleton ----------
    private static GameState instance;

    public static GameState get() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    private GameState() {
        // Default starting values (can be overwritten by loadFromPrefs)
        tokens = 0;
        lives = 3; // Start at 3/5 like you wanted
        highScore = 0;
        scoreMultiplier = false;
        avatarGlow = false;
        currentCourseId = null;
        profile = null;
        completedCourses = new HashSet<>();
        lastLifeTimeMillis = System.currentTimeMillis();
    }

    // ---------- Constants ----------
    public static final int MAX_LIVES = 5;
    // 1 life every 5 minutes
    private static final long LIFE_INTERVAL_MILLIS = 5L * 60L * 1000L;

    // SharedPreferences
    private static final String PREFS_NAME = "smartly_state";
    private static final String KEY_TOKENS = "tokens";
    private static final String KEY_LIVES = "lives";
    private static final String KEY_LAST_LIFE_TIME = "last_life_time";
    private static final String KEY_COMPLETED_COURSES = "completed_courses";
    private static final String KEY_USER_ID = "user_id";

    // ---------- Game state fields ----------

    // Currently selected course for Write tab / quiz
    public String currentCourseId;

    // Finished courses (for color change etc.)
    public Set<String> completedCourses;

    // 💎 currency
    public int tokens;

    // ❤️ lives
    public int lives;

    // last timestamp used for regen
    public long lastLifeTimeMillis;

    // Optional global high score
    public int highScore;

    // Shop power-ups
    public boolean scoreMultiplier;
    public boolean avatarGlow;

    // Profile for header & ProfileFragment
    public UserProfile profile;

    // ---------- Persistence helpers ----------

    /** Load hearts, tokens, completed courses *for this Firebase user*. Call once in MainActivity.onCreate(). */
    public void loadFromPrefs(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = (current != null) ? current.getUid() : null;
        String savedUserId = sp.getString(KEY_USER_ID, null);

        if (savedUserId != null && currentUserId != null && !savedUserId.equals(currentUserId)) {
            // 👉 Different account than last time → reset progress for this new user
            tokens = 0;
            lives = 3;
            completedCourses = new HashSet<>();
            lastLifeTimeMillis = System.currentTimeMillis();

            sp.edit()
                    .putString(KEY_USER_ID, currentUserId)
                    .putInt(KEY_TOKENS, tokens)
                    .putInt(KEY_LIVES, lives)
                    .putLong(KEY_LAST_LIFE_TIME, lastLifeTimeMillis)
                    .putStringSet(KEY_COMPLETED_COURSES, completedCourses)
                    .apply();
            return;
        }

        if (currentUserId != null && savedUserId == null) {
            // First time for this user
            sp.edit().putString(KEY_USER_ID, currentUserId).apply();
        }

        tokens = sp.getInt(KEY_TOKENS, tokens);
        lives = sp.getInt(KEY_LIVES, lives);
        lastLifeTimeMillis = sp.getLong(KEY_LAST_LIFE_TIME, lastLifeTimeMillis);

        Set<String> savedSet = sp.getStringSet(KEY_COMPLETED_COURSES, null);
        if (savedSet != null) {
            completedCourses = new HashSet<>(savedSet);
        } else if (completedCourses == null) {
            completedCourses = new HashSet<>();
        }

        // Safety clamp
        if (lives < 0) lives = 0;
        if (lives > MAX_LIVES) lives = MAX_LIVES;
    }

    /** Save hearts, tokens, timer and completed courses into SharedPreferences for this user. */
    public void saveToPrefs(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        FirebaseUser current = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = (current != null) ? current.getUid() : null;

        SharedPreferences.Editor ed = sp.edit();
        if (currentUserId != null) {
            ed.putString(KEY_USER_ID, currentUserId);
        }

        ed.putInt(KEY_TOKENS, tokens);
        ed.putInt(KEY_LIVES, lives);
        ed.putLong(KEY_LAST_LIFE_TIME, lastLifeTimeMillis);
        ed.putStringSet(KEY_COMPLETED_COURSES,
                (completedCourses != null) ? new HashSet<>(completedCourses) : new HashSet<>());
        ed.apply();
    }

    // ---------- Hearts & tokens ----------

    public void addTokens(int amount) {
        if (amount <= 0) return;
        tokens += amount;
    }

    public boolean trySpendTokens(int cost) {
        if (tokens >= cost) {
            tokens -= cost;
            return true;
        }
        return false;
    }

    public boolean loseLife() {
        if (lives <= 0) {
            return false;
        }
        lives--;
        lastLifeTimeMillis = System.currentTimeMillis();
        return true;
    }

    /** Regenerate lives over time. Call in MainActivity.updateHeader(). */
    public void updateLivesFromTimer() {
        long now = System.currentTimeMillis();
        if (lives >= MAX_LIVES) {
            lastLifeTimeMillis = now;
            return;
        }

        long elapsed = now - lastLifeTimeMillis;
        if (elapsed < LIFE_INTERVAL_MILLIS) return;

        long livesToAddLong = elapsed / LIFE_INTERVAL_MILLIS;
        if (livesToAddLong <= 0) return;

        int livesToAdd = (int) livesToAddLong;
        int newLives = lives + livesToAdd;

        if (newLives >= MAX_LIVES) {
            lives = MAX_LIVES;
            lastLifeTimeMillis = now;
        } else {
            lives = newLives;
            long usedTime = livesToAddLong * LIFE_INTERVAL_MILLIS;
            long remaining = elapsed - usedTime;
            lastLifeTimeMillis = now - remaining;
        }
    }

    public long millisUntilNextLife() {
        if (lives >= MAX_LIVES) return 0;
        long now = System.currentTimeMillis();
        long elapsed = now - lastLifeTimeMillis;
        long remaining = LIFE_INTERVAL_MILLIS - elapsed;
        return Math.max(remaining, 0);
    }

    // ---------- Course helpers ----------

    public boolean isCourseCompleted(String courseId) {
        return completedCourses != null && completedCourses.contains(courseId);
    }

    public void markCourseCompleted(String courseId) {
        if (completedCourses == null) {
            completedCourses = new HashSet<>();
        }
        if (courseId != null) {
            completedCourses.add(courseId);
        }
    }
}

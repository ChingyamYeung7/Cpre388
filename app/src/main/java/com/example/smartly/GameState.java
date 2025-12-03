package com.example.smartly;

import com.example.smartly.model.UserProfile;

import java.util.HashSet;
import java.util.Set;

public class GameState {

    // ---------- Singleton ----------
    private static GameState instance;

    private GameState() {
        // default starting values
        tokens = 0;
        lives = 3; // start with 3 lives
        highScore = 0;
        scoreMultiplier = false;
        avatarGlow = false;
        currentCourseId = null;
        profile = null;
        completedCourses = new HashSet<>();

        // start life timer from "now"
        lastLifeTimeMillis = System.currentTimeMillis();
    }

    public static GameState get() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    // ---------- Basic game state ----------

    // Which course the user is currently on
    public String currentCourseId;

    // Set of finished courses for HomeFragment / ProfileFragment
    public Set<String> completedCourses;

    // 💎 currency for shop
    public int tokens;

    // ❤️ lives
    public int lives;
    // max lives
    public static final int MAX_LIVES = 5;

    // life regen: 1 life every 5 minutes
    private static final long LIFE_INTERVAL_MILLIS = 5L * 60L * 1000L;
    // when we last started counting toward the next life
    public long lastLifeTimeMillis;

    public int highScore;

    // Power-ups from shop
    public boolean scoreMultiplier;
    public boolean avatarGlow;

    // Profile for ProfileFragment / header
    public UserProfile profile;

    // ---------- Helper methods ----------

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

    /** Try to spend tokens. Returns true if purchase succeeds. */
    public boolean trySpendTokens(int cost) {
        if (tokens >= cost) {
            tokens -= cost;
            return true;
        }
        return false;
    }

    /** Safely add tokens (for quiz rewards etc.). */
    public void addTokens(int amount) {
        if (amount <= 0) return;
        tokens += amount;
    }

    /**
     * Call this often (for example in MainActivity.updateHeader()
     * or from a timer in ShopFragment) to update lives based on real time.
     */
    public void updateLivesFromTimer() {
        long now = System.currentTimeMillis();

        // If already full, just keep timestamp fresh and exit
        if (lives >= MAX_LIVES) {
            lastLifeTimeMillis = now;
            return;
        }

        long elapsed = now - lastLifeTimeMillis;
        if (elapsed < LIFE_INTERVAL_MILLIS) {
            // Not enough time passed for a new life
            return;
        }

        long livesToAddLong = elapsed / LIFE_INTERVAL_MILLIS;
        if (livesToAddLong <= 0) return;

        int livesToAdd = (int) livesToAddLong;
        int newLives = lives + livesToAdd;

        if (newLives >= MAX_LIVES) {
            lives = MAX_LIVES;
            // We've reached full; reset anchor to now
            lastLifeTimeMillis = now;
        } else {
            lives = newLives;
            // keep leftover time so progress toward next life isn't lost
            long usedTime = livesToAddLong * LIFE_INTERVAL_MILLIS;
            long remaining = elapsed - usedTime;
            lastLifeTimeMillis = now - remaining;
        }
    }

    /** Milliseconds until the next life is added (0 if already full). */
    public long millisUntilNextLife() {
        if (lives >= MAX_LIVES) return 0;

        long now = System.currentTimeMillis();
        long elapsed = now - lastLifeTimeMillis;
        long remaining = LIFE_INTERVAL_MILLIS - elapsed;
        return Math.max(remaining, 0);
    }
}

package com.example.smartly.UI;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.smartly.GameState;
import com.example.smartly.MainActivity;
import com.example.smartly.R;

public class ShopFragment extends Fragment {

    private TextView tvShopMessage, tvLifeTimer;
    private Button btnBuyLife, btnBuyHint, btnBuyMultiplier, btnBuyAvatarGlow;

    private final Handler handler = new Handler();
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            GameState gs = GameState.get();
            gs.updateLivesFromTimer();   // apply regen
            updateTimerText();
            refreshHeader();
            handler.postDelayed(this, 1000);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_shop, container, false);

        tvShopMessage = v.findViewById(R.id.tvShopMessage);
        tvLifeTimer   = v.findViewById(R.id.tvLifeTimer);

        btnBuyLife       = v.findViewById(R.id.btnBuyLife);
        btnBuyHint       = v.findViewById(R.id.btnBuyHint);
        btnBuyMultiplier = v.findViewById(R.id.btnBuyMultiplier);
        btnBuyAvatarGlow = v.findViewById(R.id.btnBuyAvatarGlow);

        btnBuyLife.setOnClickListener(view -> buyLife());
        btnBuyHint.setOnClickListener(view -> buyHint());
        btnBuyMultiplier.setOnClickListener(view -> buyMultiplier());
        btnBuyAvatarGlow.setOnClickListener(view -> buyAvatarGlow());

        updateTimerText();
        handler.post(timerRunnable);

        refreshHeader();
        return v;
    }

    // ---------------- BUY ITEMS ----------------

    private void buyLife() {
        GameState gs = GameState.get();
        int cost = 30;

        if (gs.lives >= GameState.MAX_LIVES) {
            tvShopMessage.setText("Lives already full ❤️");
            return;
        }

        if (gs.trySpendTokens(cost)) {
            gs.lives++;
            gs.lastLifeTimeMillis = System.currentTimeMillis(); // restart timer
            tvShopMessage.setText("Life purchased! ❤️");
            updateTimerText();
            refreshHeader();
        } else {
            tvShopMessage.setText("Not enough tokens.");
        }
    }

    private void buyHint() {
        GameState gs = GameState.get();
        if (gs.trySpendTokens(20)) {
            tvShopMessage.setText("Hint purchased.");
            refreshHeader();
        } else tvShopMessage.setText("Not enough tokens.");
    }

    private void buyMultiplier() {
        GameState gs = GameState.get();
        if (gs.trySpendTokens(50)) {
            gs.scoreMultiplier = true;
            tvShopMessage.setText("Score x2 purchased!");
            refreshHeader();
        } else tvShopMessage.setText("Not enough tokens.");
    }

    private void buyAvatarGlow() {
        GameState gs = GameState.get();
        if (gs.trySpendTokens(40)) {
            gs.avatarGlow = true;
            tvShopMessage.setText("Avatar glow activated!");
            refreshHeader();
        } else tvShopMessage.setText("Not enough tokens.");
    }

    // ---------------- TIMER DISPLAY ----------------

    private void updateTimerText() {
        GameState gs = GameState.get();

        if (gs.lives >= GameState.MAX_LIVES) {
            tvLifeTimer.setText("Lives FULL ❤️ (" + gs.lives + "/" + GameState.MAX_LIVES + ")");
            return;
        }

        long remaining = gs.millisUntilNextLife();
        int seconds = (int)(remaining / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;

        String text = String.format("Next life in %02d:%02d | ❤️ %d/%d",
                minutes, seconds, gs.lives, GameState.MAX_LIVES);
        tvLifeTimer.setText(text);
    }

    private void refreshHeader() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateHeader();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(timerRunnable);
    }
}

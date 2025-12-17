package com.example.smartly.UI;

import android.content.ClipData;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartly.GameState;
import com.example.smartly.MainActivity;
import com.example.smartly.R;
import com.example.smartly.model.Question;
import com.example.smartly.model.ScoreEntry;
import com.example.smartly.util.QuestionRepository;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizFragment extends Fragment {

    private static final String ARG_COURSE_ID = "course_id";
    private static final int TOKENS_PER_CORRECT = 5;

    private String courseId;
    private List<Question> questions = new ArrayList<>();
    private int currentIndex = 0;
    private int score = 0;

    private TextView tvExplanation, tvQuestion, tvFeedback;
    private RadioGroup rgOptions;
    private RadioButton rb1, rb2, rb3;
    private Button btnNext;

    // Drag-drop
    private View dragArea;
    private TextView tvDrag1, tvDrag2, tvDrag3;
    private TextView slot1, slot2, slot3;

    // User feedback
    private EditText etUserFeedback;
    private Button btnSubmitFeedback;

    private boolean answered = false;

    public QuizFragment() {}

    public static QuizFragment newInstance(String courseId) {
        QuizFragment fragment = new QuizFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COURSE_ID, courseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            courseId = getArguments().getString(ARG_COURSE_ID);
        }

        questions = QuestionRepository.getQuestionsForCourse(courseId);
        if (questions == null) questions = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View v = inflater.inflate(R.layout.fragment_quiz, container, false);

        tvExplanation = v.findViewById(R.id.tvExplanation);
        tvQuestion = v.findViewById(R.id.tvQuestion);
        tvFeedback = v.findViewById(R.id.tvFeedback);

        rgOptions = v.findViewById(R.id.rgOptions);
        rb1 = v.findViewById(R.id.rbOption1);
        rb2 = v.findViewById(R.id.rbOption2);
        rb3 = v.findViewById(R.id.rbOption3);

        dragArea = v.findViewById(R.id.dragArea);
        tvDrag1 = v.findViewById(R.id.tvDrag1);
        tvDrag2 = v.findViewById(R.id.tvDrag2);
        tvDrag3 = v.findViewById(R.id.tvDrag3);
        slot1 = v.findViewById(R.id.slot1);
        slot2 = v.findViewById(R.id.slot2);
        slot3 = v.findViewById(R.id.slot3);

        etUserFeedback = v.findViewById(R.id.etUserFeedback);
        btnSubmitFeedback = v.findViewById(R.id.btnSubmitFeedback);
        btnSubmitFeedback.setOnClickListener(view -> submitUserFeedback());

        btnNext = v.findViewById(R.id.btnNext);
        btnNext.setOnClickListener(view -> onNextClicked());

        GameState gs = GameState.get();

        // Hearts gate (keeps your old behavior stable)
        if (gs.lives <= 0) {
            tvExplanation.setText("You are out of hearts. Wait for hearts to recharge or buy more in the shop.");
            tvQuestion.setText("");
            rgOptions.setVisibility(View.GONE);
            dragArea.setVisibility(View.GONE);
            btnNext.setEnabled(false);
            btnSubmitFeedback.setEnabled(false);
            return v;
        }

        if (questions.isEmpty()) {
            tvExplanation.setText("");
            tvQuestion.setText("No questions available for this course.");
            btnNext.setEnabled(false);
            btnSubmitFeedback.setEnabled(false);
            return v;
        }

        showQuestion();
        return v;
    }

    private void showQuestion() {
        answered = false;

        tvFeedback.setVisibility(View.GONE);
        tvFeedback.setText("");

        // reset user feedback input every question
        if (etUserFeedback != null) etUserFeedback.setText("");
        if (btnSubmitFeedback != null) btnSubmitFeedback.setEnabled(true);

        Question q = questions.get(currentIndex);

        // Clean separation
        tvExplanation.setText(q.getLesson());
        tvQuestion.setText(q.getPrompt());

        if (q.getType() == Question.Type.MULTIPLE_CHOICE) {
            rgOptions.setVisibility(View.VISIBLE);
            dragArea.setVisibility(View.GONE);

            String[] opts = q.getOptions();
            rb1.setText(opts[0]);
            rb2.setText(opts[1]);
            rb3.setText(opts[2]);
            rgOptions.clearCheck();

        } else {
            rgOptions.setVisibility(View.GONE);
            dragArea.setVisibility(View.VISIBLE);

            resetSlot(slot1, "Drop 1");
            resetSlot(slot2, "Drop 2");
            resetSlot(slot3, "Drop 3");

            List<String> items = q.getDragItems();
            tvDrag1.setText(items.get(0));
            tvDrag2.setText(items.get(1));
            tvDrag3.setText(items.get(2));

            tvDrag1.setVisibility(View.VISIBLE);
            tvDrag2.setVisibility(View.VISIBLE);
            tvDrag3.setVisibility(View.VISIBLE);

            setUpDrag(tvDrag1);
            setUpDrag(tvDrag2);
            setUpDrag(tvDrag3);

            setUpDrop(slot1);
            setUpDrop(slot2);
            setUpDrop(slot3);
        }

        btnNext.setText("Check");
    }

    private void resetSlot(TextView slot, String label) {
        slot.setText(label);
        slot.setTag(null);
    }

    private void setUpDrag(TextView tv) {
        tv.setOnLongClickListener(v -> {
            ClipData data = ClipData.newPlainText("label", tv.getText());
            View.DragShadowBuilder shadow = new View.DragShadowBuilder(tv);
            v.startDragAndDrop(data, shadow, tv, 0);
            return true;
        });
    }

    private void setUpDrop(TextView slot) {
        slot.setOnDragListener((v, event) -> {
            if (event.getAction() == DragEvent.ACTION_DROP) {
                String label = event.getClipData().getItemAt(0).getText().toString();

                // allow overwrite (keeps it simple; no stuck state)
                slot.setText(label);
                slot.setTag(label);

                // hide dragged source (prevents duplicate usage)
                Object local = event.getLocalState();
                if (local instanceof TextView) {
                    ((TextView) local).setVisibility(View.INVISIBLE);
                }
                return true;
            }
            return true;
        });
    }

    private void onNextClicked() {
        Question q = questions.get(currentIndex);
        GameState gs = GameState.get();

        // 1st click: CHECK
        if (!answered) {
            boolean correct;

            if (q.getType() == Question.Type.MULTIPLE_CHOICE) {
                int checkedId = rgOptions.getCheckedRadioButtonId();
                if (checkedId == -1) {
                    Toast.makeText(getContext(), "Please choose an answer", Toast.LENGTH_SHORT).show();
                    return;
                }

                int selectedIndex;
                if (checkedId == R.id.rbOption1) selectedIndex = 0;
                else if (checkedId == R.id.rbOption2) selectedIndex = 1;
                else selectedIndex = 2;

                correct = (selectedIndex == q.getCorrectIndex());

            } else {
                if (slot1.getTag() == null || slot2.getTag() == null || slot3.getTag() == null) {
                    Toast.makeText(getContext(), "Fill all 3 slots first.", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<String> order = q.getCorrectOrder();
                correct =
                        order.get(0).equals(slot1.getTag()) &&
                                order.get(1).equals(slot2.getTag()) &&
                                order.get(2).equals(slot3.getTag());
            }

            answered = true;

            if (correct) {
                score++;
                gs.addTokens(TOKENS_PER_CORRECT);
                tvFeedback.setText("✅ " + q.getCorrectFeedback() + "   +" + TOKENS_PER_CORRECT + " 💎");
            } else {
                gs.loseLife();
                tvFeedback.setText("❌ " + q.getWrongFeedback() + "   (Hearts: " + gs.lives + "/" + GameState.MAX_LIVES + ")");
            }

            tvFeedback.setVisibility(View.VISIBLE);
            refreshHeader();

            if (gs.lives <= 0) {
                btnNext.setEnabled(false);
                return;
            }

            btnNext.setText(currentIndex == questions.size() - 1 ? "Finish" : "Next");
            return;
        }

        // 2nd click: NEXT
        currentIndex++;
        if (currentIndex < questions.size()) {
            showQuestion();
        } else {
            onQuizFinished();
        }
    }

    private void onQuizFinished() {
        GameState gs = GameState.get();
        gs.markCourseCompleted(courseId);

        if (!isAdded()) return;

        // Persist state (don’t break old flow)
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateHeader();
        } else if (getContext() != null) {
            gs.saveToPrefs(getContext().getApplicationContext());
        }

        // ✅ Safe leaderboard write: never crash -> avoids “finish then logout” feeling
        try {
            String name;
            if (gs.profile != null && gs.profile.username != null && !gs.profile.username.isEmpty()) {
                name = gs.profile.username;
            } else {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                name = (user != null && user.getEmail() != null) ? user.getEmail() : "Player";
            }

            ScoreEntry entry = new ScoreEntry(name, score);
            FirebaseFirestore.getInstance()
                    .collection("scores")
                    .add(entry)
                    .addOnFailureListener(e -> { /* swallow */ });

        } catch (Exception ignored) {}

        // Go to congrats screen (your existing style)
        CongratsFragment fragment = CongratsFragment.newInstance(courseId, score, questions.size());
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack("congrats")
                .commit();
    }

    private void refreshHeader() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateHeader();
        }
    }

    private void submitUserFeedback() {
        if (etUserFeedback == null) return;

        String text = etUserFeedback.getText().toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(getContext(), "Please type feedback first.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (questions == null || questions.isEmpty() || currentIndex >= questions.size()) return;
        Question q = questions.get(currentIndex);

        Map<String, Object> data = new HashMap<>();
        data.put("courseId", courseId);
        data.put("questionIndex", currentIndex);
        data.put("prompt", q.getPrompt());
        data.put("lesson", q.getLesson());
        data.put("feedbackText", text);
        data.put("createdAt", Timestamp.now());

        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                data.put("userUid", user.getUid());
                if (user.getEmail() != null) data.put("userEmail", user.getEmail());
            } else {
                data.put("userUid", "anonymous");
            }
        } catch (Exception ignored) {
            data.put("userUid", "unknown");
        }

        try {
            btnSubmitFeedback.setEnabled(false);

            FirebaseFirestore.getInstance()
                    .collection("question_feedback")
                    .add(data)
                    .addOnSuccessListener(doc -> {
                        Toast.makeText(getContext(), "Thanks! Feedback submitted.", Toast.LENGTH_SHORT).show();
                        etUserFeedback.setText("");
                        btnSubmitFeedback.setEnabled(true);
                    })
                    .addOnFailureListener(e -> {
                        btnSubmitFeedback.setEnabled(true);
                        Toast.makeText(getContext(), "Feedback not sent (offline/Firebase config).", Toast.LENGTH_SHORT).show();
                    });

        } catch (Exception e) {
            btnSubmitFeedback.setEnabled(true);
            Toast.makeText(getContext(), "Feedback not available (Firebase not ready).", Toast.LENGTH_SHORT).show();
        }
    }
}

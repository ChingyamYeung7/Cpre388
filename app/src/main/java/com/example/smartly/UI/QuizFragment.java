package com.example.smartly.UI;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartly.GameState;
import com.example.smartly.R;
import com.example.smartly.model.Course;
import com.example.smartly.model.Question;
import com.example.smartly.util.CourseRepository;
import com.example.smartly.util.QuestionRepository;

import java.util.ArrayList;
import java.util.List;

public class QuizFragment extends Fragment {

    private static final String ARG_COURSE_ID = "course_id";

    private String courseId;
    private List<Question> questions = new ArrayList<>();
    private int currentIndex = 0;
    private int score = 0;

    private TextView tvExplanation;
    private TextView tvQuestion;
    private RadioGroup rgOptions;
    private RadioButton rb1, rb2, rb3;
    private Button btnNext;

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
        GameState.get().currentCourseId = courseId;

        questions = QuestionRepository.getQuestionsForCourse(courseId);
        if (questions == null) {
            questions = new ArrayList<>();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_quiz, container, false);

        tvExplanation = v.findViewById(R.id.tvExplanation);
        tvQuestion = v.findViewById(R.id.tvQuestion);
        rgOptions = v.findViewById(R.id.rgOptions);
        rb1 = v.findViewById(R.id.rbOption1);
        rb2 = v.findViewById(R.id.rbOption2);
        rb3 = v.findViewById(R.id.rbOption3);
        btnNext = v.findViewById(R.id.btnNext);

        btnNext.setOnClickListener(view -> onNextClicked());

        if (!questions.isEmpty()) {
            showQuestion();
        } else {
            tvQuestion.setText("No questions for this course yet.");
            btnNext.setEnabled(false);
        }

        return v;
    }

    private void showQuestion() {
        Question q = questions.get(currentIndex);

        // Explanation BEFORE the actual question
        tvExplanation.setText(q.getExplanation());

        // Simple label like "Choose the best answer:"
        tvQuestion.setText("Choose the best answer:");

        String[] opts = q.getOptions();
        rb1.setText(opts[0]);
        rb2.setText(opts[1]);
        rb3.setText(opts[2]);

        rgOptions.clearCheck();

        if (currentIndex == questions.size() - 1) {
            btnNext.setText("Finish");
        } else {
            btnNext.setText("Next");
        }
    }

    private void onNextClicked() {
        int checkedId = rgOptions.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(getContext(), "Please choose an answer", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedIndex;
        if (checkedId == R.id.rbOption1) selectedIndex = 0;
        else if (checkedId == R.id.rbOption2) selectedIndex = 1;
        else selectedIndex = 2;

        Question q = questions.get(currentIndex);
        if (selectedIndex == q.getCorrectIndex()) {
            score++;
            Toast.makeText(getContext(), "✅ Correct!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "❌ Not quite, but good try!", Toast.LENGTH_SHORT).show();
        }

        currentIndex++;
        if (currentIndex < questions.size()) {
            showQuestion();
        } else {
            onQuizFinished();
        }
    }

    private void onQuizFinished() {
        GameState.get().markCourseCompleted(courseId);

        // Open a big congrats page
        CongratsFragment fragment = CongratsFragment.newInstance(courseId, score, questions.size());
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack("congrats")
                .commit();
    }
}

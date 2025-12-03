package com.example.smartly.UI;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.smartly.R;
import com.example.smartly.model.Course;
import com.example.smartly.util.CourseRepository;

public class CongratsFragment extends Fragment {

    private static final String ARG_COURSE_ID = "course_id";
    private static final String ARG_SCORE = "score";
    private static final String ARG_TOTAL = "total";

    private String courseId;
    private int score;
    private int total;

    public CongratsFragment() {}

    public static CongratsFragment newInstance(String courseId, int score, int total) {
        CongratsFragment fragment = new CongratsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COURSE_ID, courseId);
        args.putInt(ARG_SCORE, score);
        args.putInt(ARG_TOTAL, total);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            courseId = getArguments().getString(ARG_COURSE_ID);
            score = getArguments().getInt(ARG_SCORE);
            total = getArguments().getInt(ARG_TOTAL);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_congrats, container, false);

        TextView tvTitle = v.findViewById(R.id.tvCongratsTitle);
        TextView tvMessage = v.findViewById(R.id.tvCongratsMessage);
        Button btnHome = v.findViewById(R.id.btnBackHome);

        Course course = CourseRepository.getCourseById(courseId);
        String name = (course != null) ? course.title : "this course";

        tvTitle.setText("🎉 Congratulations!");
        tvMessage.setText("You finished " + name + "!\n"
                + "Your score: " + score + " / " + total + "\n\n"
                + "You can now go back to Home and pick the next course.");

        btnHome.setOnClickListener(view -> {
            // Clear back stack and return to Home
            FragmentManager fm = requireActivity().getSupportFragmentManager();
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

            // Replace with HomeFragment as the main screen
            fm.beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        });

        return v;
    }
}

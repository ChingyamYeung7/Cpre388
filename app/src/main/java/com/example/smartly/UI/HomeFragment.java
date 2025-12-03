package com.example.smartly.UI;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smartly.GameState;
import com.example.smartly.MainActivity;
import com.example.smartly.R;
import com.example.smartly.model.Course;
import com.example.smartly.util.CourseRepository;

import java.util.List;

public class HomeFragment extends Fragment {

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        LinearLayout courseContainer = v.findViewById(R.id.courseContainer);

        List<Course> courses = CourseRepository.getAllCourses();
        GameState gs = GameState.get();

        for (Course c : courses) {
            TextView panel = new TextView(requireContext());
            panel.setText(c.title + "\n" + c.shortDesc);
            panel.setTextSize(18f);
            panel.setPadding(32, 32, 32, 32);
            panel.setTextColor(Color.WHITE);

            boolean completed = gs.isCourseCompleted(c.id);

            if (completed) {
                // Completed = green
                panel.setBackgroundColor(Color.parseColor("#2E7D32"));
            } else {
                // Not completed yet = blue
                panel.setBackgroundColor(Color.parseColor("#1565C0"));
            }

            // Space between panels
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(0, 0, 0, 32);
            panel.setLayoutParams(lp);

            panel.setOnClickListener(view -> {
                gs.currentCourseId = c.id;
                openQuizForCourse(c.id);
            });

            courseContainer.addView(panel);
        }

        return v;
    }

    private void openQuizForCourse(String courseId) {
        QuizFragment fragment = QuizFragment.newInstance(courseId);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack("quiz")
                .commit();
    }
}

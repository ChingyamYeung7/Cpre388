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
        GameState gs = GameState.get();

        // Get all courses/tasks
        List<Course> courses = CourseRepository.getAllCourses();

        courseContainer.removeAllViews();

        for (Course c : courses) {
            View panel = inflater.inflate(R.layout.item_course_panel, courseContainer, false);

            TextView tvTitle = panel.findViewById(R.id.tvCourseTitle);
            TextView tvDesc  = panel.findViewById(R.id.tvCourseDescription);

            // Use actual fields from Course.java
            tvTitle.setText(c.title);
            tvDesc.setText(c.shortDesc);

            // Color change if completed
            boolean completed = gs.isCourseCompleted(c.id);

            if (completed) {
                // Completed = green
                panel.setBackgroundColor(Color.parseColor("#2E7D32"));
            } else {
                // Not completed yet = blue
                panel.setBackgroundColor(Color.parseColor("#1565C0"));
            }

            // Set spacing between course tiles
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(0, 0, 0, 32);
            panel.setLayoutParams(lp);

            // On click → open Write tab → start the quiz
            panel.setOnClickListener(view -> {
                gs.currentCourseId = c.id;
                openQuizForCourse(c.id);
            });

            courseContainer.addView(panel);
        }

        return v;
    }

    private void openQuizForCourse(String courseId) {
        GameState.get().currentCourseId = courseId;

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).openWriteTabForCurrentCourse();
        }
    }
}

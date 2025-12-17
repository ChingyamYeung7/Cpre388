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

    private LinearLayout courseContainer;

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);
        courseContainer = v.findViewById(R.id.courseContainer);

        renderCourses(inflater);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        // ✅ makes panel colors update even if HomeFragment isn't recreated
        if (getView() != null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            renderCourses(inflater);
        }
    }

    private void renderCourses(LayoutInflater inflater) {
        if (courseContainer == null) return;

        GameState gs = GameState.get();
        List<Course> courses = CourseRepository.getAllCourses();

        courseContainer.removeAllViews();

        for (Course c : courses) {
            View panel = inflater.inflate(R.layout.item_course_panel, courseContainer, false);

            TextView tvTitle = panel.findViewById(R.id.tvCourseTitle);
            TextView tvDesc  = panel.findViewById(R.id.tvCourseDescription);

            tvTitle.setText(c.title);
            tvDesc.setText(c.shortDesc);

            boolean completed = gs.isCourseCompleted(c.id);

            if (completed) {
                panel.setBackgroundColor(Color.parseColor("#2E7D32")); // green
            } else {
                panel.setBackgroundColor(Color.parseColor("#1565C0")); // blue
            }

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
    }

    private void openQuizForCourse(String courseId) {
        GameState.get().currentCourseId = courseId;
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).openWriteTabForCurrentCourse();
        }
    }
}

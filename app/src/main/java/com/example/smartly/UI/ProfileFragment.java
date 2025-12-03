package com.example.smartly.UI;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.smartly.model.UserProfile;
import com.example.smartly.model.Course;
import com.example.smartly.util.CourseRepository;

public class ProfileFragment extends Fragment {

    private EditText etUsername, etEmail;
    private RadioGroup rgAvatar;
    private TextView tvCurrentCourse;

    public ProfileFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        etUsername = v.findViewById(R.id.etUsername);
        etEmail = v.findViewById(R.id.etEmail);
        rgAvatar = v.findViewById(R.id.rgAvatar);
        Button btnSave = v.findViewById(R.id.btnSaveProfile);
        Button btnLoad = v.findViewById(R.id.btnLoadProfile);
        Button btnDelete = v.findViewById(R.id.btnDeleteProfile);
        tvCurrentCourse = v.findViewById(R.id.tvCurrentCourse);
        updateCurrentCourse();

        btnSave.setOnClickListener(view -> saveProfile());
        btnLoad.setOnClickListener(view -> loadProfile());
        btnDelete.setOnClickListener(view -> deleteProfile());

        return v;
    }

    private void saveProfile() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), "Please enter username and email", Toast.LENGTH_SHORT).show();
            return;
        }

        int checkedId = rgAvatar.getCheckedRadioButtonId();
        RadioButton rb = rgAvatar.findViewById(checkedId);
        String avatar = rb != null ? rb.getText().toString() : "🙂";

        UserProfile profile = new UserProfile(username, email, avatar);
        GameState.get().profile = profile;

        SharedPreferences prefs = requireContext().getSharedPreferences("smartly_prefs", MODE_PRIVATE);
        prefs.edit()
                .putString("username", username)
                .putString("email", email)
                .putString("avatar", avatar)
                .apply();

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateHeader();
        }

        Toast.makeText(getContext(), "Profile saved", Toast.LENGTH_SHORT).show();
    }

    private void loadProfile() {
        SharedPreferences prefs = requireContext().getSharedPreferences("smartly_prefs", MODE_PRIVATE);
        String username = prefs.getString("username", "");
        String email = prefs.getString("email", "");
        String avatar = prefs.getString("avatar", "");

        etUsername.setText(username);
        etEmail.setText(email);

        for (int i = 0; i < rgAvatar.getChildCount(); i++) {
            View child = rgAvatar.getChildAt(i);
            if (child instanceof RadioButton) {
                RadioButton rb = (RadioButton) child;
                if (avatar.equals(rb.getText().toString())) {
                    rb.setChecked(true);
                }
            }
        }

        GameState.get().profile = new UserProfile(username, email, avatar);

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateHeader();
        }

        Toast.makeText(getContext(), "Profile loaded", Toast.LENGTH_SHORT).show();
    }

    private void updateCurrentCourse() {
        String id = GameState.get().currentCourseId;
        Course course = CourseRepository.getCourseById(id);
        if (course != null) {
            tvCurrentCourse.setText("Current course: " + course.title);
        } else {
            tvCurrentCourse.setText("Current course: -");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (tvCurrentCourse != null) {
            updateCurrentCourse();
        }
    }

    private void deleteProfile() {
        SharedPreferences prefs = requireContext().getSharedPreferences("smartly_prefs", MODE_PRIVATE);
        prefs.edit().clear().apply();
        etUsername.setText("");
        etEmail.setText("");
        rgAvatar.clearCheck();
        GameState.get().profile = null;

        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateHeader();
        }

        Toast.makeText(getContext(), "Profile deleted", Toast.LENGTH_SHORT).show();
    }
}

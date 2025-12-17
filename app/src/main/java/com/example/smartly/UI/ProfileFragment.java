package com.example.smartly.UI;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartly.GameState;
import com.example.smartly.LoginActivity;
import com.example.smartly.MainActivity;
import com.example.smartly.R;
import com.example.smartly.model.Course;
import com.example.smartly.model.UserProfile;
import com.example.smartly.util.CourseRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    private EditText etUsername, etEmail;
    private ImageView ivAvatar;
    private TextView tvCurrentCourse;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private String currentAvatarName = "avatar_default";

    private final String[] AVATAR_OPTIONS = {
            "avatar_default",
            "cool_pepe",
            "pepe_2",
            "pepe_3",
            "pepe_4",
            "pepe_5",
            "pepe_6"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        // Init Views
        etUsername = v.findViewById(R.id.etUsername);
        etEmail = v.findViewById(R.id.etEmail);
        ivAvatar = v.findViewById(R.id.ivProfileAvatar);
        tvCurrentCourse = v.findViewById(R.id.tvCurrentCourse);

        Button btnChangeAvatar = v.findViewById(R.id.btnChangeAvatar);
        Button btnSave = v.findViewById(R.id.btnSaveProfile);
        Button btnLoad = v.findViewById(R.id.btnLoadProfile);   // Restored
        Button btnDelete = v.findViewById(R.id.btnDeleteProfile); // Restored
        Button btnLogout = v.findViewById(R.id.btnLogout);

        // Init Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Listeners
        btnChangeAvatar.setOnClickListener(view -> showAvatarDialog());
        btnSave.setOnClickListener(view -> saveProfile());
        btnLoad.setOnClickListener(view -> loadProfile());     // Restored Logic
        btnDelete.setOnClickListener(view -> deleteProfile()); // Restored Logic
        btnLogout.setOnClickListener(view -> logout());

        // Load Data automatically on start
        loadProfile();
        updateCurrentCourse();

        return v;
    }

    private void showAvatarDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose your Pepe");
        builder.setItems(AVATAR_OPTIONS, (dialog, which) -> {
            currentAvatarName = AVATAR_OPTIONS[which];
            updateAvatarView(currentAvatarName);
        });
        builder.show();
    }

    private void updateAvatarView(String fileName) {
        int resId = getResources().getIdentifier(fileName, "drawable", requireContext().getPackageName());
        if (resId != 0) {
            ivAvatar.setImageResource(resId);
        }
    }


    private void saveProfile() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getContext(), "Username required", Toast.LENGTH_SHORT).show();
            return;
        }

        UserProfile profile = new UserProfile(username, email, currentAvatarName);

        // 1. Update GameState
        GameState.get().profile = profile;
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateHeader();
        }

        // 2. Save to Cloud
        if (auth.getCurrentUser() != null) {
            db.collection("users").document(auth.getCurrentUser().getUid())
                    .set(profile)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Profile Saved!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Save Failed", Toast.LENGTH_SHORT).show());
        }
    }

    private void loadProfile() {
        if (auth.getCurrentUser() == null) return;

        // Manual visual feedback
        Toast.makeText(getContext(), "Loading...", Toast.LENGTH_SHORT).show();

        db.collection("users").document(auth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        UserProfile profile = document.toObject(UserProfile.class);
                        if (profile != null) {
                            etUsername.setText(profile.getUsername());
                            etEmail.setText(profile.getEmail());

                            currentAvatarName = profile.getAvatarName();
                            if (currentAvatarName == null) currentAvatarName = "avatar_default";
                            updateAvatarView(currentAvatarName);

                            GameState.get().profile = profile;
                            if (getActivity() instanceof MainActivity) {
                                ((MainActivity) getActivity()).updateHeader();
                            }
                            Toast.makeText(getContext(), "Profile Loaded!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "No profile found.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteProfile() {
        if (auth.getCurrentUser() == null) return;

        // 1. Delete from Firestore Database
        db.collection("users").document(auth.getCurrentUser().getUid())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // 2. Delete Authentication Account
                    auth.getCurrentUser().delete()
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(getContext(), "Account Deleted", Toast.LENGTH_SHORT).show();
                                logout(); // Go back to login
                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to delete account auth", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to delete database record", Toast.LENGTH_SHORT).show());
    }

    private void logout() {
        auth.signOut();
        GameState.get().profile = null;
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void updateCurrentCourse() {
        String id = GameState.get().currentCourseId;
        Course course = CourseRepository.getCourseById(id);
        tvCurrentCourse.setText(course != null ? "Current: " + course.title : "Current: -");
    }


}
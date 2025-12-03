package com.example.smartly.UI;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartly.R;
import com.example.smartly.model.ScoreEntry;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardFragment extends Fragment {

    private ListView listLeaderboard;
    private FirebaseFirestore db;
    private ArrayAdapter<String> adapter;
    private List<String> items = new ArrayList<>();

    public LeaderboardFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        listLeaderboard = v.findViewById(R.id.listLeaderboard);
        Button btnRefresh = v.findViewById(R.id.btnRefreshLeaderboard);

        adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_list_item_1, items);
        listLeaderboard.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        btnRefresh.setOnClickListener(view -> loadScores());

        loadScores();
        return v;
    }

    private void loadScores() {
        items.clear();
        db.collection("scores")
                .orderBy("score", Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (com.google.firebase.firestore.QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        ScoreEntry e = doc.toObject(ScoreEntry.class);
                        items.add(e.toString());
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {});
    }
}

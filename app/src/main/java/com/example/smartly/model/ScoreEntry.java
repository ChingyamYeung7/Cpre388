package com.example.smartly.model;

public class ScoreEntry {
    public String name;
    public int score;

    public ScoreEntry() {}

    public ScoreEntry(String name, int score) {
        this.name = name;
        this.score = score;
    }

    @Override
    public String toString() {
        return name + " - " + score;
    }
}

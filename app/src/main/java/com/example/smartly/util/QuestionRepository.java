package com.example.smartly.util;

import com.example.smartly.model.Question;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuestionRepository {

    public static List<Question> getQuestionsForCourse(String courseId) {
        switch (courseId) {
            case "week1_voltage":
                return getWeek1VoltageQuestions();
            case "week2_current":
                return getWeek2CurrentQuestions();
            case "week3_resistors":
                return getWeek3ResistorsQuestions();
            case "week4_series":
                return getWeek4SeriesQuestions();
            default:
                return new ArrayList<>();
        }
    }

    // ---------- WEEK 1: VOLTAGE ----------
    private static List<Question> getWeek1VoltageQuestions() {
        List<Question> list = new ArrayList<>();

        list.add(Question.mcq(
                "week1_voltage",
                "Lesson: Voltage is like electrical “height” (energy level). Higher voltage means higher electrical potential.",
                "Q1: Voltage is most similar to:",
                new String[]{
                        "Height / energy level",
                        "Wire thickness",
                        "Current flow speed"
                },
                0,
                "Correct — voltage represents electrical potential (like height).",
                "Not quite — thickness and speed are not voltage."
        ));

        list.add(Question.mcq(
                "week1_voltage",
                "Lesson: Electricity “wants to move” when there is a voltage difference (high to low).",
                "Q2: What is required for current to start flowing?",
                new String[]{
                        "A voltage difference",
                        "Only a resistor",
                        "Only ground (0V) with nothing else"
                },
                0,
                "Yes — voltage difference provides the push.",
                "No — you need a difference (high vs low)."
        ));

        // Technique (easy + not ambiguous)
        list.add(Question.mcq(
                "week1_voltage",
                "Lesson: Ground is a reference point, defined as 0 V.",
                "Q3 (Technique): If point A is 9 V and ground is 0 V, the voltage difference is:",
                new String[]{
                        "9 V",
                        "0 V",
                        "-9 V"
                },
                0,
                "Correct — 9V above ground means a 9V difference.",
                "Careful — ground is 0V, so the difference is 9V."
        ));

        return list;
    }

    // ---------- WEEK 2: CURRENT ----------
    private static List<Question> getWeek2CurrentQuestions() {
        List<Question> list = new ArrayList<>();

        list.add(Question.mcq(
                "week2_current",
                "Lesson: Current is the flow rate of electric charge (like water flow rate in a pipe).",
                "Q1: Electric current is:",
                new String[]{
                        "Flow of electric charge",
                        "Electrical height",
                        "Opposition to flow"
                },
                0,
                "Correct — current is charge flow per time.",
                "No — height is voltage; opposition is resistance."
        ));

        list.add(Question.mcq(
                "week2_current",
                "Lesson: Voltage is the push. With no voltage difference, there is no push.",
                "Q2: If the voltage difference is 0V, current is:",
                new String[]{
                        "0 (no flow)",
                        "Maximum",
                        "Random"
                },
                0,
                "Correct — no push means no flow (simplified model).",
                "No — current needs a voltage difference."
        ));

        // Technique rule
        list.add(Question.mcq(
                "week2_current",
                "Lesson: Conventional current direction is defined from high voltage to low voltage.",
                "Q3 (Technique): Conventional current direction is:",
                new String[]{
                        "High voltage → low voltage",
                        "Low voltage → high voltage",
                        "No defined direction"
                },
                0,
                "Correct — that’s the definition used in circuit diagrams.",
                "Not correct — diagrams use high → low for conventional current."
        ));

        return list;
    }

    // ---------- WEEK 3: RESISTORS ----------
    private static List<Question> getWeek3ResistorsQuestions() {
        List<Question> list = new ArrayList<>();

        list.add(Question.mcq(
                "week3_resistors",
                "Lesson: A resistor limits current. More resistance means less current if voltage stays the same (I = V/R).",
                "Q1: Main job of a resistor:",
                new String[]{
                        "Limit current",
                        "Increase voltage",
                        "Create energy"
                },
                0,
                "Correct — resistors limit current.",
                "No — resistors don’t increase voltage or create energy."
        ));

        list.add(Question.mcq(
                "week3_resistors",
                "Lesson: Ohm’s Law: I = V/R. If R goes up (same V), I goes down.",
                "Q2: If resistance increases, current will:",
                new String[]{
                        "Decrease",
                        "Increase",
                        "Stay the same"
                },
                0,
                "Correct — bigger R gives smaller I.",
                "Not quite — use I = V/R."
        ));

        // Drag-drop technique: cause → effect, clear and non-ambiguous
        list.add(Question.dragDropOrder(
                "week3_resistors",
                "Lesson: Resistance affects how easily current flows.",
                "Q3 (Drag): Arrange cause → effect:",
                Arrays.asList("Resistance increases", "Harder for current to flow", "Current decreases"),
                Arrays.asList("Resistance increases", "Harder for current to flow", "Current decreases"),
                "Perfect — that cause-and-effect chain is correct.",
                "Try again: resistance ↑ → harder to flow → current ↓."
        ));

        return list;
    }

    // ---------- WEEK 4: SERIES CIRCUITS ----------
    private static List<Question> getWeek4SeriesQuestions() {
        List<Question> list = new ArrayList<>();

        list.add(Question.mcq(
                "week4_series",
                "Lesson: A series circuit has ONE path for current. Components are end-to-end.",
                "Q1: A series circuit means:",
                new String[]{
                        "One path for current",
                        "Many paths for current",
                        "No current can ever flow"
                },
                0,
                "Correct — series has one path.",
                "No — many paths describes parallel."
        ));

        list.add(Question.dragDropOrder(
                "week4_series",
                "Lesson: In series, current flows through each component in order along one loop.",
                "Q2 (Drag): Arrange a valid series path order:",
                Arrays.asList("Battery", "Resistor", "LED"),
                Arrays.asList("Battery", "Resistor", "LED"),
                "Correct — that forms a simple series loop order.",
                "In series, it’s one path: source → component → component."
        ));

        list.add(Question.mcq(
                "week4_series",
                "Lesson: In a series circuit, current does not split.",
                "Q3: Which statement is TRUE in series?",
                new String[]{
                        "Current is the same everywhere",
                        "Voltage is the same everywhere",
                        "Resistance becomes zero"
                },
                0,
                "Correct — current is the same through all series components.",
                "No — voltage divides in series; current stays the same."
        ));

        return list;
    }
}

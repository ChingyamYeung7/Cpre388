package com.example.smartly.util;

import com.example.smartly.model.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionRepository {

    // Central API: ask for questions by courseId
    public static List<Question> getQuestionsForCourse(String courseId) {
        switch (courseId) {
            case "week1_voltage":
                return getWeek1VoltageQuestions();
            case "week2_current":
                return getWeek2CurrentQuestions();
            default:
                return new ArrayList<>();
        }
    }

    // ---------- WEEK 1: VOLTAGE ----------
    private static List<Question> getWeek1VoltageQuestions() {
        List<Question> list = new ArrayList<>();

        // Q1
        list.add(new Question(
                "Teacher: Water always flows from high places to low places.\n"
                        + "Question: Why doesn’t water normally flow upward?",
                new String[]{
                        "Because of gravity / height difference",
                        "Because it is lazy",
                        "Because it is invisible"
                },
                0,
                "week1_voltage"
        ));

        // Q2
        list.add(new Question(
                "Teacher: A higher place stores more potential energy.\n"
                        + "Imagine you jump off somewhere. From where do you feel more energy?",
                new String[]{
                        "From a high platform",
                        "From flat ground",
                        "Same energy everywhere"
                },
                0,
                "week1_voltage"
        ));

        // Q3
        list.add(new Question(
                "Teacher: In the world of electricity, we also have a 'height'.\n"
                        + "We call this height 'voltage'. Higher voltage = higher electrical level.",
                new String[]{
                        "Voltage is like height for electricity",
                        "Voltage is the color of electricity",
                        "Voltage is the sound of electricity"
                },
                0,
                "week1_voltage"
        ));

        // Q4
        list.add(new Question(
                "Teacher: Voltage difference makes electricity want to move.\n"
                        + "Just like water flows from a high place to a low place.",
                new String[]{
                        "Electricity flows from high voltage to low voltage",
                        "Electricity flows from low voltage to high voltage",
                        "Electricity doesn’t flow at all"
                },
                0,
                "week1_voltage"
        ));

        // Q5
        list.add(new Question(
                "Teacher: We call the lowest point 'ground'.\n"
                        + "Ground is 0 volts, like the floor in our height example.",
                new String[]{
                        "Ground is the lowest point, 0 V",
                        "Ground is the highest point",
                        "Ground has no voltage concept"
                },
                0,
                "week1_voltage"
        ));

        // Q6 (concept summary)
        list.add(new Question(
                "Teacher: So voltage is like the height of a slide.\n"
                        + "The higher the slide, the more the water/electricity wants to go down.",
                new String[]{
                        "Voltage is like the height of a slide",
                        "Voltage is like the color of a slide",
                        "Voltage is like the sound of a slide"
                },
                0,
                "week1_voltage"
        ));

        return list;
    }

    // ---------- WEEK 2: CURRENT ----------
    private static List<Question> getWeek2CurrentQuestions() {
        List<Question> list = new ArrayList<>();

        // Q1
        list.add(new Question(
                "Teacher: Last week we said voltage is like height difference.\n"
                        + "If there is height difference, water will flow.",
                new String[]{
                        "That water flow is called 'water current'",
                        "That water flow is called 'water color'",
                        "That water flow is called 'water sound'"
                },
                0,
                "week2_current"
        ));

        // Q2
        list.add(new Question(
                "Teacher: In electricity, something flows too – not water but electrons.\n"
                        + "The flow of electrons is called 'electric current'.",
                new String[]{
                        "Current is the flow of electricity",
                        "Current is the shape of the wire",
                        "Current is just voltage with a new name"
                },
                0,
                "week2_current"
        ));

        // Q3
        list.add(new Question(
                "Teacher: Voltage is like the height difference that pushes water.\n"
                        + "Without height difference, water will not flow.",
                new String[]{
                        "Without voltage, there is no current",
                        "Without voltage, current gets bigger",
                        "Current and voltage have no relationship"
                },
                0,
                "week2_current"
        ));

        // Q4
        list.add(new Question(
                "Teacher: We define the direction of current as from high voltage to low voltage.\n"
                        + "Even though electrons move the opposite way, we will use this rule.",
                new String[]{
                        "Current flows from high voltage to low voltage",
                        "Current flows from low voltage to high voltage",
                        "Current direction is completely random"
                },
                0,
                "week2_current"
        ));

        // Q5
        list.add(new Question(
                "Teacher: The size of current is like how strong the water flow is.\n"
                        + "Bigger current = more charge flowing every second.",
                new String[]{
                        "If a bulb is brighter, current is larger",
                        "If a bulb is brighter, current is smaller",
                        "Bulb brightness has nothing to do with current"
                },
                0,
                "week2_current"
        ));

        // Q6 summary
        list.add(new Question(
                "Teacher: Current is the flow of electricity in a wire.\n"
                        + "Voltage is the push; current is the actual flow.",
                new String[]{
                        "Current is like water flowing in a pipe",
                        "Current is like the pipe’s color",
                        "Current is like the pipe’s name"
                },
                0,
                "week2_current"
        ));

        return list;
    }
}

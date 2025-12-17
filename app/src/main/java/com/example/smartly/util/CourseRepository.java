package com.example.smartly.util;

import com.example.smartly.model.Course;

import java.util.ArrayList;
import java.util.List;

public class CourseRepository {

    private static final List<Course> COURSES = new ArrayList<>();

    static {
        // Week 1: Voltage
        COURSES.add(new Course(
                "week1_voltage",
                "Week 1 – Voltage",
                "Voltage as height & energy"
        ));

        // Week 2: Current
        COURSES.add(new Course(
                "week2_current",
                "Week 2 – Current",
                "Current as electric flow"
        ));

        // ✅ Week 3: Resistors (NEW)
        COURSES.add(new Course(
                "week3_resistors",
                "Week 3 – Discuss Resistors",
                "What a resistor does and how it limits current"
        ));

        // ✅ Week 4: Series Circuits (NEW)
        COURSES.add(new Course(
                "week4_series",
                "Week 4 – Circuit in Series",
                "One path: current stays the same; voltages add up"
        ));

        // 👉 In future, just add new Course(...) here for Week 5, 6, ...
    }

    public static List<Course> getAllCourses() {
        return COURSES;
    }

    public static Course getCourseById(String id) {
        for (Course c : COURSES) {
            if (c.id.equals(id)) return c;
        }
        return null;
    }
}

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

        // 👉 In future, just add new Course(...) here for Week 3, 4, ...
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

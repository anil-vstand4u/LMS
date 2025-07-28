package com.lms.service;

import com.lms.model.Course;
import com.lms.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CourseService {
    Course createCourse(Course course);
    Course updateCourse(Course course);
    void deleteCourse(Long id);
    Course findById(Long id);
    Page<Course> findAll(Pageable pageable);
    List<Course> findByInstructor(User instructor);
    void enrollStudent(Long courseId, Long studentId);
    void unenrollStudent(Long courseId, Long studentId);
    List<Course> findEnrolledCourses(Long studentId);
    boolean isEnrolled(Long courseId, Long studentId);
    boolean hasAvailableSpots(Long courseId);
    void validateEnrollment(Long courseId, Long studentId);
}

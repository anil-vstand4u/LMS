package com.lms.service.impl;

import com.lms.model.Course;
import com.lms.model.Enrollment;
import com.lms.model.User;
import com.lms.repository.CourseRepository;
import com.lms.repository.EnrollmentRepository;
import com.lms.repository.UserRepository;
import com.lms.service.CourseService;
import com.lms.service.EmailService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final EmailService emailService;

    public CourseServiceImpl(CourseRepository courseRepository,
                           UserRepository userRepository,
                           EnrollmentRepository enrollmentRepository,
                           EmailService emailService) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.emailService = emailService;
    }

    @Override
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public Course updateCourse(Course course) {
        Course existingCourse = findById(course.getId());
        return courseRepository.save(course);
    }

    @Override
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    @Override
    public Course findById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    }

    @Override
    public Page<Course> findAll(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }

    @Override
    public List<Course> findByInstructor(User instructor) {
        return courseRepository.findByInstructor(instructor);
    }

    @Override
    public void enrollStudent(Long courseId, Long studentId) {
        Course course = findById(courseId);
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        validateEnrollment(courseId, studentId);

        Enrollment enrollment = Enrollment.builder()
                .course(course)
                .student(student)
                .build();

        course.addEnrollment(enrollment);
        courseRepository.save(course);
        
        // Notify student of successful enrollment
        emailService.sendEmail(student.getEmail(),
                "Course Enrollment Confirmation",
                "You have been successfully enrolled in " + course.getTitle());
    }

    @Override
    public void unenrollStudent(Long courseId, Long studentId) {
        Course course = findById(courseId);
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Enrollment enrollment = enrollmentRepository.findByCourseAndStudent(course, student)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        course.removeEnrollment(enrollment);
        courseRepository.save(course);
    }

    @Override
    public List<Course> findEnrolledCourses(Long studentId) {
        return courseRepository.findByEnrollmentsStudentId(studentId);
    }

    @Override
    public boolean isEnrolled(Long courseId, Long studentId) {
        return enrollmentRepository.existsByCourseIdAndStudentId(courseId, studentId);
    }

    @Override
    public boolean hasAvailableSpots(Long courseId) {
        Course course = findById(courseId);
        if (course.getMaxStudents() == null) {
            return true;
        }
        long enrolledCount = enrollmentRepository.countByCourseId(courseId);
        return enrolledCount < course.getMaxStudents();
    }

    @Override
    public void validateEnrollment(Long courseId, Long studentId) {
        if (isEnrolled(courseId, studentId)) {
            throw new RuntimeException("Student already enrolled in this course");
        }
        if (!hasAvailableSpots(courseId)) {
            throw new RuntimeException("Course is full");
        }
    }
}

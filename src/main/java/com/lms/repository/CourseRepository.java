package com.lms.repository;

import com.lms.model.Course;
import com.lms.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    
    List<Course> findByInstructor(User instructor);
    
    Page<Course> findByInstructor(User instructor, Pageable pageable);
    
    @Query("SELECT c FROM Course c WHERE c.startDate >= :startDate AND c.endDate <= :endDate")
    List<Course> findCoursesInDateRange(@Param("startDate") LocalDate startDate, 
                                      @Param("endDate") LocalDate endDate);
    
    @Query("SELECT c FROM Course c WHERE LOWER(c.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Course> searchCourses(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT c FROM Course c WHERE c.maxStudents > " +
           "(SELECT COUNT(e) FROM Enrollment e WHERE e.course = c AND e.status = 'ACTIVE')")
    List<Course> findAvailableCourses();
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId AND e.status = 'ACTIVE'")
    long getActiveEnrollmentsCount(@Param("courseId") Long courseId);
}

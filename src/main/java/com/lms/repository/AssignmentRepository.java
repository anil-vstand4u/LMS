package com.lms.repository;

import com.lms.model.Assignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    
    List<Assignment> findByCourseId(Long courseId);
    
    Page<Assignment> findByCourseId(Long courseId, Pageable pageable);
    
    @Query("SELECT a FROM Assignment a WHERE a.course.id = :courseId AND a.deadline > :now")
    List<Assignment> findUpcomingAssignments(@Param("courseId") Long courseId, 
                                           @Param("now") LocalDateTime now);
    
    @Query("SELECT a FROM Assignment a WHERE a.deadline BETWEEN :start AND :end")
    List<Assignment> findAssignmentsInDateRange(@Param("start") LocalDateTime start, 
                                              @Param("end") LocalDateTime end);
    
    @Query("SELECT a FROM Assignment a WHERE a.course.instructor.id = :instructorId " +
           "AND a.deadline > :now ORDER BY a.deadline ASC")
    List<Assignment> findUpcomingAssignmentsByInstructor(@Param("instructorId") Long instructorId, 
                                                        @Param("now") LocalDateTime now);
    
    @Query("SELECT COUNT(s) FROM Submission s WHERE s.assignment.id = :assignmentId")
    long countSubmissions(@Param("assignmentId") Long assignmentId);
}

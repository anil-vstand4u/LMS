package com.lms.repository;

import com.lms.model.Submission;
import com.lms.model.SubmissionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    
    List<Submission> findByAssignmentId(Long assignmentId);
    
    List<Submission> findByStudentId(Long studentId);
    
    Optional<Submission> findByStudentIdAndAssignmentId(Long studentId, Long assignmentId);
    
    @Query("SELECT s FROM Submission s WHERE s.assignment.id = :assignmentId AND s.status = :status")
    Page<Submission> findByAssignmentIdAndStatus(@Param("assignmentId") Long assignmentId,
                                               @Param("status") SubmissionStatus status,
                                               Pageable pageable);
    
    @Query("SELECT s FROM Submission s WHERE s.assignment.course.instructor.id = :instructorId " +
           "AND s.status = 'SUBMITTED' ORDER BY s.submissionDate ASC")
    List<Submission> findPendingGradingByInstructor(@Param("instructorId") Long instructorId);
    
    @Query("SELECT AVG(s.grade) FROM Submission s WHERE s.assignment.id = :assignmentId " +
           "AND s.status = 'GRADED'")
    Double calculateAverageGrade(@Param("assignmentId") Long assignmentId);
    
    @Query("SELECT s FROM Submission s WHERE s.assignment.course.id = :courseId " +
           "AND s.student.id = :studentId ORDER BY s.submissionDate DESC")
    List<Submission> findStudentSubmissionsForCourse(@Param("courseId") Long courseId,
                                                    @Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(s) FROM Submission s WHERE s.assignment.id = :assignmentId " +
           "AND s.submissionDate > s.assignment.deadline")
    long countLateSubmissions(@Param("assignmentId") Long assignmentId);
}

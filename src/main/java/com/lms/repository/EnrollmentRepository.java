package com.lms.repository;

import com.lms.model.Enrollment;
import com.lms.model.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    
    List<Enrollment> findByStudentId(Long studentId);
    
    List<Enrollment> findByCourseId(Long courseId);
    
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
    
    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.status = :status")
    List<Enrollment> findByStudentIdAndStatus(@Param("studentId") Long studentId, 
                                            @Param("status") EnrollmentStatus status);
    
    @Query("SELECT e FROM Enrollment e WHERE e.course.id = :courseId AND e.status = :status")
    Page<Enrollment> findByCourseIdAndStatus(@Param("courseId") Long courseId, 
                                           @Param("status") EnrollmentStatus status,
                                           Pageable pageable);
    
    @Query("SELECT AVG(e.grade) FROM Enrollment e WHERE e.course.id = :courseId AND e.status = 'COMPLETED'")
    Double calculateAverageCourseGrade(@Param("courseId") Long courseId);
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId AND e.status = :status")
    long countByCourseIdAndStatus(@Param("courseId") Long courseId, 
                                 @Param("status") EnrollmentStatus status);
}

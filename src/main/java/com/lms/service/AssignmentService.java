package com.lms.service;

import com.lms.model.Assignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AssignmentService {
    Assignment createAssignment(Assignment assignment);
    Assignment updateAssignment(Assignment assignment);
    void deleteAssignment(Long id);
    Assignment findById(Long id);
    List<Assignment> findByCourseId(Long courseId);
    Page<Assignment> findAll(Pageable pageable);
    List<Assignment> findPendingAssignments(Long studentId);
    boolean isOverdue(Assignment assignment);
    void notifyStudentsOfNewAssignment(Assignment assignment);
    void notifyStudentsOfDeadline(Assignment assignment);
}

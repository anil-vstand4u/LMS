package com.lms.service;

import com.lms.model.Submission;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SubmissionService {
    Submission submitAssignment(Long assignmentId, Long studentId, MultipartFile file);
    Submission gradeSubmission(Long submissionId, Double grade, String feedback);
    Submission findById(Long id);
    List<Submission> findByAssignmentId(Long assignmentId);
    List<Submission> findByStudentId(Long studentId);
    boolean hasSubmitted(Long assignmentId, Long studentId);
    void validateSubmission(Long assignmentId, Long studentId);
    String storeFile(MultipartFile file);
    void deleteFile(String filePath);
}

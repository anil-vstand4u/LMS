package com.lms.service.impl;

import com.lms.model.Assignment;
import com.lms.model.Submission;
import com.lms.model.User;
import com.lms.repository.AssignmentRepository;
import com.lms.repository.SubmissionRepository;
import com.lms.repository.UserRepository;
import com.lms.service.EmailService;
import com.lms.service.SubmissionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final Path fileStorageLocation;

    public SubmissionServiceImpl(
            SubmissionRepository submissionRepository,
            AssignmentRepository assignmentRepository,
            UserRepository userRepository,
            EmailService emailService,
            @Value("${file.upload-dir}") String uploadDir) {
        this.submissionRepository = submissionRepository;
        this.assignmentRepository = assignmentRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create upload directory", ex);
        }
    }

    @Override
    public Submission submitAssignment(Long assignmentId, Long studentId, MultipartFile file) {
        validateSubmission(assignmentId, studentId);
        
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        String filePath = storeFile(file);
        
        Submission submission = Submission.builder()
                .assignment(assignment)
                .student(student)
                .submissionDate(LocalDateTime.now())
                .filePath(filePath)
                .build();

        submission = submissionRepository.save(submission);

        // Notify instructor of new submission
        emailService.sendEmail(
            assignment.getCourse().getInstructor().getEmail(),
            "New Assignment Submission",
            String.format("Student %s has submitted assignment: %s",
                student.getEmail(),
                assignment.getTitle())
        );

        return submission;
    }

    @Override
    public Submission gradeSubmission(Long submissionId, Double grade, String feedback) {
        Submission submission = findById(submissionId);
        submission.setGrade(grade);
        submission.setFeedback(feedback);
        submission.setGradedDate(LocalDateTime.now());
        
        submission = submissionRepository.save(submission);

        // Notify student of grading
        emailService.sendGradeNotification(
            submission.getStudent().getEmail(),
            submission.getAssignment().getCourse().getTitle(),
            submission.getAssignment().getTitle(),
            grade
        );

        return submission;
    }

    @Override
    public Submission findById(Long id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found"));
    }

    @Override
    public List<Submission> findByAssignmentId(Long assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId);
    }

    @Override
    public List<Submission> findByStudentId(Long studentId) {
        return submissionRepository.findByStudentId(studentId);
    }

    @Override
    public boolean hasSubmitted(Long assignmentId, Long studentId) {
        return submissionRepository.existsByAssignmentIdAndStudentId(assignmentId, studentId);
    }

    @Override
    public void validateSubmission(Long assignmentId, Long studentId) {
        if (hasSubmitted(assignmentId, studentId)) {
            throw new RuntimeException("Assignment already submitted");
        }

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));

        if (assignment.getDueDate() != null && LocalDateTime.now().isAfter(assignment.getDueDate())) {
            throw new RuntimeException("Assignment submission deadline has passed");
        }
    }

    @Override
    public String storeFile(MultipartFile file) {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + fileName, ex);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            Path targetLocation = this.fileStorageLocation.resolve(filePath);
            Files.deleteIfExists(targetLocation);
        } catch (IOException ex) {
            throw new RuntimeException("Could not delete file " + filePath, ex);
        }
    }
}

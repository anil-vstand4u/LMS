package com.lms.service.impl;

import com.lms.model.Assignment;
import com.lms.model.Course;
import com.lms.model.Enrollment;
import com.lms.repository.AssignmentRepository;
import com.lms.service.AssignmentService;
import com.lms.service.EmailService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AssignmentServiceImpl implements AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final EmailService emailService;

    public AssignmentServiceImpl(AssignmentRepository assignmentRepository,
                               EmailService emailService) {
        this.assignmentRepository = assignmentRepository;
        this.emailService = emailService;
    }

    @Override
    public Assignment createAssignment(Assignment assignment) {
        Assignment savedAssignment = assignmentRepository.save(assignment);
        notifyStudentsOfNewAssignment(savedAssignment);
        return savedAssignment;
    }

    @Override
    public Assignment updateAssignment(Assignment assignment) {
        Assignment existingAssignment = findById(assignment.getId());
        return assignmentRepository.save(assignment);
    }

    @Override
    public void deleteAssignment(Long id) {
        assignmentRepository.deleteById(id);
    }

    @Override
    public Assignment findById(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
    }

    @Override
    public List<Assignment> findByCourseId(Long courseId) {
        return assignmentRepository.findByCourseId(courseId);
    }

    @Override
    public Page<Assignment> findAll(Pageable pageable) {
        return assignmentRepository.findAll(pageable);
    }

    @Override
    public List<Assignment> findPendingAssignments(Long studentId) {
        return assignmentRepository.findPendingAssignmentsForStudent(studentId);
    }

    @Override
    public boolean isOverdue(Assignment assignment) {
        return assignment.getDueDate() != null && 
               LocalDateTime.now().isAfter(assignment.getDueDate());
    }

    @Override
    public void notifyStudentsOfNewAssignment(Assignment assignment) {
        Course course = assignment.getCourse();
        for (Enrollment enrollment : course.getEnrollments()) {
            emailService.sendAssignmentNotification(
                enrollment.getStudent().getEmail(),
                assignment.getTitle(),
                course.getTitle()
            );
        }
    }

    @Override
    public void notifyStudentsOfDeadline(Assignment assignment) {
        Course course = assignment.getCourse();
        for (Enrollment enrollment : course.getEnrollments()) {
            emailService.sendEmail(
                enrollment.getStudent().getEmail(),
                "Assignment Deadline Reminder",
                String.format("The assignment '%s' for course '%s' is due on %s",
                    assignment.getTitle(),
                    course.getTitle(),
                    assignment.getDueDate())
            );
        }
    }

    @Scheduled(cron = "0 0 10 * * *") // Run at 10:00 AM every day
    public void checkAndNotifyUpcomingDeadlines() {
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        List<Assignment> assignments = assignmentRepository.findByDueDateBetween(
            tomorrow.withHour(0).withMinute(0),
            tomorrow.withHour(23).withMinute(59)
        );
        
        for (Assignment assignment : assignments) {
            notifyStudentsOfDeadline(assignment);
        }
    }
}

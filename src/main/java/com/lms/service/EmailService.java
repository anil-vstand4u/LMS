package com.lms.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
    void sendPasswordResetEmail(String to, String resetToken);
    void sendAssignmentNotification(String to, String assignmentTitle, String courseName);
    void sendGradeNotification(String to, String courseName, String assignmentTitle, Double grade);
}

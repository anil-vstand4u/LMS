package com.lms.service.impl;

import com.lms.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetToken) {
        String subject = "Password Reset Request";
        String body = "To reset your password, click the following link: \n"
                   + "http://localhost:8080/reset-password?token=" + resetToken;
        sendEmail(to, subject, body);
    }

    @Override
    public void sendAssignmentNotification(String to, String assignmentTitle, String courseName) {
        String subject = "New Assignment Posted: " + assignmentTitle;
        String body = String.format("A new assignment '%s' has been posted in the course '%s'.", 
                                  assignmentTitle, courseName);
        sendEmail(to, subject, body);
    }

    @Override
    public void sendGradeNotification(String to, String courseName, String assignmentTitle, Double grade) {
        String subject = "Assignment Graded: " + assignmentTitle;
        String body = String.format("Your assignment '%s' in course '%s' has been graded. Grade: %.2f", 
                                  assignmentTitle, courseName, grade);
        sendEmail(to, subject, body);
    }
}

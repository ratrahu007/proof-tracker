package com.prooftracker.notification.provider;

import com.prooftracker.notification.dto.NotificationRequest;
import com.prooftracker.notification.enums.NotificationChannel;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;


@Component
public class EmailNotificationProvider implements NotificationProvider{

    private final JavaMailSender mailSender;

    public EmailNotificationProvider(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public void send(NotificationRequest request) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(request.getRecipient());
        message.setSubject(request.getSubject());
        message.setText(request.getMessage());

        mailSender.send(message);
    }
}

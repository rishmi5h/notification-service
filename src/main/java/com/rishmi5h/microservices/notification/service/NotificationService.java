package com.rishmi5h.microservices.notification.service;

import com.rishmi5h.microservices.order.event.OrderPlacedEvent;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender javaMailSender;

    @KafkaListener(topics = "order-placed", groupId = "notification")
    public void listen(OrderPlacedEvent orderPlacedEvent) {
        log.info("Got Message form order-placed topic {}", orderPlacedEvent);
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            mimeMessage.setFrom("rishabhmishrabu@gmail.com");
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(orderPlacedEvent.getEmail()));
            mimeMessage.setSubject(String.format("Your Order with id %s is placed successfully", orderPlacedEvent.getOrderId()));
            mimeMessage.setText(String.format("""
                    Dear Customer,
                                       \s
                    Your order with id %s is placed successfully.
                    Thanks for shopping with us.
                                       \s
                    Regards,
                    Black Unlimited Team
                   \s""", orderPlacedEvent.getOrderId()));
        };

        try {
            javaMailSender.send(messagePreparator);
            log.info("Order Notification email sent successfully");
        } catch (MailException e) {
            log.error("Error while sending mail", e);
            throw new RuntimeException("Exception while sending mail to rishabhmishrabu@gmail.com", e);
        }
    }
}

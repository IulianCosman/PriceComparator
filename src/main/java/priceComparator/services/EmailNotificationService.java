package priceComparator.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class EmailNotificationService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPriceReachedEmail(String to, String productName, LocalDate createdAt, double price) {
        String subject = "Price Alert Triggered!";
        String body = String.format("Good news!\n\nThe product " +  productName + ", for which you have created an alert at "  + createdAt + " has dropped to " +  price +" RON.\n\nHappy shopping!");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("iuliancosman709@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}

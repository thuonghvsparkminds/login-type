package com.example.logintype.service.util;

import com.example.logintype.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Component
@RequiredArgsConstructor
public class EmailUtil {

    @Value("${spring.mail.username}")
    private String username;

    private final JavaMailSender javaMailSender;

    public void sendMailToken(String email, String token, String target) {

        try {

            String linkTarget = target.trim().replace(" ", "-");
            String link= "http://localhost:3000/" + linkTarget + "?token=" + token;
            setupMailToken(email, link, target);
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch(MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendMailOtp(String email, Long otp) {

        try {
            setupMailOtp(email, otp);
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch(MessagingException e) {
            e.printStackTrace();
        }
    }

    public void setupMailToken(String email, String resetPasswordLink, String target)
            throws UnsupportedEncodingException, MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(username, "SparkMinds");
        helper.setTo(email);

        String subject = "Link " + target;
        String content = "<p> Hello " + email + ", </p>"
                + "<p> You have requested to " + target +", </p>"
                + "<p> Click the link below to change " + target+ " </p>"
                + "<p> <a href = " + resetPasswordLink + " > Change your password </a></p>"
                + "<p> Ignore this email if you don't want to " + target + "</p>";

        helper.setSubject(subject);
        helper.setText(content,true);

        javaMailSender.send(message);
    }

    public void setupMailOtp(String email, Long Otp)
            throws UnsupportedEncodingException, MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(username, "SparkMinds");
        helper.setTo(email);

        String subject = "Verify account";
        String content = "<p> Hello " + email + ", </p>"
                + "<p> You have requested to reset password, </p>"
                + "<p> Your OTP to verify account: " + Otp
                + "<p> Ignore this email if you do remember your password </p>";

        helper.setSubject(subject);
        helper.setText(content,true);

        javaMailSender.send(message);
    }
}

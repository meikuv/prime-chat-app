package com.meikuv.chatapp.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final static Logger LOGGER = LoggerFactory
            .getLogger(EmailService.class);
    private static final Random random = new Random();
    private final JavaMailSender mailSender;

    @Async
    public void sendHTMLEmail(String to, String username, String code)  {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            String htmlTemplate = readFile("/home/meikuv/IdeaProjects/chat-app/src/main/resources/templates/verification_code.html");
            String htmlContent = htmlTemplate.replace("${username}", username);
            htmlContent = htmlContent.replace("${code}", code);

            helper.setText(htmlContent, true);
            helper.setTo(to);
            helper.setSubject("Подтвердите свою электронную почту");
            helper.setFrom("primechat@meikuv.xyz");
            mailSender.send(mimeMessage);
        } catch (MessagingException | IOException e) {
            LOGGER.error("failed to send email: ", e);
            throw new IllegalStateException("failed to send email");
        }
    }

    private String readFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);

        return Files.readString(path, StandardCharsets.UTF_8);
    }

    public String generateCode() {
        String digits = "0123456789";
        char[] DIGIT_CHARS = digits.toCharArray();
        char[] codeArray = new char[6];
        for (int i = 0; i < codeArray.length; i++) {
            codeArray[i] = DIGIT_CHARS[random.nextInt(DIGIT_CHARS.length)];
        }

        return new String(codeArray);
    }
}

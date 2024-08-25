package dac2dac.doctect.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private final Map<String, String> emailAuthMap = new HashMap<>();

    // 랜덤 인증 코드 생성
    private String createCode() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(3);

            switch (index) {
                case 0 -> key.append((char) (random.nextInt(26) + 97)); // 소문자
                case 1 -> key.append((char) (random.nextInt(26) + 65)); // 대문자
                case 2 -> key.append(random.nextInt(10)); // 숫자
            }
        }
        return key.toString();
    }

    // 메일 양식 작성
    public MimeMessage createEmailForm(String email) throws MessagingException, UnsupportedEncodingException {
        String authNum = createCode(); // 인증 코드 생성

        String title = "Doc'Tech 인증 번호"; // 이메일 제목

        MimeMessage message = emailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, email); // 받는 사람의 이메일 주소 설정
        message.setSubject(title); // 제목 설정
        message.setFrom(fromEmail); // 보내는 사람의 이메일 주소 설정
        message.setContent(setContext(authNum), "text/html; charset=utf-8");

        emailAuthMap.put(email, authNum); // 이메일과 인증 코드를 매핑하여 저장

        return message;
    }

    // 실제 메일 전송
    public String sendEmail(String toEmail) throws MessagingException, UnsupportedEncodingException {
        MimeMessage emailForm = createEmailForm(toEmail); // 메일 양식 작성
        emailSender.send(emailForm); // 메일 전송
        return emailAuthMap.get(toEmail); // 전송된 인증 코드를 반환
    }

    // 인증 코드 검증
    public boolean verifyAuthCode(String email, String authCode) {
        String storedCode = emailAuthMap.get(email);
        return storedCode != null && storedCode.equals(authCode);
    }

    // Thymeleaf를 이용한 context 설정
    public String setContext(String code) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process("mail", context); // mail.html
    }
}

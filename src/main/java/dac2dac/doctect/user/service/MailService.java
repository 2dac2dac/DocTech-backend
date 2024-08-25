package dac2dac.doctect.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;
    private String authNum; //랜덤 인증 코드

    //랜덤 인증 코드 생성
    public void createCode() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for(int i = 0; i < 8; i++) {
            int index = random.nextInt(3);

            switch (index) {
                case 0:
                    key.append((char) ((int)random.nextInt(26) + 97));
                    break;
                case 1:
                    key.append((char) ((int)random.nextInt(26) + 65));
                    break;
                case 2:
                    key.append(random.nextInt(10));
                    break;
            }
        }
        authNum = key.toString();
    }

    //메일 양식 작성
    public MimeMessage createEmailForm(String email) throws MessagingException, UnsupportedEncodingException {
        createCode(); //인증 코드 생성
        String setFrom = "nowee018@naver.com"; // 보낼 사람의 이메일 주소
        String toEmail = email; // 받는 사람의 이메일 주소
        String title = "CODEBOX 회원가입 인증 번호"; // 이메일 제목

        MimeMessage message = emailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, email); // 보낼 이메일 설정
        message.setSubject(title); // 제목 설정
        message.setFrom(setFrom); // 보내는 이메일 설정
        message.setContent(setContext(authNum), "text/html; charset=utf-8");

        return message;
    }

    //실제 메일 전송
    public String sendEmail(String toEmail) throws MessagingException, UnsupportedEncodingException {
        MimeMessage emailForm = createEmailForm(toEmail); // 메일 양식 작성
        emailSender.send(emailForm); // 메일 전송
        return authNum; // 인증 코드 반환
    }

    // Thymeleaf를 이용한 context 설정
    public String setContext(String code) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process("mail", context); // mail.html
    }
}

package dac2dac.doctect.user.controller;

import dac2dac.doctect.user.dto.EmailAuthRequestDto;
import dac2dac.doctect.user.service.MailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class EmailController {

    private final MailService mailService;

    @PostMapping("/api/v1/login/mailConfirm")
    public String mailConfirm(@RequestBody EmailAuthRequestDto emailDto) throws MessagingException, UnsupportedEncodingException {

        String authCode = mailService.sendEmail(emailDto.getEmail());
        return authCode;
    }
}

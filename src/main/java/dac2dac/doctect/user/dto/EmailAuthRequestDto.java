package dac2dac.doctect.user.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class EmailAuthRequestDto {

    @NotEmpty(message = "이메일을 입력해주세요")
    public String email;
    private String authCode; // 인증번호 추가
}

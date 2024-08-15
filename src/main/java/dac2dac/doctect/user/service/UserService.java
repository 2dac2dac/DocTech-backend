package dac2dac.doctect.user.service;

import ch.qos.logback.core.boolex.Matcher;
import dac2dac.doctect.bootpay.repository.PaymentMethodRepository;
import dac2dac.doctect.common.constant.ErrorCode;
import dac2dac.doctect.common.error.exception.NotFoundException;
import dac2dac.doctect.user.dto.response.UserInfoDto;
import dac2dac.doctect.user.entity.User;
import dac2dac.doctect.user.entity.constant.SocialType;
import dac2dac.doctect.user.jwt.JWTUtil;
import dac2dac.doctect.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtil jwtUtil;

    // 회원가입
    @Transactional
    public User registerUser(String username,  String email, String password, String phoneNumber, String code, SocialType socialType) {
        // 이메일 중복 체크
        if (userRepository.findByUsername(username) != null) {
            throw new RuntimeException("Username is already registered.");
        }

        // 이메일 중복 체크
        if (userRepository.findByEmail(email) != null) {
            throw new RuntimeException("Email is already registered.");
        }
        // 사용자 등록 로직
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));  // 비밀번호 암호화
        user.setPhoneNumber(phoneNumber);
        user.setCode(code);
        user.setCreateDate(LocalDateTime.now()); // 생성
        user.setUpdateDate(LocalDateTime.now()); // 업데이트
        user.setSocialType(socialType);
        // 사용자 저장
        return userRepository.save(user);
    }

    // 인증 서비스 구축
    // 로그인 메서드
    public boolean authenticateUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            // 비밀번호 확인
            return passwordEncoder.matches(password, user.getPassword());
        }
        return false;
    }

    public UserInfoDto getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        Integer paymentMethodCnt = paymentMethodRepository.countAllByUserId(userId);

        return UserInfoDto.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .paymentMethodCnt(paymentMethodCnt)
                .build();
    }


    public String authenticateAndGenerateToken(String username, String password) {
        // 사용자 인증 로직
        boolean authenticate = authenticateUser(username, password);
        User user = userRepository.findByUsername(username);
        if (authenticate) {
            // DB에서 조회한 사용자 ID를 사용하여 JWT 생성
            return jwtUtil.createJwt(username, user.getId(), 3600000L); // 1시간 유효한 토큰 생성
        } else {
            throw new RuntimeException("Invalid username or password");
        }
    }




}

